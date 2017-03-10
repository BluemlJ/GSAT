package gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Scanner;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.Pair;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import exceptions.ConfigNotFoundException;
import exceptions.CorruptedSequenceException;
import exceptions.DissimilarGeneException;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;
import io.FileSaver;
import io.GeneHandler;
import io.PrimerHandler;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * This class ...
 * 
 * @author bluemlj
 *
 */
public class GUIUtils {

  /**
   * This method initialize the choiceBox and adds all Gene which are stored locally in the
   * Genes.txt
   * 
   * @param genes the choiceBox to initialize
   * @return reportpair, with indicator Boolean and reportString
   */
  public static Text initializeGeneBox(ChoiceBox<String> genes) {
    try {
      GeneHandler.readGenes();
    } catch (IOException e) {
      return getRedText("Reading Gene.txt was unsuccessful\n");
    }

    genes.setItems(FXCollections.observableArrayList(GeneHandler.getGeneNamesAndOrganisms()));
    return new Text("Reading Gene.txt was successful\n");
  }

  /**
   * 
   * @param genes
   * @return
   */
  public static Text initializeGeneBox(ListView<String> genes) {
    try {
      GeneHandler.readGenes();
    } catch (IOException e) {
      return getRedText("Reading Gene.txt was unsuccessful\n");
    }

    genes.setItems(FXCollections.observableArrayList(GeneHandler.getGeneNamesAndOrganisms()));
    return new Text("Reading Gene.txt was successful");
  }

  /**
   * @param geneList
   */
  public static Text initializePrimerBox(ListView<String> geneList) {
    try {
      PrimerHandler.readPrimer();
    } catch (IOException e) {
      return getRedText("Reading Primer.txt was unsuccessful\n");
    }

    geneList.setItems(FXCollections.observableArrayList(PrimerHandler.getPrimerListWithIdAsString()));
    return new Text("Reading Primer.txt was successful");
  }


  /**
   * 
   * @param dropdown
   * @return
   */
  public static Text initializeResearchers(ChoiceBox<String> dropdown) {
    try {
      ConfigHandler.readConfig();
    } catch (IOException | UnknownConfigFieldException | ConfigNotFoundException e) {
      return getRedText("Reading researchers from config.txt was unsuccessful\n");
    }
    dropdown.setItems(FXCollections.observableArrayList(ConfigHandler.getSortedResearcherList()));
    dropdown.getSelectionModel().select(ConfigHandler.getResearcher());
    return new Text("Reading researchers from config.txt was successful");

  }

  /**
   * Main method of this class, alias the startbutton function.
   * 
   * @param sourcepath path to .ab1 file or folder
   * @param geneId ID of the Gene in the Choicebox.
   * @return a Pair or Boolean, which indicates if the method was successful and a String, which can
   *         printed in the infoarea.
   * @throws DissimilarGeneException
   */
  public static LinkedList<Text> runAnalysis(LinkedList<AnalysedSequence> sequences, String geneId,
      String resultname, ProgressBar bar) throws DissimilarGeneException {

    LinkedList<Text> resultingLines = new LinkedList<Text>();

    if (sequences == null) {
      return wrap(
          "Reading Sequences unsuccessful: "
              + "please make sure the given path is correct or the file is valid\n",
          resultingLines, true);
    }
    // get the gene from the coiceboxID
    Gene gene = null;
    if (!geneId.equals("-1")) {
      gene = getGeneFromDropDown(geneId).first;
      wrap(getGeneFromDropDown(geneId).second.second, resultingLines, false);
    }
    // foreach ab1 file
    int counter = 0;
    int allFiles = sequences.size();
    for (AnalysedSequence analysedSequence : sequences) {
      // get Sequence
      if (geneId.equals("-1")) {
        gene = StringAnalysis.findRightGene(analysedSequence, GeneHandler.getGeneList());
      }
      analysedSequence.setReferencedGene(gene);

      // checks if complementary and reversed Sequence is better, then
      // standard
      try {
        StringAnalysis.checkComplementAndReverse(analysedSequence);
      } catch (CorruptedSequenceException e) {
        return wrap("Calculation of complementary sequence unsuccessful, analysing stops\n",
            resultingLines, true);
      }

      // cut out vector
      StringAnalysis.trimVector(analysedSequence);

      int lengthBeforeTrimmingQuality = analysedSequence.getSequence().length();
      // cut out low Quality parts of sequence
      QualityAnalysis.trimLowQuality(analysedSequence);

      int stopcodonPosition = StringAnalysis.findStopcodonPosition(analysedSequence);
      if (stopcodonPosition != -1) {
        analysedSequence.trimSequence(0, stopcodonPosition * 3 + 2);
      }

      analysedSequence.setTrimPercentage(
          QualityAnalysis.percentageOfTrimQuality(lengthBeforeTrimmingQuality, analysedSequence));

      analysedSequence.setHisTagPosition(StringAnalysis.findHisTag(analysedSequence));
      // find all Mutations
      try {
        MutationAnalysis.findMutations(analysedSequence);
        MutationAnalysis.findPlasmidMix(analysedSequence);
      } catch (UndefinedTypeOfMutationException | CorruptedSequenceException e) {
        return wrap("Mutation analysis was unsuccessful because of error in "
            + analysedSequence.getFileName() + "\n", resultingLines, true);
      }

      // add average quality
      analysedSequence.setAvgQuality(QualityAnalysis.getAvgQuality(analysedSequence));

      // add entry to database
      try {
        FileSaver.setDestFileName(resultname);
        FileSaver.storeResultsLocally(analysedSequence.getFileName().replaceFirst("[.][^.]+$", ""),
            analysedSequence);

      } catch (MissingPathException e2) {
        FileSaver.setLocalPath("");
        return wrap("Missing path to destination, aborting analysis.\n", resultingLines, true);
      } catch (IOException e2) {
        return wrap("Error while storing data, aborting analysis.\n", resultingLines, true);
      }

      counter++;
      bar.setProgress(counter / (double) allFiles);

    }
    // set output parameter and return Pair.
    wrap("Analysis was successful\n", resultingLines, false);
    return resultingLines;
  }

