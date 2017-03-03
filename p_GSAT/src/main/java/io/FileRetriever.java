package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import analysis.AnalysedSequence;
import analysis.Gene;

public class FileRetriever {


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
