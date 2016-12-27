package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.BeforeClass;
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
public class DatabaseTests {

  /**
   * The relative path used for this testing scenario.
   */
  private static String path = "writingtests/";

  /**
   * The first sequence for testing the conversion method (converting a sequence into a
   * DatabaseEntry).
   */
  static AnalysedSequence seq1 =
      new AnalysedSequence("ATCG", "Klaus Bohne", "sequence1.ab1", null, 0);

  /**
   * The second sequence for testing the conversion method.
   */
  private static AnalysedSequence seq2 =
      new AnalysedSequence("ATCTTTG", "Klaus Bohne", "sequence2.ab1", null, 0);

  /**
   * The third test sequence (which will result in no DatabaseEntries).
   */
  private static AnalysedSequence seq3 =
      new AnalysedSequence("ATCTTGCGTTG", "Klaus Hafer", "sequence3.ab1", null, 0);

  /**
   * The fourth test sequence.
   */
  private static AnalysedSequence seq4 =
      new AnalysedSequence("ATC", "Kurt Bohne", "sequence3.ab1", null, 0);

  /**
   * An array of DatabaseEntries used to test the writing of a file.
   */
  private static DatabaseEntry[] entries = new DatabaseEntry[] {
      new DatabaseEntry("a.ab1", 1, "ATCGTCGATCGA", "2016-11-28", "Klaus Bohne", "No comments",
          "CCCCCCCC", "CCCCCCCC", "ATCG", true, "+1H4", MutationType.INSERTION),
      new DatabaseEntry("b.ab1", 4, "TCGATCGATCG", "2016-11-28", "Kurt Bohne", "No comments",
          "TTTTTT", "TTTTTT", "ATCG", false, "-1H4", MutationType.DELETION),
      new DatabaseEntry("c.ab1", 0, "ATCGA", "2016-11-28", "Klaus Bohne", "No comments", "AAAAAAAT",
          "AAAAAAAT", "ATCG", true, "ACC4ACG", MutationType.SILENT),
      new DatabaseEntry("d.ab1", 7, "ATCGT", "2016-11-28", "Kurt Bohne", "No comments", "AAAAAAAT",
          "AAAAAAAT", "ATCG", false, "H4D", MutationType.SUBSTITUTION),
      new DatabaseEntry("e.ab1", 1, "AA", "2016-11-28", "Kurt Bohne", "No comments; nothing to say",
          "GGGGGG", "GGGGGG", "ATCG", true, "reading frame error", MutationType.ERROR)};

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

    seq1.setReferencedGene(new Gene("ATTTTCG", 4, "FSA", "Klaus Bohne"));
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
    FileSaver.resetAll();

    File directory = new File(path);
    File[] files = directory.listFiles();
    if (files != null) {
      for (File f : files) {
        if ("gsat_results.csv".equals(f.getName())) f.delete();
      }
    }
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

    // Setup of the DatabaseConnection
    for (DatabaseEntry entry : entries) {
      FileSaver.addIntoQueue(entry);
    }

    FileSaver.setLocalPath(path);

    FileSaver.storeAllLocally("testdata");

    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/testdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "0; a.ab1; 1; ATCGTCGATCGA; 2016-11-28; Klaus Bohne; No comments; CCCCCCCC; CCCCCCCC; ATCG; true; +1H4; INSERTION",
        "1; b.ab1; 4; TCGATCGATCG; 2016-11-28; Kurt Bohne; No comments; TTTTTT; TTTTTT; ATCG; false; -1H4; DELETION",
        "2; c.ab1; 0; ATCGA; 2016-11-28; Klaus Bohne; No comments; AAAAAAAT; AAAAAAAT; ATCG; true; ACC4ACG; SILENT",
        "3; d.ab1; 7; ATCGT; 2016-11-28; Kurt Bohne; No comments; AAAAAAAT; AAAAAAAT; ATCG; false; H4D; SUBSTITUTION",
        "4; e.ab1; 1; AA; 2016-11-28; Kurt Bohne; No comments, nothing to say; GGGGGG; GGGGGG; ATCG; true; reading frame error; ERROR"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    // Size should be five, for there a five initial entries
    assertTrue(results.size() == 5);

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
    // no entries in the DatabaseConnection stored this time

    FileSaver.setLocalPath(path);