  private static LinkedList<Text> wrap(String line, LinkedList<Text> list, boolean red) {
    Text text;
    if (red) {
      text = getRedText(line);
    } else {
      text = new Text(line);
    }
    list.add(text);
    return list;
  }

  /**
   * This method opens a DirectoryChooser to set the destinationpath. Later results and reports will
   * be saved here.
   * 
   * @param destination Textfield, to place path.
   * @param sourcePath path from the source field
   * @return reportpair of boolean (indicates success) and report String
   */
  public static Pair<Boolean, Text> setDestination(TextField destination, String sourcePath) {

    boolean success = false;
    Text report = getRedText("Reading destination path was unsuccessful.\n");
    String path;

    DirectoryChooser chooser = new DirectoryChooser();

    File selectedDirectory;
    if (!sourcePath.isEmpty()) {
      sourcePath = sourcePath.trim();
      for (int i = sourcePath.length() - 1; i > 0; i--) {
        if (sourcePath.charAt(i) == File.separatorChar) {
          sourcePath = sourcePath.substring(0, i);
          break;
        }
      }
      File start = new File(sourcePath);
      chooser.setInitialDirectory(start);

    }
    try {
      selectedDirectory = chooser.showDialog(null);
    } catch (java.lang.IllegalArgumentException e) {
      chooser.setInitialDirectory(new File(System.getProperty("user.home")));
      selectedDirectory = chooser.showDialog(null);
    }
    chooser.setTitle("Set destination path");
    if (selectedDirectory != null) {
      path = selectedDirectory.getAbsolutePath();
      success = true;
      report =
          new Text("Reading destination path was successful. \nDestination is:  " + path + "\n");
      FileSaver.setLocalPath(path);
      destination.setText(path);
    }
    return new Pair<Boolean, Text>(success, report);
  }

  /**
   * This method opens a DirectoryChooser to set the sourcepath. In this path, there should be some
   * .ab1 files.
   * 
   * @param source Textfield, to place path.
   * @return reportpair of boolean (indicates success) and report String
   */
  public static Pair<Boolean, Text> setSourceFolder(TextField source) {
    boolean success = false;
    Text report = getRedText("Reading path to .ab1 file was unsuccessful.\n");
    String path;
    File selectedDirectory = null;
    String defaultPath = source.getText();
    if (!defaultPath.isEmpty()) {
      defaultPath = defaultPath.trim();
      for (int i = defaultPath.length() - 1; i > 0; i--) {
        if (defaultPath.charAt(i) == File.separatorChar) {
          defaultPath = defaultPath.substring(0, i);
          break;
        }
      }

    }

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Set path to the .ab1 file(s)");
    alert.setHeaderText(null);
    alert.setContentText("A single .ab1 file or a folder of .ab1 files?");

    ButtonType buttonTypeOne = new ButtonType("Folder");
    ButtonType buttonTypeTwo = new ButtonType("Single file");
    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == buttonTypeOne) {
      report = getRedText("Reading path to .ab1 file folder was unsuccessful.\n");
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Set path to the .ab1 files (folder)");
      if (!defaultPath.isEmpty()) {
        File start = new File(defaultPath);
        chooser.setInitialDirectory(start);
      }
      try {
        selectedDirectory = chooser.showDialog(null);
      } catch (java.lang.IllegalArgumentException e) {
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        selectedDirectory = chooser.showDialog(null);
      }
    } else if (result.get() == buttonTypeTwo) {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Set path to the .ab1 file");
      if (!defaultPath.isEmpty()) {
        File start = new File(defaultPath);
        chooser.setInitialDirectory(start);
      }
      try {
        selectedDirectory = chooser.showOpenDialog(null);
      } catch (java.lang.IllegalArgumentException e) {
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        selectedDirectory = chooser.showOpenDialog(null);
      }
    } else {
      return new Pair<Boolean, Text>(success,
          getRedText("The action to set a source folder was cancelled\n"));
    }

