package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import analysis.AnalyzedSequence;
import analysis.DNAUtils;
import analysis.Gene;
import analysis.Sequence;
import exceptions.CorruptedSequenceException;

/**
 * This class tests the behavior of the analysis parts of the project.
 *
 */
public class AnalysisTests {
  /**
   * This test checks the Levenshtein algorythm by putting in some generic test Strings
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void testLevenshtein() {
    String first = "kitten";
    String second = "sitting";
    int[][] levenMatrix = DNAUtils.leventhsein(first, second);
    // int[][] resultMatrix = new int[][]
    // {{0,1,2,3,4,5,6},{1,1,2,3,4,5,6},{2,2,1,2,3,4,5},{3,3,2,1,2,3,4},{4,4,3,2,1,2,3},{5,5,4,3,2,2,3},{6,6,5,4,3,3,2},{7,7,6,5,4,4,3}};
    int[][] resultMatrix = new int[][] {{0, 1, 2, 3, 4, 5, 6, 7}, {1, 1, 2, 3, 4, 5, 6, 7},
        {2, 2, 1, 2, 3, 4, 5, 6}, {3, 3, 2, 1, 2, 3, 4, 5}, {4, 4, 3, 2, 1, 2, 3, 4},
        {5, 5, 4, 3, 2, 2, 3, 4}, {6, 6, 5, 4, 3, 3, 2, 3}};
    assertTrue(Arrays.deepEquals(levenMatrix, resultMatrix));
  }


  /******************** Test for reportDifferences() ************************************/

  /**
   * Test for correct deletion;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesDelet() {
    LinkedList<String> list = DNAUtils.reportDifferences("helllo", "hello");
    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "d|5, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct deletion at end;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesDeletEnd() {
    LinkedList<String> list = DNAUtils.reportDifferences("hellox", "hello");
    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "d|6, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsert() {
    LinkedList<String> list = DNAUtils.reportDifferences("helo", "hello");
    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "i|3, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion at end;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsertEnd() {
    LinkedList<String> list = DNAUtils.reportDifferences("hell", "hello");
    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "i|o|4, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion at begin;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsertBegin() {
    LinkedList<String> list = DNAUtils.reportDifferences("ello", "hello");
    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "i|h|0, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct substitution;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesSubstitute() {
    LinkedList<String> list = DNAUtils.reportDifferences("helxo", "hello");
    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "s|l|4, ";
    System.out.println(result);
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * correct codonstrings
   * @author bluemlj
 * @throws CorruptedSequenceException 
   */
  @Test
  public void codonsToAminoAcidsOnCorrectUse() throws CorruptedSequenceException {
    String testA = "ATTGGGCCCATT";
    String result = DNAUtils.codonsToAminoAcids(testA);
    assertTrue(result.equals("IGPI"));
  }
  
  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * uncorrect codonstrings (with wrong nukleotides) and gets the correct error String
   * @see analysis.DNAUtils
   * @author bluemlj
   */
  @Test
  public void codonsToAminoAcidsWithNotNukleotideString() throws CorruptedSequenceException{
    String testString = "HNOFClBrI";
    String result = DNAUtils.codonsToAminoAcids(testString);
    assertTrue(result.equals("uncorrect codonString"));
    
  }
  
  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * codonstrings, that cant be correct condon string (%3) and gets the correct error message
   * @see analysis.DNAUtils
   * @author bluemlj
   */
  @Test
  public void codonsToAminoAcidsWithToShortString() throws CorruptedSequenceException{
    String testString = "ACTTTGG";
    String result = DNAUtils.codonsToAminoAcids(testString);
    assertTrue(result.equals("nukleotides not modulo 3, so not convertable"));
    
  }
  
  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * empty codonstrings and gets the correct error
   * @see analysis.DNAUtils
   * @author bluemlj
   */
  @Test
  public void codonsToAminoAcidsWithEmptyString() throws CorruptedSequenceException {
   String testString = "";
    String result = DNAUtils.codonsToAminoAcids(testString);
    assertTrue(result.equals("empty nukleotides"));
    
  }
  
  
  
  /**
   * This test checks if a sequence is correctly reversed.
   * 
   * @throws CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testGetReversedSequence() {
	  
	  Sequence seq = new Gene("", "Test1");
	  assertEquals("", seq.getReversedSequence());  
	  
	  Sequence seq2 = new Gene("TTT", "Test2");
	  assertEquals("TTT", seq2.getReversedSequence()); 
	  
	  Sequence seq3 = new Gene("ATCGATCGATCG", "Test3");
	  assertEquals("GCTAGCTAGCTA", seq3.getReversedSequence()); 
	  
	  Sequence seq4 = new Gene("GGGTACCGTGTAGG", "Test4");
	  assertEquals("GGATGTGCCATGGG", seq4.getReversedSequence()); 
	  
  }
  
  
  
  /**
   * This test checks whether the complementary sequence is correctly computed.
   * 
   * @throws CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testGetComplementarySequenceNoError() throws CorruptedSequenceException  {
	  
	  Sequence seq = new Gene("", "Test1");
	  assertEquals("", seq.getComplementarySequence());  
	  
	  Sequence seq2 = new Gene("AAA", "Test2");
	  assertEquals("TTT", seq2.getComplementarySequence()); 
	  
	  Sequence seq3 = new Gene("AATTCCGGATCG", "Test3");
	  assertEquals("TTAAGGCCTAGC", seq3.getComplementarySequence()); 
	  
	  Sequence seq4 = new Gene("ATGCTAGCTAGCCCC", "Test4");
	  assertEquals("TACGATCGATCGGGG", seq4.getComplementarySequence()); 
  }
  
  
  /**
   * This test checks whether an exception is thrown if a sequence's complementary
   * sequence is build. It's important to throw the exception and not to hide the error.
   * 
   * @throws CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test(expected = CorruptedSequenceException.class)
  public void testGetComplementarySequenceWithError() throws CorruptedSequenceException  {
	  
	  Sequence seq = new Gene("AATTCCFGATCG", "Problem");
	  String comp = seq.getComplementarySequence();
	  
	  fail("No exception thrown!");  
  }
  
  

}
