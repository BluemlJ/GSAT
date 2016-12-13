package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.MutationAnalysis;
import analysis.Gene;
import analysis.Sequence;
import analysis.StringAnalysis;
import exceptions.CorruptedSequenceException;
import exceptions.UndefinedTypeOfMutationException;

/**
 * This class tests the behavior of the analysis parts of the project.
 *
 */
public class AnalysisTests {
  

  
  
  
  /**
   * This test checks that find best match is not overfitting
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void StringAppentTest() {
    assertTrue(StringAnalysis.appentStringToLength("hallo", 10).length() == 10);
  }

  /**
   * This test checks that find best match is not overfitting
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void testBestMatchOverfit() {
    String original = "halloWieGehts".toLowerCase();
    String falseFit = "HalloWieXXXXhalloWieGehhtsABABABHALLOWieABAB".toLowerCase();
    String bestFit = "halloWieGehhts".toLowerCase();
    String result = StringAnalysis.findBestMatch(falseFit, original).value;
    assertTrue(bestFit.equals(result));
  }
  
  /**
   * This test checks findBestMatch with a half gene
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void testBestMatchIncompleteSequence() {
    String original = "halloWieGehts".toLowerCase();
    String sequence = "XXXXHalloWie".toLowerCase();
    String bestFit = "halloWie".toLowerCase();
    String result = StringAnalysis.findBestMatch(sequence, original).value;
    //System.out.println(result);
    assertTrue(bestFit.equals(result));
  }

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

    int[][] levenMatrix = StringAnalysis.calculateLevenshteinMatrix(first, second);
    // int[][] resultMatrix = new int[][]
    // {{0,1,2,3,4,5,6},{1,1,2,3,4,5,6},{2,2,1,2,3,4,5},{3,3,2,1,2,3,4},{4,4,3,2,1,2,3},{5,5,4,3,2,2,3},{6,6,5,4,3,3,2},{7,7,6,5,4,4,3}};
    int[][] resultMatrix = new int[][] {{0, 1, 2, 3, 4, 5, 6, 7}, {1, 1, 2, 3, 4, 5, 6, 7},
        {2, 2, 1, 2, 3, 4, 5, 6}, {3, 3, 2, 1, 2, 3, 4, 5}, {4, 4, 3, 2, 1, 2, 3, 4},
        {5, 5, 4, 3, 2, 2, 3, 4}, {6, 6, 5, 4, 3, 3, 2, 3}};
    assertTrue(Arrays.deepEquals(levenMatrix, resultMatrix));
  }

  /********************
   * Test for reportDifferences()
   ************************************/

  /**
   * Test for correct deletion;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesDelet() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("helllo", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "d|5|l, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct deletion at end;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesDeletEnd() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("hellox", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "d|6|x, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsert() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("helo", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "i|3|l|, ";
    // System.out.println(result);
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion at end;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsertEnd() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("hell", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "i|4|o|, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion at begin;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesInsertBegin() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("ello", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "i|0|h|, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct substitution;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesSubstitute() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("helxo", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "s|4|l|x, ";
    // System.out.println(result);
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * correct codonstrings
   * 
   * @author bluemlj
   * @throws CorruptedSequenceException
   */
  @Test

