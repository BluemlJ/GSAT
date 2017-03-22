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
import io.ProblematicComment;

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

  static AnalysedSequence seq5 =
      new AnalysedSequence("TG", "Bernd", "sequence5.ab1", new int[] {2, 6});

  static AnalysedSequence seq6 =
      new AnalysedSequence("ATC", "Alexander", "sequence6.ab1", new int[] {2, 6});

  static AnalysedSequence seq7 =
      new AnalysedSequence(null, new LinkedList<String>(), "sequence7.ab1", "GT", new Date(), "Jonas", "", false, null, 0, 0, 0);

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

    seq2.setReferencedGene(new Gene("ATTTTCG", 1, "FSA", "Karl Mueller"));
    seq2.setComments("No comments");
    seq2.addMutation("reading frame error");

    seq3.setReferencedGene(new Gene("ATTTTCG", 2, "FSA", "Lisa Weber"));

    seq4.setReferencedGene(new Gene("ATTTTCG", 3, "FSA", "Hans Gans"));
    seq4.setComments("Nothing to say");
    seq4.addMutation("AAA7CAA");
    seq4.addMutation("-1H5 (TCT)");

    seq5.setReferencedGene(g1);
    seq5.addProblematicComment(ProblematicComment.SEQUENCE_TO_SHORT);
    seq6.setReferencedGene(g1);
    seq6.addProblematicComment(ProblematicComment.SEQUENCE_TO_SHORT);
    seq7.addMutation("+1T4");
    seq7.setReferencedGene(new Gene("ATTTTCG", 0, "FSA", "Klaus Bohne", "bacteria", "comment", new Date()));
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
    FileSaver.reset();

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
        "sequence3.ab1; FSA; null; AAA7CAA, -1H5 (TCT); none; false; Nothing to say; Kurt Bohne; "
            + addingDate + "; 0; 0; ATC; none; AAA7CAA, -1H5"};

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

    assertEquals("sequence3.ab1; FSA; null; ; none; false; ; Klaus Hafer; " + addingDate
        + "; 0; 0; ATCTTGCGTTG; none; ; ", results.getFirst());
    assertTrue(results.size() == 1);
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
        "sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCG; none; A131E, G7K, +2H5"};
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
        "sequence2.ab1; FSA; null; reading frame error; none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCTTTG; none; reading frame error",
        "sequence3.ab1; FSA; null; ; none; false; ; Klaus Hafer; " + addingDate
            + "; 0; 0; ATCTTGCGTTG; none; ; "};

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
        "sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCG; none; A131E, G7K, +2H5",
        "sequence2.ab1; FSA; null; reading frame error; none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCTTTG; none; reading frame error"};

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

    String[] correctResults = new String[] {
        "sequence2.ab1; FSA; null; reading frame error; none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCTTTG; none; reading frame error"};

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

    correctResults = new String[] {"sequence3.ab1; FSA; null; ; none; false; ; Klaus Hafer; "
        + addingDate + "; 0; 0; ATCTTGCGTTG; none; ; "};
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
        "sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCG; none; A131E, G7K, +2H5"};

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
        "sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCG; none; A131E, G7K, +2H5"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results1.get(i));
      assertEquals(correctResults[i], results2.get(i));
      assertEquals(correctResults[i], results3.get(i));
    }

  }

  @Test
  public void criticalMessageInFile1() throws MissingPathException, IOException {
    FileSaver.setLocalPath(path);
    FileSaver.setSeparateFiles(false);
    FileSaver.setDestFileName("testdata1");
    FileSaver.storeResultsLocally("testdata1", seq5);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/testdata1.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "sequence5.ab1; FSA; bacteria; ; ; ; The usable part of the sequence is very short (One should probably adjust the parameters). ; ; ; ; ; ; ; "};
    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

  }

  @Test
  public void criticalMessageInFile2() throws MissingPathException, IOException {
    FileSaver.setLocalPath(path);
    FileSaver.setSeparateFiles(false);
    FileSaver.setDestFileName("testdata2");
    FileSaver.storeResultsLocally("testdata2", seq6);
    FileSaver.storeResultsLocally("testdata2", seq1);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/testdata2.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "sequence6.ab1; FSA; bacteria; ; ; ; The usable part of the sequence is very short (One should probably adjust the parameters). ; ; ; ; ; ; ; ",
        "sequence1.ab1; FSA; bacteria; A131E (ACC), G7K (ATC), +2H5 (AAC); none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCG; none; A131E, G7K, +2H5"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

  }

  @Test
  public void criticalMessageInFile3() throws MissingPathException, IOException {

    FileSaver.setSeparateFiles(false);

    FileSaver.setLocalPath(path);
    FileSaver.setDestFileName("testdata3");
    FileSaver.storeResultsLocally("testdata3", seq2);
    FileSaver.storeResultsLocally("testdata3", seq5);
    FileSaver.storeResultsLocally("testdata3", seq6);
    FileSaver.storeResultsLocally("testdata3", seq7);

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("resources/writingtests/testdata3.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "sequence2.ab1; FSA; null; reading frame error; none; false; No comments; Klaus Bohne; "
            + addingDate + "; 0; 0; ATCTTTG; none; reading frame error",
        "sequence5.ab1; FSA; bacteria; ; ; ; The usable part of the sequence is very short (One should probably adjust the parameters). ; ; ; ; ; ; ; ",
        "sequence6.ab1; FSA; bacteria; ; ; ; The usable part of the sequence is very short (One should probably adjust the parameters). ; ; ; ; ; ; ; ",
        "sequence7.ab1; FSA; bacteria; +1T4; 1; false; ; Jonas; " + addingDate
            + "; 0; 0; GT; none; +1T4"};
    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

  }

}
