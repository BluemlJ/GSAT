package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.Sequence;
import analysis.StringAnalysis;
import exceptions.CorruptedSequenceException;
import exceptions.DissimilarGeneException;
import exceptions.UndefinedTypeOfMutationException;

/**
 * This class tests the behavior of the analysis parts of the project.
 *
 */
public class AnalysisTests {

  /**
   * 
   */
  @Test
  public void checkReverseAndComplementary() {
    Gene gena = new Gene("ATGCCCCACCCCTAA", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("AATCCCCACCCCGTA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      StringAnalysis.checkComplementAndReverse(testSeq);
    } catch (CorruptedSequenceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    assertTrue(testSeq.getSequence().equals("ATGCCCCACCCCTAA"));

  }



  /**
   * @throws CorruptedSequenceException
   * 
   */
  @Test
  public void checkReverseAndComplementary2() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGGGACCCGGTTAA", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq =
        new AnalysedSequence(gena.getComplementarySequence(), "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    StringAnalysis.checkComplementAndReverse(testSeq);

    assertTrue(testSeq.getSequence().equals("ATGGGACCCGGTTAA"));

  }

  /**
   * @throws CorruptedSequenceException
   * 
   */
  @Test
  public void checkReverseAndComplementaryUnusual() throws CorruptedSequenceException {
    Gene gena = new Gene("AAAAAA", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("AAAAAA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    StringAnalysis.checkComplementAndReverse(testSeq);

    assertTrue(testSeq.getSequence().equals("AAAAAA"));

  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * correct codonstrings
   * 
   * ATT = I, GGG = G, CCC = P
   * 
   * @author bluemlj
   * @throws CorruptedSequenceException
   */
  @Test

  public void codonsToAminoAcidsOnCorrectUse() throws CorruptedSequenceException {

    String testA = "ATTGGGCCCATT";
    String result = StringAnalysis.codonsToAminoAcids(testA);
    assertTrue(result.equals("IGPI"));
  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * correct codonstrings
   * 
   * ATG = M, CAA = Q, ATT = I, GTC = V, CTG = L, TGC = C
   * 
   * @author bluemlj
   * @throws CorruptedSequenceException
   */
  @Test

  public void codonsToAminoAcidsOnCorrectUse2() throws CorruptedSequenceException {

    String testA = "ATGCAAATTGTCCTGTGCCAACTG";
    String result = StringAnalysis.codonsToAminoAcids(testA);
    assertTrue(result.equals("MQIVLCQL"));
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
    String result = StringAnalysis.codonsToAminoAcids(testString);
    assertTrue(result.equals("empty nucleotides"));

  }

  /**
   * Test if the convention from codons to amino acid (shortform) is correct, if the user uses
   * uncorrect codonstrings (with wrong nukleotides) and gets the correct error String
   * 
   * @see analysis.MutationAnalysis
   * @author bluemlj
   */
  @Test
  public void codonsToAminoAcidsWithNotNukleotideString() throws CorruptedSequenceException {
    String testString = "HNOFClBrI";

    assertTrue(StringAnalysis.codonsToAminoAcids(testString).contains("X"));

  }



  /********************
   * Test for reportDifferences()
   ************************************/

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
    String result = StringAnalysis.codonsToAminoAcids(testString);
    assertTrue(result.equals("nucleotides not modulo 3, so not convertable"));

  }

  /**
   * test the helpermethod appentString for coreckt lenght of the result
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findDifrencesInsertionTest() {
    String result = MutationAnalysis.reportDifferences("hallo", "hallxo").getFirst();
    String expected = "i|4|x|";
    assertEquals(expected, result);
  }

  @Test
  public void findStopcodon1() {
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGUUAUUUCCCTAACCCCCCC", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findStopcodonPosition(testSeq);
    System.out.println(tmp);
    assertTrue(tmp == 4);
  }

  @Test
  public void findStopcodon2() {
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGUUAUUUCCCTAACCCCCCCTAA", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findStopcodonPosition(testSeq);
    System.out.println(tmp);
    assertTrue(tmp == 4);
  }

  @Test
  public void findStopcodon3() {
    AnalysedSequence testSeq = new AnalysedSequence("ATGUUAUUUCCCCCC", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findStopcodonPosition(testSeq);
    System.out.println(tmp);
    assertTrue(tmp == -1);
  }

  /**
   * 
   */
  @Test
  public void reverseQuality() {
    int[] Quality = {0, 1, 2, 3, 4};
    int[] expect = {4, 3, 2, 1, 0};
    AnalysedSequence toTest = new AnalysedSequence("AAA", "jannis", "TEST", Quality);
    toTest.reverseQuality();
    for (int i = 0; i < expect.length; i++) {
      assertTrue(toTest.getQuality()[i] == expect[i]);
    }

  }

  /**
   * 
   */
  @Test
  public void reverseQuality2() {
    int[] Quality = {0, 0, 0, 0, 100, 100, 100, 100, 100, 0, 0, 0, 0, 100};
    int[] expect = {100, 0, 0, 0, 0, 100, 100, 100, 100, 100, 0, 0, 0, 0};
    AnalysedSequence toTest = new AnalysedSequence("AAA", "jannis", "TEST", Quality);
    toTest.reverseQuality();
    for (int i = 0; i < expect.length; i++) {
      assertTrue(toTest.getQuality()[i] == expect[i]);
    }

  }

  /**
   * 
   */
  @Test
  public void reverseQualityUnusual() {
    int[] Quality = null;
    AnalysedSequence toTest = new AnalysedSequence("AAA", "jannis", "TEST", Quality);
    toTest.reverseQuality();
    assertTrue(toTest.getQuality().length == 0);
  }

  /**
   * Checks if the robust gene sequence setting works.
   */
  @Test
  public void robustGeneTest1() {
    Gene gene = new Gene("ATC GATCG ATCG" + System.lineSeparator() + " ATC ", 0, null, null);
    assertEquals("ATCGATCGATCGATC", gene.getSequence());
  }

  /**
   * Checks if the robust gene sequence setting works.
   */
  @Test
  public void robustGeneTest2() {
    Gene gene =
        new Gene("A	TGCGC	TCGC " + System.lineSeparator() + "A			A", 0, null, null);
    assertEquals("ATGCGCTCGCAA", gene.getSequence());
  }


  /**
   * Does the gene setting work even with a sequence which only contains whitespace characters?
   */
  @Test
  public void robustGeneTestUnusual() {
    Gene gene = new Gene("   			" + System.lineSeparator(), 0, null, null);
    assertTrue(gene.getSequence().isEmpty());
  }

  /**
   * test the helpermethod appentString for coreckt lenght of the result
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void stringAppentTest() {
    assertTrue(StringAnalysis.appentStringToLength("hallo", 10).length() == 10);
  }

  /**
   * This test checks findBestMatch with a half gene DEPRICATED
   * 
   * @author Kevin Otto
   */
  @Test
  public void testBestMatchIncompleteSequence() {
    String original = "halloWieGehts".toLowerCase();
    String sequence = "XXXXHalloWie".toLowerCase();
    String bestFit = "halloWie".toLowerCase();
    String result = StringAnalysis.findBestMatch(sequence, original).second;
    // System.out.println(result);
    assertTrue(bestFit.equals(result));
  }

  /**
   * This test checks that find best match is not overfitting DEPRICATED
   * 
   * @author Kevin Otto
   */
  @Test
  public void testBestMatchOverfit() {
    String original = "halloWieGehts".toLowerCase();
    String falseFit = "HalloWieXXXXhalloWieGehhtsABABABHALLOWieABAB".toLowerCase();
    String bestFit = "halloWieGehhts".toLowerCase();
    String result = StringAnalysis.findBestMatch(falseFit, original).second;
    assertTrue(bestFit.equals(result));
  }

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
    String expected = "d|5|l|, ";
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
    String expected = "d|6|x|, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test with empty String; using the example "" -> "hello" expecting 5 insertions (User Story 007,
   * special case 3)
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDiferencesEmpty() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    // System.out.println(result);
    String expected = "i|0|h|, i|0|e|, i|0|l|, i|0|l|, i|0|o|, ";
    // System.out.println(result);
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion; using the example "helo" -> "hello" with insertion of 'l' at
   * possition 3
   * 
   * (User Story 007, typical behavior 1)
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

    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test for correct insertion at begin; using the example "ello" -> "hello" with insertion of 'h'
   * at possition 0 (User Story 007, special case 2)
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
   * Test for correct insertion at end; using the example "hell" -> "hello" with insertion of 'o' at
   * possition 4 (User Story 007, special case 1)
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
   * Test for correct substitution; using the example "helxo" -> "hello" with substitution of 'x' to
   * 'l' at possition 4 (User Story 007, typical behavior 2)
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
   * Test if FindingGene finds the right Gene
   * 
   * @author bluemlj
   * @throws DissimilarGeneException
   */
  @Test(expected = DissimilarGeneException.class)
  public void testFindingGene() throws DissimilarGeneException {
    Gene gena = new Gene("hallo", 0, "testGen1", "Jannis");
    Gene genb = new Gene("bonjour", 1, "testGen1", "Jannis");
    Gene genc = new Gene("ola", 2, "testGen1", "Jannis");
    LinkedList<Gene> testDatabase = new LinkedList<>();
    testDatabase.add(gena);
    testDatabase.add(genb);
    testDatabase.add(genc);
    AnalysedSequence testSeq = new AnalysedSequence("hello", "Jannis", "toAnalyse", null);
    AnalysedSequence testSeq2 = new AnalysedSequence("ola", "Jannis", "toAnalyse", null);
    AnalysedSequence testSeq3 = new AnalysedSequence("mochi", "Jannis", "toAnalyse", null);

    Gene result = StringAnalysis.findRightGene(testSeq, testDatabase);
    assertTrue(result.getId() == (gena.getId()));

    result = StringAnalysis.findRightGene(testSeq2, testDatabase);
    assertTrue(result.getId() == (genc.getId()));

    result = StringAnalysis.findRightGene(testSeq3, testDatabase);
    fail();
  }

  @Test
  /**
   * @JANNIS TODO beschreibung
   * @throws CorruptedSequenceException
   */
  public void testFindingMultipleMutations() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGUUUCCCCAACCCCCA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGUUAUUUCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);

      // System.out.println(testSeq.getMutations().get(1));
      assertTrue(testSeq.getMutations().getFirst().equals("+1L1"));
      assertTrue(testSeq.getMutations().get(1).equals("-1Q4"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  /**
   * @JANNIS TODO beschreibung
   * @throws CorruptedSequenceException
   */
  public void testFindingMultipleMutations2() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGUUUCCCCAA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGUUACCA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);

      assertTrue(testSeq.getMutations().getFirst().equals("F2L"));
      assertTrue(testSeq.getMutations().get(1).equals("CCC3CCA"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  /**
   * @JANNIS TODO beschreibung
   * 
   * @throws CorruptedSequenceException
   */
  public void testFindingMutationOnEmptySequence() throws CorruptedSequenceException {
    Gene gena = new Gene("GGGGGGGGGGGGGGGGGATGGGGGGGGGGG", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().isEmpty());
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testFindingRightGeneOnCorrectUse() throws DissimilarGeneException {
    AnalysedSequence testA = new AnalysedSequence("AGGGT", "Jannis", "testA", null);
    Gene testGeneA = new Gene("AGGGC", 0, "testGeneA", "Jannis");
    Gene testGeneB = new Gene("AGTTTTTGGC", 1, "testGeneB", "Jannis");
    Gene testGeneC = new Gene("AGCCTCTCTCTCTGGC", 2, "testGeneC", "Jannis");
    LinkedList<Gene> testGenes = new LinkedList<>();
    testGenes.add(testGeneA);
    testGenes.add(testGeneB);
    testGenes.add(testGeneC);

    Gene result = StringAnalysis.findRightGene(testA, testGenes);

    assertTrue(result == testGeneA);
  }


  @Test(expected = DissimilarGeneException.class)
  public void testFindingRightGeneOnIncorrectUse() throws DissimilarGeneException {
    AnalysedSequence testA = new AnalysedSequence("BBBBCKCKCKCKCS", "Jannis", "testA", null);
    Gene testGeneA = new Gene("AGGGC", 0, "testGeneA", "Jannis");
    Gene testGeneB = new Gene("AGTTTTTGGC", 1, "testGeneB", "Jannis");
    Gene testGeneC = new Gene("AGCCTCTCTCTCTGGC", 2, "testGeneC", "Jannis");
    LinkedList<Gene> testGenes = new LinkedList<>();
    testGenes.add(testGeneA);
    testGenes.add(testGeneB);
    testGenes.add(testGeneC);

    Gene result = StringAnalysis.findRightGene(testA, testGenes);
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
  @Test
  public void testGetComplementarySequenceWithError() throws CorruptedSequenceException {
    Sequence seq = new Gene("AATTCCFGATCG", 0, "Problem", null);
    assertTrue(seq.getComplementarySequence().contains("X"));
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

  /**
   * This test checks the Levenshtein algorythm by putting in one empty and one normal String
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void testLevenshteinAsymetric() {
    String first = "";
    String second = "Hallo";

    int[][] levenMatrix = StringAnalysis.calculateLevenshteinMatrix(first, second);

    int[][] resultMatrix = new int[][] {{0, 1, 2, 3, 4, 5}};
    assertTrue(Arrays.deepEquals(levenMatrix, resultMatrix));
  }

  /**
   * This test checks the Levenshtein algorythm with empty Strings
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void testLevenshteinEmpty() {

    int[][] levenMatrix = StringAnalysis.calculateLevenshteinMatrix("", "");

    assertTrue(levenMatrix.length == 1);
    assertTrue(levenMatrix[0].length == 1);
    assertTrue(levenMatrix[0][0] == 0);
  }

  @Test
  public void testSilentMutationFinding() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGUUAGGGCCC", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGUUGGGGCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("UUA2UUG"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testSilentMutationFinding2() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGCAAGTTCTAGGGCCC", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGCAAGTCCTAGGGCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("GTT3GTC"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  /**
   * @JANNIS TODO beschreibung
   * @throws CorruptedSequenceException
   */
  public void testsimpleDeletionFinding() throws CorruptedSequenceException {
    Gene gena = new Gene("UAUUUUUAUCCCCCC", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("UAUUAUCCCCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);

      assertTrue(testSeq.getMutations().getFirst().equals("-1F2"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  /**
   * @JANNIS TODO beschreibung
   * @throws CorruptedSequenceException
   */
  public void testsimpleDeletionFinding2() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGUUCUUAUUUTAA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGUUCUUUTAA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("-1L3"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testsimpleInsertionFinding() throws CorruptedSequenceException {
    Gene gena = new Gene("UAUUAU", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("UAUUUCUAU", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("+1F1"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testsimpleInsertionFinding2() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGCCCAAATAA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGCCCGGGAAATAA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("+1G2"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  @Test
  public void testsimpleSubstitutionFinding() throws CorruptedSequenceException {
    Gene gena = new Gene("UUUUUUUUU", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("UUUUUAUUU", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("F2L"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  @Test
  public void testsimpleSubstitutionFinding2() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGCCCCACCCCTAA", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGCCCCCCCCCTAA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    try {
      MutationAnalysis.findMutations(testSeq);
      assertTrue(testSeq.getMutations().getFirst().equals("H3P"));
    } catch (UndefinedTypeOfMutationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



}
