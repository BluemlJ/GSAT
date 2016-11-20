package test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.junit.Test;

import analysis.DNAUtils;
import io.ConsoleIO;
import junit.framework.Assert;

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
	
	
	/********************Test for reportDifferences()************************************/
	
	/**
	 * Test for correct deletion;
	 * @author Kevin Otto
	 */
	@Test
	public void testDiferencesDelet(){
		LinkedList<String> list = DNAUtils.reportDifferences("helllo", "hello");
		String result = "";
		for (String string : list) {
			result += string + ", ";
		}
		String expected = "d|l|5, "; 
		assertTrue(result.equals(expected));
	}
	
	/**
	 * Test for correct deletion at end;
	 * @author Kevin Otto
	 */
	@Test
	public void testDiferencesDeletEnd(){
		LinkedList<String> list = DNAUtils.reportDifferences("hellox", "hello");
		String result = "";
		for (String string : list) {
			result += string + ", ";
		}
		String expected = "d|x|6, "; 
		assertTrue(result.equals(expected));
	}
	
	/**
	 * Test for correct insertion;
	 * @author Kevin Otto
	 */
	@Test
	public void testDiferencesInsert(){
		LinkedList<String> list = DNAUtils.reportDifferences("helo", "hello");
		String result = "";
		for (String string : list) {
			result += string + ", ";
		}
		String expected = "i|l|3, "; 
		assertTrue(result.equals(expected));
	}
	
	/**
	 * Test for correct insertion at end;
	 * @author Kevin Otto
	 */
	@Test
	public void testDiferencesInsertEnd(){
		LinkedList<String> list = DNAUtils.reportDifferences("hell", "hello");
		String result = "";
		for (String string : list) {
			result += string + ", ";
		}
		String expected = "i|o|4, "; 
		assertTrue(result.equals(expected));
	}
	
	/**
	 * Test for correct insertion at begin;
	 * @author Kevin Otto
	 */
	@Test
	public void testDiferencesInsertBegin(){
		LinkedList<String> list = DNAUtils.reportDifferences("ello", "hello");
		String result = "";
		for (String string : list) {
			result += string + ", ";
		}
		String expected = "i|h|0, "; 
		assertTrue(result.equals(expected));
	}
	
	/**
	 * Test for correct substitution;
	 * @author Kevin Otto
	 */
	@Test
	public void testDiferencesSubstitute(){
		LinkedList<String> list = DNAUtils.reportDifferences("helxo", "hello");
		String result = "";
		for (String string : list) {
			result += string + ", ";
		}
		String expected = "s|l|4, "; 
		System.out.println(result);
		assertTrue(result.equals(expected));
	}
}
