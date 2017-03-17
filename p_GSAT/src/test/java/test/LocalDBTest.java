package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;

import com.mysql.cj.jdbc.MysqlDataSource;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.Primer;
import exceptions.ConfigNotFoundException;
import exceptions.DatabaseConnectionException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;
import io.DatabaseConnection;
import io.PrimerHandler;

// import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * local test to try to connect to a local mysql database and read some properties
 * 
 * @author lovisheindrich
 *
 */
@Ignore
public class LocalDBTest {
  static String user = "root";
  static String pass = "rootpassword";
  static int port = 3306;
  static String server = "127.0.0.1";

  static String userOnline = "gsatadmin";
  static String passOnline = "";
  static int portOnline = 3306;
  static String serverOnline = "130.83.37.145";

  Connection conn = null;
  java.sql.Statement stmt = null;
  ResultSet rs = null;

  /**
   * NOT A TEST (used to manually cleanup database)
   * 
   * @throws DatabaseConnectionException
   * @throws SQLException
   * @throws UnknownConfigFieldException
   * @throws ConfigNotFoundException
   * @throws IOException
   */
  @Ignore
  @Test
  public void resetDB() throws DatabaseConnectionException, SQLException,
      UnknownConfigFieldException, ConfigNotFoundException, IOException {
    ConfigHandler.readConfig();
    DatabaseConnection.setDatabaseConnection(ConfigHandler.getDbUser(), ConfigHandler.getDbPass(),
        ConfigHandler.getDbPort(), ConfigHandler.getDbUrl());
    DatabaseConnection.createDatabase();
  }

