package io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import exceptions.MissingPathException;
import exceptions.PathUsage;

/**
 * Class to communicate with the database. It's also responsible for storing files locally, if there
 * is e.g. no connection to the database available.
 * 
 * @author Ben Kohr
 *
 */
public class FileSaver {

  /**
   * List off all entries to be written into files/a file. These are typically the results of the
   * analysis of a single file.
   */
  private static LinkedList<DatabaseEntry> queue = new LinkedList<DatabaseEntry>();

  /**
   * Specifies the path where local files shall be created. This specifies the folder, not the file!
   */
  private static String localPath;


  private static final String DEST_FILE_NAME = "gsat_results.csv";


  /**
   * Indicates whether one or multiple files shall be used for storage.
   */
  private static boolean separateFiles = false;

  /**
   * Indicating whether this is the first call on this class.
   */
  private static boolean firstCall = true;


  /**
   * This value is needed to keep track of the momentarily used id of the data.
   */
  private static long id = 0;



  /**
   * Inserts the data points into a local file (e.g. if there is no connection to the database
   * available right now).
   * 
   * @throws IOException
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
      writer = new FileWriter(localPath + "\\" + filename + ".csv");
      writer.write(
          "id; file name; gene id; sequence; date; researcher; comments; left vector; right vector; promotor; manually checked; mutation; mutation type"
              + System.lineSeparator());
    } else {
      if (firstCall) {
        writer = new FileWriter(localPath + "\\" + DEST_FILE_NAME);
        writer.write(
            "id; file name; gene id; sequence; date; researcher; comments; left vector; right vector; promotor; manually checked; mutation; mutation type"
                + System.lineSeparator());
        firstCall = false;
      } else {
        writer = new FileWriter(localPath + "\\" + DEST_FILE_NAME, true);
      }
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

    resetIDsIfNecessary();
  }


  /**
   * Puts a single entry in the waiting queue for being written into the database.
   * 
   * @param entry New Data point to be written into the database
   * 
   * @author Ben Kohr
   */
  public static void addIntoQueue(DatabaseEntry entry) {
    setIdOnEntry(entry);
    queue.add(entry);
  }


  /**
   * Puts all entries from a given list into this class's waiting queue.
   * 
   * @param entries A list of entries to be stored
   */
  public static void addAllIntoQueue(LinkedList<DatabaseEntry> entries) {
    for (DatabaseEntry entry : entries) {
      addIntoQueue(entry);
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
   * Sets the momentarily used id to zero, if separate files are desired.
   * 
   * @author Ben Kohr
   */
  private static void resetIDsIfNecessary() {
    if (separateFiles) {
      resetIDs();
    }
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
   * Empties the current waiting queue.
   * 
   * @author Ben Kohr
   */
  public static void flushQueue() {
    queue.clear();
  }


  /**
   * Sets the path where local files shall be created if necessary.
   * 
   * @param path The path to create local files as a String
   * 
   * @author Ben Kohr
   */
  public static void setLocalPath(String path) {
    localPath = path;
  }


  /**
   * Sets the path where local files shall be created if necessary.
   * 
   * @param path The path to create local files as a String
   * 
   * @author Ben Kohr
   */
  public static void setSeparateFiles(Boolean separate) {
    separateFiles = separate;
  }


  public static void resetAll() {
    flushQueue();
    resetIDs();
    firstCall = true;
  }

}
