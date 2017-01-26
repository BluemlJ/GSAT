package gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import io.FileSaver;
import io.GeneReader;
import io.SequenceReader;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
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

  /**
   * This method initialize the choiceBox and adds all Gene which are stored locally in the
   * Genes.txt
   * 
   * @param genes the choiceBox to initialize
   * @return reportpair, with indicator Boolean and reportString
   */
  public static Pair<Boolean, String> initializeGeneBox(ChoiceBox<String> genes) {
    try {
      GeneReader.readGenes();
    } catch (IOException e) {
      return new Pair<Boolean, String>(false,
          "Reading Gene.txt was unsuccessful\n" + e.getMessage());
    }

    genes.setItems(FXCollections.observableArrayList(GeneReader.getGeneNames()));
    return new Pair<Boolean, String>(true, "Reading Gene.txt was successful");
  }

  public static Pair<Boolean, String> initializeGeneBox(ListView<String> genes) {
    try {
      GeneReader.readGenes();
    } catch (IOException e) {
      return new Pair<Boolean, String>(false,
          "Reading Gene.txt was unsuccessful\n" + e.getMessage());
    }

    genes.setItems(FXCollections.observableArrayList(GeneReader.getGeneNames()));
    return new Pair<Boolean, String>(true, "Reading Gene.txt was successful");
  }

  
  /**
   * This method initialize the choiceBox and adds all Gene which are stored locally in the
   * Genes.txt
   * 
   * @param genes the choiceBox to initialize
   * @return reportpair, with indicator Boolean and reportString
   */
  public static Pair<Boolean, String> initializeResearchers(ChoiceBox<String> dropdown) {
    try {
      Config.readConfig();
    } catch (IOException | ConfigReadException | ConfigNotFoundException e) {
      return new Pair<Boolean, String>(false,
          "Reading researchers from config.txt was unsuccessful\n" + e.getMessage());
    }
    dropdown.setItems(FXCollections.observableArrayList(Config.getResearchers()));
    dropdown.getSelectionModel().select(Config.getResearcher());;
    return new Pair<Boolean, String>(true, "Reading researchers from config.txt was successful");
  }


  /**
   * Main method of this class, alias the startbutton function.
   * 
   * @param sourcepath path to .ab1 file or folder
   * @param GeneID ID of the Gene in the Choicebox.
   * @return a Pair or Boolean, which indicates if the method was successful and a String, which can
   *         printed in the infoarea.
   */
  public static Pair<Boolean, String> runAnalysis(String sourcepath, int GeneID) {
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
    Gene gene = getGeneFromDropDown(GeneID).first;
    report.append(getGeneFromDropDown(GeneID).second.second + "\n");

    // foreach ab1 file
    for (File file : sequences.first) {
      // get Sequence
      AnalysedSequence toAnalyse = readSequenceFromFile(file).first;
      toAnalyse.setReferencedGene(gene);

      // checks if complementary and reversed Sequence is better, then standard
      try {
        StringAnalysis.checkComplementAndReverse(toAnalyse);
      } catch (CorruptedSequenceException e) {
        report.append("Calculation of complementary sequence unsuccessful, analysing stops\n");
        return new Pair<Boolean, String>(success, report.toString());
      }

      // cut out vector
      StringAnalysis.trimVector(toAnalyse);

      // cut out low Quality parts of sequence
      QualityAnalysis.trimLowQuality(toAnalyse);


      if (StringAnalysis.findStopcodonPosition(toAnalyse) != -1)
        toAnalyse.trimSequence(0, StringAnalysis.findStopcodonPosition(toAnalyse) * 3 + 2);


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
        FileSaver.storeResultsLocally(file.getName().replaceFirst("[.][^.]+$", "") + "_result",
            toAnalyse);
      } catch (MissingPathException e2) {
        report.append("Missing path to destination, aborting analysis.\n");
        FileSaver.setLocalPath("");
        return new Pair<Boolean, String>(success, report.toString());
      } catch (IOException e2) {
        report.append("Error while storing data, aborting analysis.\n");
        return new Pair<Boolean, String>(success, report.toString());
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
   * @return reportpair of boolean (indicates success) and report String
   */
  public static Pair<Boolean, String> setDestination(TextField destination) {

    boolean success = false;
    String report = "Reading destination path was unsuccessful.";
    String path;

    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("Set destination path");
    File selectedDirectory = chooser.showDialog(null);

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

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Set path to the .ab1 file(s)");
    alert.setHeaderText("A single .ab1 file or a folder of .ab1 files?");


    ButtonType buttonTypeOne = new ButtonType("Folder");
    ButtonType buttonTypeTwo = new ButtonType("Single file");
    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == buttonTypeOne) {
      report = "Reading path to .ab1 file folder was unsuccessful.";
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Set path to the .ab1 files (folder)");
      selectedDirectory = chooser.showDialog(null);
    } else if (result.get() == buttonTypeTwo) {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Set path to the .ab1 file");
      selectedDirectory = chooser.showOpenDialog(null);
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
  private static Pair<Gene, Pair<Boolean, String>> getGeneFromDropDown(int dropdownID) {
    return new Pair<Gene, Pair<Boolean, String>>(GeneReader.getGeneAt(dropdownID),
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

  /**
   * 
   * @param configpath
   * @return
   */
  private static Pair<Boolean, String> runConfiguration(TextField configpath) {
    Config.setPath("resources/config.ini");
    boolean success = false;
    String report = "Reading configuration file was unsuccessful with unknown error";
    try {
      Config.readConfig();
      success = true;
      report = "Reading configuration file was successful";
    } catch (ConfigReadException e) {
      report = "An error occured while reading the configuration file.";
    } catch (ConfigNotFoundException e) {
      report = "No configuration file was found at the given path.";
    } catch (IOException e) {
      System.out.println("Error during reading occurred.");
    }
    return new Pair<Boolean, String>(success, report);
  }
}
