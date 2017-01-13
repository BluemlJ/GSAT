package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

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
   * List of all entries to be stored locally. These are the results of analyzing one or multiple
   * AB1 files.
   */
  private static LinkedList<DatabaseEntry> queue = new LinkedList<DatabaseEntry>();

  /**
   * Specifies the path where local files shall be created. This specifies the folder, not the file!
   */
  private static File localPath;

  /**
   * This is the standard file name if only one file is desired. (Otherwise, the files are named
   * after the AB1 source files.)
   */
  private static final String DEST_FILE_NAME = "gsat_results";


  /**
   * Indicates whether one or multiple files shall be used for storage.
   */
  private static boolean separateFiles = false;

  /**
   * Indicating whether this is the first call of the storage method in the current storage process.
   * This field is necessary to keep up with the labeling.
   */
  private static boolean firstCall = true;


  /**
   * This value is needed to keep track of the momentarily used id of the data. One number
   * corresponds to a single entry (not a single sequence!).
   */
  private static long id = 0;


  /**
   * Puts all entries from a given list into this class's waiting queue. Also sets the ids.
   * 
   * @param entries A list of entries to be stored
   * 
   * @see #addIntoQueue(DatabaseEntry)
   * 
   * @author Ben Kohr
   */
  public static void addAllIntoQueue(LinkedList<DatabaseEntry> entries) {
    for (DatabaseEntry entry : entries) {
      addIntoQueue(entry);
    }
  }


  /**
   * Puts a single entry in the waiting queue for being written into a file. It first sets it's id
   * (which is currently not set).
   * 
   * @param entry new data point to be written into a file
   * 
   * @author Ben Kohr
   */
  public static void addIntoQueue(DatabaseEntry entry) {
    setIdOnEntry(entry);
    queue.add(entry);
  }


  /**
   * Empties the current waiting queue. This is necessary to start a new writing process.
   * 
   * @author Ben Kohr
   */
  public static void flushQueue() {
    queue.clear();
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
          "id; file name; gene id; sequence; date; researcher; comments; left vector; right vector; promotor; manually checked; mutation; mutation type"
              + System.lineSeparator());
    }

    return writer;
  }



  /**
   * This method resets the class's state by flushing the waiting queue, resetting the ids and
   * setting {@link #firstCall} to true. This is necessary to start a completely new analyzing
   * process.
   */
  public static void resetAll() {
    flushQueue();
    resetIDs();
    firstCall = true;
  }


  /**
   * Sets the momentarily used id to zero.
   * 
   * @author Ben Kohr
   */
  public static void resetIDs() {
    id = 0;
  }


  /**
   * Sets the momentarily used id to zero, if separate files are desired (each file has it's own
   * number range, starting with zero).
   * 
   * @see #resetIDs()
   * 
   * @author Ben Kohr
   */
  private static void resetIDsIfNecessary() {
    if (separateFiles) {
      resetIDs();
    }
  }



  /**
   * Sets the momentarily used id for a DatabaseEntry. Increments it afterwards to keep it up to
   * date (unique).
   * 
   * @param entry The DatabaseEntry object which has no id right now
   * 
   * @author Ben Kohr
   */
  public static void setIdOnEntry(DatabaseEntry entry) {
    entry.setID(id);
    id++;
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
    if (pathString != null)
      localPath = new File(pathString);
    else {
      localPath = null;
    }
  }


  public static void setSeparateFiles(boolean pSeparateFiles) {
    separateFiles = pSeparateFiles;
  }



  // GETTERs and SETTERs:

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
  public static void storeAllLocally(String filename) throws MissingPathException, IOException {

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

    for (DatabaseEntry entry : queue) {

      // retrieve the data from the Database object
      long id = entry.getID();
      String fileName = entry.getFileName();
      int geneID = entry.getGeneID();
      String sequence = entry.getSequence();
      String addingDate = entry.getAddingDate();
      String researcher = entry.getResearcher();
      // As ';' is the seperator charachter, each inital semicolon is replaced
      String comments = entry.getComments().replace(';', ',');
      String leftVector = entry.getLeftVector();
      String rightVector = entry.getRightVector();
      String promotor = entry.getPromotor();
      boolean manuallyChecked = entry.isManuallyChecked();
      String mutation = entry.getMutation();
      MutationType mType = entry.getMutationType();

      // Concatenate the Strings together to one line to be written
      StringBuilder builder = new StringBuilder();
      builder.append(id).append("; ");
      builder.append(fileName).append("; ");
      builder.append(geneID).append("; ");
      builder.append(sequence).append("; ");
      builder.append(addingDate).append("; ");
      builder.append(researcher).append("; ");
      builder.append(comments).append("; ");
      builder.append(leftVector).append("; ");
      builder.append(rightVector).append("; ");
      builder.append(promotor).append("; ");
      builder.append(manuallyChecked).append("; ");
      builder.append(mutation).append("; ");
      builder.append(mType).append(System.lineSeparator());

      // write the String into the file
      String toWrite = builder.toString();
      writer.write(toWrite);
    }

    writer.close();

    // if several files are desired, then the id field has to be set to zero
    resetIDsIfNecessary();
  }


}
