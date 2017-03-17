package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import analysis.AnalysedSequence;
import exceptions.MissingPathException;
import exceptions.PathUsage;

/**
 * This class is used to store the analysis results in local files. It produces comma separated
 * value files (CSV) which can be read with Excel.
 * 
 * @author Ben Kohr
 */
public class FileSaver {

  /**
   * This is the file name if only one file is desired. If multiple files are desired, the name of
   * the original AB1 file will be added to this name for each resulting file. This name can be
   * changed by the user, the name below is the default name.
   */
  private static String destinationFileName = "gsat_results";

  /**
   * This is the class's separator char. It separates the columns of the resulting CSV file.
   */
  private static final char SEPARATOR_CHAR = ConfigHandler.SEPARATOR_CHAR;

  /**
   * Indicating whether this is the first call of the storage method in the current storage process.
   * This field is necessary to keep up with the labeling.
   */
  private static boolean firstCall = true;


  private static HashMap<ProblematicComment, String> commentMap =
      new HashMap<ProblematicComment, String>();
  static {
    commentMap.put(ProblematicComment.NO_MATCH_FOUND,
        "There was no match found, so the sequence can't be analysed.");
    commentMap.put(ProblematicComment.SEQUENCE_TO_SHORT,
        "The usable part of the sequence is very short (One should probably adjust the parameters).");
  }


  /**
   * Specifies the path where local files shall be created. This specifies the folder, not the file!
   */
  private static File localPath;

  /**
   * Indicates whether one or multiple files shall be used for storage. By default, only one file
   * will be generated.
   */
  private static boolean separateFiles = false;

  /**
   * Sets the path where local files shall be created. The String argument is converted into a File.
   * If null is passed, the path is reset to null.
   * 
   * @param pathString The path to create local files (as String)
   * 
   * @author Ben Kohr
   */
  public static void setLocalPath(String pathString) {
    if (pathString != null) {
      localPath = new File(pathString);
    } else {
      localPath = null;
    }
  }

  /**
   * The destination file name can be changed by the user. It is passed as a String argument. If the
   * String is empty, the default name "gsat_results" will be used.
   * 
   * @param destFileName the name of the destination file
   */
  public static void setDestFileName(String destFileName) {
    if (destFileName.isEmpty()) {
      destinationFileName = "gsat_results";
    } else {
      destinationFileName = destFileName;
    }
  }

  /**
   * Inserts the data of an analysed sequence into one or multiple local file(s). The name of the
   * AB1 file is given as a parameter. It may be used to create the CSV file name (if separate files
   * are desired).
   * 
   * @param ab1Filename the name of the AB1 file the stored entries were obtained from. If only one
   *        file is desired, then the name will not be used.
   * 
   * @see #constructLineToWrite(AnalysedSequence)
   * @see #getNewWriter(String, boolean)
   * @see #getNewWriterForOneFile(String)
   * 
   * @throws MissingPathException If the path to store the data is not specified.
   * @throws IOException If the writing process fails (due to the used FileWriter).
   * 
   * @author Ben Kohr
   */
  public static void storeResultsLocally(String ab1Filename, AnalysedSequence sequence)
      throws MissingPathException, IOException {

    // Without a path, writing is not possible.
    if (localPath == null) {
      throw new MissingPathException(PathUsage.WRITING);
    }

    // The writer to create a file / files.
    FileWriter writer;

    // One or multiple files?
    if (separateFiles) {
      String finalName = localPath.getAbsolutePath() + File.separatorChar + destinationFileName
          + "_" + ab1Filename + ".csv";
      writer = getNewWriter(finalName, false);
    } else {
      String finalName =
          localPath.getAbsolutePath() + File.separatorChar + destinationFileName + ".csv";
      writer = getNewWriterForOneFile(finalName);
    }

    // write a new line containing the information of the sequence
    String toWrite = constructLineToWrite(sequence);
    writer.write(toWrite);

    writer.close();

  }

