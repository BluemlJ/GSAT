package io;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import com.mysql.cj.jdbc.MysqlDataSource;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.Primer;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseErrorException;

/**
 * Class to communicate with the database.
 * 
 * @author Ben Kohr, Lovis Heindrich
 *
 */
public class DatabaseConnection {

  private static Connection conn;

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
  // private static LinkedList<DatabaseEntry> queue = new
  // LinkedList<DatabaseEntry>();

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
  // public static void addAllIntoQueue(LinkedList<DatabaseEntry> entries) {
  // for (DatabaseEntry entry : entries) {
  // addIntoQueue(entry);
  // }
  // }

  /**
   * Puts a single entry in the waiting queue for being written into the database.
   * 
   * @param entry New Data point to be written into the database
   * 
   * @author Ben Kohr
   */
  // public static void addIntoQueue(DatabaseEntry entry) {
  // queue.add(entry);
  // }

  /**
   * Empties the current waiting queue.
   * 
   * @author Ben Kohr
   */
  // public static void flushQueue() {
  // queue.clear();
  // }

  /**
   * Inserts all currently stored entries into the specified database.
   * 
   * @see #insertIntoDatabase()
   * 
   * @author Ben Kohr
   */
  // public static void insertAllIntoDatabase()
  // throws DatabaseConnectionException, DatabaseErrorException {
  // // important: just ONE analyzed file in the queue!
  // conn = establishConnection();
  //
  // DatabaseEntry entry = queue.getFirst();
  // PreparedStatement pstmt;
  // try {
  // // main table
  // pstmt = conn.prepareStatement(
  // "INSERT INTO analysis (id, filename, geneid, sequence, addingdate,
  // researcher"
  // + "comments, leftvector, rightvector, promotor, manuallychecked) VALUES "
  // + "(?, '?', '?', '?', '?', '?', '?', '?', '?', '?', ?)");
  //
  // pstmt.setLong(1, entry.getID());
  // pstmt.setString(2, entry.getFileName());
  // pstmt.setInt(3, entry.getGeneID());
  // pstmt.setString(4, entry.getSequence());
  // pstmt.setString(5, entry.getAddingDate());
  // pstmt.setString(6, entry.getResearcher());
  // pstmt.setString(7, entry.getComments().replace(';', ','));
  // pstmt.setString(8, entry.getLeftVector());
  // pstmt.setString(9, entry.getRightVector());
  // pstmt.setString(10, entry.getPromotor());
  // pstmt.setString(11, "" + entry.isManuallyChecked());
  //
  // pstmt.execute();
  // queue.removeFirst();
  // pstmt.close();
  // } catch (SQLException e) {
  // throw new DatabaseErrorException();
  // }
  //
  // try {
  // pstmt = conn.prepareStatement(
  // "INSERT INTO mutations (id, mutation, mtype) " + "VALUES (?, '?', '?')");
  // pstmt.execute();
  // pstmt.close();
  // } catch (SQLException e1) {
  // throw new DatabaseErrorException();
  // }
  //
  // while (!queue.isEmpty()) {
  // insertIntoDatabase(pstmt);
  // }
  //
  // try {
  // conn.close();
  // } catch (SQLException e) {
  // throw new DatabaseConnectionException();
  // }
  // }

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

  public static Connection establishConnection() throws DatabaseConnectionException {

    Connection conn;

    try {
      // conn = DriverManager.getConnection(CONNECTION_STRING, "testname",
      // "password");
      conn = dataSource.getConnection();

    } catch (SQLException e) {
      throw new DatabaseConnectionException();
    }

    return conn;

  }

  /**
   * Inserts a single data point into the specified database.
   */
  // private static void insertIntoDatabase(PreparedStatement pstmt)
  // throws DatabaseConnectionException, DatabaseErrorException {
  // try {
  //
  // // mutation table
  // DatabaseEntry entry = queue.getFirst();
  // pstmt.setLong(1, entry.getID());
  // pstmt.setString(2, entry.getMutation());
  // pstmt.setString(3, "" + entry.getMutationType());
  //
  // } catch (SQLException e) {
  // throw new DatabaseErrorException();
  // }
  //
  // }

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

