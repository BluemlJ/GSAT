package io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import analysis.Gene;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseErrorException;

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
  private static final String CONNECTION_STRING = "jdbc:mysql://localhost:5432/test";

  private static Connection conn;

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
   * Puts a single entry in the waiting queue for being written into the database.
   * 
   * @param entry New Data point to be written into the database
   * 
   * @author Ben Kohr
   */
  public static void addIntoQueue(DatabaseEntry entry) {
    queue.add(entry);
  }


  private static Connection establishConnection() throws DatabaseConnectionException {

    Connection conn;

    try {
      conn = DriverManager.getConnection(CONNECTION_STRING, "testname", "password");
    } catch (SQLException e) {
      throw new DatabaseConnectionException();
    }

    return conn;

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
   * Inserts all currently stored entries into the specified database.
   * 
   * @see #insertIntoDatabase()
   * 
   * @author Ben Kohr
   */
  public static void insertAllIntoDatabase()
      throws DatabaseConnectionException, DatabaseErrorException {
    // important: just ONE analyzed file in the queue!
    conn = establishConnection();

    DatabaseEntry entry = queue.getFirst();
    PreparedStatement pstmt;
    try {
      // main table
      pstmt = conn.prepareStatement(
          "INSERT INTO analysis (id, filename, geneid, sequence, addingdate, researcher"
              + "comments, leftvector, rightvector, promotor, manuallychecked) VALUES "
              + "(?, '?', '?', '?', '?', '?', '?', '?', '?', '?', ?)");

      pstmt.setLong(1, entry.getID());
      pstmt.setString(2, entry.getFileName());
      pstmt.setInt(3, entry.getGeneID());
      pstmt.setString(4, entry.getSequence());
      pstmt.setString(5, entry.getAddingDate());
      pstmt.setString(6, entry.getResearcher());
      pstmt.setString(7, entry.getComments().replace(';', ','));
      pstmt.setString(8, entry.getLeftVector());
      pstmt.setString(9, entry.getRightVector());
      pstmt.setString(10, entry.getPromotor());
      pstmt.setString(11, "" + entry.isManuallyChecked());

      pstmt.execute();
      queue.removeFirst();
      pstmt.close();
    } catch (SQLException e) {
      throw new DatabaseErrorException();
    }

    try {
      pstmt = conn.prepareStatement(
          "INSERT INTO mutations (id, mutation, mtype) " + "VALUES (?, '?', '?')");
      pstmt.execute();
      pstmt.close();
    } catch (SQLException e1) {
      throw new DatabaseErrorException();
    }

    while (!queue.isEmpty()) {
      insertIntoDatabase(pstmt);
    }

    try {
      conn.close();
    } catch (SQLException e) {
      throw new DatabaseConnectionException();
    }
  }



  /**
   * Inserts a single data point into the specified database.
   */
  private static void insertIntoDatabase(PreparedStatement pstmt)
      throws DatabaseConnectionException, DatabaseErrorException {
    try {

      // mutation table
      DatabaseEntry entry = queue.getFirst();
      pstmt.setLong(1, entry.getID());
      pstmt.setString(2, entry.getMutation());
      pstmt.setString(3, "" + entry.getMutationType());

    } catch (SQLException e) {
      throw new DatabaseErrorException();
    }



  }



  /**
   * Retrieves all genes from the database and returns them.
   * 
   * @return List of genes currently stored in the database
   * @throws DatabaseConnectionException
   * @throws DatabaseErrorException
   */
  public static LinkedList<Gene> retrieveAllGenes()
      throws DatabaseConnectionException, DatabaseErrorException {

    LinkedList<Gene> allGenes = new LinkedList<Gene>();

    conn = establishConnection();
    try {
      PreparedStatement pstmt =
          conn.prepareStatement("SELECT id, name, sequence, researcher FROM genes");

      ResultSet result = pstmt.executeQuery();
      while (result.next()) {
        int id = result.getInt(1);
        String name = result.getString(2);
        String sequence = result.getString(3);
        String researcher = result.getString(4);
        Gene current = new Gene(sequence, id, name, researcher);
        allGenes.add(current);
      }

      pstmt.close();

    } catch (SQLException e) {
      throw new DatabaseErrorException();
    }

    return allGenes;
  }



  public void resetDatabaseConnection() {
    flushQueue();
  }

}
