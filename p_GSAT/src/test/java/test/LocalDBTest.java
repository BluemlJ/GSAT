package test;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import org.junit.Test;

import com.mysql.cj.jdbc.MysqlDataSource;

import exceptions.DatabaseConnectionException;
import io.DatabaseConnection;

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
	
	@Test
	public void testDatabaseConnectionConnect(){
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		assertTrue(DatabaseConnection.gsatExists());
	}
	
	
	public void testPushResearcher() throws SQLException, DatabaseConnectionException {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		Connection conn = DatabaseConnection.establishConnection();
		Statement stmt = conn.createStatement();
		//DatabaseConnection.pushReasearcher(stmt, "Lovis Heindrich");
		
		
	}

	public void testPushGene() {

	}

	public void testOnlineConUsingDBConnection() {
		DatabaseConnection.setDatabaseConnection(userOnline, passOnline, portOnline, serverOnline);
		// DatabaseConnection.createDatabase();
		System.out.println(DatabaseConnection.gsatExists());
	}

	/**
	 * Working connection setup to online database
	 */
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

	public void DBConnectionTest() {
		DatabaseConnection.setDatabaseConnection(user, pass, port, server);
		System.out.println(DatabaseConnection.gsatExists());
		DatabaseConnection.createDatabase();
		System.out.println(DatabaseConnection.gsatExists());

	}

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
