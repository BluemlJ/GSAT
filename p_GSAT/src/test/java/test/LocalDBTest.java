package test;

import org.junit.Test;

import com.mysql.cj.jdbc.MysqlDataSource;

public class LocalDBTest {

  @Test
  public void testDBConnection() {
    /*
     * OPTION 1 Context context = new InitialContext(); DataSource dataSource = (DataSource)
     * context.lookup("java:comp/env/jdbc/myDB");
     * 
     * OPTION 2
     */
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUser("root@localhost");
    dataSource.setPassword("rootpassword");
    dataSource.setServerName("localhost");

  }
}
