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
import java.util.LinkedList;

import com.mysql.cj.jdbc.MysqlDataSource;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.Primer;
import exceptions.ConfigNotFoundException;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseErrorException;
import exceptions.UnknownConfigFieldException;

/**
 * Class to communicate with the database.
 * 
 * @author Ben Kohr, Lovis Heindrich
 *
 */
public class DatabaseConnection {

	private static Connection conn;

	/**
	 * Mysql connection object.
	 */
	static MysqlDataSource dataSource;

	/**
	 * Mysql user name.
	 */
	static String user = "root";

	/**
	 * Mysql password.
	 */
	static String pass = "rootpassword";

	/**
	 * Mysql port.
	 */
	static int port = 3306;

	/**
	 * Mysql server address.
	 */
	static String server = "127.0.0.1";

	/**
	 * Initializes the database by creating a new datasource object.
	 * 
	 * @author Lovis Heindrich
	 */
	private static void initDatabase() {
		dataSource = new MysqlDataSource();
		dataSource.setUser(user);
		dataSource.setPassword(pass);
		dataSource.setPort(port);
		dataSource.setServerName(server);
	}

	/**
	 * Retrieves all genes from the database and returns them.
	 * 
	 * @return List of genes currently stored in the database
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws DatabaseErrorException
	 *             Error while creating a database connection.
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @author Lovis Heindrich
	 */
	public static LinkedList<Gene> retrieveAllGenes()
			throws DatabaseConnectionException, DatabaseErrorException, SQLException {

		LinkedList<Gene> allGenes = new LinkedList<Gene>();

		establishConnection();

		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, sequence, researcher FROM genes");

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

		conn.close();
		return allGenes;
	}