  /**
   * This methods converts the analysed sequence object passed to it into a CSV line containing all
   * the information to store.
   * 
   * @param sequence The analysed sequence object. It is assumed to contain all data to be stored.
   * 
   * @return a line to insert into a CSV file (with the information of the sequence)
   * 
   * @author Ben Kohr
   */
  private static String constructLineToWrite(AnalysedSequence sequence) {

    StringBuilder builder = new StringBuilder();

    // file name
    String fileName = sequence.getFileName();
    builder.append(fileName).append(SEPARATOR_CHAR + " ");

    // gene
    String geneName = sequence.getReferencedGene().getName();
    builder.append(geneName).append(SEPARATOR_CHAR + " ");

    // gene organism
    String organism = sequence.getReferencedGene().getOrganism();
    builder.append(organism).append(SEPARATOR_CHAR + " ");

    if (!sequence.getProblematicComments().isEmpty()) {
      builder.append(SEPARATOR_CHAR + " ").append(SEPARATOR_CHAR + " ")
          .append(SEPARATOR_CHAR + " ");
      builder.append(constructProblematicComments(sequence));
      for (int i = 0; i < 7; i++) {
        builder.append(SEPARATOR_CHAR + " ");
      }
      builder.append(System.lineSeparator());
      return builder.toString();
    }

    // mutations (with nucleotide codons)
    LinkedList<String> mutations = sequence.getMutations();
    int numberOfMutations = sequence.getMutations().size();

    if (numberOfMutations == 0) {
      builder.append(SEPARATOR_CHAR + " ");
    } else {
      for (int i = 0; i < numberOfMutations; i++) {

        String mutation = mutations.get(i);
        builder.append(mutation);
        if (i < numberOfMutations - 1) {
          builder.append(", ");
        } else {
          builder.append(SEPARATOR_CHAR + " ");
        }
      }
    }

    // his tag
    // The his tag position starts with 1 in the stored result.
    int hisTagPosition = sequence.getHisTagPosition();
    if (hisTagPosition == -1) {
      builder.append("none" + SEPARATOR_CHAR + " ");
    } else {
      builder.append((hisTagPosition + 1) + SEPARATOR_CHAR + " ");
    }

    // manually checked
    boolean manuallyChecked = sequence.isManuallyChecked();
    builder.append(manuallyChecked).append(SEPARATOR_CHAR + " ");

    // comments
    // Replace the separator by a comma
    String comments = sequence.getComments().replace(SEPARATOR_CHAR, ',');
    builder.append(comments).append(SEPARATOR_CHAR + " ");

    // researcher
    String researcher = sequence.getResearcher();
    builder.append(researcher).append(SEPARATOR_CHAR + " ");

    // date
    String addingDate = sequence.getAddingDate();
    builder.append(addingDate).append(SEPARATOR_CHAR + " ");

    // average quality
    int avgQuality = sequence.getAvgQuality();
    builder.append(avgQuality).append(SEPARATOR_CHAR + " ");

    // trim percentage
    int trimPercentage = (int) sequence.getTrimPercentage();
    builder.append(trimPercentage).append(SEPARATOR_CHAR + " ");

    // nucleotides
    String nucleotides = sequence.getSequence();
    builder.append(nucleotides).append(SEPARATOR_CHAR + " ");

    // primer (may be changed by user manually)
    builder.append("none" + SEPARATOR_CHAR + " ");

    // mutations without nucleotide codons
    if (numberOfMutations == 0) {
      builder.append("; ");
    } else {
      for (int i = 0; i < numberOfMutations; i++) {
        String mutation = mutations.get(i);
        String reducedMutation;
        if (mutation.equals("reading frame error")) {
          reducedMutation = mutation;
        } else {
          reducedMutation = (mutation.trim()).split(" ")[0];
        }
        builder.append(reducedMutation);
        if (i < numberOfMutations - 1) {
          builder.append(", ");
        }
      }
    }

    builder.append(System.lineSeparator());

    String toWrite = builder.toString();

    return toWrite;
  }

  /**
   * This method initializes and initially uses a new writer, if only one file is desired.
   *
   * @see #getNewWriter(String, boolean)
   * 
   * @return the writer object, returned to continue writing
   * 
   * @throws IOException If the creation or the usage of the FileWriter object fails
   * 
   * @author Ben Kohr
   */
  private static FileWriter getNewWriterForOneFile(String finalName) throws IOException {
    FileWriter writer;
    if (firstCall) {
      writer = getNewWriter(finalName, false);
      firstCall = false;
    } else {
      writer = getNewWriter(finalName, true);
    }
    return writer;
  }

  /**
   * This method creates a new writer for the current writing situation (one or several files?). If
   * necessary, also adds the first line. It is directly called when separate files are needed. It
   * is called via {@link #getNewWriterForOneFile()} if one file is desired.
   * 
   * @param ab1Filename The name of the new file
   * @param append Shall content be added to the file (or is it a new file)?
   * 
   * @return the writer object, returned to continue writing
   * 
   * @throws IOException If the creation or the usage of the FileWriter object fails
   * 
   * @author Ben Kohr
   */
  private static FileWriter getNewWriter(String finalName, boolean append) throws IOException {

    File newFile = new File(finalName);

    if (!append) {
      newFile.createNewFile();
    }

    FileWriter writer = new FileWriter(newFile, append);

    if (!append) {
      writer.write("file name" + SEPARATOR_CHAR + " gene" + SEPARATOR_CHAR + " gene organism"
          + SEPARATOR_CHAR + " mutations (with codons)" + SEPARATOR_CHAR + " HIS tag"
          + SEPARATOR_CHAR + " manually checked" + SEPARATOR_CHAR + " comments" + SEPARATOR_CHAR
          + " researcher" + SEPARATOR_CHAR + " date" + SEPARATOR_CHAR + " average quality (percent)"
          + SEPARATOR_CHAR + " percentage of quality trim" + SEPARATOR_CHAR + " nucleotide sequence"
          + SEPARATOR_CHAR + " primer" + SEPARATOR_CHAR + " mutations (without codons)"
          + System.lineSeparator());
    }

    return writer;
  }

  public static String constructProblematicComments(AnalysedSequence seq) {

    StringBuilder builder = new StringBuilder();
    for (ProblematicComment comment : seq.getProblematicComments()) {
      builder.append(commentMap.get(comment) + " ");
    }
    builder.append(seq.getComments());

    String resultingComments = builder.toString().replace(SEPARATOR_CHAR, ',');

    return resultingComments;

  }


  public static boolean areCommentsProblematic(String comments) {
    Collection<String> problematicComments = commentMap.values();
    for (String probComment : problematicComments) {
      if (comments.contains(probComment)) {
        return true;
      }
    }
    return false;
  }



  /**
   * This method resets the class's state by resetting the ids and setting {@link #firstCall} to
   * true. This is necessary to start a completely new analyzing process.
   */
  public static void reset() {
    firstCall = true;
  }

  // GETTERS AND SETTERS:

  public static void setSeparateFiles(boolean separateFiles) {
    FileSaver.separateFiles = separateFiles;
  }

}