      // check if old gsat database exists and drop it
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables " + "WHERE table_schema = 'gsat' LIMIT 1");
      if (rs.next()) {
        // TODO this will delete all data!
        stmt.executeUpdate("DROP DATABASE gsat");
      }
      stmt.executeUpdate("CREATE DATABASE gsat");
      stmt.execute("USE gsat");
      stmt.executeUpdate("CREATE TABLE genes (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
          + "name VARCHAR(100) NOT NULL, sequence MEDIUMTEXT NOT NULL, date DATE, "
          + "researcher INTEGER unsigned, comment VARCHAR(1000), organism VARCHAR(1000), "
          + "PRIMARY KEY(id))");
      stmt.executeUpdate("CREATE TABLE primer (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
          + "name VARCHAR(100) NOT NULL, primerid VARCHAR(100) NOT NULL, "
          + "sequence MEDIUMTEXT NOT NULL, date DATE, researcher INTEGER unsigned, "
          + "meltingpoint INTEGER, PRIMARY KEY(id))");
      //TODO Primer comment
      stmt.executeUpdate("CREATE TABLE sequences (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
          + "name VARCHAR(100) NOT NULL, sequence MEDIUMTEXT NOT NULL, date DATE, "
          + "researcher INTEGER unsigned, comment VARCHAR(1000), manualcheck CHAR(1), "
          + "gene INTEGER unsigned, promoter MEDIUMTEXT, vectorleft MEDIUMTEXT, "
          + "vectorright MEDIUMTEXT, quality MEDIUMTEXT, trimleft INTEGER unsigned, "
          + "trimright INTEGER unsigned, trimpercent INTEGER unsigned, hisflag INTEGER, "
          + "PRIMARY KEY(id))");
      //TODO add avg quality
      //TODO delete quality
      //TODO promoter -> primer
      //TODO hisflag -> histag
      stmt.executeUpdate("CREATE TABLE mutations (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
          + "mutation VARCHAR(100) NOT NULL, sequence INTEGER unsigned NOT NULL, "
          + "type VARCHAR(100), " + "PRIMARY KEY(id))");
      stmt.executeUpdate("CREATE TABLE researchers (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
          + "name VARCHAR(100) NOT NULL, PRIMARY KEY(id))");
      stmt.close();

    } catch (DatabaseConnectionException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  /**
   * checks if the given database already has the necessary tables for storing data database name
   * must be 'gsat' and table names must be 'genes', 'sequences', 'mutations'
   * 
   * @return true if database exists, false if it does not exist
   * @author Lovis Heindrich
   */
  public static boolean gsatExists() {
    try {
      conn = establishConnection();
      Statement stmt = conn.createStatement();

      // check if table 'genes' exists
      ResultSet rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
          + "WHERE table_schema = 'gsat' AND table_name = 'genes' LIMIT 1");
      if (!rs.next()) {
        stmt.close();
        return false;
      }

      // check if table 'sequences' exists
      rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
          + "WHERE table_schema = 'gsat' AND table_name = 'sequences' LIMIT 1");
      if (!rs.next()) {
        stmt.close();
        return false;
      }

      // check if table 'mutations' exists
      rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
          + "WHERE table_schema = 'gsat' AND table_name = 'mutations' LIMIT 1");
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
   * @throws DatabaseConnectionException
   * @throws SQLException
   */
  public static void setDatabaseConnection(String user, String pass, int port, String server)
      throws DatabaseConnectionException, SQLException {
    DatabaseConnection.user = user;
    DatabaseConnection.pass = pass;
    DatabaseConnection.port = port;
    DatabaseConnection.server = server;
    DatabaseConnection.initDatabase();

    // check if database structure already exists
    if (!gsatExists()) {
      createDatabase();
    }

    conn = establishConnection();
    Statement stmt = conn.createStatement();
    stmt.execute("USE gsat");
    stmt.close();
    conn.close();

  }

  // public static void resetDatabaseConnection() {
  // flushQueue();
  // }

  /**
   * Pushes all data from a recent analysis: All analysed Sequences, the reference genes, mutations
   * and the researcher
   * 
   * @throws SQLException
   * @author Lovis Heindrich
   * @throws DatabaseConnectionException
   */
  public static void pushAllData(LinkedList<AnalysedSequence> sequences)
      throws SQLException, DatabaseConnectionException {

    // get a connection
    conn = establishConnection();

    // TODO why doesn´t it work with USE gsat only here?
    Statement stmt = conn.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    // get data
    // String researcher = ConfigHandler.getResearcher();

    // TODO get sequences
    // LinkedList<AnalysedSequence> sequences = new
    // LinkedList<AnalysedSequence>();

    // for each sequence
    for (AnalysedSequence sequence : sequences) {

      // push researcher and get id
      String researcher = sequence.getResearcher();
      int researcherId = pushResearcher(conn, researcher);

      // push gene with the researcher id and get gene id
      int geneId = pushGene(conn, sequence.getReferencedGene(), researcherId);

      // push sequence and get id
      int sequenceId = pushSequence(conn, sequence, researcherId, geneId);

      // push mutations
      pushMutations(conn, sequence.getMutations(), sequenceId);

    }

    conn.close();
  }

  /**
   * pushes all mutations for a single sequence into the database
   * 
   * @param conn2 database statement
   * @param mutations list of all mutations for a single sequence
   * @param sequenceId database id of the sequence
   * @author Lovis Heindrich
   * @throws SQLException
   */
  public static void pushMutations(Connection conn2, LinkedList<String> mutations, int sequenceId)
      throws SQLException {
    Statement stmt = conn2.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    for (String mutation : mutations) {
      
      // TODO delete type
      String type = ""; 

      // check if mutation exists
      PreparedStatement pstmt = conn2.prepareStatement(
          "SELECT id, mutation, sequence FROM mutations WHERE mutation = ? AND sequence = ?");
      pstmt.setString(1, mutation);
      pstmt.setInt(2, sequenceId);
      ResultSet res = pstmt.executeQuery();

      // push
      if (!res.next()) {
        pstmt = conn2
            .prepareStatement("INSERT INTO mutations (mutation, sequence, type) VALUES (?, ?, ?)");
        pstmt.setString(1, mutation);
        pstmt.setInt(2, sequenceId);
        pstmt.setString(3, type);
        pstmt.executeUpdate();
      }
    }

  }

  /**
   * checks if a sequence already exists and pushes it otherwise
   * 
   * @param conn2 database statement
   * @param sequence sequence that will be pushed
   * @param researcherId database id of the researcher
   * @param geneId database id of the gene
   * @return database id of the new entry
   * @author Lovis Heindrich
   * @throws SQLException
   */
  public static int pushSequence(Connection conn2, AnalysedSequence sequence, int researcherId,
      int geneId) throws SQLException {
    // select database
    Statement stmt = conn2.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    // set sequence parameter
    String name = sequence.getFileName();
    String seq = sequence.getSequence();
    // date conversion from string to sql.Date
    java.util.Date localDate;
    java.sql.Date sqlDate;
    DateFormat df = ConfigHandler.getDateFormat();
    try {
      localDate = df.parse(sequence.getAddingDate());
      sqlDate = new Date(localDate.getTime());
    } catch (ParseException e) {
      sqlDate = new Date(0);
    }
    String comment = sequence.getComments();
    String checked;
    if (sequence.isManuallyChecked()) {
      checked = "y";
    } else {
      checked = "n";
    }
    String promoter = sequence.getPrimer();
    String vecLeft = sequence.getLeftVector();
    String vecRight = sequence.getRightVector();
    String qualities = Arrays.toString(sequence.getQuality());
    
    // TODO delete
    int trimLeft = 0; 
    
    // TODO delete
    int trimRight = 0;
    
    int trimPercent = (int) sequence.getTrimPercentage();
    int hisFlag = sequence.getHisTagPosition();

    // check if sequence exists
    PreparedStatement pstmt =
        conn2.prepareStatement("SELECT id, name, sequence, gene FROM sequences "
            + "WHERE name = ? AND sequence = ? AND gene = ?");
    pstmt.setString(1, name);
    pstmt.setString(2, seq);
    pstmt.setInt(3, geneId);

    ResultSet res = pstmt.executeQuery();
    if (res.next()) {
      return res.getInt(1);
    }

    // push otherwise
    pstmt = conn2.prepareStatement(
        "INSERT INTO sequences (name, sequence, date, researcher, comment, manualcheck, "
            + "gene, promoter, vectorleft, vectorright, "
            + "quality, trimleft, trimright, trimpercent, hisflag) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    pstmt.setString(1, name);
    pstmt.setString(2, seq);
    pstmt.setDate(3, sqlDate);
    pstmt.setInt(4, researcherId);
    pstmt.setString(5, comment);
    pstmt.setString(6, checked);
    pstmt.setInt(7, geneId);
    pstmt.setString(8, promoter);
    pstmt.setString(9, vecLeft);
    pstmt.setString(10, vecRight);
    pstmt.setString(11, qualities);
    pstmt.setInt(12, trimLeft);
    pstmt.setInt(13, trimRight);
    pstmt.setInt(14, trimPercent);
    pstmt.setInt(15, hisFlag);
    pstmt.executeUpdate();

    // get index of new sequence
    pstmt = conn2.prepareStatement("SELECT id, name, sequence, gene FROM sequences "
        + "WHERE name = ? AND sequence = ? AND gene = ?");
    pstmt.setString(1, name);
    pstmt.setString(2, seq);
    pstmt.setInt(3, geneId);

    res = pstmt.executeQuery();
    if (res.next()) {
      return res.getInt(1);
    } else {
      System.out.println("sequence " + sequence.getFileName() + " failed");
      return -1;
    }
  }

  /**
   * 
   * @param conn2
   * @param referencedGene
   * @param researcherId
   * @return
   * @author Lovis Heindrich
   * @throws SQLException
   */
  public static int pushGene(Connection conn2, Gene gene, int researcherId) throws SQLException {
    Statement stmt = conn2.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    String name = gene.getName();
    String sequence = gene.getSequence();
    String organism = gene.getOrganism();
    DateFormat df = ConfigHandler.getDateFormat();
    java.util.Date localDate;
    java.sql.Date sqlDate;
    try {
      localDate = df.parse(gene.getAddingDate());
      sqlDate = new Date(localDate.getTime());
    } catch (ParseException e) {
      sqlDate = new Date(0);
    }

    String comment = gene.getComment();

    // check if gene exists
    PreparedStatement pstmt =
        conn2.prepareStatement("SELECT id, name, sequence, organism FROM genes "
            + "WHERE name = ? AND sequence = ? AND organism = ?");
    pstmt.setString(1, name);
    pstmt.setString(2, sequence);
    pstmt.setString(3, organism);

    ResultSet res = pstmt.executeQuery();
    if (res.next()) {
      return res.getInt(1);
    }

    // push otherwise
    pstmt = conn2
        .prepareStatement("INSERT INTO genes (name, sequence, date, researcher, comment, organism) "
            + "VALUES (?, ?, ?, ?, ?, ?)");
    pstmt.setString(1, name);
    pstmt.setString(2, sequence);
    pstmt.setDate(3, sqlDate);
    pstmt.setInt(4, researcherId);
    pstmt.setString(5, comment);
    pstmt.setString(6, organism);
    pstmt.executeUpdate();

    // get index of new gene
    pstmt = conn2.prepareStatement("SELECT id, name, sequence, organism FROM genes "
        + "WHERE name = ? AND sequence = ? AND organism = ?");
    pstmt.setString(1, name);
    pstmt.setString(2, sequence);
    pstmt.setString(3, organism);
    res = pstmt.executeQuery();
    if (res.next()) {
      return res.getInt(1);
    } else {
      // should never be called
      return -1;
    }
  }

  /**
   * Pushes a new researcher if he does not exist yet
   * 
   * @param conn2 database statement
   * @param researcher name of the researcher
   * @return database index of the researcher
   * @author Lovis Heindrich
   * @throws SQLException
   */
  public static int pushResearcher(Connection conn2, String researcher) throws SQLException {
    // TODO figure out how to do this one time only
    Statement stmt = conn2.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    // check if researcher exists
    PreparedStatement pstmt =
        conn2.prepareStatement("SELECT id, name FROM researchers WHERE name = ?");
    pstmt.setString(1, researcher);
    ResultSet res = pstmt.executeQuery();
    if (res.next()) {
      System.out.println("researcher: " + researcher + " already exists");
      return res.getInt(1);
    }

    // push otherwise
    pstmt = conn2.prepareStatement("INSERT INTO researchers (name) VALUES (?)");
    pstmt.setString(1, researcher);
    pstmt.executeUpdate();

    // get index of new researcher
    pstmt = conn2.prepareStatement("SELECT id, name FROM researchers WHERE name = ?");
    pstmt.setString(1, researcher);
    res = pstmt.executeQuery();
    if (res.next()) {
      System.out.println("added researcher " + researcher + " with id " + res.getInt(1));
      return res.getInt(1);
    } else {
      // should never be called
      System.out.println("adding researcher " + researcher + "failed");
      return -1;
    }
  }

  /**
   * pushes all genes saved in genes.txt
   * 
   * @throws SQLException
   * @throws DatabaseConnectionException
   * @throws IOException gene reading error
   * @author Lovis Heindrich
   */
  public static void pushAllGenes() throws SQLException, DatabaseConnectionException, IOException {

    // get Genes
    GeneHandler.readGenes();
    ArrayList<Gene> genes = GeneHandler.getGeneList();

    // get a connection
    conn = establishConnection();

    // select gsat database
    Statement stmt = conn.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    for (Gene gene : genes) {
      // search for researcher
      int researcherId = pushResearcher(conn, gene.getResearcher());

      // push the gene
      pushGene(conn, gene, researcherId);

    }

    conn.close();
  }

  public static void pushAllPrimer()
      throws DatabaseConnectionException, SQLException, NumberFormatException, IOException {

    // get primer
    PrimerHandler.readPrimer();
    ArrayList<Primer> primerList = PrimerHandler.getPrimerList();

    // get a connection
    conn = establishConnection();

    // select gsat database
    Statement stmt = conn.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    for (Primer primer : primerList) {
      // search for researcher
      int researcherId = pushResearcher(conn, primer.getResearcher());
      // push the gene
      pushPrimer(conn, primer, researcherId);
    }

  }

  public static void pushPrimer(Connection conn2, Primer primer, int researcherId)
      throws SQLException {
    Statement stmt = conn2.createStatement();
    stmt.execute("USE gsat");
    stmt.close();

    String name = primer.getName();
    String primerid = primer.getId();
    String sequence = primer.getSequence();
    DateFormat df = ConfigHandler.getDateFormat();
    java.util.Date localDate;
    java.sql.Date sqlDate;
    try {
      localDate = df.parse(primer.getAddingDate());
      sqlDate = new Date(localDate.getTime());
    } catch (ParseException e) {
      sqlDate = new Date(0);
    }
    int meltingPoint = primer.getMeltingPoint();

    // check if gene exists
    PreparedStatement pstmt =
        conn2.prepareStatement("SELECT id, name, sequence, primerid FROM primer "
            + "WHERE name = ? AND sequence = ? AND primerid = ?");
    pstmt.setString(1, name);
    pstmt.setString(2, sequence);
    pstmt.setString(3, primerid);

    ResultSet res = pstmt.executeQuery();

    // push if primer does not exist yet
    if (!res.next()) {
      pstmt = conn2.prepareStatement(
          "INSERT INTO primer (name, sequence, date, researcher, primerid, meltingpoint) "
              + "VALUES (?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, name);
      pstmt.setString(2, sequence);
      pstmt.setDate(3, sqlDate);
      pstmt.setInt(4, researcherId);
      pstmt.setString(5, primerid);
      pstmt.setInt(6, meltingPoint);
      pstmt.executeUpdate();
    }
  }

}