	/**
	 * Initializes a new connection by getting a new datasource connection.
	 * 
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @author Lovis Heindrich
	 */
	public static void establishConnection() throws DatabaseConnectionException {

		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new DatabaseConnectionException();
		}

	}

	/**
	 * Creates gsat database structure consisting of four tables: genes,
	 * sequences, mutations, researcher. Names must not be changed. If a
	 * database called gsat already exists it will be dropped.
	 * 
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @author Lovis Heindrich
	 */
	public static void createDatabase() throws SQLException {
		try {

			establishConnection();
			Statement stmt = conn.createStatement();

			// check if old gsat database exists and drop it
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM information_schema.tables " + "WHERE table_schema = 'gsat' LIMIT 1");
			if (rs.next()) {
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
					+ "meltingpoint INTEGER, comment VARCHAR(1000), PRIMARY KEY(id))");
			stmt.executeUpdate("CREATE TABLE sequences (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
					+ "name VARCHAR(100) NOT NULL, sequence MEDIUMTEXT NOT NULL, date DATE, "
					+ "researcher INTEGER unsigned, comment VARCHAR(1000), manualcheck CHAR(1), "
					+ "gene INTEGER unsigned, primer MEDIUMTEXT, trimpercent INTEGER unsigned, " + "histag INTEGER, "
					+ "avgquality INTEGER, PRIMARY KEY(id))");
			stmt.executeUpdate("CREATE TABLE mutations (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
					+ "mutation VARCHAR(100) NOT NULL, sequence INTEGER unsigned NOT NULL, PRIMARY KEY(id))");
			stmt.executeUpdate("CREATE TABLE researchers (id INTEGER unsigned NOT NULL AUTO_INCREMENT, "
					+ "name VARCHAR(100) NOT NULL, PRIMARY KEY(id))");
			stmt.close();
			conn.close();
		} catch (DatabaseConnectionException | SQLException e) {
			System.out.println("Database anomaly");
		}

	}

	/**
	 * checks if the given database already has the necessary tables for storing
	 * data database name must be 'gsat' and table names must be 'genes',
	 * 'sequences', 'mutations'.
	 * 
	 * @return True if database exists, false if it does not exist.
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @author Lovis Heindrich
	 */
	public static boolean gsatExists() throws SQLException {
		try {
			establishConnection();
			Statement stmt = conn.createStatement();

			// check if table 'genes' exists
			ResultSet rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
					+ "WHERE table_schema = 'gsat' AND table_name = 'genes' LIMIT 1");
			if (!rs.next()) {
				stmt.close();
				conn.close();
				return false;
			}

			// check if table 'sequences' exists
			rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
					+ "WHERE table_schema = 'gsat' AND table_name = 'sequences' LIMIT 1");
			if (!rs.next()) {
				stmt.close();
				conn.close();
				return false;
			}

			// check if table 'mutations' exists
			rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
					+ "WHERE table_schema = 'gsat' AND table_name = 'mutations' LIMIT 1");
			if (!rs.next()) {
				stmt.close();
				return false;
			}

			// check if table 'researchers' exists
			rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
					+ "WHERE table_schema = 'gsat' AND table_name = 'researchers' LIMIT 1");
			if (!rs.next()) {
				stmt.close();
				conn.close();
				return false;
			}

			// check if table 'primer' exists
			rs = stmt.executeQuery("SELECT * FROM information_schema.tables "
					+ "WHERE table_schema = 'gsat' AND table_name = 'primer' LIMIT 1");
			if (!rs.next()) {
				stmt.close();
				conn.close();
				return false;
			}
			stmt.close();
			conn.close();
			return true;
		} catch (DatabaseConnectionException | SQLException e) {
			System.out.println("Database anomaly.");
		}
		return false;
	}

	/**
	 * Sets the values of the mysql database to be used and initializes the
	 * database.
	 * 
	 * @param user
	 *            The mysql user name.
	 * @param pass
	 *            The mysql password.
	 * @param port
	 *            The mysql server port.
	 * @param server
	 *            The mysql server address.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @author Lovis Heindrich
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

		establishConnection();
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();
		conn.close();

	}

	/**
	 * Pushes all data from a recent analysis: All analysed Sequences, the
	 * reference genes, mutations and the researcher.
	 * 
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @author Lovis Heindrich
	 */
	public static void pushAllData(LinkedList<AnalysedSequence> sequences)
			throws SQLException, DatabaseConnectionException {

		// get a connection
		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		// for each sequence
		for (AnalysedSequence sequence : sequences) {

			// push researcher and get id
			String researcher = sequence.getResearcher();
			int researcherId = pushResearcher(researcher);

			if (sequence.getReferencedGene() == null) {
				System.out.println("Hallo!");
			}

			// push gene with the researcher id and get gene id
			int geneId = pushGene(sequence.getReferencedGene(), researcherId);

			// push sequence and get id
			int sequenceId = pushSequence(sequence, researcherId, geneId);

			// push mutations
			pushMutations(sequence.getMutations(), sequenceId);

		}

		conn.close();
	}

	/**
	 * Pushes all mutations for a single sequence into the database.
	 * 
	 * @param mutations
	 *            List of all mutations for a single sequence.
	 * @param sequenceId
	 *            Database id of the sequence.
	 * 
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @author Lovis Heindrich
	 */
	public static void pushMutations(LinkedList<String> mutations, int sequenceId) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		for (String mutation : mutations) {

			// check if mutation exists
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT id, mutation, sequence FROM mutations WHERE mutation = ? AND sequence = ?");
			pstmt.setString(1, mutation);
			pstmt.setInt(2, sequenceId);
			ResultSet res = pstmt.executeQuery();

			// push
			if (!res.next()) {
				pstmt = conn.prepareStatement("INSERT INTO mutations (mutation, sequence) VALUES (?, ?)");
				pstmt.setString(1, mutation);
				pstmt.setInt(2, sequenceId);
				pstmt.executeUpdate();

			}

			pstmt.close();
		}

	}

	/**
	 * Checks if a sequence already exists and pushes it otherwise. The sequence
	 * contains ids of the correct researcher and gene.
	 * 
	 * @param sequence
	 *            The sequence that will be pushed.
	 * @param researcherId
	 *            The database id of the researcher.
	 * @param geneId
	 *            The database id of the gene.
	 * @return Database id of the new entry or the id of the existing sequence
	 *         entry.
	 * 
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static int pushSequence(AnalysedSequence sequence, int researcherId, int geneId) throws SQLException {
		// select database
		Statement stmt = conn.createStatement();
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
		String primer = sequence.getPrimer();
		int avgquality = sequence.getAvgQuality();

		int trimPercent = (int) sequence.getTrimPercentage();
		int histag = sequence.getHisTagPosition();

		// check if sequence exists
		PreparedStatement pstmt = conn.prepareStatement(
				"SELECT id, name, sequence, gene FROM sequences " + "WHERE name = ? AND sequence = ? AND gene = ?");
		pstmt.setString(1, name);
		pstmt.setString(2, seq);
		pstmt.setInt(3, geneId);

		ResultSet res = pstmt.executeQuery();
		if (res.next()) {
			int result = res.getInt(1);
			pstmt.close();
			return result;
		}
		pstmt.close();
		// push otherwise
		pstmt = conn.prepareStatement("INSERT INTO sequences (name, sequence, date, researcher, comment, manualcheck, "
				+ "gene, primer, trimpercent, histag, avgquality) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		pstmt.setString(1, name);
		pstmt.setString(2, seq);
		pstmt.setDate(3, sqlDate);
		pstmt.setInt(4, researcherId);
		pstmt.setString(5, comment);
		pstmt.setString(6, checked);
		pstmt.setInt(7, geneId);
		pstmt.setString(8, primer);
		pstmt.setInt(9, trimPercent);
		pstmt.setInt(10, histag);
		pstmt.setInt(11, avgquality);
		pstmt.executeUpdate();
		pstmt.close();
		// get index of new sequence
		pstmt = conn.prepareStatement(
				"SELECT id, name, sequence, gene FROM sequences " + "WHERE name = ? AND sequence = ? AND gene = ?");
		pstmt.setString(1, name);
		pstmt.setString(2, seq);
		pstmt.setInt(3, geneId);

		res = pstmt.executeQuery();
		if (res.next()) {
			int result = res.getInt(1);
			pstmt.close();
			return result;
		} else {
			System.out.println("sequence " + sequence.getFileName() + " failed");
			pstmt.close();
			return -1;

		}
	}

	/**
	 * Pushes a single gene and links it to the correct researcher.
	 * 
	 * @param referencedGene
	 *            The gene which will be pushed.
	 * @param researcherId
	 *            Id of the researcher.
	 * @return Id of new gene or id of the already existing gene.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static int pushGene(Gene gene, int researcherId) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		if (gene == null) {
			System.out.println("null");
		}

		System.out.println(gene);
		String name = gene.getName();
		String sequence = gene.getSequence();
		String organism = gene.getOrganism();
		if (organism == null) {
			organism = "none";
		}
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
		PreparedStatement pstmt = conn.prepareStatement(
				"SELECT id, name, sequence, organism FROM genes " + "WHERE name = ? AND sequence = ? AND organism = ?");
		pstmt.setString(1, name);
		pstmt.setString(2, sequence);
		pstmt.setString(3, organism);

		ResultSet res = pstmt.executeQuery();
		if (res.next()) {
			int result = res.getInt(1);
			pstmt.close();
			return result;
		}

		pstmt.close();

		// push otherwise
		pstmt = conn.prepareStatement("INSERT INTO genes (name, sequence, date, researcher, comment, organism) "
				+ "VALUES (?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, name);
		pstmt.setString(2, sequence);
		pstmt.setDate(3, sqlDate);
		pstmt.setInt(4, researcherId);
		pstmt.setString(5, comment);
		pstmt.setString(6, organism);
		pstmt.executeUpdate();
		pstmt.close();
		// get index of new gene
		pstmt = conn.prepareStatement(
				"SELECT id, name, sequence, organism FROM genes " + "WHERE name = ? AND sequence = ? AND organism = ?");
		pstmt.setString(1, name);
		pstmt.setString(2, sequence);
		pstmt.setString(3, organism);
		res = pstmt.executeQuery();
		if (res.next()) {
			int result = res.getInt(1);
			pstmt.close();
			return result;
		} else {
			pstmt.close();
			// should never be called
			return -1;
		}
	}

	/**
	 * Pushes a new researcher if he does not exist yet.
	 * 
	 * @param researcher
	 *            The name of the researcher.
	 * @return The database index of the researcher.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static int pushResearcher(String researcher) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		// check if researcher exists
		PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM researchers WHERE name = ?");
		pstmt.setString(1, researcher);
		ResultSet res = pstmt.executeQuery();
		if (res.next()) {
			System.out.println("researcher: " + researcher + " already exists");
			int result = res.getInt(1);
			pstmt.close();
			return result;
		}
		pstmt.close();
		// push otherwise
		pstmt = conn.prepareStatement("INSERT INTO researchers (name) VALUES (?)");
		pstmt.setString(1, researcher);
		pstmt.executeUpdate();
		pstmt.close();
		// get index of new researcher
		pstmt = conn.prepareStatement("SELECT id, name FROM researchers WHERE name = ?");
		pstmt.setString(1, researcher);
		res = pstmt.executeQuery();
		// pstmt.close();
		if (res.next()) {
			int result = res.getInt(1);
			pstmt.close();
			System.out.println("added researcher " + researcher + " with id " + res.getInt(1));
			return result;
		} else {
			// should never be called
			pstmt.close();
			System.out.println("adding researcher " + researcher + "failed");
			return -1;
		}
	}

	/**
	 * Pushes all genes saved in the genes.txt.
	 * 
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws IOException
	 *             Gene reading error.
	 * @author Lovis Heindrich
	 */
	public static void pushAllGenes() throws SQLException, DatabaseConnectionException, IOException {

		// get Genes
		GeneHandler.readGenes();
		ArrayList<Gene> genes = GeneHandler.getGeneList();

		// get a connection
		establishConnection();

		// select gsat database
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		for (Gene gene : genes) {
			// search for researcher
			int researcherId = pushResearcher(gene.getResearcher());

			// push the gene
			pushGene(gene, researcherId);

		}

		conn.close();
	}

	/**
	 * Pushes all primers stored in the primer .txt file.
	 * 
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @throws IOException
	 *             Error while reading the local primer file.
	 * @author Lovis Heindrich
	 */
	public static void pushAllPrimer() throws DatabaseConnectionException, SQLException, IOException {

		// get primer
		PrimerHandler.readPrimer();
		ArrayList<Primer> primerList = PrimerHandler.getPrimerList();

		// get a connection
		establishConnection();

		// select gsat database
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		for (Primer primer : primerList) {
			// search for researcher
			int researcherId = pushResearcher(primer.getResearcher());
			System.out.println("push researcher: " + primer.getResearcher());
			// push the gene
			pushPrimer(primer, researcherId);
		}
		conn.close();
	}

	/**
	 * Pushes a single primer and links it to the correct researcher.
	 * 
	 * @param primer
	 *            The primer which will be pushed.
	 * @param researcherId
	 *            Database id of the correct researcher.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static void pushPrimer(Primer primer, int researcherId) throws SQLException {
		Statement stmt = conn.createStatement();
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
		String comment = primer.getComment();

		// check if gene exists
		PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, sequence, primerid FROM primer "
				+ "WHERE name = ? AND sequence = ? AND primerid = ?");
		pstmt.setString(1, name);
		pstmt.setString(2, sequence);
		pstmt.setString(3, primerid);

		ResultSet res = pstmt.executeQuery();

		// push if primer does not exist yet
		if (!res.next()) {
			pstmt = conn.prepareStatement(
					"INSERT INTO primer (name, sequence, date, researcher, primerid, meltingpoint, comment) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, name);
			pstmt.setString(2, sequence);
			pstmt.setDate(3, sqlDate);
			pstmt.setInt(4, researcherId);
			pstmt.setString(5, primerid);
			pstmt.setInt(6, meltingPoint);
			pstmt.setString(7, comment);
			pstmt.executeUpdate();
			pstmt.close();
		}
		pstmt.close();
	}

	/**
	 * Pulls all primer data from database and returns it as an arraylist.
	 * 
	 * @return An arraylist with all primers.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static ArrayList<Primer> pullAllPrimer() throws DatabaseConnectionException, SQLException {
		ArrayList<Primer> primerList = new ArrayList<Primer>();

		// get a connection
		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		// primer db layout (name, sequence, date, researcher, primerid,
		// meltingpoint, comment)
		ResultSet rs = stmt.executeQuery("SELECT * FROM primer");

		// iterate over results
		while (rs.next()) {
			String name = rs.getString("name");
			String sequence = rs.getString("sequence");
			java.util.Date date = new Date(rs.getTimestamp("date").getTime());
			int researcherId = rs.getInt("researcher");
			String primerId = rs.getString("primerid");
			int meltingPoint = rs.getInt("meltingpoint");
			String comment = rs.getString("comment");

			String researcher = pullResearcherPerIndex(researcherId);
			Primer primer = new Primer(sequence, researcher, meltingPoint, primerId, name, comment, date);
			primerList.add(primer);
		}
		stmt.close();
		conn.close();
		return primerList;
	}

	/**
	 * Pulls all mutations which belong to a given sequence id and returns them
	 * as a linkedlist.
	 * 
	 * @param sequenceId
	 *            Id of the sequence.
	 * @return LinkedList containing all mutations from a given sequence.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static LinkedList<String> pullMutationsPerSequence(int sequenceId)
			throws DatabaseConnectionException, SQLException {
		LinkedList<String> mutations = new LinkedList<String>();

		// get a connection
		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		// get all mutations from the given sequence
		PreparedStatement pstmt = conn.prepareStatement("SELECT mutation FROM mutations WHERE sequence = ?");
		pstmt.setInt(1, sequenceId);

		ResultSet res = pstmt.executeQuery();

		while (res.next()) {
			String mutation = res.getString("mutation");
			mutations.add(mutation);
		}

		stmt.close();
		pstmt.close();
		conn.close();
		return mutations;
	}

	/**
	 * Pulls all researchers from database and returns them as a list.
	 * 
	 * @return An arraylist of researchers.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static ArrayList<String> pullResearcher() throws DatabaseConnectionException, SQLException {
		ArrayList<String> researchers = new ArrayList<String>();

		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		ResultSet rs = stmt.executeQuery("SELECT * FROM researchers");

		while (rs.next()) {
			String researcher = rs.getString("name");
			researchers.add(researcher);
		}

		stmt.close();
		conn.close();
		return researchers;

	}

	/**
	 * Pulls a researcher identified by his id.
	 * 
	 * @param researcherId
	 *            The id of the researcher.
	 * @return The researcher identified by researcherId or null.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @author Lovis Heindrich
	 */
	private static String pullResearcherPerIndex(int researcherId) throws SQLException, DatabaseConnectionException {
		String researcher = null;
		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		// get the gene from the given sequence
		PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM researchers WHERE id = ?");
		pstmt.setInt(1, researcherId);

		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			researcher = rs.getString("name");
		}
		stmt.close();
		pstmt.close();
		conn.close();
		return researcher;
	}

	/**
	 * Pulls all genes from database and returns them as a list.
	 * 
	 * @return Arraylist of all genes in the database.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while connecting to database.
	 * @author Lovis Heindrich
	 */
	public static ArrayList<Gene> pullAllGenes() throws DatabaseConnectionException, SQLException {
		ArrayList<Gene> genes = new ArrayList<Gene>();

		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		ResultSet rs = stmt.executeQuery("SELECT * FROM genes");

		while (rs.next()) {
			// (name, sequence, date, researcher, comment, organism)
			String name = rs.getString("name");
			String sequence = rs.getString("sequence");
			java.util.Date date = new Date(rs.getTimestamp("date").getTime());
			int researcherId = rs.getInt("researcher");
			String comment = rs.getString("comment");
			String organism = rs.getString("organism");

			// (String sequence, int id, String name, String researcher, String
			// organism, String comment)
			String researcher = pullResearcherPerIndex(researcherId);
			Gene gene = new Gene(sequence, 0, name, researcher, organism, comment, date);
			genes.add(gene);
		}
		stmt.close();
		conn.close();
		return genes;
	}

	/**
	 * Pulls a gene identified by it´s index from database.
	 * 
	 * @param index
	 *            Index of the gene which will be pulled.
	 * @return The gene from the database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @author Lovis Heindrich
	 */
	public static Gene pullGenePerIndex(int index) throws SQLException, DatabaseConnectionException {
		Gene gene = null;
		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		// get the gene from the given sequence
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM genes WHERE id = ?");
		pstmt.setInt(1, index);

		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			// (name, sequence, date, researcher, comment, organism)
			String name = rs.getString("name");
			String sequence = rs.getString("sequence");
			java.util.Date date = new Date(rs.getTimestamp("date").getTime());
			int researcherId = rs.getInt("researcher");
			String comment = rs.getString("comment");
			String organism = rs.getString("organism");

			// (String sequence, int id, String name, String researcher, String
			// organism, String comment)
			String researcher = pullResearcherPerIndex(researcherId);
			gene = new Gene(sequence, 0, name, researcher, organism, comment, date);
		}
		stmt.close();
		pstmt.close();
		conn.close();
		return gene;
	}

	/**
	 * Pulls all sequences from database and returns them as a list.
	 * 
	 * @return Arraylist of all sequences in the database.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static ArrayList<AnalysedSequence> pullAllSequences() throws DatabaseConnectionException, SQLException {
		ArrayList<AnalysedSequence> sequences = new ArrayList<AnalysedSequence>();

		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		// (name, sequence, date, researcher, comment, manualcheck, gene,
		// primer, trimpercent, histag, avgquality)
		ResultSet rs = stmt.executeQuery("SELECT * FROM sequences");

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String sequence = rs.getString("sequence");
			java.util.Date date = new Date(rs.getTimestamp("date").getTime());
			int researcherId = rs.getInt("researcher");
			String comment = rs.getString("comment");
			boolean manuallyChecked;
			if (rs.getString("manualcheck").charAt(0) == 'y') {
				manuallyChecked = true;
			} else {
				manuallyChecked = false;
			}
			int geneIndex = rs.getInt("gene");
			String primer = rs.getString("Primer");
			int trimpercent = rs.getInt("trimpercent");
			int histag = rs.getInt("histag");
			int avgquality = rs.getInt("avgquality");

			String researcher = pullResearcherPerIndex(researcherId);
			Gene gene = pullGenePerIndex(geneIndex);
			LinkedList<String> mutations = pullMutationsPerSequence(id);

			AnalysedSequence seq = new AnalysedSequence(gene, mutations, name, sequence, date, researcher, comment,
					manuallyChecked, primer, trimpercent, histag, avgquality);
			sequences.add(seq);
		}
		stmt.close();
		conn.close();
		return sequences;
	}

	/**
	 * Pulls all sequences from database which have been uploaded by a given
	 * researcher.
	 * 
	 * @param researcherName
	 *            The name of the researcher.
	 * @return All sequences which have been uploaded by the given researcher.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql statements.
	 * @author Lovis Heindrich
	 */
	public static ArrayList<AnalysedSequence> pullAllSequencesPerResearcher(String researcherName)
			throws DatabaseConnectionException, SQLException {
		ArrayList<AnalysedSequence> sequences = new ArrayList<AnalysedSequence>();

		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		int researcherId = getResearcherId(researcherName);

		// get the sequences
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM sequences WHERE researcher = ?");
		pstmt.setInt(1, researcherId);

		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String sequence = rs.getString("sequence");
			java.util.Date date = new Date(rs.getTimestamp("date").getTime());
			int researcherid = rs.getInt("researcher");
			String comment = rs.getString("comment");
			boolean manuallyChecked;
			if (rs.getString("manualcheck").charAt(0) == 'y') {
				manuallyChecked = true;
			} else {
				manuallyChecked = false;
			}
			int geneIndex = rs.getInt("gene");
			String primer = rs.getString("Primer");
			int trimpercent = rs.getInt("trimpercent");
			int histag = rs.getInt("histag");
			int avgquality = rs.getInt("avgquality");

			String researcher = pullResearcherPerIndex(researcherid);
			Gene gene = pullGenePerIndex(geneIndex);
			LinkedList<String> mutations = pullMutationsPerSequence(id);

			AnalysedSequence seq = new AnalysedSequence(gene, mutations, name, sequence, date, researcher, comment,
					manuallyChecked, primer, trimpercent, histag, avgquality);
			sequences.add(seq);

		}
		stmt.close();
		pstmt.close();
		conn.close();
		return sequences;
	}

	/**
	 * Retrieves the index of a given researcher from the database.
	 * 
	 * @param researcher
	 *            The name of the researcher.
	 * @return The database index of the given researcher or -1.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	private static int getResearcherId(String researcher) throws SQLException {
		// get a connection
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		// db query for given researcher
		PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM researchers WHERE name = ?");
		pstmt.setString(1, researcher);
		ResultSet res = pstmt.executeQuery();
		if (res.next()) {
			int result = res.getInt(1);
			// return index
			pstmt.close();
			return result;
		} else {
			// researcher does not exist
			pstmt.close();
			return 0;
		}
	}

	/**
	 * Pulls all primer data from database and adds it to the local primer.txt
	 * 
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @throws NumberFormatException
	 *             Local reading error.
	 * @throws IOException
	 *             Error while writing the primer file.
	 * @author Lovis Heindrich
	 */
	public static void pullAndSavePrimer()
			throws DatabaseConnectionException, SQLException, NumberFormatException, IOException {
		ArrayList<Primer> databasePrimer = pullAllPrimer();
		PrimerHandler.readPrimer();
		for (Primer primer : databasePrimer) {
			PrimerHandler.addPrimer(primer);
		}
		PrimerHandler.writePrimer();
	}

	/**
	 * Pulls all gene data from database and adds it to the local genes.txt.
	 * 
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @throws IOException
	 *             Error while writing local gene file.
	 * @author Lovis Heindrich
	 */
	public static void pullAndSaveGenes() throws DatabaseConnectionException, SQLException, IOException {
		ArrayList<Gene> databaseGenes = pullAllGenes();

		for (Gene gene : databaseGenes) {
			// gene handler already writes and reads config when a gene is added
			GeneHandler.addGene(gene);
		}
	}

	/**
	 * Pulls all researchers from database and adds them to the config.txt.
	 * 
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @throws UnknownConfigFieldException
	 *             Error reading config.txt.
	 * @throws ConfigNotFoundException
	 *             Error reading config.txt.
	 * @throws IOException
	 *             Error writing local file.
	 * @author Lovis Heindrich
	 */
	public static void pullAndSaveResearcher() throws DatabaseConnectionException, SQLException,
			UnknownConfigFieldException, ConfigNotFoundException, IOException {
		ArrayList<String> researcher = pullResearcher();
		ConfigHandler.readConfig();
		for (String res : researcher) {
			if (!ConfigHandler.containsResearcher(res)) {
				ConfigHandler.addResearcher(res);
			}
		}
		ConfigHandler.writeConfig();
	}

	/**
	 * Pulls all Sequences (and linked genes, researchers and mutations) between
	 * two given dates.
	 * 
	 * @param date1
	 *            First date.
	 * @param date2
	 *            Second date.
	 * @return List of the sequences which have been created in the specified
	 *         time intervall.
	 * @throws DatabaseConnectionException
	 *             Error connecting to database.
	 * @throws SQLException
	 *             Error executing sql commands.
	 * @author Lovis Heindrich
	 */
	public static ArrayList<AnalysedSequence> pullAllSequencesPerPeriod(Date date1, Date date2)
			throws DatabaseConnectionException, SQLException {
		ArrayList<AnalysedSequence> sequences = new ArrayList<AnalysedSequence>();

		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		// get the sequences
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM sequences WHERE date BETWEEN ? AND ?");
		pstmt.setDate(1, date1);
		pstmt.setDate(2, date2);

		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String sequence = rs.getString("sequence");
			java.util.Date date = new Date(rs.getTimestamp("date").getTime());
			int researcherid = rs.getInt("researcher");
			String comment = rs.getString("comment");
			boolean manuallyChecked;
			if (rs.getString("manualcheck").charAt(0) == 'y') {
				manuallyChecked = true;
			} else {
				manuallyChecked = false;
			}
			int geneIndex = rs.getInt("gene");
			String primer = rs.getString("Primer");
			int trimpercent = rs.getInt("trimpercent");
			int histag = rs.getInt("histag");
			int avgquality = rs.getInt("avgquality");

			String researcher = pullResearcherPerIndex(researcherid);
			Gene gene = pullGenePerIndex(geneIndex);
			LinkedList<String> mutations = pullMutationsPerSequence(id);

			AnalysedSequence seq = new AnalysedSequence(gene, mutations, name, sequence, date, researcher, comment,
					manuallyChecked, primer, trimpercent, histag, avgquality);
			sequences.add(seq);

		}
		stmt.close();
		pstmt.close();
		conn.close();
		return sequences;
	}

	/**
	 * Pulls sequences specified by up to four optional parameters from
	 * database.
	 * 
	 * @param startDate
	 *            Only sequences analyzed after this date will be pulled. This
	 *            parameter is optional and will be null if not needed.
	 * @param endDate
	 *            Only sequences analyzed before this date will be pulled. This
	 *            parameter is optional and will be null if not needed.
	 * @param researcher
	 *            Only sequences by this researcher will be pulled. This
	 *            parameter is optional and will be null if not needed.
	 * @param geneName
	 *            Only sequences from this gene will be pulled. This parameter
	 *            is optional and will be null if not needed.
	 * @return List of sequences pulled from the database.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @throws DatabaseConnectionException
	 *             Error while connecting to database.
	 * @author Lovis Heindrich
	 */
	public static ArrayList<AnalysedSequence> pullCustomSequences(Date startDate, Date endDate, String researcher,
			String geneName) throws SQLException, DatabaseConnectionException {
		ArrayList<AnalysedSequence> sequences = new ArrayList<AnalysedSequence>();
		establishConnection();

		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");

		boolean startDateActive = true;
		boolean endDateActive = true;
		boolean researcherActive = true;
		boolean geneActive = true;

		// check which query parameters are not set
		if (startDate == null || startDate.getTime() == 0) {
			startDateActive = false;
		}
		if (endDate == null || endDate.getTime() == 0) {
			endDateActive = false;
		}
		if (researcher == null || researcher.equals("")) {
			researcherActive = false;
		}
		if (geneName == null || geneName.equals("")) {
			geneActive = false;
		}

		// construct a query string
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM sequences");

		// append parameters if at least one input is set
		if (startDateActive || endDateActive || researcherActive || geneActive) {
			query.append(" WHERE");
			// start and enddate set
			if (startDateActive && endDateActive) {
				query.append(" date BETWEEN ? AND ?");
			} else if (startDateActive) {
				query.append(" date >= ?");
			} else if (endDateActive) {
				query.append(" date <= ?");
			}

			// AND if needed if a date condition and a later condition is active
			if ((startDateActive || endDateActive) && (researcherActive || geneActive)) {
				query.append(" AND");
			}

			// check if researcher is set
			if (researcherActive) {
				query.append(" researcher = ?");
				// AND is needed if gene is active as well
				if (geneActive) {
					query.append(" AND");
				}
			}

			// check if gene is set
			if (geneActive) {
				query.append(" gene = ?");
			}
		}

		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		// set pstmt parameter
		int index = 1;
		if (startDateActive) {
			pstmt.setDate(index, startDate);
			index++;
		}
		if (endDateActive) {
			pstmt.setDate(index, endDate);
			index++;
		}

		// check if researcher is set
		if (researcherActive) {
			int researcherId = getResearcherId(researcher);
			pstmt.setInt(index, researcherId);
			index++;
		}

		// check if gene is set
		if (geneActive) {
			int geneId = getGeneId(geneName);
			pstmt.setInt(index, geneId);
			index++;
		}

		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String sequence = rs.getString("sequence");
			java.util.Date date = new Date(rs.getTimestamp("date").getTime());
			int researcherid = rs.getInt("researcher");
			String comment = rs.getString("comment");
			boolean manuallyChecked;
			if (rs.getString("manualcheck").charAt(0) == 'y') {
				manuallyChecked = true;
			} else {
				manuallyChecked = false;
			}
			int geneIndex = rs.getInt("gene");
			String primer = rs.getString("Primer");
			int trimpercent = rs.getInt("trimpercent");
			int histag = rs.getInt("histag");
			int avgquality = rs.getInt("avgquality");

			String researcherName = pullResearcherPerIndex(researcherid);
			Gene gene = pullGenePerIndex(geneIndex);
			LinkedList<String> mutations = pullMutationsPerSequence(id);

			AnalysedSequence seq = new AnalysedSequence(gene, mutations, name, sequence, date, researcherName, comment,
					manuallyChecked, primer, trimpercent, histag, avgquality);
			sequences.add(seq);
		}
		stmt.close();
		pstmt.close();
		conn.close();
		return sequences;
	}

	/**
	 * Searches the database for a gene and returns its index.
	 *
	 * @param geneName
	 *            The name of the gene.
	 * @return The database index of the gene.
	 * @throws SQLException
	 *             Error while executing sql commands.
	 * @author Lovis Heindrich
	 */
	private static int getGeneId(String geneName) throws SQLException {
		// get a connection
		Statement stmt = conn.createStatement();
		stmt.execute("USE gsat");
		stmt.close();

		// db query for given gene
		PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM genes WHERE name = ?");
		pstmt.setString(1, geneName);
		ResultSet res = pstmt.executeQuery();
		if (res.next()) {
			int result = res.getInt(1);
			// return index
			pstmt.close();
			return result;
		} else {
			// gene does not exist
			pstmt.close();
			return 0;
		}

	}
}
