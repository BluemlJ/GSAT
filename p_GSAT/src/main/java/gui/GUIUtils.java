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
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.Config;
import io.ConsoleIO;
import io.FileSaver;
import io.GeneReader;
import io.SequenceReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class GUIUtils {

  public static Pair<Boolean, String> runAnalysis(String sourcepath, int GeneID) {
    boolean success = false;
    StringBuilder report = new StringBuilder();

    Pair<LinkedList<File>, LinkedList<File>> sequences = getSequencesFromSourceFolder(sourcepath);
    if (sequences.first == null)
      if (sequences.second == null)
        return new Pair<Boolean, String>(success,
            "Reading Sequences unsuccessfull with unknown error");
      else
        return new Pair<Boolean, String>(success, "No AB1 files were found at the given path.");
    else
      report.append("Reading .ab1-File(s) was successfull\n");

    Gene gene = getGeneFromDropDown(GeneID).first;
    report.append(getGeneFromDropDown(GeneID).second.second + "\n");

    for (File file : sequences.first) {
      AnalysedSequence toAnalyse = readSequenceFromFile(file).first;
      toAnalyse.setReferencedGene(gene);

      try {
        StringAnalysis.checkComplementAndReverse(toAnalyse);
      } catch (CorruptedSequenceException e) {
        report.append("Its not possible to get the complementary Sequence, analysing stops\n");
        return new Pair<Boolean, String>(success, report.toString());
      }
      // cut out vector
      StringAnalysis.trimVector(toAnalyse);

      // cut out low Quality parts of sequence
      QualityAnalysis.trimLowQuality(toAnalyse);

      try {
        MutationAnalysis.findMutations(toAnalyse);
        report.append("Finding Mutations was successfull.\n");
      } catch (UndefinedTypeOfMutationException | CorruptedSequenceException e) {
        report.append("FindMutation was not successfull because of Exception\n");
        return new Pair<Boolean, String>(success, report.toString());
      }
      // mutation analysis

      // add entry to database
      try {
        FileSaver.storeResultsLocally(file.getName().replaceFirst("[.][^.]+$", "") + "_result",
            toAnalyse);
      } catch (MissingPathException e2) {
        report.append("Missing Path to Destination, aborting analysing.\n");
        FileSaver.setLocalPath("");
        return new Pair<Boolean, String>(success, report.toString());
      } catch (IOException e2) {
        report.append("IOExeption in storing data, aborting analysing.\n");
        return new Pair<Boolean, String>(success, report.toString());
      }

    }
    report.append("Analysing was successfull\n");
    success = true;
    return new Pair<Boolean, String>(success, report.toString());
  }

  private static Pair<Gene, Pair<Boolean, String>> getGeneFromDropDown(int dropdownID) {
    return new Pair<Gene, Pair<Boolean, String>>(GeneReader.getGeneAt(dropdownID),
        new Pair<Boolean, String>(true, "Reading Gene was successfull"));

  }

  public static Pair<Boolean, String> initializeGeneBox(ChoiceBox<String> genes) {

    String path = new File("resources/GeneData/Genes.txt").getAbsolutePath();
    try {
      GeneReader.readGenes(path);
    } catch (IOException e) {
      return new Pair<Boolean, String>(false, "Failing with finding Gene.txt");
    }

    genes.setItems(FXCollections.observableArrayList(GeneReader.getGeneNames()));
    return new Pair<Boolean, String>(true, "Reading Gene.txt was successfull");
  }



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

  public static Pair<Boolean, String> setDestination(TextField destination) {

    boolean success = false;
    String report = "Reading destinationpath was unsuccessfull.";
    String path;

    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("Set Destinationpath");
    File defaultDirectory = new File("c:/");
    chooser.setInitialDirectory(defaultDirectory);
    File selectedDirectory = chooser.showDialog(null);

    if (selectedDirectory != null) {
      path = selectedDirectory.getAbsolutePath();
      success = true;
      report = "Reading destinationpath was successfull. \nDestination is:  " + path;
      FileSaver.setLocalPath(path);
      destination.setText(path);
    }
    return new Pair<Boolean, String>(success, report);
  }

  public static Pair<Boolean, String> setSourceFolder(TextField sourcefolder) {
    boolean success = false;
    String report = "Reading path to .ab1-File was unsuccessfull.";
    String path;
    File selectedDirectory = null;

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Set path to the ab1-File(s)");
    alert.setHeaderText("Do you have a single ab1-File or a folder of ab1-Files?");
    alert.setContentText("Choose your option.");

    ButtonType buttonTypeOne = new ButtonType("Folder of .ab1-Files");
    ButtonType buttonTypeTwo = new ButtonType("Single .ab1-File");
    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == buttonTypeOne) {
      report = "Reading path to .ab1-File Folder was unsuccessfull.";
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Set path to the .ab1-Files (Folder)");
      File defaultDirectory = new File("c:/");
      chooser.setInitialDirectory(defaultDirectory);
      selectedDirectory = chooser.showDialog(null);
    } else if (result.get() == buttonTypeTwo) {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Set path to the .ab1-File");
      File defaultDirectory = new File("c:/");
      chooser.setInitialDirectory(defaultDirectory);
      selectedDirectory = chooser.showOpenDialog(null);
    } else {
      return new Pair<Boolean, String>(success, "The Action to set a sourcefolder was canceled");
    }


    if (selectedDirectory != null) {
      path = selectedDirectory.getAbsolutePath();
      success = true;
      report = "Reading path was successfull. \nFoulder is:  " + path;
      sourcefolder.setText(path);
    }
    return new Pair<Boolean, String>(success, report);
  }

  private static Pair<Boolean, String> runConfiguration(TextField configpath) {
    Config.setPath(configpath.getText());
    boolean success = false;
    String report = "Reading configfile unsuccessfull with unknown error";
    try {
      Config.readConfig();
      success = true;
      report = "Reading configfile successfull";
    } catch (ConfigReadException e) {
      report = "An error occured while reading the configuration file.";
    } catch (ConfigNotFoundException e) {
      report = "No configuration file was found at the given path.";
    } catch (IOException e) {
      System.out.println("Error during reading occurred.");
    }
    return new Pair<Boolean, String>(success, report);
  }

  /**
   * Reads the Sequence of the given File and prints Errors if necessary
   * 
   * @param file
   * @return
   * @author Jannis
   */
  private static Pair<AnalysedSequence, Pair<Boolean, String>> readSequenceFromFile(File file) {
    String report = "Failure with" + file.getAbsolutePath() + "\n This file might be corrupted.";
    boolean success = false;
    Pair<Boolean, String> ret = new Pair<Boolean, String>(success, report);
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
}
