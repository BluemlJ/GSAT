package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.FileSaver;

/**
 * This class tests the behavior of the communication with the database and all associated behavior.
 * 
 * @author Ben Kohr
 *
 */
public class WritingTests {

  /**
   * The relative path used for this testing scenario.
   */
  private static String path = "resources" + File.separator + "writingtests" + File.separator;

  /**
   * The second sequence for testing the conversion method.
   */
  private static AnalysedSequence seq2 =
      new AnalysedSequence("ATCTTTG", "Klaus Bohne", "sequence2.ab1", new int[] {1, 3});

  /**
   * The third test sequence (which will result in no DatabaseEntries).
   */
  private static AnalysedSequence seq3 =
      new AnalysedSequence("ATCTTGCGTTG", "Klaus Hafer", "sequence3.ab1", new int[] {1, 3});

  /**
   * The fourth test sequence.
   */
  private static AnalysedSequence seq4 =
      new AnalysedSequence("ATC", "Kurt Bohne", "sequence3.ab1", new int[] {1, 3});

  /**
   * The first sequence for testing the conversion method (converting a sequence into a
   * DatabaseEntry).
   */
  static AnalysedSequence seq1 =
      new AnalysedSequence("ATCG", "Klaus Bohne", "sequence1.ab1", new int[] {1, 3});

  /**
   * This method sets the genes and adds a few mutations to the AnalyzedSequence objects to make
   * them ready to be used in the tests.
   * 
   * @see AnalzedSequence
   * 
   * @author Ben Kohr
   */
  @BeforeClass
  public static void setupSequences() {

    Gene g1 = new Gene("ATTTTCG", 4, "FSA", "Klaus Bohne");
    g1.setOrganism("bacteria");

    seq1.setReferencedGene(g1);
    seq1.setComments("No comments");
    seq1.addMutation("A131E (ACC)");
    seq1.addMutation("G7K (ATC)");
    seq1.addMutation("+2H5 (AAC)");
    seq1.setLeftVector("A");
    seq1.setRightVector("B");

    seq2.setReferencedGene(new Gene("ATTTTCG", 1, "FSA", "Karl Mueller"));
    seq2.setComments("No comments");
    seq2.addMutation("reading frame error");

    seq3.setReferencedGene(new Gene("ATTTTCG", 2, "FSA", "Lisa Weber"));

    seq4.setReferencedGene(new Gene("ATTTTCG", 3, "FSA", "Hans Gans"));
    seq4.setComments("Nothing to say");
    seq4.addMutation("AAA7CAA");
    seq4.addMutation("-1H5 (TCT)");

  }

  @AfterClass
  public static void writeNewFile() throws IOException {
    File newFile = new File(path + "test.txt");
    newFile.createNewFile();
  }

  /**
   * This method resets the state of the DatabaseConnection before a new test starts to get equal
   * test circumstances.
   * 
   * @author Ben Kohr
   * 
   */
  @Before
  public void setup() {
    FileSaver.setSeparateFiles(true);
    FileSaver.setDestFileName("");
    FileSaver.resetAll();

    File directory = new File(path);
    File[] files = directory.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
  }

  /**
   * This test checks whether an AnalyzesSequence is correctly transformed into DatabaseEntries and
   * if these entries is correctly written in a file. This means, this test checks the complete
   * local storage mechanism from the obtained sequences. (User Story 006, typical scenario 2)
   * 
   * @throws MissingPathException
   * @throws IOException
   * 
   * @see FileSaver#storeAllLocally(String)
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalysedSequence)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException
   */
  @Test
  public void testConvertAndStore()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {

    FileSaver.setLocalPath(path);
    FileSaver.storeResultsLocally("convertAndStoreTest", seq4);

    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(
        new FileReader("resources/writingtests/gsat_results_convertAndStoreTest.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults = new String[] {
        "1; sequence3.ab1; FSA; null; AAA7CAA, -1H5 (TCT); Nothing to say; Kurt Bohne; "
            + addingDate + "; 0.0; 0; ATC; null; null; none; none; AAA7CAA, -1H5; false"};

    for (int i = 0; i < correctResults.length; i++) {
      String[] correctInfo = correctResults[i].split(";");
      String[] testInfo = results.get(i).split(";");
      for (int j = 0; j < correctInfo.length; j++)
        assertEquals(correctInfo[j], testInfo[j]);
    }

  }

  /**
   * 
   * This test checks if a MissingPathException is thrown if the writing path is missing.
   * 
   * @throws MissingPathException
   * @throws IOException
   * 
   * @see FileSaver#storeAllLocally(String)
   * @see MissingPathException
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException
   */
  @Test(expected = MissingPathException.class)
  public void testStoreAllLocallyMissingPath()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {

    // Setting the path to null
    FileSaver.setLocalPath(null);

    // Now, path is null. This means, a MissingPathException is to be
    // thrown.
    FileSaver.storeResultsLocally("problemtest", seq1);

    // This line should not be reached!
    fail();

  }

  /**
   * 
   * This test checks if there is no problem with the writing even if there are no database entries
   * given. (User Story 006, unusual scenario)
   * 
   * @throws MissingPathException
   * @throws IOException
   * 
   * @see FileSaver#storeAllLocally(String)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException
   */
  @Test
  public void testStoreAllLocallyNoEntries()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {

    FileSaver.setLocalPath(path);

    FileSaver.storeResultsLocally("notestdata", seq3);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/gsat_results_notestdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    assertEquals("1; sequence3.ab1; FSA; null; ; ; Klaus Hafer; " + addingDate
        + "; 0.0; 0; ATCTTGCGTTG; null; null; none; none; ; false", results.getFirst());
    reader.close();
  }

  /**
   * This test checks if the writing of local files is done correctly. (User Story 006, typical
   * scenario 1)
   * 
   * @throws MissingPathException
   * @throws IOException
   * 
   * @see FileSaver#storeAllLocally(String)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException
   */
  @Test
  public void testStoreAllLocallyNormal()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {

    FileSaver.setLocalPath(path);

    FileSaver.storeResultsLocally("testdata", seq1);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/gsat_results_testdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "1; sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); No comments; Klaus Bohne; "
            + addingDate + "; 0.0; 0; ATCG; A; B; none; none; A131E, G7K, +2H5; false"};
    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

  }

