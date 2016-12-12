package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import analysis.AnalysedSequence;
import exceptions.FileReadingException;
import io.SequenceReader;

/**
 * This class tests the behavior of reading AB1 files and converting them into
 * sequences.
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
		correctSequence = "ACCACGTAATTCCCTCTAAAAATAATTTTGTTTACTTTAAGAAGGAGATATACATATGGCACATCACCACCACCATCACTCCGCGGCCCGATTAATGGCGCATTCAATCGAGGAGTTGGCGATTACGACGATTCGAACGCTGTCGATTGACGCGATCGAAAAAGCGAAATCCGGGCATCCGGGCATGCCGATGGGCGCGGCGCCAATGGCGTACACGCTTTGGACGAAATTTATGAATCATAACCCGGCGAATCCAAACTGGTTCAACCGCGACCGTTTTGTCTTGTCAGCCGGGCACGGGTCGATGTTATTGTACAGCTTGCTTCATTTAAGCGGCTACGACGTATCGATGGATGATTTGAAACAATTCCGTCAATGGGGAAGCAAAACGCCGGGCCATCCGGAATACGGCCATACGCCGGGCGTGGAAGCGACGACCGGCCCACTCGGCCAAGGGATTGCGATGGCGGTCGGCATGGCGATGGCGGAACGGCATTTGGCCGCTACATACAACCGCGACGGGTTTGAGATTATCAATCATTATACGTACGCCATTTGCGGCGATGGCGATTTGATGGAAGGAGTGGCGAGCGAAGCTGCGTCACTCGCCGGCCACTTGAAGCTCGGTCGACTGATCGTCCTGTATGACTCGAACGACATTTCGCTGGACGGGGAGCTCAACCTGTCGTTCTCGGAAAACGTCGCCCAACGTTTCCAAGCATACGGCTGGCAATATTTGCGCGTTGAGGACGGCAACAATATTGAAGAAATCGCCAAAGCGCTGGAGGAGGCGCGGGCGGACCTCAGCCGGCCGACGCTCATTGAAGTAAAAACGACGATTGGCTACGGCGCGCCAAATAAAGCGGGCACGTCCGGCGTCCACGGTGCTCCGCTCGGCGCCCAAGAGGCGAAGCTGACGAAAGAGGCGTATCGTTGGACATTTGCGGAAGATTTTTACGTGCCAGAAGAAGTGTACGCCCACTTTCCGTGCGACGGTGCAAGAAGCCGGGAGCGAAAAAAAGAGGCGAAATGGAATGGAGCAGCTCCGCCGCCTATGGAACAGGGCCCATCCCGGAACTGGGCCGCCCAATTTGAAGCCAAGCGATCCGAAGGCAAACTTCCCAGATGGGATGGGAAAGCTTTCTTTTGCCGGGTATACCGAAAGCAGGGCAAAAAGCTTTGGGCAACCCCGCTTCATCCGTCCCGGGGGAAAGTGGATTCAACGCCCAATCGGCCAAAAGCGGGTACCCGCAAATTGGTTTTGGGCGGGTTCCGGCGGGAACTTTGGGCAAAAGCTCCGGAAATAAAAAACGGCCTCCATCCAAAGGCGGGGGGGGCAAACTTTTCTTTCCCCGGGGGCAACCCTACCAAAAAGGGGGGCCCAACCCTTTTTTGGGTTTTGGCGCGGGGGGCCCCAAAATTTTTTCCCCCTTGGGGGGGGGCGCGGGCCCTCTAAAAACCGGGGTTAGGGGGCGGTTTTCC";
		// set SequenceReader file path
		SequenceReader.configurePath(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
		testSequence = SequenceReader.convertFileIntoSequence();
	}

	/**
	 * This tests checks if it is possible to read a correct DNA Sequence from
	 * an .abi file
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
	 * This tests checks if a corrupt file leads to an ioexception
	 * 
	 * @throws FileReadingException
	 */
	@Test
	public void testCorruptSequence() throws FileReadingException {
		SequenceReader.configurePath(getClass().getResource("/ab1/corrupt.ab1").getFile());
		try {
			SequenceReader.convertFileIntoSequence();
		} catch (IOException e) {
			assertEquals(e.getMessage(), "unknown chromatogram format (not ab1, scf or ztr)");
		}
	}

	/**
	 * This test reads a sequence by passing the file directly as an argument
	 * 
	 * @throws FileReadingException
	 * @throws IOException
	 */
	@Test
	public void readFromFileTest() throws FileReadingException, IOException {
		File parsedFile = new File(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
		AnalysedSequence parsedSequence = SequenceReader.convertFileIntoSequence(parsedFile);
		assertEquals(parsedSequence.getSequence().toLowerCase(), correctSequence.toLowerCase());
	}

}
