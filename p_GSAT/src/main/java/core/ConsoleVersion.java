package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.biojava.bio.symbol.IllegalSymbolException;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.Pair;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import exceptions.ConfigNotFoundException;
import exceptions.CorruptedSequenceException;
import exceptions.DuplicateGeneException;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;
import io.ConsoleIO;
import io.FileSaver;
import io.GeneHandler;
import io.SequenceReader;

/**
 * 
 * <h1>This class is from an early version of our system. It only supports the main feature: Finding
 * mutations and writing the results to a .csv file.</h1>
 * <p>
 * 
 * </p>
 * This program version can be started with the console by adding the parameter -c.
 * <p>
 * 
 * 
 * </p>
 * 
 * @author Lovis Heindrich
 * @author Ben Kohr
 * @author Kevin Otto
 *
 */
public class ConsoleVersion {

  /**
   * Activates automatic gene matching for each sequence that is analysed.
   */
  private static boolean geneRecognition = false;

  /**
   * Creates, prints and stores a report of the reading of the files.
   * 
   * @param destination The destination path (where to store the results).
   * @param validFiles The list of valid AB1/ABI files.
   * @param badFiles The list of invalid files.
   * @param configReport A message on the level of success from reading the configuration file.
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
   * Starts the console version and runs the general analysis pipeline. Results will be stored in a
   * .csv file.
   * 
   * @author Lovis Heindrich
   */
  public static void startConsoleVersion() {
    ConsoleIO.clearConsole();
    // ask user for filepath
    Pair<LinkedList<File>, LinkedList<File>> okayAndOddFiles = askForAB1Files();

    // read config file
    String configReport = getConfig(
        System.getProperty("user.home") + File.separator + "gsat" + File.separator + "config.txt");

    // set path for results and set database path
    String destinationPath = processPath();

    // does the user want one or multiple files for local storage?
    askForOneOrMultipleFiles();

    // write a report on parsed files
    reportOnInput(destinationPath, okayAndOddFiles.first, okayAndOddFiles.second, configReport);

    // ask user for reference gene
    // Gene gene = askForGene();

    Gene gene = readGene();

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
   * Saves the results of one sequence in a .csv file.
   * 
   * @param activeSequence The sequence which will be saved.
   * @param file The file where the sequence has been read. Used to retrieve the file name.
   * @param destinationPath The path where the result will be saved.
   * @author Ben Kohr
   */
  private static void addLocalEntry(AnalysedSequence activeSequence, File file,
      String destinationPath) {
    try {
      FileSaver.storeResultsLocally(file.getName().replaceFirst("[.][^.]+$", "") + "_result",
          activeSequence);
    } catch (MissingPathException e) {
      FileSaver.setLocalPath(destinationPath);
    } catch (IOException e) {
      System.out.println("File " + file.getName() + " could not be written.");
    }

  }

  /**
   * Asks the User for the path to the AB1 files and returns a list of the found files Also checks
   * corectness of path and asks again if neccesary
   * 
   * @return A pair of all parsed files. The first list contains all correctly parsed .abi files and
   *         the second list all other files.
   * @author Kevin Otto
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
   * Asks user for gene sequence and gene name and adds it to the local gene file.
   * 
   * @return Gene containing the parsed data
   * @author Lovis Heindrich
   */
  private static Gene askForGene() {
    String strGene = null;
    String strGeneName = null;
    String saveGene = null;
    boolean noGene = true;
    while (noGene) {
      try {
        strGene = ConsoleIO.readLine("Please enter the nulceotide sequence of the reference gene."
            + System.lineSeparator() + "REFERENCE GENE SEQUENCE: ");
        noGene = false;
        strGeneName = ConsoleIO.readLine("Please enter the name of the gene."
            + System.lineSeparator() + "REFERENCE GENE NAME: ");
        saveGene = ConsoleIO.readLine("Do you want to save this gene for future use? (y/n)");
        if (saveGene.toLowerCase().equals("y")) {
          try {
            GeneHandler.addGene(strGeneName, strGene);
          } catch (DuplicateGeneException e) {
            System.out.println(e.getMessage());
          }
        }
      } catch (IOException e2) {
        noGene = true;
      }
    }

    return new Gene(strGene, 0, strGeneName, "");
  }

  /**
   * This method asks the user if they want a single output file or a file for each analyzed
   * sequence.
   * 
   * @author Ben Kohr
   */
  private static void askForOneOrMultipleFiles() {
    String message =
        "Would you like separate files for each input file?" + System.lineSeparator() + "y/n: ";

    boolean invalidInput = true;

    while (invalidInput) {
      try {
        String entered = ConsoleIO.readLine(message);

        if (entered.toLowerCase().equals("y")) {
          FileSaver.setSeparateFiles(true);
          return;
        } else if (entered.toLowerCase().equals("n")) {
          FileSaver.setSeparateFiles(false);
          return;
        }

      } catch (IOException e) {
        invalidInput = true;
      }
    }
  }

  /**
   * Prints done message in the console and closes the program.
   * 
   * @author Kevin Otto
   */
  private static void closeProgram() {
    System.out.println();
    System.out.println("DONE");
    System.out.println("Press enter to close console");
    try {
      ConsoleIO.readLine("");
      System.exit(0);
    } catch (IOException e) {
      System.exit(0);
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
    ConfigHandler.setPath(readingPath);
    String report = "Configuration file found";
    try {
      ConfigHandler.initConfig();
      ConfigHandler.readConfig();
    } catch (UnknownConfigFieldException e) {
      report = "An error occurred while reading the configuration file.";
    } catch (ConfigNotFoundException e) {
      report = "No configuration file was found at the given path.";
    } catch (IOException e) {
      System.out.println("Error during reading occurred.");
    }
    return report;
  }

  /**
   * Finds all mutations for a sequence and saves them in the sequence object.
   * 
   * @param sequence The sequence which will be analyzed.
   * @param file File of the sequence, used only for the file name.
   * @author Lovis Heindrich
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
    }
  }

  /**
   * Asks the user for a destination path where the results will be stored. The path is saved in
   * FileSaver.
   * 
   * @return The path entered by the user if the path is valid, null otherwise.
   * @author Lovis Heindrich
   */
  private static String processPath() {
    String message = "Please enter the path where the results will be stored."
        + System.lineSeparator() + "DESTINATION PATH: ";
    boolean invalidPath = true;
    while (invalidPath) {
      try {
        String path = ConsoleIO.readLine(message);
        invalidPath = false;
        // set destination path for database entries
        FileSaver.setLocalPath(path);
        return path;
      } catch (IOException e) {
        invalidPath = true;
      }
    }
    return null;
  }

  /**
   * Runs the complete analysis pipeline for a single sequence and adds the sequence to the saved
   * files.
   * 
   * @param gene The gene the sequence will be compared to.
   * @param file The file which contains the sequence.
   * @param destinationPath The path where the results will be saved.
   * @author Lovis Heindrich
   */
  private static void processSequence(Gene gene, File file, String destinationPath) {
    AnalysedSequence activeSequence = null;

    // read sequence from file
    activeSequence = readSequenceFromFile(file);

    if (geneRecognition) {
      gene = StringAnalysis.findRightGene(activeSequence, GeneHandler.getGeneList());
    }
    activeSequence.setReferencedGene(gene);

    // checks if reversed or comeplenetary Sequence is better then the
    // active Sequence
    try {
      StringAnalysis.checkComplementAndReverse(activeSequence);
    } catch (CorruptedSequenceException e) {
      System.err.println("Unknown symbol detected in sequence.");
    }
    // cut out vector
    StringAnalysis.trimVector(activeSequence);

    // cut out low Quality parts of sequence
    QualityAnalysis.trimLowQuality(activeSequence);

    if (StringAnalysis.findStopcodonPosition(activeSequence) != -1) {
      activeSequence.trimSequence(0, StringAnalysis.findStopcodonPosition(activeSequence) * 3 + 2);
    }

    // checks if Sequence is corrupted
    try {
      QualityAnalysis.checkIfSequenceIsClean(activeSequence);
    } catch (CorruptedSequenceException e) {
      System.err.println("Sequence seems to be corrupted.");
    }
    // mutation analysis
    processMutations(activeSequence, file);

    // ask for comment
    // askForComment(activeSequence, file);

    // add entry to database
    addLocalEntry(activeSequence, file, destinationPath);
  }

  /**
   * Reads genes from file and returns the correct gene. Genes must be in a .txt file named
   * genes.txt in the same folder as the ab1 files. If genes.txt can not be found the user will be
   * asked to enter a new gene.
   * 
   * @return The selected or added Gene which will be used for the analysis.
   * @author Lovis Heindrich
   * 
   */
  private static Gene readGene() {
    String path = "/GeneData/Genes.txt";
    try {
      GeneHandler.readGenes(path);
      if (GeneHandler.getNumGenes() == 0) {
        // genes.txt empty
        System.out.println("Genes.txt is empty");
        return askForGene();
      } else {
        System.out.println("The following genes have been found:");
        String[] geneNames = GeneHandler.getGeneNames();
        for (int i = 0; i < geneNames.length; i++) {
          System.out.println((i + 1) + ": " + geneNames[i]);
        }
        System.out.println((geneNames.length + 1) + ": Enter new gene");
        System.out.println(
            (geneNames.length + 2) + ": Use automatic gene recognition (gene must be in database)");
        System.out.println();
        int index = Integer
            .parseInt(ConsoleIO.readLine("Please enter the Index of the gene you want to use."
                + System.lineSeparator() + "INDEX: "))
            - 1;

        // add new gene
        if (index == geneNames.length) {
          return askForGene();

          // use automatic gene recognition
        } else if (index == geneNames.length + 1) {
          geneRecognition = true;
          return null;

          // an existing gene has been chosen
        } else if (index >= 0 && index < geneNames.length) {
          return GeneHandler.getGeneAt(index);

          // bad user input
        } else {
          return null;
        }
      }
    } catch (IOException e) {
      // no genes.txt found
      System.out.println("Error reading genes.txt");
      // ask user for gene
      return askForGene();
    }
  }

  /**
   * Reads the sequence from a given File and prints error messages if necessary.
   * 
   * @param file The file of the sequence which will be parsed.
   * @return The parsed sequence.
   * @author Kevin Otto
   */
  private static AnalysedSequence readSequenceFromFile(File file) {
    try {
      return SequenceReader.convertFileIntoSequence(file);
    } catch (FileReadingException e) {
      System.err.println("Could not read file " + e.filename + ". This file might be corrupted.");
      System.out.println();
    } catch (IOException e) {
      System.err.print("Reading failed.");
    } catch (MissingPathException e) {
      System.err.print("No reading path specified.");
    } catch (IllegalSymbolException e) {
      System.err.print("Unknown number format observed.");
    }
    return null;
  }

}
