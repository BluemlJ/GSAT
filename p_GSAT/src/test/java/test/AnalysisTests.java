package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.Sequence;
import analysis.StringAnalysis;
import exceptions.CorruptedSequenceException;
import exceptions.DissimilarGeneException;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.SequenceReader;

/**
 * This class tests the behavior of the analysis parts of the project.
 *
 */
public class AnalysisTests {

  /**
   * 
   */
  @Test
  public void checkReverseAndComplementary() throws CorruptedSequenceException {
    Gene gena = new Gene("ATGCCCCACCCCTAA", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("AATCCCCACCCCGTA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    StringAnalysis.checkComplementAndReverse(testSeq);

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
  public void codonsToAminoAcidsWithNotNucleotideString() throws CorruptedSequenceException {
    String testString = "CAAHNOFClBrIATG";

    assertTrue(StringAnalysis.codonsToAminoAcids(testString).equals("QXXXM"));

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
   * test if findDifferences finds insertions
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findDifferencesInsertionTest() {
    String result = MutationAnalysis.reportDifferences("hallo", "hallxo").getFirst();
    String expected = "i|4|x|";
    assertEquals(expected, result);
  }

  /**
   * test if findDifferences finds deletions at begining
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findDifferencesDeletedBeginTest() {
    String result = MutationAnalysis.reportDifferences("hallo", "allo").getFirst();
    String expected = "d|1|h|";
    assertEquals(expected, result);
  }

  @Test
  public void findHISTAG1() throws CorruptedSequenceException {
    AnalysedSequence testSeq = new AnalysedSequence("ATGUUAUUUCCCTAACCCCCCCACCACCACCACCACTAA",
        "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findHisTag(testSeq);
    assertTrue(tmp == 21);
  }

  @Test
  public void findHISTAG2() throws CorruptedSequenceException {
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGCCTCCCCACCACCACCACCACCACTAA", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findHisTag(testSeq);
    assertTrue(tmp == 9);
  }

  @Test
  public void findHISTAG3() throws CorruptedSequenceException {
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGTTATTTCCCCCCTAA", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findHisTag(testSeq);
    assertTrue(tmp == -1);
  }

  @Test
  public void findStopcodon1() {
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGTTATTTCCCTAACCCCCCC", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findStopcodonPosition(testSeq);
    assertTrue(tmp == 4);
  }

  @Test
  public void findStopcodon2() {
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGTTATTTCCCTAACCCCCCCTAA", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findStopcodonPosition(testSeq);
    assertTrue(tmp == 4);
  }

  @Test
  public void findStopcodon3() {
    AnalysedSequence testSeq = new AnalysedSequence("ATGTTATTTCCCCCC", "Jannis", "toAnalyse", null);
    int tmp = StringAnalysis.findStopcodonPosition(testSeq);
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
   * 
   * @author Ben Kohr
   */
  @Test
  public void robustGeneTest1() {
    Gene gene = new Gene("ATC GATCG ATCG" + System.lineSeparator() + " ATC ", 0, null, null);
    assertEquals("ATCGATCGATCGATC", gene.getSequence());
  }

  /**
   * Checks if the robust gene sequence setting works (II).
   * 
   * @author Ben Kohr
   */
  @Test
  public void robustGeneTest2() {
    Gene gene = new Gene("A TGCGC " + "\t" + " TCGC " + System.lineSeparator() + "A            A",
        0, null, null);
    assertEquals("ATGCGCTCGCAA", gene.getSequence());
  }

  /**
   * Does the gene setting work even with a sequence which only contains whitespace characters?
   * 
   * @author Ben Kohr
   */
  @Test
  public void robustGeneTestUnusual() {
    Gene gene = new Gene("              " + System.lineSeparator(), 0, null, null);
    assertTrue(gene.getSequence().isEmpty());
  }

  /**
   * test the helpermethod appentString for coreckt lenght of the result
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void stringAppendTest() {
    assertTrue(StringAnalysis.appendStringToLength("hallo", 10).equals("hallo     "));
    assertTrue(StringAnalysis.appendStringToLength("a", 10).equals("a         "));
    assertTrue(
        StringAnalysis.appendStringToLength("hallohallohallo", 10).equals("hallohallohallo"));
  }


  /*
   * Test for correct deletion;
   * 
   * @author Kevin Otto
   */
  @Test
  public void testDifferencesDelete() {

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
  public void testDifferencesDeleteEnd() {

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
  public void testDifferencesEmpty() {

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
  public void testDifferencesInsert() {

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
  public void testDifferencesInsertBegin() {

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
  public void testDifferencesInsertEnd() {

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
  public void testDifferencesSubstitute() {

    LinkedList<String> list = MutationAnalysis.reportDifferences("helxo", "hello");

    StringBuilder result = new StringBuilder();
    for (String string : list) {
      result.append(string + ", ");
    }
    String expected = "s|4|l|x, ";
    assertTrue(result.toString().equals(expected));
  }

  /**
   * Test if FindingGene finds the right Gene
   * 
   * @author bluemlj
   * @throws DissimilarGeneException
   */
  @Test
  public void testFindingGene() throws DissimilarGeneException {
    Gene gena = new Gene("hallo", 0, "testGen1", "Jannis");
    Gene genb = new Gene("bonjour", 1, "testGen1", "Jannis");
    Gene genc = new Gene("ola", 2, "testGen1", "Jannis");
    ArrayList<Gene> testDatabase = new ArrayList<>();
    testDatabase.add(gena);
    testDatabase.add(genb);
    testDatabase.add(genc);
    AnalysedSequence testSeq = new AnalysedSequence("hello", "Jannis", "toAnalyse", null);
    AnalysedSequence testSeq2 = new AnalysedSequence("ola", "Jannis", "toAnalyse", null);

    Gene result = StringAnalysis.findRightGene(testSeq, testDatabase);
    assertTrue(result.getId() == (gena.getId()));

    result = StringAnalysis.findRightGene(testSeq2, testDatabase);
    assertTrue(result.getId() == (genc.getId()));
  }

  @Test
  /**
   * @throws CorruptedSequenceException
   */
  public void testFindingMultipleMutations()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("ATGTTTCCCCAA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGTTATTTCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().size() == 2);
    assertTrue(testSeq.getMutations().getFirst().equals("+1L2 (TTA)"));
    assertTrue(testSeq.getMutations().get(1).equals("-1Q4"));
  }

  @Test
  /**
   * @throws CorruptedSequenceException
   */
  public void testFindingMultipleMutations2()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("ATGTTTCCCCAA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGTTACCA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().getFirst().equals("F2L (TTA)"));

  }

  @Test
  /**
   * 
   * @throws CorruptedSequenceException
   */
  public void testFindingMutationOnEmptySequence()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("GGGGGGGGGGGGGGGGGATGGGGGGGGGGG", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().isEmpty());
  }

  @Test
  public void testFindingRightGeneOnCorrectUse() {
    AnalysedSequence testA = new AnalysedSequence("AGTTGATGGC", "Jannis", "testA", null);
    Gene testGeneA = new Gene("AGGGC", 0, "testGeneA", "Jannis");
    Gene testGeneB = new Gene("AGTTTTTGGC", 1, "testGeneB", "Jannis");
    Gene testGeneC = new Gene("AGCCTCTCTCTCTGGC", 2, "testGeneC", "Jannis");
    ArrayList<Gene> testGenes = new ArrayList<>();
    testGenes.add(testGeneA);
    testGenes.add(testGeneB);
    testGenes.add(testGeneC);

    Gene result = StringAnalysis.findRightGene(testA, testGenes);

    assertTrue(result == testGeneB);
  }

  @Test
  public void testFindingRightGeneOnIncorrectUse() {
    AnalysedSequence testA = new AnalysedSequence("C", "a", "a", null);
    Gene testGeneA = new Gene("AGGGC", 0, "testGeneA", "Jannis");
    Gene testGeneB = new Gene("AGTTTTTGGC", 1, "testGeneB", "Jannis");
    Gene testGeneC = new Gene("AGCCTCTCTCTCTGGC", 2, "testGeneC", "Jannis");
    ArrayList<Gene> testGenes = new ArrayList<Gene>();
    testGenes.add(testGeneA);
    testGenes.add(testGeneB);
    testGenes.add(testGeneC);
    Gene right = StringAnalysis.findRightGene(testA, testGenes);
    assertTrue(right == testGeneA);
  }

  /**
   * This test checks whether the complementary sequence is correctly computed.
   * 
   * @author Ben Kohr
   */
  @Test

  public void testGetComplementarySequenceNoError() throws CorruptedSequenceException {

    Sequence seq = new Gene("", 0, "Test1", null);
    assertEquals("", seq.getComplementarySequence());

    Sequence seq2 = new AnalysedSequence("AAA", "test", "Test2.ab1", null);
    assertEquals("TTT", seq2.getComplementarySequence());

    Sequence seq3 = new Gene("AATTCCGGATCG", 0, "Test3", null);
    assertEquals("TTAAGGCCTAGC", seq3.getComplementarySequence());

    Sequence seq4 = new AnalysedSequence("ATGCTAGCTAGCCCC", "test", "Test4.ab1", null);
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
    Sequence seq = new Gene("AOATDTCCFGATCATCTGTHCTCIG", 0, "Problem", null);
    assertEquals("TXTAXAGGXCTAGTAGACAXGAGXC", seq.getComplementarySequence());
  }

  /**
   * This test checks if a sequence is correctly reversed.
   * 
   * @author Ben Kohr
   */
  @Test
  public void testGetReversedSequence() {

    Sequence seq = new Gene("", 0, "Test1", null);
    assertEquals("", seq.getReversedSequence());

    Sequence seq2 = new AnalysedSequence("TTT", "test", "Test2.ab1", null);
    assertEquals("TTT", seq2.getReversedSequence());

    Sequence seq3 = new Gene("ATCGATCGATCG", 0, "Test3", null);
    assertEquals("GCTAGCTAGCTA", seq3.getReversedSequence());

    Sequence seq4 = new AnalysedSequence("GGGTACCGTGTAGG", "test", "Test4.ab1", null);
    assertEquals("GGATGTGCCATGGG", seq4.getReversedSequence());

  }

  /**
   * This test checks the Levenshtein algorythm by putting in one empty and one normal String
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void testLevenshtein() {
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
  public void testSilentMutationFinding()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("ATGTTAGGGCCC", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGTTGGGGCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().size() == 1);
    assertTrue(testSeq.getMutations().getFirst().equals("TTA2TTG"));
  }

  @Test
  public void testSilentMutationFinding2()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("ATGCAAGTTCTAGGGCCC", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq =
        new AnalysedSequence("ATGCAAGTCCTAGGGCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().size() == 1);
    assertTrue(testSeq.getMutations().getFirst().equals("GTT3GTC"));
  }

  @Test
  /**
   * @throws CorruptedSequenceException
   */
  public void testsimpleDeletionFinding()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("TATTTTTATCCCCCC", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("TATTATCCCCCC", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().getFirst().equals("-1F2 (TTT)"));
    assertTrue(testSeq.getMutations().get(1).equals("+1P5"));

  }

  @Test
  /**
   * @throws CorruptedSequenceException
   */
  public void testsimpleDeletionFinding2()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("ATGTTCTTATTTTAA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGTTCTTTTAA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assert (testSeq.getMutations().getFirst().equals("-1L3 (TTA)"));

  }

  @Test
  public void testsimpleInsertionFinding()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("TATTAT", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("TATTTCTAT", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().getFirst().equals("+1F2 (TTC)"));

  }

  @Test
  public void testsimpleInsertionFinding2()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("ATGCCCAAATAA", 0, "testGen1", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGCCCGGGAAATAA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().getFirst().equals("+1G3 (GGG)"));

  }

  @Test
  public void testsimpleSubstitutionFinding()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("TTTTTTTTT", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("TTTTTATTT", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().getFirst().equals("F2L (TTA)"));

  }

  @Test
  public void testsimpleSubstitutionFinding2()
      throws CorruptedSequenceException, UndefinedTypeOfMutationException {
    Gene gena = new Gene("ATGCCCCACCCCTAA", 0, "testGenA", "Jannis");
    AnalysedSequence testSeq = new AnalysedSequence("ATGCCCCCCCCCTAA", "Jannis", "toAnalyse", null);
    testSeq.setReferencedGene(gena);

    MutationAnalysis.findMutations(testSeq);
    assertTrue(testSeq.getMutations().getFirst().equals("H3P (CCC)"));

  }



  @Test
  public void testPlasmidMix()
      throws FileReadingException, IOException, MissingPathException, IllegalSymbolException {
    SequenceReader
        .configurePath(new File("resources/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getAbsolutePath());
    AnalysedSequence testSequence = SequenceReader.convertFileIntoSequence();
    MutationAnalysis.findPlasmidMix(testSequence);
    // cut out findings due to bad quality (only focus on the good quality area)
    assertTrue(testSequence.getComments().contains("There are possible plasmidmixes at the positions 2, "));
  }

  @Test
  public void testPlasmidMix2()
      throws FileReadingException, IOException, MissingPathException, IllegalSymbolException {
    SequenceReader
        .configurePath(new File("resources/ab1/Tk_Gs40Hits/Forward/95EI64.ab1").getAbsolutePath());
    AnalysedSequence testSequence = SequenceReader.convertFileIntoSequence();
    MutationAnalysis.findPlasmidMix(testSequence);
    // cut out findings due to bad quality (only focus on the good quality area)
    assertTrue(testSequence.getComments().contains("There are possible plasmidmixes at the positions 2, "));
  }

  @Test
  public void testPlasmidMix3()
      throws FileReadingException, IOException, MissingPathException, IllegalSymbolException {
    SequenceReader
        .configurePath(new File("resources/ab1/Tk_Gs40Hits/Forward/95EI61.ab1").getAbsolutePath());
    AnalysedSequence testSequence = SequenceReader.convertFileIntoSequence();
    MutationAnalysis.findPlasmidMix(testSequence);
    // cut out findings due to bad quality (only focus on the good quality area)
    assertTrue(testSequence.getComments().contains("There are possible plasmidmixes at the positions 2, 3, 8, "));
  }

}
