package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Pair;
import exceptions.FileReadingException;
import io.SequenceReader;

/**
 * This class tests the behavior of reading AB1 files and converting them into sequences.
 *
 * @author Lovis Heindrich
 */
public class ReadingTests {

  String correctSequence;
  AnalysedSequence testSequence;

  /**
   * Initializes a sample .abi File and the correct sequence represented by it
   * 
   * @throws FileReadingException
   * @throws IOException
   */
  @Before
  public void initializeSequence() throws FileReadingException, IOException {
    // sequence for ab1/Tk_Gs40Hits/Forward/95EI60.ab1 obtained with Chromas
    // (http://technelysium.com.au/wp/chromas/)
    correctSequence =
        "ACCACGTAATTCCCTCTAAAAATAATTTTGTTTACTTTAAGAAGGAGATATACATATGGCACATCACCACCACCATCACTCCGCGGCCCGATTAATGGCGCATTCAATCGAGGAGTTGGCGATTACGACGATTCGAACGCTGTCGATTGACGCGATCGAAAAAGCGAAATCCGGGCATCCGGGCATGCCGATGGGCGCGGCGCCAATGGCGTACACGCTTTGGACGAAATTTATGAATCATAACCCGGCGAATCCAAACTGGTTCAACCGCGACCGTTTTGTCTTGTCAGCCGGGCACGGGTCGATGTTATTGTACAGCTTGCTTCATTTAAGCGGCTACGACGTATCGATGGATGATTTGAAACAATTCCGTCAATGGGGAAGCAAAACGCCGGGCCATCCGGAATACGGCCATACGCCGGGCGTGGAAGCGACGACCGGCCCACTCGGCCAAGGGATTGCGATGGCGGTCGGCATGGCGATGGCGGAACGGCATTTGGCCGCTACATACAACCGCGACGGGTTTGAGATTATCAATCATTATACGTACGCCATTTGCGGCGATGGCGATTTGATGGAAGGAGTGGCGAGCGAAGCTGCGTCACTCGCCGGCCACTTGAAGCTCGGTCGACTGATCGTCCTGTATGACTCGAACGACATTTCGCTGGACGGGGAGCTCAACCTGTCGTTCTCGGAAAACGTCGCCCAACGTTTCCAAGCATACGGCTGGCAATATTTGCGCGTTGAGGACGGCAACAATATTGAAGAAATCGCCAAAGCGCTGGAGGAGGCGCGGGCGGACCTCAGCCGGCCGACGCTCATTGAAGTAAAAACGACGATTGGCTACGGCGCGCCAAATAAAGCGGGCACGTCCGGCGTCCACGGTGCTCCGCTCGGCGCCCAAGAGGCGAAGCTGACGAAAGAGGCGTATCGTTGGACATTTGCGGAAGATTTTTACGTGCCAGAAGAAGTGTACGCCCACTTTCCGTGCGACGGTGCAAGAAGCCGGGAGCGAAAAAAAGAGGCGAAATGGAATGGAGCAGCTCCGCCGCCTATGGAACAGGGCCCATCCCGGAACTGGGCCGCCCAATTTGAAGCCAAGCGATCCGAAGGCAAACTTCCCAGATGGGATGGGAAAGCTTTCTTTTGCCGGGTATACCGAAAGCAGGGCAAAAAGCTTTGGGCAACCCCGCTTCATCCGTCCCGGGGGAAAGTGGATTCAACGCCCAATCGGCCAAAAGCGGGTACCCGCAAATTGGTTTTGGGCGGGTTCCGGCGGGAACTTTGGGCAAAAGCTCCGGAAATAAAAAACGGCCTCCATCCAAAGGCGGGGGGGGCAAACTTTTCTTTCCCCGGGGGCAACCCTACCAAAAAGGGGGGCCCAACCCTTTTTTGGGTTTTGGCGCGGGGGGCCCCAAAATTTTTTCCCCCTTGGGGGGGGGCGCGGGCCCTCTAAAAACCGGGGTTAGGGGGCGGTTTTCC";
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
      if (!f.getName().substring(f.getName().length() - 3).equals("ab1")) {
        working = false;
        System.err.println(f.getName());
      }
    }
    assertTrue(working);
  }

  @Test
  public void listFilesEmptyFolderTest() {
    // File parsedFile = new
    // File(getClass().getResource("/test-results/test/binary").getFile());
    File f = new File("/p_GSAT/build/test-results/test/empty");
    SequenceReader.configurePath(f.getAbsolutePath());
    Pair<LinkedList<File>, LinkedList<File>> pair = SequenceReader.listFiles();
    assertTrue(pair.first.size() == 0 && pair.second.size() == 0);
  }

  @Test
  public void listFilesHalfABITest() {
    File parsedFile = new File(new File("resources/ab1/Tk_Gs40Hits/Forward").getAbsolutePath());

    SequenceReader.configurePath(parsedFile.getAbsolutePath());
    Pair<LinkedList<File>, LinkedList<File>> pair = SequenceReader.listFiles();
    assertTrue(pair.first.size() == 10 && pair.second.size() == 1);
  }

  /**
   * This test reads a sequence by passing the file directly as an argument (Userstory 003 -
   * Expected behavior)
   * 
   * @throws FileReadingException
   * @throws IOException
   */
  @Test
  public void readFromFileTest() throws FileReadingException, IOException {

    File parsedFile =
        new File(new File("resources/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getAbsolutePath());
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

    assertEquals(testSequence.getSequence().toLowerCase(), correctSequence.toLowerCase());
  }

  /**
   * This tests checks if a corrupt file leads to an ioexception (Userstory 003 - Unusual behavior)
   * 
   * @throws FileReadingException
   */
  @Test
  public void testCorruptSequence() throws FileReadingException {
    SequenceReader.configurePath(new File("resources/ab1/corrupt.ab1").getAbsolutePath());
    try {
      SequenceReader.convertFileIntoSequence();
    } catch (IOException e) {
      assertEquals(e.getMessage(), "unknown chromatogram format (not ab1, scf or ztr)");
    }
  }

}
