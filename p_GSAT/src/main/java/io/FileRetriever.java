package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import analysis.AnalysedSequence;

/**
 * This class is used to read in stored CSV result files and converts them into AnalysedSequences.
 * This is necessary to be able to store the information in the database, after a user may have
 * manually changed some of the values in the file.
 * 
 * @author Ben Kohr
 *
 */
public class FileRetriever {


  /**
   * This methods extracts all the stored information of all CSV files in the folder indicated by
   * the given path and stores them inside an AnalysedSequence object.
   * 
   * @param path The path of the folder where the CSV files are located
   * 
   * @return a list of AnalysedSequence objects, each representing an analysis result
   * 
   * @see #getFiles(String)
   * @see #convertLineToSequence(String)
   * 
   * @throws IOException if problems during file access occur
   * 
   * @author Ben Kohr
   */
  public static LinkedList<AnalysedSequence> convertFilesToSequences(String path)
      throws IOException {

    LinkedList<AnalysedSequence> sequences = new LinkedList<AnalysedSequence>();

    List<File> csvFiles = getFiles(path);

    for (File file : csvFiles) {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      LinkedList<String> lines = new LinkedList<String>();

      reader.lines().skip(1).forEach(line -> {
        lines.add(line);
      });
      reader.close();

      for (String line : lines) {
        AnalysedSequence seq = convertLineToSequence(line);
        sequences.add(seq);
      }
    }

    return sequences;

  }



  /**
   * This method creates File objects for each CSV file in a given folder and returns them as a
   * list.
   * 
   * @param path The path where the files are located
   * 
   * @return A list of file objects (one object for each CSV file)
   * 
   * @author Ben Kohr
   */
  private static List<File> getFiles(String path) {

    File pathAsFile = new File(path);
    File[] files = pathAsFile.listFiles();
    LinkedList<File> fileList = new LinkedList<File>();

    if (files == null) {
      files = new File[0];
    }

    for (int i = 0; i < files.length; i++) {
      if (files[i].getName().endsWith(".csv")) {
        fileList.add(files[i]);
      }
    }

    return fileList;

  }


  /**
   * Converts a CSV result file line into an AnalysedSequence object by placing the information into
   * the corresponding fields.
   * 
   * @param line One line containing analysis information from a CSV file
   * 
   * @return An AnaylsedSequence object containing the information of the given line
   * 
   * @author Ben Kohr
   */
  private static AnalysedSequence convertLineToSequence(String line) {

    String[] data = line.split(ConfigHandler.SEPARATOR_CHAR + "");

    for (int i = 0; i < data.length; i++) {
      data[i] = data[i].trim();
    }

    AnalysedSequence sequence = new AnalysedSequence();

    // data[0] contains the id
    sequence.setFileName(data[1]);
    sequence.setReferencedGene(GeneHandler.getGene(data[2]));

    // data[3] contains the organism (given by GeneHandler)

    String[] mutations = data[4].split(",");
    for (int i = 0; i < mutations.length; i++) {
      mutations[i] = mutations[i].trim();
      sequence.addMutation(mutations[i]);
    }

    sequence.setComments(data[5]);
    sequence.setResearcher(data[6]);
    sequence.setAddingDate(data[7]);
    sequence.setAvgQuality(Double.parseDouble(data[8]));
    sequence.setTrimPercentage(Double.parseDouble(data[9]));
    sequence.setSequence(data[10]);
    sequence.setLeftVector(data[11]);
    sequence.setRightVector(data[12]);
    sequence.setPrimer(data[13]);

    if (data[14].equals("none")) {
      sequence.setHisTagPosition(-1);
    } else {
      sequence.setHisTagPosition(Integer.parseInt(data[14]));
    }

    // data[15] contains the mutations again

    if (data[16].equalsIgnoreCase("yes") || data[16].equalsIgnoreCase("y")
        || data[16].equalsIgnoreCase("true")) {
      sequence.setManuallyChecked(true);
    } else {
      sequence.setManuallyChecked(false);
    }

    return sequence;

  }



}