  public void codonsToAminoAcidsOnCorrectUse() throws CorruptedSequenceException {

    String testA = "ATTGGGCCCATT";
    String result = MutationAnalysis.codonsToAminoAcids(testA);
    assertTrue(result.equals("IGPI"));
  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * uncorrect codonstrings (with wrong nukleotides) and gets the correct error String
   * 
   * @see analysis.MutationAnalysis
   * @author bluemlj
   */
  @Test(expected = CorruptedSequenceException.class)
  public void codonsToAminoAcidsWithNotNukleotideString() throws CorruptedSequenceException {
    String testString = "HNOFClBrI";
    String result = MutationAnalysis.codonsToAminoAcids(testString);

  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * codonstrings, that cant be correct condon string (%3) and gets the correct error message
   * 
   * @see analysis.MutationAnalysis
   * @author bluemlj
   */
  @Test

  public void codonsToAminoAcidsWithToShortString() throws CorruptedSequenceException {
    String testString = "ACTTTGG";
    String result = MutationAnalysis.codonsToAminoAcids(testString);
    assertTrue(result.equals("nucleotides not modulo 3, so not convertable"));

  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses empty
   * codonstrings and gets the correct error
   * 
   * @see analysis.MutationAnalysis
   * @author bluemlj
   */
  @Test

  public void codonsToAminoAcidsWithEmptyString() throws CorruptedSequenceException {

    String testString = "";
    String result = MutationAnalysis.codonsToAminoAcids(testString);
    assertTrue(result.equals("empty nucleotides"));

  }

  /**
   * Test if FindingGene finds the right Gene
   * 
   * @author bluemlj
   */
  @Test
  public void testFindingGene() {
    Gene gena = new Gene("hallo", 0, "testGen1", "Jannis");
    Gene genb = new Gene("bonjour", 1, "testGen1", "Jannis");
    Gene genc = new Gene("ola", 2, "testGen1", "Jannis");
    LinkedList<Gene> testDatabase = new LinkedList<>();
    testDatabase.add(gena);
    testDatabase.add(genb);
    testDatabase.add(genc);
    AnalysedSequence testSeq = new AnalysedSequence("hello", "Jannis", "toAnalyse", null, 0);
    AnalysedSequence testSeq2 = new AnalysedSequence("ola", "Jannis", "toAnalyse", null, 0);
    AnalysedSequence testSeq3 = new AnalysedSequence("mochi", "Jannis", "toAnalyse", null, 0);

    Gene result = MutationAnalysis.findRightGene(testSeq, testDatabase);
    assertTrue(result.getId() == (gena.getId()));

    result = MutationAnalysis.findRightGene(testSeq2, testDatabase);
    assertTrue(result.getId() == (genc.getId()));

    result = MutationAnalysis.findRightGene(testSeq3, testDatabase);
    assertTrue(result == null);

  }

  @Test
  public void testsimpleFindingMutations() {
    Gene gena = new Gene("UUUUUUUUU", 0, "testGen1", "Jannis");
    Gene genb = new Gene("UUUUUUUUC", 1, "testGen1", "Jannis");
    Gene genc = new Gene("ola", 2, "testGen1", "Jannis");
    Gene gend = new Gene("HalloWieGehts", 3, "testgen1", "Jannis");
    LinkedList<Gene> testDatabase = new LinkedList<>();
    testDatabase.add(gena);
    testDatabase.add(genb);
    testDatabase.add(genc);
    testDatabase.add(gend);
    AnalysedSequence testSeq = new AnalysedSequence("UUUUUCUUU", "Jannis", "toAnalyse", null, 0);
    AnalysedSequence testSeq2 = new AnalysedSequence("olla", "Jannis", "toAnalyse", null, 0);
    AnalysedSequence testSeq3 = new AnalysedSequence("UUUUUUUC", "Jannis", "toAnalyse", null, 0);
    AnalysedSequence testSeq4 = new AnalysedSequence("HaloWieGeGtEs", "Jannis", "toAnalyze", null, 0);
    testSeq.setReferencedGene(gena);
    testSeq2.setReferencedGene(genc);
    testSeq3.setReferencedGene(genb);
    testSeq4.setReferencedGene(gend);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("U6C"));

      MutationAnalysis.findMutations(testSeq2);
      System.out.println(testSeq2.getMutations().getFirst());
      assertTrue(testSeq2.getMutations().getFirst().equals("+1l2"));

      MutationAnalysis.findMutations(testSeq3);
      assertTrue(testSeq3.getMutations().getFirst().equals("-1U8"));
      
      MutationAnalysis.findMutations(testSeq4);
      assertTrue(testSeq4.getMutations().size()==3);
      assertTrue(testSeq4.getMutations().getFirst().equals("-1l4"));
      assertTrue(testSeq4.getMutations().get(1).equals("h11G"));
      assertTrue(testSeq4.getMutations().get(2).equals("+1E12"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // TODO change these four to correct values! (NEW CONSTRUCTOR!)
  // AnalyzedSequence testA = new AnalyzedSequence("AGGGT", "testA", "FFFF");
  // Gene testGeneA = new Gene("AGGGC", "testGeneA");
  // Gene testGeneB = new Gene("AGTTTTTGGC", "testGeneB");
  // Gene testGeneC = new Gene("AGCCTCTCTCTCTGGC", "testGeneC");
  LinkedList<Gene> testGenes = new LinkedList<>();

  /*
   * public void testFindingRightGeneOnCorrectUse(){ testGenes.add(testGeneA);
   * testGenes.add(testGeneB); testGenes.add(testGeneC);
   * 
   * Gene result = DNAUtils.findRightGene(testA,testGenes);
   * System.out.println(result.getSequence()); assertTrue(result == testGeneA); }
   * 
   * verschoben bis checksimilarity vorhanden TODO wieder nutzen!
   */

  /**
   * This test checks if a sequence is correctly reversed.
   * 
   * @throws CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testGetReversedSequence() {

    Sequence seq = new Gene("", 0, "Test1", null);
    assertEquals("", seq.getReversedSequence());

    Sequence seq2 = new Gene("TTT", 0, "Test2", null);
    assertEquals("TTT", seq2.getReversedSequence());

    Sequence seq3 = new Gene("ATCGATCGATCG", 0, "Test3", null);
    assertEquals("GCTAGCTAGCTA", seq3.getReversedSequence());

    Sequence seq4 = new Gene("GGGTACCGTGTAGG", 0, "Test4", null);
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

  public void testGetComplementarySequenceNoError() throws CorruptedSequenceException {

    Sequence seq = new Gene("", 0, "Test1", null);
    assertEquals("", seq.getComplementarySequence());

    Sequence seq2 = new Gene("AAA", 0, "Test2", null);
    assertEquals("TTT", seq2.getComplementarySequence());

    Sequence seq3 = new Gene("AATTCCGGATCG", 0, "Test3", null);
    assertEquals("TTAAGGCCTAGC", seq3.getComplementarySequence());

    Sequence seq4 = new Gene("ATGCTAGCTAGCCCC", 0, "Test4", null);
    assertEquals("TACGATCGATCGGGG", seq4.getComplementarySequence());
  }

  /**
   * This test checks whether an exception is thrown if a sequence's complementary sequence is
   * build. It's important to throw the exception and not to hide the error.
   * 
   * @throws CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test(expected = CorruptedSequenceException.class)

  public void testGetComplementarySequenceWithError() throws CorruptedSequenceException {

    Sequence seq = new Gene("AATTCCFGATCG", 0, "Problem", null);
    String comp = seq.getComplementarySequence();

    fail("No exception thrown!");
  }

}