  @Ignore
  @Test
  public void testPullCustom() throws DatabaseConnectionException, SQLException,
      UnknownConfigFieldException, ConfigNotFoundException, IOException {
    // DatabaseConnection.setDatabaseConnection(user, pass, port, server);

    DatabaseConnection.setDatabaseConnection(userOnline, passOnline, portOnline, serverOnline);

    DatabaseConnection.createDatabase();

    java.sql.Date d1 = java.sql.Date.valueOf("2000-01-01");
    java.sql.Date d2 = java.sql.Date.valueOf("2000-12-31");
    java.sql.Date d3 = java.sql.Date.valueOf("2007-12-31");
    java.sql.Date d4 = java.sql.Date.valueOf("2016-6-19");

    Gene g1 = new Gene("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1");
    Gene g2 = new Gene("aaatttgggaaa", 0, "fsa2", "Kevin Otto", "fsa", "comment1");
    LinkedList<String> m1 = new LinkedList<String>();
    m1.add("a5t");

    AnalysedSequence s1 = new AnalysedSequence(g1, m1, "seq1", "atatat", d1, "Lovis Heindrich",
        "comment1", false, "primer1", 80, 0, 99);
    AnalysedSequence s2 = new AnalysedSequence(g2, m1, "seq2", "atatat", d3, "Kevin Otto",
        "comment1", false, "primer1", 80, 0, 99);
    AnalysedSequence s3 = new AnalysedSequence(g1, m1, "seq3", "atatat", d3, "Kevin Otto",
        "comment1", false, "primer1", 80, 0, 99);
    AnalysedSequence s4 = new AnalysedSequence(g1, m1, "seq4", "atatat", d2, "Lovis Heindrich",
        "comment1", false, "primer1", 80, 0, 99);

    LinkedList<AnalysedSequence> seq = new LinkedList<AnalysedSequence>();
    seq.add(s1);
    seq.add(s2);
    seq.add(s3);
    seq.add(s4);

    DatabaseConnection.pushAllData(seq);

    // no parameters
    ArrayList<AnalysedSequence> res1 =
        DatabaseConnection.pullCustomSequences(null, null, null, null);
    assertEquals(res1.size(), 4);

    // researcher set
    ArrayList<AnalysedSequence> res2 =
        DatabaseConnection.pullCustomSequences(null, null, "Kevin Otto", null);
    assertEquals(res2.size(), 2);
    ArrayList<AnalysedSequence> res3 =
        DatabaseConnection.pullCustomSequences(null, null, "Lovis Heindrich", null);
    assertEquals(res3.size(), 2);

    // gene set
    ArrayList<AnalysedSequence> res4 =
        DatabaseConnection.pullCustomSequences(null, null, null, g1.getName());
    assertEquals(res4.size(), 3);
    ArrayList<AnalysedSequence> res5 =
        DatabaseConnection.pullCustomSequences(null, null, null, g2.getName());
    assertEquals(res5.size(), 1);

    // date set
    ArrayList<AnalysedSequence> res6 = DatabaseConnection.pullCustomSequences(d1, null, null, null);
    assertEquals(res6.size(), 4);
    ArrayList<AnalysedSequence> res7 = DatabaseConnection.pullCustomSequences(d2, null, null, null);
    assertEquals(res7.size(), 3);
    ArrayList<AnalysedSequence> res8 = DatabaseConnection.pullCustomSequences(d4, null, null, null);
    assertEquals(res8.size(), 0);
    ArrayList<AnalysedSequence> res9 = DatabaseConnection.pullCustomSequences(null, d2, null, null);
    assertEquals(res9.size(), 2);
    ArrayList<AnalysedSequence> res10 = DatabaseConnection.pullCustomSequences(d1, d2, null, null);
    assertEquals(res10.size(), 2);
    ArrayList<AnalysedSequence> res11 = DatabaseConnection.pullCustomSequences(d1, d4, null, null);
    assertEquals(res11.size(), 4);

    // combinations
    ArrayList<AnalysedSequence> res12 =
        DatabaseConnection.pullCustomSequences(null, null, "Kevin Otto", g1.getName());
    assertEquals(res12.size(), 1);
    ArrayList<AnalysedSequence> res13 =
        DatabaseConnection.pullCustomSequences(null, d4, "Lovis Heindrich", null);
    assertEquals(res13.size(), 2);
    ArrayList<AnalysedSequence> res14 =
        DatabaseConnection.pullCustomSequences(d2, d4, "Lovis Heindrich", g1.getName());
    assertEquals(res14.size(), 1);
    ArrayList<AnalysedSequence> res15 =
        DatabaseConnection.pullCustomSequences(d1, d2, null, g1.getName());
    assertEquals(res15.size(), 2);

    // check values
    ArrayList<AnalysedSequence> res16 =
        DatabaseConnection.pullCustomSequences(d2, d4, "Lovis Heindrich", g1.getName());
    assertEquals(res16.size(), 1);
    // s4: (g1, m1, "seq4", "atatat", d2, "Lovis Heindrich", "comment1",
    // false, "primer1", 80, 0, 99
    AnalysedSequence test1 = res16.get(0);

    // check gene values
    // ("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1")
    assertEquals(test1.getReferencedGene().getSequence(), g1.getSequence());
    assertEquals(test1.getReferencedGene().getName(), g1.getName());
    assertEquals(test1.getReferencedGene().getResearcher(), g1.getResearcher());
    assertEquals(test1.getReferencedGene().getOrganism(), g1.getOrganism());
    assertEquals(test1.getReferencedGene().getComment(), g1.getComment());

    // check mutation
    assertEquals(test1.getMutations().getFirst(), m1.getFirst());

    // check sequence data
    assertEquals(test1.getSequence(), s4.getSequence());
    assertEquals(test1.getFileName(), s4.getFileName());
    assertEquals(test1.getResearcher(), s4.getResearcher());
    assertEquals(test1.getAddingDate(), s4.getAddingDate());
    assertEquals(test1.getComments(), s4.getComments());
    assertEquals(test1.getPrimer(), s4.getPrimer());
    assertEquals(test1.getTrimPercentage(), s4.getTrimPercentage(), 0.0001);
    assertEquals(test1.getHisTagPosition(), s4.getHisTagPosition());
    assertEquals(test1.getAvgQuality(), s4.getAvgQuality());

    // refresh database
    DatabaseConnection.createDatabase();
  }

  @Ignore
  @Test
  public void testPullAndSavePrimer()
      throws SQLException, DatabaseConnectionException, NumberFormatException, IOException {
    // TODO manual test with 2 computers
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();
    PrimerHandler.readPrimer();

    DatabaseConnection.pullAndSavePrimer();

  }

