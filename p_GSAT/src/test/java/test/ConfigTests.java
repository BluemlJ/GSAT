package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

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
    File path = new File("resources/lh_config");
    Config.setPath(path.getAbsolutePath());
    Config.readConfig();
    AnalysedSequence testSeq =
        new AnalysedSequence("atg", Config.researcher, "seq1.abi", new int[] {100, 100, 100}, 100);
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
    //Config.setPath(getClass().getResource("/lh_config").getFile());
    File path = new File("resources/lh_config");
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
    File path = new File("resources/corrupt_config");
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
}
