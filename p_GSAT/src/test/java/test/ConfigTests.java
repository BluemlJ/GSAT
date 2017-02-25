package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import analysis.AnalysedSequence;
import exceptions.ConfigNotFoundException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;

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

  @Test
  public void configMethods() throws IOException {
    ConfigHandler.initConfig();
    assertTrue(ConfigHandler.exists());
  }

  /**
   * Test for accessing the home directory
   */
  @Test
  public void testConfigExists() {
    File config = new File(path);
    assertTrue(config.exists());
  }

  /**
   * Test for creating a configuration file in the home directory
   * 
   * @throws IOException
   */

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
   * @throws UnknownConfigFieldException
   * @throws ConfigNotFoundException
   */
  @Test
  public void testAnalysedSeqRead()
      throws IOException, UnknownConfigFieldException, ConfigNotFoundException {
    ConfigHandler.setResearcher(null);
    File path = new File("resources/lh_config/config.txt");
    ConfigHandler.setPath(path.getAbsolutePath());
    ConfigHandler.readConfig();
    AnalysedSequence testSeq = new AnalysedSequence("atg", ConfigHandler.getResearcher(),
        "seq1.abi", new int[] {100, 100, 100});
    assertEquals(testSeq.getResearcher(), "lovis heindrich");
  }

  /**
   * Test reading a researcher name from a sample config file (Userstory 017 - Expected behavior)
   * 
   * @throws IOException
   * @throws UnknownConfigFieldException
   * @throws ConfigNotFoundException
   */
  @Test
  public void testConfigRead() throws IOException, UnknownConfigFieldException, ConfigNotFoundException {
    // Config.setPath(getClass().getResource("/lh_config").getFile());
    ConfigHandler.setResearcher(null);
    File path = new File("resources/lh_config/config.txt");
    ConfigHandler.setPath(path.getAbsolutePath());
    ConfigHandler.readConfig();
    assertEquals(ConfigHandler.getResearcher(), "lovis heindrich");
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
    ConfigHandler.setPath(path.getAbsolutePath());
    try {
      ConfigHandler.readConfig();
    } catch (UnknownConfigFieldException e) {
      assertEquals(e.getMessage(), "Error while reading field 'resr' from the configuration file.");
    }
  }

  /**
   * This tests tries to read from a file that doesn't exist (Userstory 017 - Unusual behavior)
   * 
   * @throws IOException
   * @throws UnknownConfigFieldException
   */
  @Test
  public void wrongConfigPath() throws IOException, UnknownConfigFieldException {

    File path = new File("resources/");
    try {
      ConfigHandler.setPath(path.getAbsolutePath());
      ConfigHandler.readConfig();
    } catch (ConfigNotFoundException e) {
      assertEquals(e.getMessage(),
          "Configuration file could not be found at path " + path.getAbsolutePath() + ".");
    }
  }

  /**
   * Test reading multiple researchers from a sample config file (Userstory xxx - Expected behavior)
   * 
   * @throws IOException
   * @throws UnknownConfigFieldException
   * @throws ConfigNotFoundException
   */
  @Test
  public void testMultipleUsersConfigRead()
      throws IOException, UnknownConfigFieldException, ConfigNotFoundException {
    ConfigHandler.setResearcher(null);
    ConfigHandler.setResearcherList(null);
    File path = new File("resources/lh_config/config.txt");
    ConfigHandler.setPath(path.getAbsolutePath());
    ConfigHandler.readConfig();
    assertEquals(ConfigHandler.getResearcherList()[0], "lovis heindrich");
    assertEquals(ConfigHandler.getResearcherList()[1], "jannis blueml");
    assertEquals(ConfigHandler.getResearcherList()[2], "kevin otto");
    assertEquals(ConfigHandler.getResearcherList()[3], "ben Kohr");
    assertEquals(ConfigHandler.getResearcher(), "lovis heindrich");
  }

  @Test
  public void testConfigWriting() throws UnknownConfigFieldException, ConfigNotFoundException, IOException {
    ConfigHandler.setResearcher(null);
    ConfigHandler.setResearcherList(null);

    // read config
    File path = new File("resources/lh_config/config.txt");
    ConfigHandler.setPath(path.getAbsolutePath());
    ConfigHandler.readConfig();

    // change config parameters and write them to the file
    ConfigHandler.setResearcher("testresearcher1");
    ConfigHandler.setResearcherInResearcherList("testresearcher2", 1);
    ConfigHandler.writeConfig();

    // reread configuration file
    ConfigHandler.setResearcher(null);
    ConfigHandler.setResearcherList(null);
    ConfigHandler.readConfig();

    // check for changed parameters
    assertEquals(ConfigHandler.getResearcher(), "testresearcher1");
    assertEquals(ConfigHandler.getResearcherList()[1], "testresearcher2");

    // change parameters back
    ConfigHandler.setResearcher("lovis heindrich");
    ConfigHandler.setResearcherInResearcherList("jannis blueml", 1);
    ConfigHandler.writeConfig();

    // reread configuration file
    ConfigHandler.setResearcher(null);
    ConfigHandler.setResearcherList(null);
    ConfigHandler.readConfig();

    // check for old values
    assertEquals(ConfigHandler.getResearcher(), "lovis heindrich");
    assertEquals(ConfigHandler.getResearcherList()[1], "jannis blueml");
  }

  @Test
  public void testQualityParameter()
      throws UnknownConfigFieldException, ConfigNotFoundException, IOException {
    // read config
    File path = new File("resources/lh_config/config.txt");
    ConfigHandler.setPath(path.getAbsolutePath());
    ConfigHandler.readConfig();

    int avgApproximationStartOld = ConfigHandler.getAvgApproximationStart();

    ConfigHandler.setAvgApproximationStart(50);
    ConfigHandler.writeConfig();
    ConfigHandler.readConfig();
    assertEquals(50, ConfigHandler.getAvgApproximationStart());

    ConfigHandler.setAvgApproximationStart(avgApproximationStartOld);
    ConfigHandler.writeConfig();
    ConfigHandler.readConfig();
    assertEquals(avgApproximationStartOld, ConfigHandler.getAvgApproximationStart());
  }

  /**
   * This test checks if it is possible to add a researcher to the researchers array
   */
  @Test
  public void testAddResearcher() {
    String[] res = {"res1", "res2"};
    ConfigHandler.setResearcherList(res);
    ConfigHandler.addResearcher("res3");
    res = ConfigHandler.getResearcherList();
    assertEquals(res[0], "res1");
    assertEquals(res[1], "res2");
    assertEquals(res[2], "res3");
  }

  /**
   * This test checks if it is possible to delete a researcher from the researchers array
   */
  @Test
  public void testDeleteResearcher() {
    String[] res = {"res1", "res2", "res3", "res4"};
    ConfigHandler.setResearcherList(res);
    ConfigHandler.deleteResearcher("res2");
    res = ConfigHandler.getResearcherList();
    assertEquals(res[0], "res1");
    assertEquals(res[1], "res3");
    assertEquals(res[2], "res4");
  }

}
