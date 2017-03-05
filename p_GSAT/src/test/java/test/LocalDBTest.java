package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;

import com.mysql.cj.jdbc.MysqlDataSource;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.Primer;
import exceptions.DatabaseConnectionException;
import io.DatabaseConnection;
import io.PrimerHandler;

// import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * local test to try to connect to a local mysql database and read some
 * properties
 * 
 * @author lovisheindrich
 *
 */
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

	@Ignore
	@Test
	public void testPullAllPrimer()
			throws DatabaseConnectionException, SQLException, NumberFormatException, IOException {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		Connection conn = DatabaseConnection.establishConnection();
		DatabaseConnection.createDatabase();

		Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1", "comment1");
		Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2", "comment2");
		DatabaseConnection.pushPrimer(conn, p1, 0);
		DatabaseConnection.pushPrimer(conn, p2, 0);

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
		Connection conn = DatabaseConnection.establishConnection();
		DatabaseConnection.createDatabase();
		Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1", "comment1");
		Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2", "comment2");
		DatabaseConnection.pushPrimer(conn, p1, 0);
		DatabaseConnection.pushPrimer(conn, p2, 0);
		DatabaseConnection.pushPrimer(conn, p1, 0);
		DatabaseConnection.pushPrimer(conn, p2, 0);
	}

	@Ignore
	@Test
	public void testPushAllGenes() throws DatabaseConnectionException, SQLException, IOException {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		Connection conn = DatabaseConnection.establishConnection();
		DatabaseConnection.pushAllGenes();
	}

	@Ignore
	@Test
	public void testDatabasePushPipeline() throws SQLException, DatabaseConnectionException {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		conn = DatabaseConnection.establishConnection();
		DatabaseConnection.createDatabase();
		LinkedList<String> mutations1 = new LinkedList<String>(Arrays.asList("t5a", "t6a"));
		LinkedList<String> mutations2 = new LinkedList<String>(Arrays.asList("t1a", "t2a"));
		AnalysedSequence sequence1 = new AnalysedSequence("aataataat", "Lovis Heindrich", "Sequence1", null);
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
		Connection conn = DatabaseConnection.establishConnection();

		LinkedList<String> mutations1 = new LinkedList<String>(Arrays.asList("t5a", "t6a"));
		LinkedList<String> mutations2 = new LinkedList<String>(Arrays.asList("t1a", "t2a"));
		DatabaseConnection.pushMutations(conn, mutations1, 0);
		DatabaseConnection.pushMutations(conn, mutations1, 0);
		DatabaseConnection.pushMutations(conn, mutations2, 0);
		DatabaseConnection.pushMutations(conn, mutations1, 1);
	}

	@Ignore
	@Test
	public void testPushSequence() throws DatabaseConnectionException, SQLException {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		Connection conn = DatabaseConnection.establishConnection();
		AnalysedSequence sequence1 = new AnalysedSequence("aataat", "Lovis", "Sequence1", null);
		AnalysedSequence sequence2 = new AnalysedSequence("ttatta", "Kevin", "Sequence2", null);
		int seq1 = DatabaseConnection.pushSequence(conn, sequence1, 0, 0);
		int seq2 = DatabaseConnection.pushSequence(conn, sequence2, 0, 0);
		int seq3 = DatabaseConnection.pushSequence(conn, sequence1, 0, 0);
		int seq4 = DatabaseConnection.pushSequence(conn, sequence2, 0, 0);

		assertEquals(seq1, seq3);
		assertEquals(seq2, seq4);
	}

	@Ignore
	@Test
	public void testPushResearcher() throws SQLException, DatabaseConnectionException {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		Connection conn = DatabaseConnection.establishConnection();
		int lh1 = DatabaseConnection.pushResearcher(conn, "Lovis Heindrich");
		int ko1 = DatabaseConnection.pushResearcher(conn, "Kevin Otto");
		int lh2 = DatabaseConnection.pushResearcher(conn, "Lovis Heindrich");
		int ko2 = DatabaseConnection.pushResearcher(conn, "Kevin Otto");
		assertEquals(lh1, lh2);
		assertEquals(ko1, ko2);

	}

	@Ignore
	@Test
	public void testPushGene() throws DatabaseConnectionException, SQLException {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		Connection conn = DatabaseConnection.establishConnection();
		Gene gene1 = new Gene("aaatttggg", 0, "fsa1", "Lovis Heindrich", "fsa", "comment1");
		Gene gene2 = new Gene("gggtttaaa", 0, "fsa2", "Lovis Heindrich", "fsa", "comment2");
		int g11 = DatabaseConnection.pushGene(conn, gene1, 0);
		int g21 = DatabaseConnection.pushGene(conn, gene2, 0);
		int g12 = DatabaseConnection.pushGene(conn, gene1, 0);
		int g22 = DatabaseConnection.pushGene(conn, gene2, 0);
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
