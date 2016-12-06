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

import analysis.AnalysedSequence;
import analysis.Gene;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.DatabaseConnection;
import io.DatabaseEntry;
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
      new AnalysedSequence("ATCG", "2016-11-28", "Klaus Bohne", "sequence1.ab1", "No comments", null, 0);


  /**
   * The second sequence for testing the conversion method.
   */
  private static AnalysedSequence seq2 =
      new AnalysedSequence("ATCTTTG", "2016-11-29", "Klaus Bohne", "sequence2.ab1", "No comments", null, 0);


  /**
   * The third test sequence (which will result in no DatabaseEntries).
   */
  private static AnalysedSequence seq3 =
      new AnalysedSequence("ATCTTGCGTTG", "2016-11-27", "Klaus Hafer", "sequence3.ab1", "No comments", null, 0);


  /**
   * The fourth test sequence.
   */
  private static AnalysedSequence seq4 =
      new AnalysedSequence("ATC", "2016-11-25", "Kurt Bohne", "sequence3.ab1", "Nothing to say", null, 0);


  /**
   * An array of DatabaseEntries used to test the writing of a file.
   */
  private static DatabaseEntry[] entries =
      new DatabaseEntry[] {
          new DatabaseEntry("a.ab1", 1, "ATCGTCGATCGA", "2016-11-28", "Klaus Bohne", "No comments", "CCCCCCCC", "ATCG", true, "+1H4", MutationType.INSERTION),
          new DatabaseEntry("b.ab1", 4, "TCGATCGATCG", "2016-11-28", "Kurt Bohne", "No comments", "TTTTTT", "ATCG", false, "-1H4", MutationType.DELETION),
          new DatabaseEntry("c.ab1", 0, "ATCGA", "2016-11-28", "Klaus Bohne", "No comments", "AAAAAAAT", "ATCG", true, "ACC4ACG", MutationType.SILENT),
          new DatabaseEntry("d.ab1", 7, "ATCGT", "2016-11-28", "Kurt Bohne", "No comments", "AAAAAAAT", "ATCG", false, "H4D", MutationType.SUBSTITUTION),
          new DatabaseEntry("e.ab1", 1, "AA", "2016-11-28", "Kurt Bohne", "No comments; nothing to say", "GGGGGG", "ATCG", true, "reading frame error", MutationType.ERROR)
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
    seq1.addMutation("A131E");
    seq1.addMutation("G7K");
    seq1.addMutation("+2H5");

    seq2.setReferencedGene(new Gene("ATTTTCG", 1, "FSA", "2016-11-28", "Karl Mueller"));
    seq2.addMutation("reading frame error");

    seq3.setReferencedGene(new Gene("ATTTTCG", 2, "FSA", "2016-11-28", "Lisa Weber"));

    seq4.setReferencedGene(new Gene("ATTTTCG", 3, "FSA", "2016-11-28", "Hans Gans"));
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
  public void setupDatabaseConnection() {
    DatabaseConnection.flushQueue();
    DatabaseConnection.resetIDs();
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
    DatabaseEntry dbe1 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne", "No comments", null, null, false, "A131E", MutationType.SUBSTITUTION);
    DatabaseEntry dbe2 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne", "No comments", null, null, false, "G7K", MutationType.SUBSTITUTION);
    DatabaseEntry dbe3 = new DatabaseEntry("sequence1.ab1", 4, "ATCG", "2016-11-28", "Klaus Bohne", "No comments", null, null, false, "+2H5", MutationType.INSERTION);
    DatabaseEntry[] correctResult = new DatabaseEntry[] {dbe1, dbe2, dbe3};

    
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
    DatabaseEntry correctDBE = new DatabaseEntry("sequence2.ab1", 1, "ATCTTTG", "2016-11-29", "Klaus Bohne", "No comments", null, null, false, "reading frame error", MutationType.ERROR);
   
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
        "0; a.ab1; 1; ATCGTCGATCGA; 2016-11-28; Klaus Bohne; No comments; CCCCCCCC; ATCG; true; +1H4; INSERTION",
        "1; b.ab1; 4; TCGATCGATCG; 2016-11-28; Kurt Bohne; No comments; TTTTTT; ATCG; false; -1H4; DELETION",
        "2; c.ab1; 0; ATCGA; 2016-11-28; Klaus Bohne; No comments; AAAAAAAT; ATCG; true; ACC4ACG; SILENT",
        "3; d.ab1; 7; ATCGT; 2016-11-28; Kurt Bohne; No comments; AAAAAAAT; ATCG; false; H4D; SUBSTITUTION",
        "4; e.ab1; 1; AA; 2016-11-28; Kurt Bohne; No comments, nothing to say; GGGGGG; ATCG; true; reading frame error; ERROR"  
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
   * @see DatabaseEntry#convertSequenceIntoEntries(AnalysedSequence)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException 
   */
  @Test
  public void testConvertAndStore() throws MissingPathException, IOException, UndefinedTypeOfMutationException {

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
       "0; sequence3.ab1; 3; ATC; 2016-11-25; Kurt Bohne; Nothing to say; null; null; false; AAA7CAA; SILENT",
       "1; sequence3.ab1; 3; ATC; 2016-11-25; Kurt Bohne; Nothing to say; null; null; false; -1H5; DELETION"
    };

    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    // Size should be three, for there a two initial entries.
    assertTrue(results.size() == 2);

  }
  
  

}
