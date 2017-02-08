package gui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.Pair;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;
import exceptions.CorruptedSequenceException;
import exceptions.DissimilarGeneException;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.ConfigHandler;
import io.FileSaver;
import io.GeneHandler;
import io.SequenceReader;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * This class ...
 * 
 * @author bluemlj
 *
 */
public class GUIUtils {

	private static final String NORMAL_COLOR = "linear-gradient(#732423 0%, #553f4e 20%, #f7f553 80%, #63ee5e 100%)";
	private static final String HOVER_COLOR = "linear-gradient(#745fea 0%, #affeef 20%, #123456 100%)";
	
	
  /**
   * This method initialize the choiceBox and adds all Gene which are stored locally in the
   * Genes.txt
   * 
   * @param genes the choiceBox to initialize
   * @return reportpair, with indicator Boolean and reportString
   */
  public static Pair<Boolean, String> initializeGeneBox(ChoiceBox<String> genes) {
    try {
      GeneHandler.readGenes();
    } catch (IOException e) {
      return new Pair<Boolean, String>(false,
          "Reading Gene.txt was unsuccessful\n" + e.getMessage());
    }

    genes.setItems(FXCollections.observableArrayList(GeneHandler.getGeneNamesAndOrganisms()));
    return new Pair<Boolean, String>(true, "Reading Gene.txt was successful");
  }

  public static Pair<Boolean, String> initializeGeneBox(ListView<String> genes) {
    try {
      GeneHandler.readGenes();
    } catch (IOException e) {
      return new Pair<Boolean, String>(false,
          "Reading Gene.txt was unsuccessful\n" + e.getMessage());
    }

    genes.setItems(FXCollections.observableArrayList(GeneHandler.getGeneNamesAndOrganisms()));
    return new Pair<Boolean, String>(true, "Reading Gene.txt was successful");
  }

  public static Pair<Boolean, String> initializeResearchers(ChoiceBox<String> dropdown) {
    try {
      ConfigHandler.readConfig();
    } catch (IOException | ConfigReadException | ConfigNotFoundException e) {
      return new Pair<Boolean, String>(false,
          "Reading researchers from config.txt was unsuccessful\n" + e.getMessage());
    }
    dropdown.setItems(FXCollections.observableArrayList(ConfigHandler.getSortedResearchers()));
    dropdown.getSelectionModel().select(ConfigHandler.getResearcher());
    return new Pair<Boolean, String>(true, "Reading researchers from config.txt was successful");
  }

  /**
   * Main method of this class, alias the startbutton function.
   * 
   * @param sourcepath path to .ab1 file or folder
   * @param GeneID ID of the Gene in the Choicebox.
   * @return a Pair or Boolean, which indicates if the method was successful and a String, which can
   *         printed in the infoarea.
   * @throws DissimilarGeneException 
   */
  public static Pair<Boolean, String> runAnalysis(String sourcepath, String GeneID,
      String resultname) throws DissimilarGeneException {
    boolean success = false;
    StringBuilder report = new StringBuilder();

    // get all ab1 files
    Pair<LinkedList<File>, LinkedList<File>> sequences = getSequencesFromSourceFolder(sourcepath);
    if (sequences.first == null)
      if (sequences.second == null)
        return new Pair<Boolean, String>(success,
            "Reading Sequences unsuccessful, please make sure the given path is correct or the file s valid");
      else
        return new Pair<Boolean, String>(success,
            "No AB1 files were found at the given path or the file is invalid.");
    else
      report.append("Reading .ab1 file(s) was successful\n");

    // get the gene from the coiceboxID
    Gene gene = null;
    if(!GeneID.equals("-1")){
    gene = getGeneFromDropDown(GeneID).first;
    report.append(getGeneFromDropDown(GeneID).second.second + "\n");
    }
    // foreach ab1 file
    for (File file : sequences.first) {
      // get Sequence
      AnalysedSequence toAnalyse = readSequenceFromFile(file).first;
      if(GeneID.equals("-1")) gene = StringAnalysis.findRightGene(toAnalyse, GeneHandler.getGeneList());
      toAnalyse.setReferencedGene(gene);

      // checks if complementary and reversed Sequence is better, then
      // standard
      try {
        StringAnalysis.checkComplementAndReverse(toAnalyse);
      } catch (CorruptedSequenceException e) {
        report.append("Calculation of complementary sequence unsuccessful, analysing stops\n");
        return new Pair<Boolean, String>(success, report.toString());
      }

      // cut out vector
      StringAnalysis.trimVector(toAnalyse);

      int lengthBeforeTrimmingQuality = toAnalyse.getSequence().length();
      // cut out low Quality parts of sequence
      QualityAnalysis.trimLowQuality(toAnalyse);

      int stopcodonPosition = StringAnalysis.findStopcodonPosition(toAnalyse);
      if (stopcodonPosition != -1) toAnalyse.trimSequence(0, stopcodonPosition * 3 + 2);

      toAnalyse.setTrimPercentage(
          QualityAnalysis.percentageOfTrimQuality(lengthBeforeTrimmingQuality, toAnalyse));

      toAnalyse.setHisTagPosition(StringAnalysis.findHISTag(toAnalyse));
      // find all Mutations
      try {
        MutationAnalysis.findMutations(toAnalyse);
      } catch (UndefinedTypeOfMutationException | CorruptedSequenceException e) {
        report.append(
            "Mutation analysis was unsuccessful because of error in " + file.getName() + "\n");
        return new Pair<Boolean, String>(success, report.toString());
      }

      // add entry to database
      try {
        FileSaver.setDestFileName(resultname);
        FileSaver.storeResultsLocally(file.getName().replaceFirst("[.][^.]+$", ""), toAnalyse);

      } catch (MissingPathException e2) {
        report.append("Missing path to destination, aborting analysis.\n");
        FileSaver.setLocalPath("");
        return new Pair<Boolean, String>(success, report.toString());
      } catch (IOException e2) {
        report.append("Error while storing data, aborting analysis.\n");
        return new Pair<Boolean, String>(success, report.toString());
      } catch (UndefinedTypeOfMutationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    // set output parameter and return Pair.
    report.append("Analysis was successful\n");
    success = true;
    return new Pair<Boolean, String>(success, report.toString());
  }

  /**
   * This method opens a DirectoryChooser to set the destinationpath. Later results and reports will
   * be saved here.
   * 
   * @param destination Textfield, to place path.
   * @param sourcepath path from the source field
   * @return reportpair of boolean (indicates success) and report String
   */
  public static Pair<Boolean, String> setDestination(TextField destination, String sourcePath) {

    boolean success = false;
    String report = "Reading destination path was unsuccessful.";
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
      report = "Reading destination path was successful. \nDestination is:  " + path;
      FileSaver.setLocalPath(path);
      destination.setText(path);
    }
    return new Pair<Boolean, String>(success, report);
  }

  /**
   * This method opens a DirectoryChooser to set the sourcepath. In this path, there should be some
   * .ab1 files.
   * 
   * @param source Textfield, to place path.
   * @return reportpair of boolean (indicates success) and report String
   */
  public static Pair<Boolean, String> setSourceFolder(TextField source) {
    boolean success = false;
    String report = "Reading path to .ab1 file was unsuccessful.";
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
      report = "Reading path to .ab1 file folder was unsuccessful.";
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
      return new Pair<Boolean, String>(success, "The action to set a source folder was cancelled");
    }

    if (selectedDirectory != null) {
      path = selectedDirectory.getAbsolutePath();
      success = true;
      report = "Reading path was successful. \nFolder is:  " + path;
      source.setText(path);
    }
    return new Pair<Boolean, String>(success, report);
  }

