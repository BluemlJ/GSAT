package io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import com.mysql.cj.jdbc.MysqlDataSource;

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

  private static Connection conn;

  /**
   * Specifies the location of the database.
   */
  private static final String CONNECTION_STRING = "jdbc:mysql://localhost:5432/test";

  /**
   * Mysql connection object
   */
  static MysqlDataSource dataSource;


  /**
   * Mysql user name
   */
  static String user = "root";

  /**
   * Mysql password
   */
  static String pass = "rootpassword";

  /**
   * Mysql port
   */
  static int port = 3306;

  /**
   * Mysql server address
   */
  static String server = "127.0.0.1";

  /**
   * List off all entries to be written into the database. These are typically the results of the
   * analysis of a single file.
   */
  private static LinkedList<DatabaseEntry> queue = new LinkedList<DatabaseEntry>();

  private static void initDatabase() {
    dataSource = new MysqlDataSource();
    dataSource.setUser(user);
    dataSource.setPassword(pass);
    dataSource.setPort(port);
    dataSource.setServerName(server);
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
   * Puts a single entry in the waiting queue for being written into the database.
   * 
   * @param entry New Data point to be written into the database
   * 
   * @author Ben Kohr
   */
  public static void addIntoQueue(DatabaseEntry entry) {
    queue.add(entry);
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



  private static Connection establishConnection() throws DatabaseConnectionException {

    Connection conn;

    try {
      // conn = DriverManager.getConnection(CONNECTION_STRING, "testname", "password");
      conn = dataSource.getConnection();
    } catch (SQLException e) {
      throw new DatabaseConnectionException();
    }

    return conn;

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
   * Creates gsat databse structure
   */
  private static void createDatabase() {
    try {
      conn = establishConnection();
      Statement stmt = conn.createStatement();

      stmt.executeQuery("CREATE DATABASE gsat");
      stmt.executeQuery("USE gsat");
      stmt.executeQuery(
          "CREATE TABLE genes (id INT unsigned NOT NULL, name VARCHAR(100) NOT NULL, sequence MEDIUMTEXT NOT NULL, date DATE, researcher unsigned INT,  PRIMARY KEY(id))");
      stmt.executeQuery("CREATE TABLE sequences (id INT unsigned NOT NULL, name VARCHAR(100) NOT NULL, sequence MEDIUMTEXT NOT NULL, date DATE, researcher unsigned INT, comment VARCHAR(1000), manualcheck CHAR(1), gene unsigned INT, promoter MEDIUMTEXT, vector-left MEDIUMTEXT, vector-right MEDIUMTEXT, quality MEDIUMTEXT, trim-left unsigned INT, trim-right unsigned INT, trim-percent unsigned INT, his-flag VARCHAR(100)");
      stmt.executeQuery(
          "CREATE TABLE mutations (id INT unsigned NOT NULL, name VARCHAR(100) NOT NULL, mutation VARCHAR(100) NOT NULL, type VARCHAR(100), PRIMARY KEY(id))");
      stmt.executeQuery(
          "CREATE TABLE researchers (id INT unsigned NOT NULL, name VARCHAR(100) NOT NULL, PRIMARY KEY(id))");



    } catch (DatabaseConnectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }


  /**
   * checks if the given database already has the necessary tables for storing data database name
   * must be 'gsat' and table names must be ‘genes‘, ‘sequences‘, ‘mutations‘
   * 
   * @return true if database exists, false if it does not exist
   */
  private static boolean gsatExists() {
    try {
      conn = establishConnection();
      Statement stmt = conn.createStatement();

      // check if table ‘genes‘ exists
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'genes' LIMIT 1");
      if (!rs.next()) {
        return false;
      }

      // check if table ‘sequences‘ exists
      rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'sequences' LIMIT 1");
      if (!rs.next()) {
        return false;
      }

      // check if table ‘mutations‘ exists
      rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'mutations' LIMIT 1");
      if (!rs.next()) {
        return false;
      }
      return true;
    } catch (DatabaseConnectionException | SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false;
  }

  /**
   * sets the values of the mysql database to be used and initializes the database
   * 
   * @param user mysql user name
   * @param pass mysql password
   * @param port mysql server port
   * @param server mysql server address
   * @return
   * @author Lovis Heindrich
   */
  public static void setDatabaseConnection(String user, String pass, int port, String server) {
    DatabaseConnection.user = user;
    DatabaseConnection.pass = pass;
    DatabaseConnection.port = port;
    DatabaseConnection.server = server;
    DatabaseConnection.initDatabase();
  }

  public void resetDatabaseConnection() {
    flushQueue();
  }

}