  @Ignore
  @Test
  public void testPullSequencesPerResearcher() throws SQLException, DatabaseConnectionException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();
    LinkedList<String> mutations1 = new LinkedList<String>(Arrays.asList("t5a", "t6a"));
    LinkedList<String> mutations2 = new LinkedList<String>(Arrays.asList("t1a", "t2a"));
    AnalysedSequence sequence1 =
        new AnalysedSequence("aataataat", "Lovis Heindrich", "Sequence1", null);
    sequence1.setMutations(mutations1);
    AnalysedSequence sequence3 =
        new AnalysedSequence("gataataat", "Lovis Heindrich", "Sequence3", null);
    sequence1.setMutations(mutations1);
    AnalysedSequence sequence4 =
        new AnalysedSequence("tataataat", "Lovis Heindrich", "Sequence3", null);
    sequence1.setMutations(mutations1);
    sequence4.setMutations(mutations1);
    sequence3.setMutations(mutations1);
    AnalysedSequence sequence2 = new AnalysedSequence("ttattatta", "Kevin Otto", "Sequence2", null);
    sequence2.setMutations(mutations2);
    Gene gene1 = new Gene("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1");
    Gene gene2 = new Gene("gggtttaaa", 0, "fsa2", "Lovis Heindrich", "fsa", "comment2");
    sequence1.setReferencedGene(gene1);
    sequence3.setReferencedGene(gene1);
    sequence4.setReferencedGene(gene1);
    sequence2.setReferencedGene(gene2);
    LinkedList<AnalysedSequence> sequences = new LinkedList<AnalysedSequence>();
    sequences.add(sequence1);
    sequences.add(sequence2);
    sequences.add(sequence3);
    sequences.add(sequence4);

    DatabaseConnection.pushAllData(sequences);

    ArrayList<AnalysedSequence> lovisSequences =
        DatabaseConnection.pullAllSequencesPerResearcher("Lovis Heindrich");
    ArrayList<AnalysedSequence> kevinSequences =
        DatabaseConnection.pullAllSequencesPerResearcher("Kevin Otto");

