package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import analysis.AnalysedSequence;
import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;
import io.Config;

/**
 * Tests for reading a config file
 * 
 * @author lovisheindrich
 *
 */
public class ConfigTests {

  /**
   * OS independent path for the configuration file
   */
  private String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "config.txt";
  
  
  @Ignore
  @Test
  public void configMethods() throws IOException{
    System.out.println(Config.exists());
    Config.initConfig();
    System.out.println(Config.exists());
    System.out.println(Config.getPath());
  }
  
  @Ignore
  @Test
  public void testConfigExists() {
    System.out.println(path);
    File config = new File(path);
    System.out.println(config.exists());
  }

  @Ignore
  @Test
  public void testConfigPath() throws IOException {
    File configFile =
        new File(System.getProperty("user.home"), "gsat" + File.separator + "config.txt");
    configFile.getParentFile().mkdirs();
    configFile.createNewFile();
    System.out.println(path);
    assertTrue(new File(path).exists());
  }

  /**
   * Test reading a researcher name from a sample config file and sets the researcher in an analysed
   * sequence (Userstory 017 - Expected behavior)
   * 
   * @throws IOException
   * @throws ConfigReadException
   * @throws ConfigNotFoundException
   */
  @Test
  public void testAnalysedSeqRead()
      throws IOException, ConfigReadException, ConfigNotFoundException {
    Config.researcher = null;
    File path = new File("resources/lh_config/config.txt");
    Config.setPath(path.getAbsolutePath());
    Config.readConfig();
    AnalysedSequence testSeq =
        new AnalysedSequence("atg", Config.researcher, "seq1.abi", new int[] {100, 100, 100});
    assertEquals(testSeq.getResearcher(), "lovis heindrich");
  }

  /**
   * Test reading a researcher name from a sample config file (Userstory 017 - Expected behavior)
   * 
   * @throws IOException
   * @throws ConfigReadException
   * @throws ConfigNotFoundException
   */
  @Test
  public void testConfigRead() throws IOException, ConfigReadException, ConfigNotFoundException {
    // Config.setPath(getClass().getResource("/lh_config").getFile());
    Config.researcher = null;
    File path = new File("resources/lh_config/config.txt");
    Config.setPath(path.getAbsolutePath());
    Config.readConfig();
    assertEquals(Config.researcher, "lovis heindrich");
  }

  /**
   * Tests if trying to read a corrupt config file leads to a ConfigReadException (Userstory 017 -
   * Unusual behavior)
   * 
   * @throws IOException
   * @throws ConfigNotFoundException
   */
  @Test
  public void testCorruptConfig() throws IOException, ConfigNotFoundException {
    File path = new File("resources/corrupt_config/config.txt");
    Config.setPath(path.getAbsolutePath());
    try {
      Config.readConfig();
    } catch (ConfigReadException e) {
      assertEquals(e.getMessage(), "Error while reading researcher from config");
    }
  }

  /**
   * This tests tries to read from a file that doesn't exist (Userstory 017 - Unusual behavior)
   * 
   * @throws IOException
   * @throws ConfigReadException
   */
  @Test
  public void wrongConfigPath() throws IOException, ConfigReadException {
    Config.setPath("/corrupt_path");
    try {
      Config.readConfig();
    } catch (ConfigNotFoundException e) {
      assertEquals(e.getMessage(), "Config at path: /corrupt_path could not be found");
    }
  }
  
  
  /**
   * Test reading multiple researchers from a sample config file (Userstory xxx - Expected behavior)
   * 
   * @throws IOException
   * @throws ConfigReadException
   * @throws ConfigNotFoundException
   */
  @Test
  public void testMultipleUsersConfigRead() throws IOException, ConfigReadException, ConfigNotFoundException {
    Config.researcher = null;
    Config.researchers = null;
    File path = new File("resources/lh_config/config.txt");
    Config.setPath(path.getAbsolutePath());
    Config.readConfig();
    assertEquals(Config.researchers[0], "lovis heindrich");
    assertEquals(Config.researchers[1], "jannis blueml");
    assertEquals(Config.researchers[2], "kevin otto");
    assertEquals(Config.researchers[3], "ben Kohr");
    assertEquals(Config.researcher, "lovis heindrich");
  }
  
  @Test
  public void testConfigWriting() throws ConfigReadException, ConfigNotFoundException, IOException{
    Config.researcher = null;
    Config.researchers = null;
    
    //read config
    File path = new File("resources/lh_config/config.txt");
    Config.setPath(path.getAbsolutePath());
    Config.readConfig();
    
    //change config parameters and write them to the file
    Config.researcher = "testresearcher1";
    Config.researchers[1] = "testresearcher2";
    Config.writeConfig();
    
    //reread configuration file
    Config.researcher = null;
    Config.researchers = null;
    Config.readConfig();
    
    // check for changed parameters
    assertEquals(Config.researcher, "testresearcher1");
    assertEquals(Config.researchers[1], "testresearcher2");
    
    
    //change parameters back
    Config.researcher = "lovis heindrich";
    Config.researchers[1] = "jannis blueml";
    Config.writeConfig();
   
    
    //reread configuration file
    Config.researcher = null;
    Config.researchers = null;
    Config.readConfig();
    
    //check for old values
    assertEquals(Config.researcher, "lovis heindrich");
    assertEquals(Config.researchers[1], "jannis blueml");
  }
}
