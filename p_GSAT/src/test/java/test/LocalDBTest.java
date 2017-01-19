package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import com.mysql.cj.jdbc.MysqlDataSource;

// import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * local test to try to connect to a local mysql database and read some properties
 * 
 * @author lovisheindrich
 *
 */
public class LocalDBTest {


  //@Ignore
  @Test
  public void testDBConnection() {
    
    Connection conn = null;
    java.sql.Statement stmt = null;
    ResultSet rs = null;

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
      stmt.executeQuery("USE Gsat");
      
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
