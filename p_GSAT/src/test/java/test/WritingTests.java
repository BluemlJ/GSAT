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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.DatabaseEntry;
import io.FileSaver;
import io.MutationType;

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
  private static String path = "writingtests/";

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
    seq1.addMutation("A131E");
    seq1.addMutation("G7K");
    seq1.addMutation("+2H5");
    seq1.setLeftVector("A");
    seq1.setRightVector("B");

    seq2.setReferencedGene(new Gene("ATTTTCG", 1, "FSA", "Karl Mueller"));
    seq2.setComments("No comments");
    seq2.addMutation("reading frame error");

    seq3.setReferencedGene(new Gene("ATTTTCG", 2, "FSA", "Lisa Weber"));

    seq4.setReferencedGene(new Gene("ATTTTCG", 3, "FSA", "Hans Gans"));
    seq4.setComments("Nothing to say");
    seq4.addMutation("AAA7CAA");
    seq4.addMutation("-1H5");

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
    BufferedReader reader =
        new BufferedReader(new FileReader("writingtests/gsat_results_convertAndStoreTest.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults = new String[] {"1; sequence3.ab1; FSA; ATC; " + addingDate
        + "; Kurt Bohne; Nothing to say; null; null; null; 2.0; 0.0; none; false; AAA7CAA, -1H5"};

    for (int i = 0; i < correctResults.length; i++) {
      String[] correctInfo = correctResults[i].split(";");
      String[] testInfo = results.get(i).split(";");
      for (int j = 0; j < correctInfo.length; j++)
        assertEquals(correctInfo[j], testInfo[j]);
    }

    // Size should be three, for there a two initial entries.
    assertTrue(results.size() == 1);

  }

  /**
   * This tests checks the conversion from a AnalyzedSequence into a DatabaseEntry is working
   * correctly without any entry to store (i.e. there is no mutation).
   * 
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalysedSequence)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException
   */
  @Test
  public void testConvertSequenceIntoDBEsNoMutation() throws UndefinedTypeOfMutationException {

    // Use method
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq3);

    // No mutation: Size should be zero
    assertTrue(entries.size() == 0);

  }

  /**
   * This tests checks if the conversion from a AnalyzedSequence into a DatabaseEntry is working
   * correctly.
   * 
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalysedSequence)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException
   */
  @Test
  public void testConvertSequenceIntoDBEsNormal() throws UndefinedTypeOfMutationException {

    // Use method
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq1);

    // Prepare correct result
    DatabaseEntry dbe1 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne",
        "No comments", "A", "B", null, false, "A131E", MutationType.SUBSTITUTION);
    DatabaseEntry dbe2 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne",
        "No comments", "A", "B", null, false, "G7K", MutationType.SUBSTITUTION);
    DatabaseEntry dbe3 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne",
        "No comments", "A", "B", null, false, "+2H5", MutationType.INSERTION);
    DatabaseEntry[] correctResult = new DatabaseEntry[] {dbe1, dbe2, dbe3};

    // compare all attributes in each of the pairings
    for (int i = 0; i < correctResult.length; i++) {
      assertEquals(-1, entries.get(i).getID());
      assertEquals(correctResult[i].getFileName(), entries.get(i).getFileName());
      assertEquals(correctResult[i].getGeneID(), entries.get(i).getGeneID());
      assertEquals(correctResult[i].getSequence(), entries.get(i).getSequence());
      assertEquals(correctResult[i].getResearcher(), entries.get(i).getResearcher());
      assertEquals(correctResult[i].getComments(), entries.get(i).getComments());
      assertEquals(correctResult[i].getLeftVector(), entries.get(i).getLeftVector());
      assertEquals(correctResult[i].getRightVector(), entries.get(i).getRightVector());
      assertEquals(correctResult[i].getPromotor(), entries.get(i).getPromotor());
      assertEquals(correctResult[i].getMutation(), entries.get(i).getMutation());
      assertEquals(correctResult[i].getMutationType(), entries.get(i).getMutationType());
    }

    // Result should have 3 elements.
    assertTrue(entries.size() == 3);
  }

  /**
   * This tests checks the conversion from a AnalyzedSequence into a DatabaseEntry is working
   * correctly with just one entry.
   * 
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalysedSequence)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException
   */
  @Test
  public void testConvertSequenceIntoDBEsNormal2() throws UndefinedTypeOfMutationException {

    // Use method
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq2);

    // Prepare correct result
    DatabaseEntry correctDBE =
        new DatabaseEntry("sequence2.ab1", 1, "ATCTTTG", "2016-11-29", "Klaus Bohne", "No comments",
            null, null, null, false, "reading frame error", MutationType.ERROR);

    // compare all attributes (except for the date)
    assertEquals(-1, entries.get(0).getID());
    assertEquals(correctDBE.getFileName(), entries.get(0).getFileName());
    assertEquals(correctDBE.getGeneID(), entries.get(0).getGeneID());
    assertEquals(correctDBE.getSequence(), entries.get(0).getSequence());
    assertEquals(correctDBE.getResearcher(), entries.get(0).getResearcher());
    assertEquals(correctDBE.getComments(), entries.get(0).getComments());
    assertEquals(correctDBE.getLeftVector(), entries.get(0).getLeftVector());
    assertEquals(correctDBE.getRightVector(), entries.get(0).getRightVector());
    assertEquals(correctDBE.getPromotor(), entries.get(0).getPromotor());
    assertEquals(correctDBE.getMutation(), entries.get(0).getMutation());
    assertEquals(correctDBE.getMutationType(), entries.get(0).getMutationType());

    // Only one entry expected
    assertTrue(entries.size() == 1);

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
   */
  @Test(expected = MissingPathException.class)
  public void testStoreAllLocallyMissingPath() throws MissingPathException, IOException {

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
   */
  @Test
  public void testStoreAllLocallyNoEntries() throws MissingPathException, IOException {

    FileSaver.setLocalPath(path);

    FileSaver.storeResultsLocally("notestdata", seq3);

    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results_notestdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));

    // Check whether the input is correctly empty.
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
   */
  @Test
  public void testStoreAllLocallyNormal() throws MissingPathException, IOException {


    FileSaver.setLocalPath(path);

    FileSaver.storeResultsLocally("testdata", seq1);

    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results_testdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {"1; sequence1.ab1; FSA (bacteria); ATCG; " + addingDate
        + "; Klaus Bohne; No comments; A; B; null; 2.0; 0.0; none; false; A131E, G7K, +2H5"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    // Size should be five, for there a five initial entries
    assertTrue(results.size() == 1);

  }



  @Test
  public void testStoreLocallyAsOneFile1() throws MissingPathException, IOException {
    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);

    // First bunch of data
    FileSaver.storeResultsLocally("separate1", seq2);

    // Second bunch, to be stored in a different file
    FileSaver.storeResultsLocally("separate2", seq3);

    // Test the resulting file
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults = new String[] {
        "1; sequence2.ab1; FSA; ATCTTTG; " + addingDate
            + "; Klaus Bohne; No comments; null; null; null; 2.0; 0.0; none; false; reading frame error",
        "2; sequence3.ab1; FSA; ATCTTGCGTTG; " + addingDate
            + "; Klaus Hafer; ; null; null; null; 2.0; 0.0; none; false; ",};

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
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults = new String[] {
        "1; sequence1.ab1; FSA (bacteria); ATCG; " + addingDate
            + "; Klaus Bohne; No comments; A; B; null; 2.0; 0.0; none; false; A131E, G7K, +2H5",
        "2; sequence2.ab1; FSA; ATCTTTG; " + addingDate
            + "; Klaus Bohne; No comments; null; null; null; 2.0; 0.0; none; false; reading frame error"};

    for (int i = 0; i < correctResults.length; i++) {
      String[] correctInfo = correctResults[i].split(";");
      String[] testInfo = results.get(i).split(";");
      for (int j = 0; j < correctInfo.length; j++)

        assertEquals(correctInfo[j], testInfo[j]);
    }

    assertTrue(results.size() == 2);

  }



  @Test
  public void testStoreLocallyAsOneFileEmpty()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {
    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);

    // Nothing added (no mutations) - does it still work?
    FileSaver.storeResultsLocally("", seq3);

    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // No entries expected
    assertTrue(results.size() == 1);

  }


  @Test
  public void testStoreLocallyAsSeparateFiles1() throws MissingPathException, IOException {
    FileSaver.setSeparateFiles(true);
    FileSaver.setLocalPath(path);

    // First bunch of data
    FileSaver.storeResultsLocally("separate1", seq2);

    // Second bunch, to be stored in a different file
    FileSaver.storeResultsLocally("separate2", seq3);

    // Test the first file
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results_separate1.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    String[] correctResults = new String[] {"1; sequence2.ab1; FSA; ATCTTTG; " + addingDate
        + "; Klaus Bohne; No comments; null; null; null; 2.0; 0.0; none; false; reading frame error"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    assertTrue(results.size() == 1);

    // Test the second file
    reader = new BufferedReader(new FileReader("writingtests/gsat_results_separate2.csv"));

    LinkedList<String> results2 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results2.add(line));
    reader.close();

    correctResults = new String[] {"1; sequence3.ab1; FSA; ATCTTGCGTTG; " + addingDate
        + "; Klaus Hafer; ; null; null; null; 2.0; 0.0; none; false; "};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results2.get(i));
    }

    // Size should be five, for there a five initial entries
    assertTrue(results2.size() == 1);

  }

  
  @Test
  public void testStoreOneFileWithSetFileName() throws MissingPathException, IOException {
	  
	FileSaver.setSeparateFiles(false);  
    FileSaver.setLocalPath(path);
    FileSaver.setDestFileName("testname");
    FileSaver.storeResultsLocally("A73817", seq1);

    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/testname.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {"1; sequence1.ab1; FSA (bacteria); ATCG; " + addingDate
        + "; Klaus Bohne; No comments; A; B; null; 2.0; 0.0; none; false; A131E, G7K, +2H5"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

  }
  
  @Test
  public void testStoreSeparateFilesWithSetFileName() throws MissingPathException, IOException {
	  
	FileSaver.setSeparateFiles(true);
    FileSaver.setLocalPath(path);
    FileSaver.setDestFileName("testname");
    FileSaver.storeResultsLocally("A73817", seq1);
    FileSaver.storeResultsLocally("test2", seq1);
    FileSaver.storeResultsLocally("NEXT", seq1);
    
    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/testname_A73817.csv"));
    LinkedList<String> results1 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results1.add(line));
    reader.close();

    reader = new BufferedReader(new FileReader("writingtests/testname_test2.csv"));
    LinkedList<String> results2 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results2.add(line));
    reader.close();

    reader = new BufferedReader(new FileReader("writingtests/testname_NEXT.csv"));
    LinkedList<String> results3 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results3.add(line));
    reader.close();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String addingDate = df.format(new Date());

    // Check whether the input is correct
    String[] correctResults = new String[] {"1; sequence1.ab1; FSA (bacteria); ATCG; " + addingDate
        + "; Klaus Bohne; No comments; A; B; null; 2.0; 0.0; none; false; A131E, G7K, +2H5"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results1.get(i));
      assertEquals(correctResults[i], results2.get(i));
      assertEquals(correctResults[i], results3.get(i));
    }

  }


}
