package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import core.ConsoleVersion;
import core.Main;

/**
 * This class tests the writing of reports.
 *
 * @author Ben Kohr
 */
public class ReportTests {


  private static LinkedList<File> files1 = new LinkedList<File>();
  private static LinkedList<File> files2 = new LinkedList<File>();
  private static LinkedList<File> oddFiles1 = new LinkedList<File>();
  private static LinkedList<File> oddFiles2 = new LinkedList<File>();

  @BeforeClass
  public static void setupSequences() {

    files1.add(new File("seq1.ab1"));
    files1.add(new File("seq2.abi"));
    files1.add(new File("seq3.ab1"));
    files1.add(new File("seq4.AB1"));

    oddFiles1.add(new File("text.txt"));
    oddFiles1.add(new File("story.pdf"));
    oddFiles1.add(new File("story2.pdf"));

    oddFiles2.add(new File("story.odt"));

    files2.add(new File("anotherSeq.ab1"));
    files2.add(new File("anotherSeq2.abi"));

  }

  /**
   * This test checks that no error occures when there are neither valid nor invalid files passed to
   * the method. (User Story 019, unusual behavior)
   * 
   * @throws IOException
   * 
   * @see Main#reportOnInput(String, LinkedList, LinkedList, String)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testReadingReportNoEntries() throws IOException {
    ConsoleVersion.reportOnInput("reporttests", new LinkedList<File>(), new LinkedList<File>(),
        "error while reading the configuration file");

    BufferedReader reader = new BufferedReader(new FileReader("reporttests/report.txt"));

    StringBuilder builder = new StringBuilder();
    reader.lines().forEach(line -> {
      builder.append(line);
      builder.append(System.lineSeparator());
    });
    reader.close();

    String expectedString = "No valid AB1/ABI found." + System.lineSeparator()
        + System.lineSeparator() + "No invalid files detected." + System.lineSeparator()
        + System.lineSeparator() + "Config file: error while reading the configuration file"
        + System.lineSeparator() + System.lineSeparator();

    assertEquals(expectedString, builder.toString());
  }


  /**
   * This test checks if a report on the reading process is correctly created and stored. (User
   * Story 019, typical behavior 1)
   * 
   * @throws IOException
   * 
   * @see Main#reportOnInput(String, LinkedList, LinkedList, String)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testReadingReportNormal() throws IOException {
    ConsoleVersion.reportOnInput("reporttests", files1, oddFiles1, "No configuration file found");

    BufferedReader reader = new BufferedReader(new FileReader("reporttests/report.txt"));

    StringBuilder builder = new StringBuilder();
    reader.lines().forEach(line -> {
      builder.append(line);
      builder.append(System.lineSeparator());
    });
    reader.close();

    String expectedString = "The following files have been detected as valid AB1/ABI files:"
        + System.lineSeparator() + ">>> seq1.ab1" + System.lineSeparator() + ">>> seq2.abi"
        + System.lineSeparator() + ">>> seq3.ab1" + System.lineSeparator() + ">>> seq4.AB1"
        + System.lineSeparator() + System.lineSeparator()

        + "Number of valid files: 4" + System.lineSeparator() + System.lineSeparator()

        + "The following files were invalid:" + System.lineSeparator() + ">>> text.txt"
        + System.lineSeparator() + ">>> story.pdf" + System.lineSeparator() + ">>> story2.pdf"
        + System.lineSeparator() + System.lineSeparator()

        + "Number of invalid files: 3" + System.lineSeparator() + System.lineSeparator()

        + "Config file: No configuration file found" + System.lineSeparator()
        + System.lineSeparator();

    assertEquals(expectedString, builder.toString());
  }



  /**
   * This test checks if a report on the reading process is correctly created and stored. This time,
   * there are less files passed to the reporting method. (User Story 019, typical behavior 2)
   * 
   * @throws IOException
   * 
   * @see Main#reportOnInput(String, LinkedList, LinkedList, String)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testReadingReportNormal2() throws IOException {
    ConsoleVersion.reportOnInput("reporttests", files2, oddFiles2, "found");

    BufferedReader reader = new BufferedReader(new FileReader("reporttests/report.txt"));

    StringBuilder builder = new StringBuilder();
    reader.lines().forEach(line -> {
      builder.append(line);
      builder.append(System.lineSeparator());
    });
    reader.close();

    String expectedString = "The following files have been detected as valid AB1/ABI files:"
        + System.lineSeparator() + ">>> anotherSeq.ab1" + System.lineSeparator()
        + ">>> anotherSeq2.abi" + System.lineSeparator() + System.lineSeparator()

        + "Number of valid files: 2" + System.lineSeparator() + System.lineSeparator()

        + "The following files were invalid:" + System.lineSeparator() + ">>> story.odt"
        + System.lineSeparator() + System.lineSeparator()

        + "Number of invalid files: 1" + System.lineSeparator() + System.lineSeparator()

        + "Config file: found" + System.lineSeparator() + System.lineSeparator();

    assertEquals(expectedString, builder.toString());
  }


}