  /**
   * This method gets the Gene from his ID.
   * 
   * @param dropdownID ID of Gene in the choiceBox
   * @return Gene and reportpair
   */
  private static Pair<Gene, Pair<Boolean, String>> getGeneFromDropDown(String name) {
    return new Pair<Gene, Pair<Boolean, String>>(GeneHandler.getGene(name),
        new Pair<Boolean, String>(true, "Reading gene was successful"));

  }

  /**
   * This method gets all ab1 files from an given path. It sorts them to ab1 files and other
   * 
   * @param source the path to check about .abi-Files
   * @return two lists in form of a Pair. The .ab1 files and the not usuable files.
   */
  private static Pair<LinkedList<File>, LinkedList<File>> getSequencesFromSourceFolder(
      String source) {

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
   * Reads the Sequence of the given File and prints Errors if necessary
   * 
   * @param file the .ab1 file
   * @return Analysedsequence and reportpair
   * @author Jannis
   */
  private static Pair<AnalysedSequence, Pair<Boolean, String>> readSequenceFromFile(File file) {
    String report = "Failure with: " + file.getAbsolutePath() + ".\n This file might be corrupted.";
    boolean success = false;
    Pair<Boolean, String> ret = null;
    try {
      success = true;
      report = "";
      ret = new Pair<Boolean, String>(success, report);
      return new Pair<AnalysedSequence, Pair<Boolean, String>>(
          SequenceReader.convertFileIntoSequence(file), ret);
    } catch (FileReadingException e) {
      System.out.println("Could not read from file.");
    } catch (IOException e) {
      System.out.println("Error during reading occured.");
    }
    return new Pair<AnalysedSequence, Pair<Boolean, String>>(null, ret);
  }
  
  
  
  
  public static void setColorOnNode(Node node, Color color) {
	  
	  String normalColor1 = "";
	  String normalColor2 = "";
	  String hoverColor1 = "";
	  String hoverColor2 = "";
	  switch(color) {
	  	case GREEN:
	  		normalColor1 = "rgb(184,239,54)";
	  		normalColor2 = "rgb(128,200,1)";
	  		hoverColor1 = "rgb(184,239,54)";
	  		hoverColor2 = "rgb(128,200,1)";
	  		break;
	  	case RED:
	  		normalColor1 = "rgb(251,117,117)";
	  		normalColor2 = "rgb(255,40,40)";
	  		hoverColor1 = "rgb(252,158,158)";
	  		hoverColor2 = "rgb(255,104,104)";
	  		break;
	  	case BLUE:
	  		normalColor1 = "rgb(60,204,204)";
	  		normalColor2 = "rgb(50,153,255)";
	  		hoverColor1 = "rgb(118,219,219)";
	  		hoverColor2 = "rgb(90,173,255)";
	  		break;
	  }

	  
	  String normalStyle = "-fx-background-color: radial-gradient(center 50% -39%, radius 200%, " + normalColor1 + " 45%, " + normalColor2 + " 50%); -fx-border-color: gray; -fx-border-radius: 3px;";
	  String hoverStyle = "-fx-background-color: radial-gradient(center 50% -39%, radius 200%, " + hoverColor1 + " 45%, " + hoverColor2 + " 50%); -fx-border-color: gray; -fx-border-radius: 3px;";
	  
	  
	  node.setStyle(normalStyle);
	  node.setOnMouseEntered(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent t) {
	        	node.setStyle(hoverStyle);
	        }});

	  node.setOnMouseExited(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent t) {
	        	node.setStyle(normalStyle);
	        }   
	    });
  	
  }
  
  
  
}
