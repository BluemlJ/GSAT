package test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.junit.Test;

import analysis.DNAUtils;
import io.ConsoleIO;

/**
 * This class tests the behavior of the analysis parts of the project.
 *
 */
public class AnalysisTests {
	/**
	 * This test checks the Levenshtein algorythm
	 * by putting in some generic test Strings
	 * 
	 * 
	 * @author Kevin Otto
	 */
	@Test
	public void testLevenshtein(){
		String first = "kitten";
		String second = "sitting";
		int[][] levenMatrix = DNAUtils.leventhsein(first, second);
		//int[][] resultMatrix = new int[][] {{0,1,2,3,4,5,6},{1,1,2,3,4,5,6},{2,2,1,2,3,4,5},{3,3,2,1,2,3,4},{4,4,3,2,1,2,3},{5,5,4,3,2,2,3},{6,6,5,4,3,3,2},{7,7,6,5,4,4,3}};
		int[][] resultMatrix = new int[][] {{0,1,2,3,4,5,6,7},{1,1,2,3,4,5,6,7},{2,2,1,2,3,4,5,6},{3,3,2,1,2,3,4,5},{4,4,3,2,1,2,3,4},{5,5,4,3,2,2,3,4},{6,6,5,4,3,3,2,3}};
		assertTrue(Arrays.deepEquals(levenMatrix, resultMatrix));
	}
}
