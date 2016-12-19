package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

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
import io.DatabaseConnection;
import io.DatabaseEntry;
import io.SequenceReader;

/**
 * This class coordinates the overall behavior of the program. It moderates the analyzing pipeline.
 *
 * @author Ben Kohr
 */
public class Main {

  /**
   * Start of the GSAT program.
   * 
   * @param args Unused input parameters
   */
  public static void main(String[] args) {
    boolean consoleMode = false;
    // DEBUG: TODO remove
    consoleMode = true;
    // DEBUG END
    for (int i = 0; i < args.length; i++) {
      if (args[i].toLowerCase().equals("c")) {
        consoleMode = true;
        break;
      }
    }

    if (consoleMode) startConsoleVersion();

  }

  private static void startConsoleVersion() {
    ConsoleIO.clearConsole();
    // ask user for filepath
    Pair<LinkedList<File>, LinkedList<File>> okayAndOddFiles = askForAB1Files();

    // read config file
    String configReport = getConfig(SequenceReader.getPath());

    // set path for results and set database path
    String destinationPath = processPath();

    // write a report on parsed files
    reportOnInput(destinationPath, okayAndOddFiles.first, okayAndOddFiles.second, configReport);    

    // ask user for reference gene
    Gene gene = askForGene();

    // get ab1 files
    LinkedList<File> files = okayAndOddFiles.first;

    // process all ab1 files
    for (File file : files) {
      // run pipeline for every sequence
      processSequence(gene, file, destinationPath);
    }

    // close console
    closeProgram();
  }


  /**
   * Creates, prints and stores a report of the reading of the files.
   * 
   * @param destination The destination path (where to store the results)
   * @param validFiles The list of valid AB1/ABI files
   * @param badFiles The list of invalid files
   * @param configReport A message on the level of success from reading the configuration file
   * 
   * @author Ben Kohr
   * 
   */
  public static void reportOnInput(String destination, LinkedList<File> validFiles,
      LinkedList<File> badFiles, String configReport) {
    StringBuilder builder = new StringBuilder();

    if (validFiles.isEmpty()) {
      builder.append("No valid AB1/ABI found.");
    } else {
      builder.append("The following files have been detected as valid AB1/ABI files:");
      builder.append(System.lineSeparator());

      // List all valid files
      for (File goodFile : validFiles) {
        builder.append(">>> ");
        builder.append(goodFile.getName());
        builder.append(System.lineSeparator());
      }

      builder.append(System.lineSeparator());

      // Number of files
      builder.append("Number of valid files: " + validFiles.size());
    }
    builder.append(System.lineSeparator());
    builder.append(System.lineSeparator());

    if (badFiles.isEmpty()) {
      builder.append("No invalid files detected.");
    } else {
      builder.append("The following files were invalid:");
      builder.append(System.lineSeparator());

      // List all valid files
      for (File badFile : badFiles) {
        builder.append(">>> ");
        builder.append(badFile.getName());
        builder.append(System.lineSeparator());
      }
      builder.append(System.lineSeparator());

      // Number of files
      builder.append("Number of invalid files: " + badFiles.size());
    }
    builder.append(System.lineSeparator());
    builder.append(System.lineSeparator());

    // configuration file
    builder.append("Config file: " + configReport);
    builder.append(System.lineSeparator());
    builder.append(System.lineSeparator());

    String message = builder.toString();
    System.out.println();
    System.out.print(message);
    try {
      FileWriter writer = new FileWriter(destination + "/report.txt");
      writer.write(message);
      writer.close();
    } catch (IOException e) {
      System.out.println("Report could not be saved as a file.");
    }
  }

  /**
   * runs the complete analysis pipeline for a single sequence and adds a database entry for the
   * sequence
   * 
   * @param activeSequence
   * @param gene
   * @param file
   */
  private static void processSequence(Gene gene, File file, String destinationPath) {
    AnalysedSequence activeSequence = null;

    // read sequence from file
    activeSequence = readSequenceFromFile(file);
    activeSequence.setReferencedGene(gene);
    // cut out low Quality parts of sequence
    QualityAnalysis.trimLowQuality(activeSequence);

    // cut out vector
    StringAnalysis.trimVector(activeSequence, gene);

    // mutation analysis
    processMutations(activeSequence, file);

    // ask for comment
    askForComment(activeSequence, file);

    // add entry to database
    addDatabaseEntry(activeSequence, file, destinationPath);
  }

  /**
   * Asks user for gene and gene name
   * 
   * @return Gene containing the parsed data
   */
  private static Gene askForGene() {
    // TODO Aks for GEN
    // String genPath = askForPath("Please give path to gene");

    // TODO Read GENE
    // TODO REALLY DO IT
    String strGene = null;
    String strGeneName = null;
    boolean noGene = true;
    while (noGene) {
      try {
        strGene = ConsoleIO.readLine("Please enter the nulceotide sequence of the reference gene."
            + System.lineSeparator() + "REFERENCE GENE SEQUENCE: ");
        noGene = false;
        strGeneName = ConsoleIO.readLine("Please enter the name of this gene."
            + System.lineSeparator() + "REFERENCE GENE NAME: ");
      } catch (IOException e2) {
        noGene = true;
      }
    }

    return new Gene(strGene, 0, strGeneName, "");
  }

