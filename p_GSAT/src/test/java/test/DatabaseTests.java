package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import analysis.AnalyzedSequence;
import analysis.Gene;
import exceptions.MissingPathException;
import io.DatabaseConnection;
import io.DatabaseEntry;

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
  static AnalyzedSequence seq1 =
      new AnalyzedSequence("ATCG", "2016-11-28", "Klaus Bohne", "sequence1.ab1", "No comments", null, null, null, null, null);


  /**
   * The second sequence for testing the conversion method.
   */
  private static AnalyzedSequence seq2 =
      new AnalyzedSequence("ATCTTTG", "2016-11-29", "Klaus Bohne", "sequence2.ab1", "No comments", null, null, null, null, null);


  /**
   * The third test sequence (which will result in no DatabaseEntries).
   */
  private static AnalyzedSequence seq3 =
      new AnalyzedSequence("ATCTTGCGTTG", "2016-11-27", "Klaus Hafer", "sequence3.ab1", "No comments", null, null, null, null, null);


  /**
   * The fourth test sequence.
   */
  private static AnalyzedSequence seq4 =
      new AnalyzedSequence("ATC", "2016-11-25", "Kurt Bohne", "sequence3.ab1", "Nothing to say", null, null, null, null, null);


  /**
   * An array of DatabaseEntries used to test the writing of a file.
   */
  private static DatabaseEntry[] entries =
      new DatabaseEntry[] {
          new DatabaseEntry("a.ab1", 1, "ATCGTCGATCGA", "2016-11-28", "Klaus Bohne", "No comments", "CCCCCCCC", "ATCG", true, "A|1|T", false),
          new DatabaseEntry("b.ab1", 4, "TCGATCGATCG", "2016-11-28", "Kurt Bohne", "No comments", "TTTTTT", "ATCG", false, "A|2|T", false),
          new DatabaseEntry("c.ab1", 0, "ATCGA", "2016-11-28", "Klaus Bohne", "No comments", "AAAAAAAT", "ATCG", true, "A|3|T", false),
          new DatabaseEntry("d.ab1", 7, "ATCGT", "2016-11-28", "Kurt Bohne", "No comments", "AAAAAAAT", "ATCG", false, "A|4|T", false),
          new DatabaseEntry("e.ab1", 1, "AA", "2016-11-28", "Kurt Bohne", "No comments; nothing to say", "GGGGGG", "ATCG", true, "A|5|T", false)
          };


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

    seq1.setReferencedGene(new Gene("ATTTTCG", 4, "FSA", "2016-11-28", "Klaus Bohne"));
    seq1.addMutation("A|131|E");
    seq1.addMutation("K|7|K");
    seq1.addSilentMutation("P|32|S");
    seq1.addSilentMutation("Y|757|E");

    seq2.setReferencedGene(new Gene("ATTTTCG", 1, "FSA", "2016-11-28", "Karl Mueller"));
    seq2.addMutation("A|1|E");

    seq3.setReferencedGene(new Gene("ATTTTCG", 2, "FSA", "2016-11-28", "Lisa Weber"));

    seq4.setReferencedGene(new Gene("ATTTTCG", 3, "FSA", "2016-11-28", "Hans Gärtner"));
    seq4.addMutation("D|23|E");
    seq4.addSilentMutation("G|88|B");
    seq4.addMutation("D|44|P");

  }



  /**
   * This method resets the state of the DatabaseConnection before a new test starts to get equal
   * test circumstances.
   * 
   * @author Ben Kohr
   * 
   */
  @Before
  public void setupDatabaseConnection() {
    DatabaseConnection.flushQueue();
    DatabaseConnection.resetIDs();
  }



  /**
   * This tests checks if the conversion from a AnalyzedSequence into a DatabaseEntry is working
   * correctly.
   * 
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalyzedSequence)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testConvertSequenceIntoDBEsNormal() {

    // Use method
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq1);
    
    // Prepare correct result
    DatabaseEntry dbe1 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne", "No comments", null, null, false, "A|131|E", false);
    DatabaseEntry dbe2 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne", "No comments", null, null, false, "K|7|K", false);
    DatabaseEntry dbe3 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne", "No comments", null, null, false, "P|32|S", true);
    DatabaseEntry dbe4 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne", "No comments", null, null, false, "Y|757|E", true);
    DatabaseEntry[] correctResult = new DatabaseEntry[] {dbe1, dbe2, dbe3, dbe4};

    
    // compare all attributes in each of the pairings
    for (int i = 0; i < correctResult.length; i++) {
      assertEquals(-1, entries.get(i).getID());
      assertEquals(correctResult[i].getFileName(), entries.get(i).getFileName());
      assertEquals(correctResult[i].getGeneID(), entries.get(i).getGeneID());
      assertEquals(correctResult[i].getSequence(), entries.get(i).getSequence());
      assertEquals(correctResult[i].getAddingDate(), entries.get(i).getAddingDate());
      assertEquals(correctResult[i].getResearcher(), entries.get(i).getResearcher());
      assertEquals(correctResult[i].getComments(), entries.get(i).getComments());
      assertEquals(correctResult[i].getVector(), entries.get(i).getVector());
      assertEquals(correctResult[i].getPromotor(), entries.get(i).getPromotor());
      assertEquals(correctResult[i].getMutation(), entries.get(i).getMutation());
      assertEquals(correctResult[i].isSilent(), entries.get(i).isSilent());
    }

    // Result should have 4 elements.
    assertTrue(entries.size() == 4);

  }



  /**
   * This tests checks the conversion from a AnalyzedSequence into a DatabaseEntry is working
   * correctly with just one entry.
   * 
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalyzedSequence)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testConvertSequenceIntoDBEsNormal2() {

    // Use method
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq2);
    
    // Prepare correct result
    DatabaseEntry correctDBE = new DatabaseEntry("sequence2.ab1", 1, "ATCTTTG", "2016-11-29", "Klaus Bohne", "No comments", null, null, false, "A|1|E", false);
   
    // compare all attributes
    assertEquals(-1, entries.get(0).getID());
    assertEquals(correctDBE.getFileName(), entries.get(0).getFileName());
    assertEquals(correctDBE.getGeneID(), entries.get(0).getGeneID());
    assertEquals(correctDBE.getSequence(), entries.get(0).getSequence());
    assertEquals(correctDBE.getAddingDate(), entries.get(0).getAddingDate());
    assertEquals(correctDBE.getResearcher(), entries.get(0).getResearcher());
    assertEquals(correctDBE.getComments(), entries.get(0).getComments());
    assertEquals(correctDBE.getVector(), entries.get(0).getVector());
    assertEquals(correctDBE.getPromotor(), entries.get(0).getPromotor());
    assertEquals(correctDBE.getMutation(), entries.get(0).getMutation());
    assertEquals(correctDBE.isSilent(), entries.get(0).isSilent());

    assertTrue(entries.size() == 1);

  }


  /**
   * This tests checks the conversion from a AnalyzedSequence into a DatabaseEntry is working
   * correctly without any entry to store (i.e. there is no mutation).
   * 
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalyzedSequence)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testConvertSequenceIntoDBEsNoMutation() {

    // Use method
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq3);

    // No mutation: Size should be zero
    assertTrue(entries.size() == 0);

  }



  /**
   * This test checks if the writing of local files is done correctly.
   * 
   * @throws MissingPathException
   * @throws IOException
   * 
   * @see DatabaseConnection#storeAllLocally(String)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testStoreAllLocallyNormal() throws MissingPathException, IOException {

    // Setup of the DatabaseConnection
    for (DatabaseEntry entry : entries) {
      DatabaseConnection.addIntoQueue(entry);
    }

    DatabaseConnection.setLocalPath(path);

    DatabaseConnection.storeAllLocally("testdata");


    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/testdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close(); 


    // Check whether the input is correct
    String[] correctResults = new String[] {
        "0; a.ab1; 1; ATCGTCGATCGA; 2016-11-28; Klaus Bohne; No comments; CCCCCCCC; ATCG; true; A|1|T; false",
        "1; b.ab1; 4; TCGATCGATCG; 2016-11-28; Kurt Bohne; No comments; TTTTTT; ATCG; false; A|2|T; false",
        "2; c.ab1; 0; ATCGA; 2016-11-28; Klaus Bohne; No comments; AAAAAAAT; ATCG; true; A|3|T; false",
        "3; d.ab1; 7; ATCGT; 2016-11-28; Kurt Bohne; No comments; AAAAAAAT; ATCG; false; A|4|T; false",
        "4; e.ab1; 1; AA; 2016-11-28; Kurt Bohne; No comments, nothing to say; GGGGGG; ATCG; true; A|5|T; false"
        
    };
    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }


    // Size should be five, for there a five initial entries
    assertTrue(results.size() == 5);

  }
  
  
  
  /**
   * 
   * This test checks if there is no problem with the writing even if there are no database entries
   * given
   * 
   * @throws MissingPathException
   * @throws IOException
   * 
   * @see DatabaseConnection#storeAllLocally(String)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testStoreAllLocallyNoEntries() throws MissingPathException, IOException {

    // no entries in the DatabaseConnection stored this time

    DatabaseConnection.setLocalPath(path);

    DatabaseConnection.storeAllLocally("notestdata");


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
   * @see DatabaseConnection#storeAllLocally(String)
   * @see MissingPathException
   * 
   * @author Ben Kohr
   */
  @Test(expected = MissingPathException.class)
  public void testStoreAllLocallyMissingPath() throws MissingPathException, IOException {

    // Setting the path to null
    DatabaseConnection.setLocalPath(null);

    // Now, path is null. This means, a MissingPathException is to be thrown.
    DatabaseConnection.storeAllLocally("problemtest");
    
    // This line should not be reached!
    fail();

  }



  /**
   * This test checks whether an AnalyzesSequence is correctly transformed into DatabaseEntries and
   * if these entries is correctly written in a file. This means, this test checks the complete
   * local storage mechanism from the obtained sequences.
   * 
   * @throws MissingPathException
   * @throws IOException
   * 
   * @see DatabaseConnection#storeAllLocally(String)
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalyzedSequence)
   * 
   * @author Ben Kohr
   */
  @Test
  public void testConvertAndStore() throws MissingPathException, IOException {

    // Converting sequence into DBEs
    LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(seq4);

    // Storing them in the DatabaseConnection
    DatabaseConnection.addAllIntoQueue(entries);

    DatabaseConnection.setLocalPath(path);
    DatabaseConnection.storeAllLocally("convertAndStoreTest");


    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("writingtests/convertAndStoreTest.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().skip(1).forEach(line -> results.add(line));
    reader.close();

    
    // Check whether the input is correct
    String[] correctResults = new String[] {
       "0; sequence3.ab1; 3; ATC; 2016-11-25; Kurt Bohne; Nothing to say; null; null; false; D|23|E; false",
       "1; sequence3.ab1; 3; ATC; 2016-11-25; Kurt Bohne; Nothing to say; null; null; false; D|44|P; false",
       "2; sequence3.ab1; 3; ATC; 2016-11-25; Kurt Bohne; Nothing to say; null; null; false; G|88|B; true"
    };
    
    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    // Size should be three, for there a three initial entries.
    assertTrue(results.size() == 3);

  }
  
  

}
