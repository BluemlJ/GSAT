package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.junit.Before;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Pair;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import io.SequenceReader;

/**
 * This class tests the behavior of reading AB1 files and converting them into sequences.
 *
 * @author Lovis Heindrich
 */
public class ReadingTests {

  String correctSequence;
  String correctSequence2;
  AnalysedSequence testSequence;

  /**
   * Initializes a sample .abi File and the correct sequence represented by it
   * 
   * @throws FileReadingException
   * @throws IOException
   * @throws MissingPathException
   * @throws IllegalSymbolException
   */
  @Before
  public void initializeSequence()
      throws FileReadingException, IOException, MissingPathException, IllegalSymbolException {
    // sequence for ab1/Tk_Gs40Hits/Forward/95EI60.ab1 obtained with Chromas
    // (http://technelysium.com.au/wp/chromas/)
    correctSequence =
        "ttagcgatattccctctataaatattttgtttactttaagaaggagatatacatatggcacatcaccaccaccatcactccgcggcccgattaatggcgcattcaatcgaggagttggcgattacgacgattcgaacgctgtcgattgacgcgatcgaaaaagcgaaatccgggcatccgggcatgccgatgggcgcggcgccaatggcgtacacgctttggacgaaatttatgaatcataacccggcgaatccaaactggttcaaccgcgaccgttttgtcttgtcagccgggcacgggtcgatgttattgtacagcttgcttcatttaagcggctacgacgtatcgatggatgatttgaaacaattccgtcaatggggaagcaaaacgccgggccatccggaatacggccatacgccgggcgtggaagcgacgaccggcccactcggccaagggattgcgatggcggtcggcatggcgatggcggaacggcatttggccgctacatacaaccgcgacgggtttgagattatcaatcattatacgtacgccatttgcggcgatggcgatttgatggaaggagtggcgagcgaagctgcgtcactcgccggccacttgaagctcggtcgactgatcgtcctgtatgactcgaacgacatttcgctggacggggagctcaacctgtcgttctcggaaaacgtcgcccaacgtttccaagcatacggctggcaatatttgcgcgttgaggacggcaacaatattgaagaaatcgccaaagcgctggaggaggcgcgggcggacctcagccggccgacgctcattgaagtaaaaacgacgattggctacggcgcgccaaataaagcgggcacgtccggcgtccacggtgctccgctcggcgcccaagaggcgaagctgacgaaagaggcgtatcgttggacatttgcggaagatttttacgtgccagaagaagtgtacgcccacttccgtgcgacggtgcaagaagccgggagcgaaaaaaagaggcgaaatggaaatgagcagctcggccgcctatgaacagggcccatcccggaactgggccgcccaaattgaagcgaaccgatcgaaaggcaaacttcccgaatggatgggaaagcttcttttgccgggttaccgaagcaagggaaaagcttgggcaaccccgctcatccgtccgggggaaagggatcaaccgccatccgccaaaagcgggacccggcaattggtttgggccggttccggcgggaatttcccaaaggctccgaattaaaacggtccctccaaaaggcggggggggaaacttctttccccgggg";
    correctSequence2 =
        "accacgtaattccctctaaaaataattttgtttactttaagaaggagatatacatatggcacatcaccaccaccatcactccgcggcccgattaatggcgcattcaatcgaggagttggcgattacgacgattcgaacgctgtcgattgacgcgatcgaaaaagcgaaatccgggcatccgggcatgccgatgggcgcggcgccaatggcgtacacgctttggacgaaatttatgaatcataacccggcgaatccaaactggttcaaccgcgaccgttttgtcttgtcagccgggcacgggtcgatgttattgtacagcttgcttcatttaagcggctacgacgtatcgatggatgatttgaaacaattccgtcaatggggaagcaaaacgccgggccatccggaatacggccatacgccgggcgtggaagcgacgaccggcccactcggccaagggattgcgatggcggtcggcatggcgatggcggaacggcatttggccgctacatacaaccgcgacgggtttgagattatcaatcattatacgtacgccatttgcggcgatggcgatttgatggaaggagtggcgagcgaagctgcgtcactcgccggccacttgaagctcggtcgactgatcgtcctgtatgactcgaacgacatttcgctggacggggagctcaacctgtcgttctcggaaaacgtcgcccaacgtttccaagcatacggctggcaatatttgcgcgttgaggacggcaacaatattgaagaaatcgccaaagcgctggaggaggcgcgggcggacctcagccggccgacgctcattgaagtaaaaacgacgattggctacggcgcgccaaataaagcgggcacgtccggcgtccacggtgctccgctcggcgcccaagaggcgaagctgacgaaagaggcgtatcgttggacatttgcggaagatttttacgtgccagaagaagtgtacgcccactttccgtgcgacggtgcaagaagccgggagcgaaaaaaagaggcgaaatggaatggagcagctccgccgcctatggaacagggcccatcccggaactgggccgcccaatttgaagccaagcgatccgaaggcaaacttcccagatgggatgggaaagctttcttttgccgggtataccgaaagcagggcaaaaagctttgggcaaccccgcttcatccgtcccgggggaaagtggattcaacgcccaatcggccaaaagcgggtacccgcaaattggttttgggcgggttccggcgggaactttgggcaaaagctccggaaataaaaaacggcctccatccaaaggcggggggggcaaacttttctttccccgggggcaaccctaccaaaaaggggggcccaacccttttttgggttttggcgcggggggccccaaaattttttcccccttgggggggggcgcgggccctctaaaaaccggggttagggggcggttttcc";
    // set SequenceReader file path
    SequenceReader
        .configurePath(new File("resources/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getAbsolutePath());
    testSequence = SequenceReader.convertFileIntoSequence();
  }

  @Test
  public void listFilesAllABITest() {
    File parsedFile = new File(new File("resources/ab1/Tk_Gs40Hits/Forward/").getAbsolutePath());

    SequenceReader.configurePath(parsedFile.getAbsolutePath());
    Pair<LinkedList<File>, LinkedList<File>> pair = SequenceReader.listFiles();
    boolean working = true;
    for (File f : pair.first) {
      if (!(f.getName().endsWith("ab1") || f.getName().endsWith("abi"))) {
        working = false;
      }
    }

    for (File f : pair.second) {
      if (f.getName().endsWith("ab1") || f.getName().endsWith("abi")) {
        working = false;
      }
    }

    assertTrue(working);
  }

  @Test
  public void listFilesEmptyFolderTest() {
    File f = new File("/p_GSAT/build/test-results/test/empty");
    SequenceReader.configurePath(f.getAbsolutePath());
    Pair<LinkedList<File>, LinkedList<File>> pair = SequenceReader.listFiles();
    assertTrue(pair.first.size() == 0 && pair.second.size() == 0);
  }

  @Test
  public void listFilesHalfABITest() {
    File parsedFile = new File(new File("resources/ab1/Tk_Gs40Hits/Reverse").getAbsolutePath());

    SequenceReader.configurePath(parsedFile.getAbsolutePath());
    Pair<LinkedList<File>, LinkedList<File>> pair = SequenceReader.listFiles();
    assertTrue(pair.first.size() == 10 && pair.second.size() == 4);
  }

  /**
   * This test reads a sequence by passing the file directly as an argument (Userstory 003 -
   * Expected behavior)
   * 
   * @throws FileReadingException
   * @throws IOException
   * @throws MissingPathException
   * @throws IllegalSymbolException
   */
  @Test
  public void readFromFileTest()
      throws FileReadingException, IOException, MissingPathException, IllegalSymbolException {

    File parsedFile =
        new File(new File("resources/ab1/Tk_Gs40Hits/Forward/95EI61.ab1").getAbsolutePath());
    AnalysedSequence parsedSequence = SequenceReader.convertFileIntoSequence(parsedFile);
    assertEquals(parsedSequence.getSequence().toLowerCase(), correctSequence.toLowerCase());
  }

  /**
   * This tests checks if it is possible to read a correct DNA Sequence from an .abi file (Userstory
   * 003 - Expected behavior)
   * 
   * @throws IllegalSymbolException
   * @throws FileReadingException
   * @throws IOException
   */
  @Test
  public void sequenceReadTest() throws FileReadingException, IOException {

    assertEquals(testSequence.getSequence().toLowerCase(), correctSequence2.toLowerCase());
  }

  /**
   * This tests checks if a corrupt file leads to an ioexception (Userstory 003 - Unusual behavior)
   * 
   * @throws FileReadingException
   * @throws MissingPathException
   * @throws IllegalSymbolException
   */
  @Test
  public void testCorruptSequence()
      throws FileReadingException, MissingPathException, IllegalSymbolException {
    SequenceReader.configurePath(new File("resources/ab1/corrupt.ab1").getAbsolutePath());
    try {
      SequenceReader.convertFileIntoSequence();
      fail();
    } catch (IOException e) {
      assertEquals(e.getMessage(), "unknown chromatogram format (not ab1, scf or ztr)");
    }
  }

}