    assertEquals(lovisSequences.size(), 3);
    assertEquals(kevinSequences.size(), 1);
  }

  @Ignore
  @Test
  public void testPullSequences() throws SQLException, DatabaseConnectionException,
      UnknownConfigFieldException, ConfigNotFoundException, IOException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);

    // for running the test online - this will delete all online data!
    // ConfigHandler.readConfig();
    // DatabaseConnection.setDatabaseConnection(ConfigHandler.getDbUser(),
    // ConfigHandler.getDbPass(), ConfigHandler.getDbPort(),
    // ConfigHandler.getDbUrl());

    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();

    // push test sequence
    LinkedList<String> mutations1 = new LinkedList<String>(Arrays.asList("t5a", "t6a"));
    AnalysedSequence sequence1 =
        new AnalysedSequence("AATAATAAT", "Lovis Heindrich", "Sequence1", null);
    sequence1.setMutations(mutations1);
    Gene gene1 = new Gene("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1");
    sequence1.setReferencedGene(gene1);
    LinkedList<AnalysedSequence> sequences = new LinkedList<AnalysedSequence>();
    sequences.add(sequence1);

    DatabaseConnection.pushAllData(sequences);

    ArrayList<AnalysedSequence> sequenceList = DatabaseConnection.pullAllSequences();

    assertEquals(sequenceList.size(), 1);
    AnalysedSequence sequence = sequenceList.get(0);

    // check sequence data
    assertEquals(sequence.getSequence(), "AATAATAAT");
    assertEquals(sequence.getFileName(), "Sequence1");

    // check researcher
    assertEquals(sequence.getResearcher(), "Lovis Heindrich");

    // check mutations data
    assertEquals(sequence.getMutations().size(), 2);
    assertEquals(sequence.getMutations().get(0), "t5a");

    // check gene data
    assertEquals(sequence.getReferencedGene().getName(), "fsa1");

    // reset db
    DatabaseConnection.createDatabase();
  }

  @Ignore
  @Test
  public void testPullGenes() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();

    Gene gene1 =
        new Gene("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1", new Date(10));
    Gene gene2 =
        new Gene("gggtttaaa", 0, "fsa2", "Lovis Heindrich", "fsa", "comment2", new Date(100));

    DatabaseConnection.pushGene(gene1, 0);
    DatabaseConnection.pushGene(gene2, 0);

    ArrayList<Gene> genes = DatabaseConnection.pullAllGenes();

    assertEquals(genes.size(), 2);
    assertTrue(genes.get(0).getName().equals("fsa1") || genes.get(0).getName().equals("fsa2"));
    assertTrue(genes.get(1).getName().equals("fsa1") || genes.get(1).getName().equals("fsa2"));

    assertTrue(
        genes.get(0).getAddingDate().equals(ConfigHandler.getDateFormat().format(new Date(10)))
            || genes.get(0).getAddingDate()
                .equals(ConfigHandler.getDateFormat().format(new Date(100))));
  }

  @Ignore
  @Test
  public void testPullResearcher() throws SQLException, DatabaseConnectionException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();

    DatabaseConnection.pushResearcher("Lovis Heindrich");
    DatabaseConnection.pushResearcher("Kevin Otto");

    ArrayList<String> researchers = DatabaseConnection.pullResearcher();

    assertEquals(researchers.size(), 2);
    assertTrue(researchers.contains("Lovis Heindrich"));
    assertTrue(researchers.contains("Kevin Otto"));
  }

  @Ignore
  @Test
  public void testPullMutations() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();

    LinkedList<String> mutations1 = new LinkedList<String>(Arrays.asList("t5a", "t6a"));
    LinkedList<String> mutations2 = new LinkedList<String>(Arrays.asList("t1a", "t2a"));
    DatabaseConnection.pushMutations(mutations1, 0);
    DatabaseConnection.pushMutations(mutations2, 1);

    LinkedList<String> mutations = DatabaseConnection.pullMutationsPerSequence(0);

    assertEquals(mutations.size(), 2);
    assertTrue(mutations.get(0).equals("t5a") || mutations.get(0).equals("t6a"));
    assertTrue(mutations.get(1).equals("t5a") || mutations.get(1).equals("t6a"));
  }

  @Ignore
  @Test
  public void testPullAllPrimer()
      throws DatabaseConnectionException, SQLException, NumberFormatException, IOException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();

    Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1", "comment1");
    Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2", "comment2");
    DatabaseConnection.pushPrimer(p1, 0);
    DatabaseConnection.pushPrimer(p2, 0);

    ArrayList<Primer> primerList = DatabaseConnection.pullAllPrimer();
    assertEquals(primerList.size(), 2);

    Primer p3 = primerList.get(0);
    Primer p4 = primerList.get(0);

    assertTrue(p3.getName().equals("primer1") || p3.getName().equals("primer2"));
    assertTrue(p4.getName().equals("primer1") || p4.getName().equals("primer2"));

  }

  @Ignore
  @Test
  public void testPushAllPrimer()
      throws DatabaseConnectionException, SQLException, NumberFormatException, IOException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.createDatabase();
    Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1", "comment1");
    Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2", "comment2");
    ArrayList<Primer> primerList = new ArrayList<Primer>();
    primerList.add(p1);
    primerList.add(p2);
    PrimerHandler.setPrimerList(primerList);
    DatabaseConnection.pushAllPrimer();
  }

  @Ignore
  @Test
  public void testPushPrimer() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();
    Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1", "comment1");
    Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2", "comment2");
    DatabaseConnection.pushPrimer(p1, 0);
    DatabaseConnection.pushPrimer(p2, 0);
    DatabaseConnection.pushPrimer(p1, 0);
    DatabaseConnection.pushPrimer(p2, 0);
  }

  @Ignore
  @Test
  public void testPushAllGenes() throws DatabaseConnectionException, SQLException, IOException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.pushAllGenes();
  }

  @Ignore
  @Test
  public void testDatabasePushPipeline() throws SQLException, DatabaseConnectionException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    DatabaseConnection.createDatabase();
    LinkedList<String> mutations1 = new LinkedList<String>(Arrays.asList("t5a", "t6a"));
    LinkedList<String> mutations2 = new LinkedList<String>(Arrays.asList("t1a", "t2a"));
    AnalysedSequence sequence1 =
        new AnalysedSequence("aataataat", "Lovis Heindrich", "Sequence1", null);
    sequence1.setMutations(mutations1);
    AnalysedSequence sequence2 = new AnalysedSequence("ttattatta", "Kevin Otto", "Sequence2", null);
    sequence2.setMutations(mutations2);
    Gene gene1 = new Gene("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1");
    Gene gene2 = new Gene("gggtttaaa", 0, "fsa2", "Lovis Heindrich", "fsa", "comment2");
    sequence1.setReferencedGene(gene1);
    sequence2.setReferencedGene(gene2);
    LinkedList<AnalysedSequence> sequences = new LinkedList<AnalysedSequence>();
    sequences.add(sequence1);
    sequences.add(sequence2);
    sequences.add(sequence1);

    DatabaseConnection.pushAllData(sequences);
  }

  @Ignore
  @Test
  public void testDatabaseConnectionConnect() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    assertTrue(DatabaseConnection.gsatExists());
  }

  @Ignore
  @Test
  public void testPushMutation() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();

    LinkedList<String> mutations1 = new LinkedList<String>(Arrays.asList("t5a", "t6a"));
    LinkedList<String> mutations2 = new LinkedList<String>(Arrays.asList("t1a", "t2a"));
    DatabaseConnection.pushMutations(mutations1, 0);
    DatabaseConnection.pushMutations(mutations1, 0);
    DatabaseConnection.pushMutations(mutations2, 0);
    DatabaseConnection.pushMutations(mutations1, 1);
  }

  @Ignore
  @Test
  public void testPushSequence() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    AnalysedSequence sequence1 = new AnalysedSequence("aataat", "Lovis", "Sequence1", null);
    AnalysedSequence sequence2 = new AnalysedSequence("ttatta", "Kevin", "Sequence2", null);
    int seq1 = DatabaseConnection.pushSequence(sequence1, 0, 0);
    int seq2 = DatabaseConnection.pushSequence(sequence2, 0, 0);
    int seq3 = DatabaseConnection.pushSequence(sequence1, 0, 0);
    int seq4 = DatabaseConnection.pushSequence(sequence2, 0, 0);

    assertEquals(seq1, seq3);
    assertEquals(seq2, seq4);
  }

  @Ignore
  @Test
  public void testPushResearcher() throws SQLException, DatabaseConnectionException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    int lh1 = DatabaseConnection.pushResearcher("Lovis Heindrich");
    int ko1 = DatabaseConnection.pushResearcher("Kevin Otto");
    int lh2 = DatabaseConnection.pushResearcher("Lovis Heindrich");
    int ko2 = DatabaseConnection.pushResearcher("Kevin Otto");
    assertEquals(lh1, lh2);
    assertEquals(ko1, ko2);

  }

  @Ignore
  @Test
  public void testPushGene() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    DatabaseConnection.establishConnection();
    Gene gene1 = new Gene("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1");
    Gene gene2 = new Gene("gggtttaaa", 0, "fsa2", "Lovis Heindrich", "fsa", "comment2");
    int g11 = DatabaseConnection.pushGene(gene1, 0);
    int g21 = DatabaseConnection.pushGene(gene2, 0);
    int g12 = DatabaseConnection.pushGene(gene1, 0);
    int g22 = DatabaseConnection.pushGene(gene2, 0);
    assertEquals(g11, g12);
    assertEquals(g21, g22);
    System.out.println(g11 + " " + g12 + " " + g21 + " " + g22);
  }

  public void testOnlineConUsingDBConnection() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(userOnline, passOnline, portOnline, serverOnline);
    // DatabaseConnection.createDatabase();
    System.out.println(DatabaseConnection.gsatExists());
  }

  /**
   * Working connection setup to online database
   */
  @Ignore
  @Test
  public void testOnlineDBConnection() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUser(userOnline);
    dataSource.setPassword(passOnline);
    dataSource.setPort(portOnline);
    dataSource.setServerName(serverOnline);

    try {
      conn = dataSource.getConnection();
      stmt = conn.createStatement();

      stmt.execute("USE gsat");

      rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'mutations' LIMIT 1");
      while (rs.next()) {
        System.out.println(rs.getString(1));
      }

      rs = stmt.executeQuery("SHOW TABLES");
      while (rs.next()) {
        System.out.println(rs.getString(1));

      }

      rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'gsat' AND table_name = 'mutations' LIMIT 1");
      while (rs.next()) {
        System.out.println(rs.getString(1));
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Ignore
  @Test
  public void DBConnectionTest() throws DatabaseConnectionException, SQLException {
    DatabaseConnection.setDatabaseConnection(user, pass, port, server);
    System.out.println(DatabaseConnection.gsatExists());
    DatabaseConnection.createDatabase();
    System.out.println(DatabaseConnection.gsatExists());

  }

  @Ignore
  @Test
  public void checkDBExists() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUser(user);
    dataSource.setPassword(pass);
    dataSource.setPort(port);
    dataSource.setServerName(server);

    try {
      conn = dataSource.getConnection();
      stmt = conn.createStatement();

      System.out.println("");
      rs = stmt.executeQuery(
          "SELECT * FROM information_schema.tables WHERE table_schema = 'Gsat' AND table_name = 'Gene' LIMIT 1");

      if (rs.next()) {
        if (!rs.getString(1).equals("def")) {
          System.out.println("false");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  @Ignore
  @Test
  public void testDBConnection() {

    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUser("root");
    dataSource.setPassword("rootpassword");
    dataSource.setPort(3306);
    dataSource.setServerName("127.0.0.1");

    try {
      conn = dataSource.getConnection();
      stmt = conn.createStatement();

      // Get DB List
      System.out.println("");
      System.out.println("SHOW DATABASES");
      rs = stmt.executeQuery("SHOW DATABASES");
      while (rs.next()) {
        System.out.println(rs.getString(1));
      }

      // USE Gsat
      System.out.println("");
      System.out.println("USE Gsat");
      stmt.execute("USE Gsat");

      // Get Genes
      System.out.println("");
      System.out.println("Get Genes");
      rs = stmt.executeQuery("SELECT * FROM Gen");
      while (rs.next()) {
        System.out.println("ID: " + rs.getInt("id"));
        System.out.println("NAME: " + rs.getString("name"));
        System.out.println("SEQUENCE: " + rs.getString("sequence"));
        System.out.println("");
      }

      // Add Entry
      System.out.println("");
      System.out.println("Add Gen: ('3', 'added from eclipse', 'aaaaaaaaaaaaaa')");
      stmt.executeUpdate("INSERT INTO Gen VALUES ('3', 'added from eclipse', 'aaaaaaaaaaaaaa')");

      // Get Genes
      System.out.println("");
      System.out.println("Get new Gene");
      rs = stmt.executeQuery("SELECT * FROM Gen WHERE name='added from eclipse'");
      while (rs.next()) {
        System.out.println("ID: " + rs.getInt("id"));
        System.out.println("NAME: " + rs.getString("name"));
        System.out.println("SEQUENCE: " + rs.getString("sequence"));
        System.out.println("");
      }

      // Update new Gene
      System.out.println("");
      System.out.println("Update new Gene");
      stmt.executeUpdate("UPDATE Gen SET sequence='tttttttttt' WHERE name='added from eclipse'");

      // Get Genes
      System.out.println("");
      System.out.println("Print Updated Gene");
      rs = stmt.executeQuery("SELECT * FROM Gen WHERE name='added from eclipse'");
      while (rs.next()) {
        System.out.println("ID: " + rs.getInt("id"));
        System.out.println("NAME: " + rs.getString("name"));
        System.out.println("SEQUENCE: " + rs.getString("sequence"));
        System.out.println("");
      }

      // Delete new Gene
      System.out.println("");
      System.out.println("Delete new Gene");
      stmt.executeUpdate("DELETE FROM Gen WHERE name='added from eclipse'");

      // Get Genes
      System.out.println("");
      System.out.println("Try to find deleted Gene");
      rs = stmt.executeQuery("SELECT * FROM Gen WHERE name='added from eclipse'");
      while (rs.next()) {
        System.out.println("ID: " + rs.getInt("id"));
        System.out.println("NAME: " + rs.getString("name"));
        System.out.println("SEQUENCE: " + rs.getString("sequence"));
        System.out.println("");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }
}
