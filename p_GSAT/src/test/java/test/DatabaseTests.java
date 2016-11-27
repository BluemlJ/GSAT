package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
   * The first sequence for testing the conversion method (converting a sequence into a
   * DatabaseEntry).
   * 
   */
  static AnalyzedSequence seq1 =
      new AnalyzedSequence("ATCG", "sequence1.ab1", "primer1", null, null, null, null, null);


  /**
   * The second sequence for testing the conversion method.
   */
  static AnalyzedSequence seq2 =
      new AnalyzedSequence("CCCCC", "sequence2.ab1", "primer2", null, null, null, null, null);


  /**
   * The third test sequence (which will result in no DatabaseEntries).
   */
  static AnalyzedSequence seq3 =
      new AnalyzedSequence("A", "sequence3.ab1", "primer3", null, null, null, null, null);


  /**
   * The fourth test sequence.
   */
  static AnalyzedSequence seq4 =
      new AnalyzedSequence("GGG", "sequence4.ab1", "primer4", null, null, null, null, null);


  /**
   * An array of DatabaseEntries used to test the writing of a file.
   */
  static DatabaseEntry[] entries =
      new DatabaseEntry[] {new DatabaseEntry("a.ab1", "FSA", "primer1", "A|34|E", false),
          new DatabaseEntry("b.ab1", "FSA", "primer2", "A|7|B", true),
          new DatabaseEntry("c.ab1", "TEST", "primer3", "Q|5|R", true),
          new DatabaseEntry("d.ab1", "TEST", "primer4", "A|3|R", false),
          new DatabaseEntry("e.ab1", "TEST", "primer5", "E|55|O", false)};


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

    seq1.setReferencedGene(new Gene("TTT", "FSA"));
    seq1.addMutation("A|131|E");
    seq1.addMutation("K|7|K");
    seq1.addSilentMutation("P|32|S");
    seq1.addSilentMutation("Y|757|E");

    seq2.setReferencedGene(new Gene("ATTTCTGCAGTCC", "FSB"));
    seq2.addMutation("A|1|E");

    seq3.setReferencedGene(new Gene("TGCTGCTGCT", "FSC"));

    seq4.setReferencedGene(new Gene("AGAGGA", "FSD"));
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
    DatabaseEntry dbe1 = new DatabaseEntry("sequence1.ab1", "FSA", "primer1", "A|131|E", false);
    DatabaseEntry dbe2 = new DatabaseEntry("sequence1.ab1", "FSA", "primer1", "K|7|K", false);
    DatabaseEntry dbe3 = new DatabaseEntry("sequence1.ab1", "FSA", "primer1", "P|32|S", true);
    DatabaseEntry dbe4 = new DatabaseEntry("sequence1.ab1", "FSA", "primer1", "Y|757|E", true);

    DatabaseEntry[] correctResult = new DatabaseEntry[] {dbe1, dbe2, dbe3, dbe4};

    // compare all attributes in each of the pairings
    for (int i = 0; i < correctResult.length; i++) {
      assertEquals(correctResult[i].getID(), entries.get(i).getID());
      assertEquals(correctResult[i].getFileName(), entries.get(i).getFileName());
      assertEquals(correctResult[i].getGene(), entries.get(i).getGene());
      assertEquals(correctResult[i].getPrimer(), entries.get(i).getPrimer());
      assertEquals(correctResult[i].getMutation(), entries.get(i).getMutation());
      assertEquals(correctResult[i].getSilentBoolean(), entries.get(i).getSilentBoolean());
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
    DatabaseEntry correctDBE = new DatabaseEntry("sequence2.ab1", "FSB", "primer2", "A|1|E", false);

    // compare all attributes
    assertEquals(correctDBE.getID(), entries.get(0).getID());
    assertEquals(correctDBE.getFileName(), entries.get(0).getFileName());
    assertEquals(correctDBE.getGene(), entries.get(0).getGene());
    assertEquals(correctDBE.getPrimer(), entries.get(0).getPrimer());
    assertEquals(correctDBE.getMutation(), entries.get(0).getMutation());
    assertEquals(correctDBE.getSilentBoolean(), entries.get(0).getSilentBoolean());

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
    for (DatabaseEntry dbe : entries) {
      DatabaseConnection.addIntoQueue(dbe);
    }

    DatabaseConnection.setLocalPath("");

    DatabaseConnection.storeAllLocally("testdata");



    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("testdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().forEach(line -> results.add(line));
    reader.close();


    // Check whether the input is correct
    String[] correctResults = new String[] {"0; a.ab1; FSA; primer1; A|34|E; false",
        "1; b.ab1; FSA; primer2; A|7|B; true", "2; c.ab1; TEST; primer3; Q|5|R; true",
        "3; d.ab1; TEST; primer4; A|3|R; false", "4; e.ab1; TEST; primer5; E|55|O; false"};
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

    DatabaseConnection.setLocalPath("");

    DatabaseConnection.storeAllLocally("notestdata");


    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("notestdata.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().forEach(line -> results.add(line));

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
    for (DatabaseEntry dbe : entries) {
      DatabaseConnection.addIntoQueue(dbe);
    }
    DatabaseConnection.setLocalPath("");
    DatabaseConnection.storeAllLocally("convertAndStoreTest");


    // Code for reading the file in again
    BufferedReader reader = new BufferedReader(new FileReader("convertAndStoreTest.csv"));

    LinkedList<String> results = new LinkedList<String>();
    reader.lines().forEach(line -> results.add(line));
    reader.close();


    // Check whether the input is correct
    String[] correctResults = new String[] {"0; sequence4.ab1; FSD; primer4; D|23|E; false",
        "1; sequence4.ab1; FSD; primer4; D|44|P; false",
        "2; sequence4.ab1; FSD; primer4; G|88|B; true"};
    for (int i = 0; i < correctResults.length; i++) {
      assertEquals(correctResults[i], results.get(i));
    }

    // Size should be three, for there a three initial entries
    assertTrue(results.size() == 3);

  }



}
