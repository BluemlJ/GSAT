package test;

import org.junit.Test;

public class LocalDBTest {

  @Test
  public void testDBConnection() {
    /*
     * OPTION 1 Context context = new InitialContext(); DataSource dataSource = (DataSource)
     * context.lookup("java:comp/env/jdbc/myDB");
     * 
     * OPTION 2 MysqlDataSource dataSource = new MysqlDataSource(); dataSource.setUser("scott");
     * dataSource.setPassword("tiger"); dataSource.setServerName("myDBHost.example.org");
     */
  }
}
