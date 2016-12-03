package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.junit.Before;
import org.junit.Test;

import analysis.AnalyzedSequence;
import exceptions.FileReadingException;
import io.SequenceReader;

/**
 * This class tests the behavior of reading AB1 files and converting them into sequences.
 *
 */
public class ReadingTests {
  
  String correctSequence;
  AnalyzedSequence testSequence;
  
  @Before
  public void initializeSequence() throws IllegalSymbolException, FileReadingException, IOException{
    // sequence for ab1/Tk_Gs40Hits/Forward/95EI60.ab1 obtained with Chromas (http://technelysium.com.au/wp/chromas/)
    correctSequence =
        "ACCACGTAATTCCCTCTAAAAATAATTTTGTTTACTTTAAGAAGGAGATATACATATGGCACATCACCACCACCATCACTCCGCGGCCCGATTAATGGCGCATTCAATCGAGGAGTTGGCGATTACGACGATTCGAACGCTGTCGATTGACGCGATCGAAAAAGCGAAATCCGGGCATCCGGGCATGCCGATGGGCGCGGCGCCAATGGCGTACACGCTTTGGACGAAATTTATGAATCATAACCCGGCGAATCCAAACTGGTTCAACCGCGACCGTTTTGTCTTGTCAGCCGGGCACGGGTCGATGTTATTGTACAGCTTGCTTCATTTAAGCGGCTACGACGTATCGATGGATGATTTGAAACAATTCCGTCAATGGGGAAGCAAAACGCCGGGCCATCCGGAATACGGCCATACGCCGGGCGTGGAAGCGACGACCGGCCCACTCGGCCAAGGGATTGCGATGGCGGTCGGCATGGCGATGGCGGAACGGCATTTGGCCGCTACATACAACCGCGACGGGTTTGAGATTATCAATCATTATACGTACGCCATTTGCGGCGATGGCGATTTGATGGAAGGAGTGGCGAGCGAAGCTGCGTCACTCGCCGGCCACTTGAAGCTCGGTCGACTGATCGTCCTGTATGACTCGAACGACATTTCGCTGGACGGGGAGCTCAACCTGTCGTTCTCGGAAAACGTCGCCCAACGTTTCCAAGCATACGGCTGGCAATATTTGCGCGTTGAGGACGGCAACAATATTGAAGAAATCGCCAAAGCGCTGGAGGAGGCGCGGGCGGACCTCAGCCGGCCGACGCTCATTGAAGTAAAAACGACGATTGGCTACGGCGCGCCAAATAAAGCGGGCACGTCCGGCGTCCACGGTGCTCCGCTCGGCGCCCAAGAGGCGAAGCTGACGAAAGAGGCGTATCGTTGGACATTTGCGGAAGATTTTTACGTGCCAGAAGAAGTGTACGCCCACTTTCCGTGCGACGGTGCAAGAAGCCGGGAGCGAAAAAAAGAGGCGAAATGGAATGGAGCAGCTCCGCCGCCTATGGAACAGGGCCCATCCCGGAACTGGGCCGCCCAATTTGAAGCCAAGCGATCCGAAGGCAAACTTCCCAGATGGGATGGGAAAGCTTTCTTTTGCCGGGTATACCGAAAGCAGGGCAAAAAGCTTTGGGCAACCCCGCTTCATCCGTCCCGGGGGAAAGTGGATTCAACGCCCAATCGGCCAAAAGCGGGTACCCGCAAATTGGTTTTGGGCGGGTTCCGGCGGGAACTTTGGGCAAAAGCTCCGGAAATAAAAAACGGCCTCCATCCAAAGGCGGGGGGGGCAAACTTTTCTTTCCCCGGGGGCAACCCTACCAAAAAGGGGGGCCCAACCCTTTTTTGGGTTTTGGCGCGGGGGGCCCCAAAATTTTTTCCCCCTTGGGGGGGGGCGCGGGCCCTCTAAAAACCGGGGTTAGGGGGCGGTTTTCC";
    // set SequenceReader file path
    SequenceReader.configurePath(
      getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
      //"./src/main/resources/ab1/Tk_Gs40Hits/Forward/95EI60.ab1");
    testSequence = SequenceReader.convertFileIntoSequence();
  }

  /**
   * This tests checks if it is possible to read a DNA Sequence from an .abi file
   * 
   * @throws IllegalSymbolException
   * @throws FileReadingException
   * @throws IOException
   */
  @Test
  public void sequenceReadTest() throws IllegalSymbolException, FileReadingException, IOException {

    assertEquals(testSequence.getSequence().toLowerCase(), correctSequence.toLowerCase());
  }
}
