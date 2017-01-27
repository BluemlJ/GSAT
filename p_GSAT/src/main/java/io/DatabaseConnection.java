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
 * @author Ben Kohr, Lovis Heindrich
 *
 */
public class DatabaseConnection {

  private static Connection conn;

  /**
   * Specifies the location of the database.
   */
  // unused
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
   * Creates gsat database structure consisting of four tables: genes, sequences, mutations,
   * researcher. Names must not be changed. If a database called gsat already exists it will be
   * dropped.
   * 
   * @author Lovis Heindrich
   */
  public static void createDatabase() {
    try {

      conn = establishConnection();
      Statement stmt = conn.createStatement();
      // TODO this will delete all data!
      stmt.executeUpdate("DROP DATABASE gsat");
      stmt.executeUpdate("CREATE DATABASE gsat");
      stmt.execute("USE gsat");
      stmt.executeUpdate("CREATE TABLE testtable (id INTEGER unsigned NOT NULL, PRIMARY KEY (id))");
      stmt.executeUpdate(
          "CREATE TABLE genes (id INTEGER unsigned NOT NULL, name VARCHAR(100) NOT NULL, sequence MEDIUMTEXT NOT NULL, date DATE, researcher INTEGER unsigned,  PRIMARY KEY(id))");
      stmt.executeUpdate(
          "CREATE TABLE sequences (id INTEGER unsigned NOT NULL, name VARCHAR(100) NOT NULL, sequence MEDIUMTEXT NOT NULL, date DATE, researcher INTEGER unsigned, comment VARCHAR(1000), manualcheck CHAR(1), gene INTEGER unsigned, promoter MEDIUMTEXT, vectorleft MEDIUMTEXT, vectorright MEDIUMTEXT, quality MEDIUMTEXT, trimleft INTEGER unsigned, trimright INTEGER unsigned, trimpercent INTEGER unsigned, hisflag VARCHAR(100))");
      stmt.executeUpdate(
          "CREATE TABLE mutations (id INTEGER unsigned NOT NULL, name VARCHAR(100) NOT NULL, mutation VARCHAR(100) NOT NULL, type VARCHAR(100), PRIMARY KEY(id))");
      stmt.executeUpdate(
          "CREATE TABLE researchers (id INTEGER unsigned NOT NULL, name VARCHAR(100) NOT NULL, PRIMARY KEY(id))");
      stmt.close();


    } catch (DatabaseConnectionException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }


  }


  /**
   * checks if the given database already has the necessary tables for storing data database name
   * must be 'gsat' and table names must be ‘genes‘, ‘sequences‘, ‘mutations‘
   * 
   * @return true if database exists, false if it does not exist
   * @author Lovis Heindrich
   */
  public static boolean gsatExists() {
    try {
      conn = establishConnection();
      Statement stmt = conn.createStatement();

      // check if table ‘genes‘ exists
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'genes' LIMIT 1");
      if (!rs.next()) {
        stmt.close();
        return false;
      }

      // check if table ‘sequences‘ exists
      rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'sequences' LIMIT 1");
      if (!rs.next()) {
        stmt.close();
        return false;
      }

      // check if table ‘mutations‘ exists
      rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'mutations' LIMIT 1");
      if (!rs.next()) {
        stmt.close();
        return false;
      }
      stmt.close();
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

    // check if database structure already exists
    if (!gsatExists()) {
      createDatabase();
    }
  }

  public void resetDatabaseConnection() {
    flushQueue();
  }

}