  @Test
  public void testStoreLocallyAsOneFile1()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {
    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);

    // First bunch of data
    FileSaver.storeResultsLocally("separate1", seq2);

    // Second bunch, to be stored in a different file
    FileSaver.storeResultsLocally("separate2", seq3);

    // Test the resulting file
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults = new String[] {
        "1; sequence2.ab1; FSA; null; reading frame error; No comments; Klaus Bohne; " + addingDate
            + "; 0.0; 0; ATCTTTG; null; null; none; none; reading frame error; false",
        "2; sequence3.ab1; FSA; null; ; ; Klaus Hafer; " + addingDate
            + "; 0.0; 0; ATCTTGCGTTG; null; null; none; none; ; false"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    // Size should be five, for there a five initial entries
    assertTrue(results.size() == 2);

  }

  @Test
  public void testStoreLocallyAsOneFile2()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {
    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);

    // Two bunches of data
    FileSaver.storeResultsLocally("a", seq1);
    FileSaver.storeResultsLocally("b", seq2);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults = new String[] {
        "1; sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); No comments; Klaus Bohne; "
            + addingDate + "; 0.0; 0; ATCG; A; B; none; none; A131E, G7K, +2H5; false",
        "2; sequence2.ab1; FSA; null; reading frame error; No comments; Klaus Bohne; " + addingDate
            + "; 0.0; 0; ATCTTTG; null; null; none; none; reading frame error; false"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    assertTrue(results.size() == 2);

  }

  @Test
  public void testStoreLocallyAsOneFileEmpty()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {
    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);

    AnalysedSequence seq = new AnalysedSequence();
    seq.setReferencedGene(new Gene("A", 0, "A", null));
    
    // Nothing added (no mutations) - does it still work?
    FileSaver.storeResultsLocally("", seq);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // No entries expected
    assertTrue(results.size() == 1);

  }

  @Test
  public void testStoreLocallyAsSeparateFiles1()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {
    FileSaver.setSeparateFiles(true);
    FileSaver.setLocalPath(path);

    // First bunch of data
    FileSaver.storeResultsLocally("separate1", seq2);

    // Second bunch, to be stored in a different file
    FileSaver.storeResultsLocally("separate2", seq3);

    // Test the first file
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/gsat_results_separate1.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults =
        new String[] {"1; sequence2.ab1; FSA; null; reading frame error; No comments; Klaus Bohne; "
            + addingDate + "; 0.0; 0; ATCTTTG; null; null; none; none; reading frame error; false"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    assertTrue(results.size() == 1);

    // Test the second file
    reader =
        new BufferedReader(new FileReader("resources/writingtests/gsat_results_separate2.csv"));

    LinkedList<String> results2 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results2.add(line));
    reader.close();

    correctResults = new String[] {"1; sequence3.ab1; FSA; null; ; ; Klaus Hafer; " + addingDate
        + "; 0.0; 0; ATCTTGCGTTG; null; null; none; none; ; false"};
    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results2.get(i));
    }

    // Size should be five, for there a five initial entries
    assertTrue(results2.size() == 1);

  }

  @Test
  public void testStoreOneFileWithSetFileName()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {

    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);
    FileSaver.setDestFileName("testname");
    FileSaver.storeResultsLocally("A73817", seq1);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/testname.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "1; sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); No comments; Klaus Bohne; "
            + addingDate + "; 0.0; 0; ATCG; A; B; none; none; A131E, G7K, +2H5; false"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

  }

  @Test
  public void testStoreSeparateFilesWithSetFileName()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {

    FileSaver.setSeparateFiles(true);
    FileSaver.setLocalPath(path);
    FileSaver.setDestFileName("testname");
    FileSaver.storeResultsLocally("A73817", seq1);
    FileSaver.storeResultsLocally("test2", seq1);
    FileSaver.storeResultsLocally("NEXT", seq1);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/testname_A73817.csv"));
    LinkedList<String> results1 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results1.add(line));
    reader.close();

    reader = new BufferedReader(new FileReader("resources/writingtests/testname_test2.csv"));
    LinkedList<String> results2 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results2.add(line));
    reader.close();

    reader = new BufferedReader(new FileReader("resources/writingtests/testname_NEXT.csv"));
    LinkedList<String> results3 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results3.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "1; sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); No comments; Klaus Bohne; "
            + addingDate + "; 0.0; 0; ATCG; A; B; none; none; A131E, G7K, +2H5; false"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results1.get(i));
      assertEquals(correctResults[i], results2.get(i));
      assertEquals(correctResults[i], results3.get(i));
    }

  }

}
