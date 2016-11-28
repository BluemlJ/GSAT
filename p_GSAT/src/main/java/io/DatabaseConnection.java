package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import analysis.Gene;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseErrorException;
import exceptions.MissingPathException;
import exceptions.PathUsage;

/**
 * Class to communicate with the database. It's also responsible for storing files locally, if there
 * is e.g. no connection to the database available.
 * 
 * @author Ben Kohr
 *
 */
public class DatabaseConnection {

  /**
   * List off all entries to be written into the database. These are typically the results of the
   * analysis of a single file.
   */
  private static LinkedList<DatabaseEntry> queue = new LinkedList<DatabaseEntry>();

  /**
   * Specifies the location of the database.
   */
  private static String connectionString;

  /**
   * Specifies the path where local files shall be created (if necessary). This specifies the
   * folder, not the file!
   */
  private static String localPath;


  /**
   * This value is needed to keep track of the momentarily used id of the data.
   */
  private static int id = 0;



  /**
   * Inserts all currently stored entries into the specified database.
   * 
   * @see #insertIntoDatabase()
   * 
   * @author Ben Kohr
   */
  public static void insertAllIntoDatabase()
      throws DatabaseConnectionException, DatabaseErrorException {
    while (!queue.isEmpty()) {
      insertIntoDatabase();
    }
  }


  /**
   * Inserts a single data point into the specified database.
   */
  private static void insertIntoDatabase()
      throws DatabaseConnectionException, DatabaseErrorException {

  }


  /**
   * Inserts the data points into a local file (e.g. if there is no connection to the database
   * available right now).
   * 
   * @throws IOException
   */
  public static void storeAllLocally(String filename) throws MissingPathException, IOException {

    if (localPath == null) {
      throw new MissingPathException(PathUsage.WRITING);
    }

    FileWriter writer = new FileWriter(localPath + filename + ".csv");
    
    writer.write("id; file name; gene id; sequence; date; researcher; comments; vector; promotor; manually checked; mutation; silent" + System.lineSeparator());
    
    for (DatabaseEntry entry : queue) {

      // retrieve the data from the Database object
      int id = entry.getID();
      String fileName = entry.getFileName();
      int geneID = entry.getGeneID();
      String sequence = entry.getSequence();
      String addingDate = entry.getAddingDate();
      String researcher = entry.getResearcher();
      // As ';' is the seperator charachter, each inital semicolon is replaced
      String comments = entry.getComments().replace(';', ',');
      String vector = entry.getVector();
      String promotor = entry.getPromotor();
      boolean manuallyChecked = entry.isManuallyChecked();
      String mutation = entry.getMutation();
      boolean silent = entry.isSilent();

      // Concatenate the Strings together to one line to be written
      StringBuilder builder = new StringBuilder();
      builder.append(id).append("; ");
      builder.append(fileName).append("; ");
      builder.append(geneID).append("; ");
      builder.append(sequence).append("; ");
      builder.append(addingDate).append("; ");
      builder.append(researcher).append("; ");
      builder.append(comments).append("; ");
      builder.append(vector).append("; ");
      builder.append(promotor).append("; ");
      builder.append(manuallyChecked).append("; ");
      builder.append(mutation).append("; ");
      builder.append(silent).append(System.lineSeparator());

      // write the String into the file
      String toWrite = builder.toString();
      writer.write(toWrite);
    }

    writer.close();
  }


  /**
   * Puts a single entry in the waiting queue for being written into the database.
   * 
   * @param dbe New Data point to be written into the database
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
    for(DatabaseEntry entry : entries) {
      addIntoQueue(entry);
    }
  }
  

  /**
   * Sets the momentarily used id for a DatabaseEntry. Increments it afterwards to keep it up to
   * date (unique).
   * 
   * @param dbe The DatabaseEntry object which has no id right now
   * 
   * @author Ben Kohr
   */
  public static void setIdOnEntry(DatabaseEntry dbe) {
    dbe.setID(id);
    id++;
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
   * Retrieves all genes from the database and returns them.
   * 
   * @return List of genes currently stored in the database
   */
  public static LinkedList<Gene> retrieveAllGenes() {
    return null;
  }

}