  /**
   * Calls all necessary functions to process Mutations of a sequence
   * 
   * @param sequence
   * @param file
   */
  private static void processMutations(AnalysedSequence sequence, File file) {
    try {
      MutationAnalysis.findMutations(sequence);
    } catch (UndefinedTypeOfMutationException e) {
      System.err.println("Unknown mutation type found.");
      System.err.println("Mutation: " + e.mutationString);
      System.out.println();
    } catch (CorruptedSequenceException e) {
      System.err.println("The file " + file.getName()
          + " seems to be corrupted. An unknown nulceotide symbol was detected.");
      e.printStackTrace();
    }
  }

  /**
   * Asks user for a comment and sets the comment field of the referenced analysedSequence
   * 
   * @param sequence
   * @param file
   */
  private static void askForComment(AnalysedSequence sequence, File file) {
    try {
      sequence.setComments(ConsoleIO.readLine("Please enter a comment for file " + file.getName()
          + " or press ENTER directly to skip."));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  /**
   * prints done message and closes the console
   */
  private static void closeProgram() {
    System.out.println("PROGRAMM END");
    System.out.println("Press enter to terminate");
    try {
      ConsoleIO.readLine("");
    } catch (IOException e) {}
  }

  /**
   * adds a database entry for a sequence
   * 
   * @param activeSequence
   * @param file
   * @param destinationPath
   */
  private static void addDatabaseEntry(AnalysedSequence activeSequence, File file,
      String destinationPath) {
    try {
      LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(activeSequence);
      DatabaseConnection.addAllIntoQueue(entries);
      DatabaseConnection.storeAllLocally(file.getName().replaceFirst("[.][^.]+$", "") + "_result");
      resetPipeline();
    } catch (UndefinedTypeOfMutationException e) {
      System.err.println("Unknown mutation type found.");
      System.err.println("Mutation:" + e.mutationString);
      System.out.println();
    } catch (MissingPathException e) {
      DatabaseConnection.setLocalPath(destinationPath);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Reads in the config file.
   * 
   * @return Message indicating the level of success for reading the configuration.
   * 
   * @author Ben Kohr
   */
  private static String getConfig(String readingPath) {

    Config.setPath(readingPath);
    String report = "found";
    try {
      Config.readConfig();
    } catch (ConfigReadException e) {
      report = "An error occured while reading the configuration file.";
    } catch (ConfigNotFoundException e) {
      report = "No configuration file was found at the given path.";
    } catch (IOException e) {
      System.out.println("Error during reading occurred.");
    }
    return report;
  }



  /**
   * Reads the Sequence of the given File and prints Errors if necessary
   * 
   * @param file
   * @return
   * @author Kevin
   */
  private static AnalysedSequence readSequenceFromFile(File file) {
    try {
      return SequenceReader.convertFileIntoSequence(file);
    } catch (FileReadingException e) {
      System.err.println("Could not read file " + e.filename + ". This file might be corrupted.");
      System.out.println();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * asks user for destination path and sets the value in DatanbaseConnection
   * @return
   */
  private static String processPath() {
    String message = "Please enter the path where the results shall be stored."
        + System.lineSeparator() + "DESTINATION PATH: ";
    boolean invalidPath = true;
    while (invalidPath) {
      try {
        String path = ConsoleIO.readLine(message);
        invalidPath = false;
        // set destination path for database entries
        DatabaseConnection.setLocalPath(path);
        return path;
      } catch (IOException e) {
        invalidPath = true;
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Asks the User for the path to the AB1 files and returns a list of the found files Also checks
   * corectness of path and asks again if neccesary
   * 
   * @return
   */
  private static Pair<LinkedList<File>, LinkedList<File>> askForAB1Files() {
    boolean inputInvalid = true;

    LinkedList<File> files = null;
    LinkedList<File> oddFiles = null;
    // Ask User for AB1 File
    while (inputInvalid) {
      try {
        String path = ConsoleIO.readLine("Please enter the path to the AB1 files to be analyzed."
            + System.lineSeparator() + "SOURCE PATH: ");

        io.SequenceReader.configurePath(path);
        Pair<LinkedList<File>, LinkedList<File>> fileLists = io.SequenceReader.listFiles();
        files = fileLists.first;
        oddFiles = fileLists.second;

        if (files.isEmpty()) {
          System.err.println("No AB1 files were found at the given path.");
        } else {
          inputInvalid = false;
        }
      } catch (IOException e) {
        System.err.println("Invalid Input detected. Please try again.");
      }
    }
    return new Pair<LinkedList<File>, LinkedList<File>>(files, oddFiles);
  }

  /**
   * Resets the analysis pipeline to be able to start with a completely new analyzing process.
   * 
   * @author Ben Kohr
   */
  private static void resetPipeline() {
    DatabaseConnection.flushQueue();
    DatabaseConnection.resetIDs();
  }

}