    if (selectedDirectory != null) {
      path = selectedDirectory.getAbsolutePath();
      success = true;
      report = new Text("Reading path was successful. \nFolder is:  " + path + "\n");
      source.setText(path);
    }
    return new Pair<Boolean, Text>(success, report);
  }

  /**
   * This method gets the Gene from his ID.
   * 
   * @param dropdownID ID of Gene in the choiceBox
   * @return Gene and reportpair
   */
  private static Pair<Gene, Pair<Boolean, String>> getGeneFromDropDown(String name) {
    return new Pair<Gene, Pair<Boolean, String>>(GeneHandler.getGene(name),
        new Pair<Boolean, String>(true, "Reading gene was successful\n"));

  }

  /**
   * This method gets all ab1 files from an given path. It sorts them to ab1 files and other
   * 
   * @param source the path to check about .abi-Files
   * @return two lists in form of a Pair. The .ab1 files and the not usuable files.
   */
  static Pair<LinkedList<File>, LinkedList<File>> getSequencesFromSourceFolder(String source) {

    LinkedList<File> files = null;
    LinkedList<File> oddFiles = null;

    io.SequenceReader.configurePath(source);
    Pair<LinkedList<File>, LinkedList<File>> fileLists = io.SequenceReader.listFiles();
    files = fileLists.first;
    oddFiles = fileLists.second;

    if (files.isEmpty()) {
      return new Pair<LinkedList<File>, LinkedList<File>>(null, oddFiles);
    }
    return new Pair<LinkedList<File>, LinkedList<File>>(files, oddFiles);
  }


  /**
   * This methods sets a color on a given node object and also provides EventHandlers to change the
   * color slighly when the mouse is above, clicks or leaves the node. The available colors can be
   * found in {@link ButtonColor}.
   * 
   * @param button The button to be colored
   * @param color A ButtonColor element to specify the desired color scheme.
   */
  public static void setColorOnButton(Button button, ButtonColor color) {

    String normalColor = "";
    String hoverColor = "";
    String pressedColor = "";

    switch (color) {
      case GREEN:
        normalColor = "rgb(180, 241, 207)";
        hoverColor = "rgb(170, 221, 177)";
        pressedColor = "rgb(145, 206, 162)";
        break;
      case RED:
        normalColor = "rgb(255, 158, 128)";
        hoverColor = "rgb(235,138,108)";
        pressedColor = "rgb(215, 118, 88)";
        break;
      case BLUE:
        normalColor = "rgb(130, 177, 255)";
        hoverColor = "rgb(120, 157, 235)";
        pressedColor = "rgb(100, 137, 215)";
        break;
      case GRAY:
        int normal = 200;
        normalColor = "rgb(" + normal + ", " + normal + ", " + normal + ")";
        hoverColor = "rgb(" + (normal - 20) + ", " + (normal - 20) + ", " + (normal - 20) + ")";
        pressedColor = "rgb(" + (normal - 40) + ", " + (normal - 40) + ", " + (normal - 40) + ")";
        break;
      default:
        return;
    }

    String normalStyle = "-fx-background-color: " + normalColor
        + "; -fx-border-color: gray; -fx-border-radius: 3px;";
    String hoverStyle =
        "-fx-background-color: " + hoverColor + "; -fx-border-color: gray; -fx-border-radius: 3px;";
    String pressedStyle = "-fx-background-color: " + pressedColor
        + "; -fx-border-color: gray; -fx-border-radius: 3px;";

    button.setStyle(normalStyle);
    button.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent t) {
        button.setStyle(hoverStyle);
      }
    });

    button.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent t) {
        button.setStyle(normalStyle);
      }
    });
    button.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent t) {
        button.setStyle(pressedStyle);
      }
    });
    button.setOnMouseReleased(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent t) {
        button.setStyle(hoverStyle);
      }
    });

  }

  static Text getRedText(String message) {
    Text text = new Text(message);
    text.setStyle("-fx-stroke: red; -fx-stroke-width: 1;");
    return text;
  }


  public static String convertStreamToString(InputStream is) {
    Scanner s = new Scanner(is);
    String ret;
    StringBuilder builder = new StringBuilder();
    while (s.hasNextLine()) {
      builder.append(s.nextLine()).append("\n");
    }
    ret = builder.toString();
    System.out.println(ret);
    s.close();
    return ret;
  }



}