    FileSaver.storeAllLocally("notestdata");

    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/notestdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));

    // Check whether the input is correctly empty.
    assertTrue(results.size() == 0);
    reader.close();
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
    FileSaver.storeAllLocally("problemtest");

    // This line should not be reached!
    fail();

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

    // Converting sequence into DBEs
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq4);

    // Storing them in the DatabaseConnection
    FileSaver.addAllIntoQueue(entries);

    FileSaver.setLocalPath(path);
    FileSaver.storeAllLocally("convertAndStoreTest");

    // Code for reading the file in again
    BufferedReader reader =
        new BufferedReader(new FileReader("writingtests/convertAndStoreTest.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "0; sequence3.ab1; 3; ATC; 2016-11-25; Kurt Bohne; Nothing to say; null; null; null; false; AAA7CAA; SILENT",
        "1; sequence3.ab1; 3; ATC; 2016-11-25; Kurt Bohne; Nothing to say; null; null; null; false; -1H5; DELETION"};

    for (int i = 0; i < correctResults.length; i++) {
      String[] correctInfo = correctResults[i].split(";");
      String[] testInfo = results.get(i).split(";");
      for (int j = 0; j < correctInfo.length; j++)

        // Date cannot be compared
        if (j != 4) assertEquals(correctInfo[i], testInfo[i]);
    }

    // Size should be three, for there a two initial entries.
    assertTrue(results.size() == 2);

  }



  @Test
  public void testStoreLocallyAsSeparateFiles1() throws MissingPathException, IOException {
    FileSaver.setSeparateFiles(true);
    FileSaver.setLocalPath(path);

    // First bunch of data
    FileSaver.addIntoQueue(entries[0]);
    FileSaver.addIntoQueue(entries[1]);
    FileSaver.storeAllLocally("separate1");

    // flush the queue
    FileSaver.flushQueue();

    // Second bunch, to be stored in a different file
    FileSaver.addIntoQueue(entries[2]);
    FileSaver.addIntoQueue(entries[3]);
    FileSaver.storeAllLocally("separate2");

    // Test the first file
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/separate1.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    String[] correctResults = new String[] {
        "0; a.ab1; 1; ATCGTCGATCGA; 2016-11-28; Klaus Bohne; No comments; CCCCCCCC; CCCCCCCC; ATCG; true; +1H4; INSERTION",
        "1; b.ab1; 4; TCGATCGATCG; 2016-11-28; Kurt Bohne; No comments; TTTTTT; TTTTTT; ATCG; false; -1H4; DELETION"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    assertTrue(results.size() == 2);

    // Test the second file
    reader = new BufferedReader(new FileReader("writingtests/separate2.csv"));

    LinkedList<String> results2 = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results2.add(line));
    reader.close();

    correctResults = new String[] {
        "0; c.ab1; 0; ATCGA; 2016-11-28; Klaus Bohne; No comments; AAAAAAAT; AAAAAAAT; ATCG; true; ACC4ACG; SILENT",
        "1; d.ab1; 7; ATCGT; 2016-11-28; Kurt Bohne; No comments; AAAAAAAT; AAAAAAAT; ATCG; false; H4D; SUBSTITUTION"};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results2.get(i));
    }

    // Size should be five, for there a five initial entries
    assertTrue(results2.size() == 2);

  }


  @Test
  public void testStoreLocallyAsOneFile1() throws MissingPathException, IOException {
    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);

    // First bunch of data
    FileSaver.addIntoQueue(entries[0]);
    FileSaver.addIntoQueue(entries[1]);
    FileSaver.storeAllLocally("separate1");

    // flush the queue
    FileSaver.flushQueue();

    // Second bunch, to be stored in a different file
    FileSaver.addIntoQueue(entries[2]);
    FileSaver.addIntoQueue(entries[3]);
    FileSaver.storeAllLocally("separate2");

    // Test the resulting file
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    String[] correctResults = new String[] {
        "0; a.ab1; 1; ATCGTCGATCGA; 2016-11-28; Klaus Bohne; No comments; CCCCCCCC; CCCCCCCC; ATCG; true; +1H4; INSERTION",
        "1; b.ab1; 4; TCGATCGATCG; 2016-11-28; Kurt Bohne; No comments; TTTTTT; TTTTTT; ATCG; false; -1H4; DELETION",
        "2; c.ab1; 0; ATCGA; 2016-11-28; Klaus Bohne; No comments; AAAAAAAT; AAAAAAAT; ATCG; true; ACC4ACG; SILENT",
        "3; d.ab1; 7; ATCGT; 2016-11-28; Kurt Bohne; No comments; AAAAAAAT; AAAAAAAT; ATCG; false; H4D; SUBSTITUTION",};

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    // Size should be five, for there a five initial entries
    assertTrue(results.size() == 4);

  }



  @Test
  public void testStoreLocallyAsOneFile2()
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {
    FileSaver.setSeparateFiles(false);
    FileSaver.setLocalPath(path);

    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq4);

    // Two bunches of data
    FileSaver.addIntoQueue(entries.get(0));
    FileSaver.storeAllLocally("");
    FileSaver.flushQueue();

    FileSaver.addIntoQueue(entries.get(1));
    FileSaver.storeAllLocally("");
    FileSaver.flushQueue();


    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // Check whether the input is correct
    String[] correctResults = new String[] {
        "0; sequence3.ab1; 3; ATC; null; Kurt Bohne; Nothing to say; null; null; null; false; AAA7CAA; SILENT",
        "1; sequence3.ab1; 3; ATC; null; Kurt Bohne; Nothing to say; null; null; null; false; -1H5; DELETION"};

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

    LinkedList<DatabaseEntry> noEntries = new LinkedList<DatabaseEntry>();

    // Nothing added - does it still work?
    FileSaver.addAllIntoQueue(noEntries);
    FileSaver.storeAllLocally("");
    FileSaver.flushQueue();

    FileSaver.addAllIntoQueue(noEntries);
    FileSaver.storeAllLocally("");
    FileSaver.flushQueue();

    FileSaver.addAllIntoQueue(noEntries);
    FileSaver.storeAllLocally("");
    FileSaver.flushQueue();


    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/gsat_results.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    // No entries expected
    assertTrue(results.size() == 0);

  }



}
