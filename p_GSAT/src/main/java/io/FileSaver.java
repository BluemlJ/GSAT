package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import analysis.AnalysedSequence;
import exceptions.MissingPathException;
import exceptions.PathUsage;

/**
 * Class to store the analysis results in local files. This class produces comma separated value
 * files (CSV) which can be read with Excel.
 * 
 * @author Ben Kohr
 */
public class FileSaver {


  /**
   * This is the standard file name if only one file is desired. (Otherwise, the files are named
   * after the AB1 source files.)
   */
  private static final String DEST_FILE_NAME = "gsat_results";

  /**
   * Indicating whether this is the first call of the storage method in the current storage process.
   * This field is necessary to keep up with the labeling.
   */
  private static boolean firstCall = true;


  /**
   * This value is needed to keep track of the momentarily used id of the data. One number
   * corresponds to a single sequence. Id start with one to be human-understandable.
   */
  private static long id = 1;

  /**
   * Specifies the path where local files shall be created. This specifies the folder, not the file!
   */
  private static File localPath;


  /**
   * Indicates whether one or multiple files shall be used for storage.
   */
  private static boolean separateFiles = false;


  /**
   * This method resets the class's state by resetting the ids and setting {@link #firstCall} to
   * true. This is necessary to start a completely new analyzing process.
   */
  public static void resetAll() {
    resetIDs();
    firstCall = true;
  }



  /**
   * Sets the momentarily used id to one.
   * 
   * @author Ben Kohr
   */
  public static void resetIDs() {
    id = 1;
  }



  /**
   * Sets the path where local files shall be created. The String argument is converted into a File.
   * If null is passed, the path is reset to null.
   * 
   * @param path The path to create local files (as String)
   * 
   * @author Ben Kohr
   */
  public static void setLocalPath(String pathString) {
    if (pathString != null) {
     // System.out.println(pathString);
      localPath = new File(pathString);

    } else {
      localPath = null;
    }
  }


  public static void setSeparateFiles(boolean pSeparateFiles) {
    separateFiles = pSeparateFiles;
  }


  /**
   * Inserts the stored data entries into one or multiple local file(s). The name of the AB1 file is
   * given as a parameter. It can be used to create the CSV file name.
   * 
   * @param filename the name of the AB1 file the stored entries were obtained from. If only one
   *        file is desired, then the name will not be used.
   * 
   * @throws MissingPathException If the path to store the data is not specified
   * @throws IOException If the writing process fails (due to the used FileWriter)
   * 
   * @author Ben Kohr
   */
  public static void storeResultsLocally(String filename, AnalysedSequence sequence)
      throws MissingPathException, IOException {

    // Without a path, writing is not possible.
    if (localPath == null) {
      throw new MissingPathException(PathUsage.WRITING);
    }

    // The writer to create a file / files.
    FileWriter writer;

    // One or multiple files?
    if (separateFiles) {
      writer = getNewWriter(filename, false);
    } else {
      writer = getNewWriter();
    }

    // retrieve the data from the AnalysedSequence object
    String fileName = sequence.getFileName();
    int geneID = sequence.getReferencedGene().getId();
    String nucleotides = sequence.getSequence();
    String addingDate = sequence.getAddingDate();
    String researcher = sequence.getResearcher();
    // As ';' is the seperator charachter, each inital semicolon is replaced
    String comments = sequence.getComments().replace(';', ',');
    String leftVector = sequence.getLeftVector();
    String rightVector = sequence.getRightVector();
    String promotor = sequence.getPromotor();
    double avgQuality = sequence.getAvgQuality();
    double trimPercentage = sequence.getTrimPercentage();
    int hisTagPosition = sequence.getHisTagPosition();
    boolean manuallyChecked = sequence.isManuallyChecked();

    // Concatenate the Strings together to one line to be written
    StringBuilder builder = new StringBuilder();
    builder.append(id).append("; ");
    builder.append(fileName).append("; ");
    builder.append(geneID).append("; ");
    builder.append(nucleotides).append("; ");
    builder.append(addingDate).append("; ");
    builder.append(researcher).append("; ");
    builder.append(comments).append("; ");
    builder.append(leftVector).append("; ");
    builder.append(rightVector).append("; ");
    builder.append(promotor).append("; ");
    builder.append(avgQuality).append("; ");
    builder.append(trimPercentage).append("; ");

    // The his tag position starts with 1 in the stored result.
    if (hisTagPosition == -1)
      builder.append("none; ");
    else
      builder.append((hisTagPosition + 1) + "; ");

    builder.append(manuallyChecked).append("; ");

    LinkedList<String> mutations = sequence.getMutations();
    int numberOfMutations = sequence.getMutations().size();
    for (int i = 0; i < numberOfMutations; i++) {
      builder.append(mutations.get(i));
      if (i < numberOfMutations - 1) {
        builder.append(", ");
      }
    }

    builder.append(System.lineSeparator());

    // write the String into the file
    String toWrite = builder.toString();
    writer.write(toWrite);


    writer.close();

    // if several files are desired, then the id field has to be set to zero. Also, ids have to be
    // incremented.
    updateIDs();
  }



  /**
   * This method returns and initially uses a new writer, if only one file is desired.
   *
   * @see #getNewWriter(String, boolean)
   * 
   * @return the writer object, returned to continue writing
   * 
   * @throws IOException If the creation or the usage of the FileWriter object fails
   */
  private static FileWriter getNewWriter() throws IOException {
    FileWriter writer;
    if (firstCall) {
      writer = getNewWriter(DEST_FILE_NAME, false);
      firstCall = false;
    } else {
      writer = getNewWriter(DEST_FILE_NAME, true);
    }
    return writer;
  }


  /**
   * This method creates a new writer for the current writing situation (one or several files?). If
   * necessary, also adds the first line. It is directly called when separate files are needed. It
   * is called via {@link #getNewWriter()} if one file is desired.
   * 
   * @param filename The name of the new file
   * @param append Shall content be added to the file (or is it a new file)?
   * 
   * @return the writer object, returned to continue writing
   * 
   * @throws IOException If the creation or the usage of the FileWriter object fails
   */
  private static FileWriter getNewWriter(String filename, boolean append) throws IOException {

    File newFile = new File(localPath.getAbsolutePath() + File.separatorChar + filename + ".csv");

    if (!append) {
      newFile.createNewFile();
    }

    FileWriter writer = new FileWriter(newFile, append);

    if (!append) {
      writer.write(
          "id; file name; gene id; sequence; date; researcher; comments; left vector; right vector; promotor; average quality; percentage of quality trim; HIS tag; manually checked; mutations"
              + System.lineSeparator());
    }

    return writer;
  }



  /**
   * Sets the momentarily used id to one, if separate files are desired (each file has it's own
   * number range, starting with zero).
   * 
   * @see #resetIDs()
   * 
   * @author Ben Kohr
   */
  private static void updateIDs() {
    if (separateFiles) {
      resetIDs();
    } else {
      id++;
    }
  }


}
