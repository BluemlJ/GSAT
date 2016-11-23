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
    String result = "";
    for (String string : list) {
      result += string + ", ";
    }
    String expected = "d|5, ";
    assertTrue(result.equals(expected));
  }

  /**
   * Test for correct deletion at end;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesDeletEnd() {
    LinkedList<String> list = DNAUtils.reportDifferences("hellox", "hello");
    String result = "";
    for (String string : list) {
      result += string + ", ";
    }
    String expected = "d|6, ";
    assertTrue(result.equals(expected));
  }

  /**
   * Test for correct insertion;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsert() {
    LinkedList<String> list = DNAUtils.reportDifferences("helo", "hello");
    String result = "";
    for (String string : list) {
      result += string + ", ";
    }
    String expected = "i|3, ";
    assertTrue(result.equals(expected));
  }

  /**
   * Test for correct insertion at end;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsertEnd() {
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
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsertBegin() {
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
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesSubstitute() {
    LinkedList<String> list = DNAUtils.reportDifferences("helxo", "hello");
    String result = "";
    for (String string : list) {
      result += string + ", ";
    }
    String expected = "s|l|4, ";
    System.out.println(result);
    assertTrue(result.equals(expected));
  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * correct codonstrings
   * @author bluemlj
   */
  @Test
  public void codonsToAminoAcidsOnCorrectUse() {
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
  public void codonsToAminoAcidsWithNotNukleotideString() {
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
  public void codonsToAminoAcidsWithToShortString() {
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
  public void codonsToAminoAcidsWithEmptyString() {
   String testString = "";
    String result = DNAUtils.codonsToAminoAcids(testString);
    assertTrue(result.equals("empty nukleotides"));
    
  }
  
  

}
