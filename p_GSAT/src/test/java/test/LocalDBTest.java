package test;

import org.junit.Ignore;
import org.junit.Test;

// import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * local test to try to connect to a local mysql database and read some properties
 * 
 * @author lovisheindrich
 *
 */
public class LocalDBTest {


  @Ignore
  @Test
  public void testDBConnection() {
    /*
     * OPTION 1 Context context = new InitialContext(); DataSource dataSource = (DataSource)
     * context.lookup("java:comp/env/jdbc/myDB");
     * 
     * OPTION 2
     */
    // Connection conn = null;
    // java.sql.Statement stmt = null;
    // ResultSet rs = null;

    // MysqlDataSource dataSource = new MysqlDataSource();
    // dataSource.setUser("root");
    // dataSource.setPassword("rootpassword");
    // dataSource.setPort(3306);
    // dataSource.setServerName("127.0.0.1");

    // try {
    // conn = dataSource.getConnection();
    // stmt = conn.createStatement();
    // rs = stmt.executeQuery("SHOW DATABASES");
    // while(rs.next()){
    // System.out.println(rs.getString(1));
    // }
    // System.out.println(dataSource.getDatabaseName());
    // } catch (SQLException e) {
    // TODO Auto-generated catch block
    // e.printStackTrace();
    // }


    /*
     * Class.forName("com.mysql.jdbc.Driver"); Connection con = DriverManager.getConnection(
     * "jdbc:mysql://localhost/dbName", "root", "secret");
     */

  }
}
