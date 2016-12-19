package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.StringAnalysis;

/**
 * This Class Tests the behavior of all Vector Trimming related Methods
 * 
 * @author Kevin
 *
 */
public class VectorTrimTests {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @Before
  public void suppressOutput() {
     System.setOut(new PrintStream(outContent));
     System.setErr(new PrintStream(errContent));
  }

  @After
  public void cleanUpStreams() {
     System.setOut(null);
     System.setErr(null);
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorBasicTest() {
    AnalysedSequence sequence =
        new AnalysedSequence("XXhallo3wieGehts3dirheute3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "hallo3wieGehts3dirheute3";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorMutatedTest() {
    AnalysedSequence sequence =
        new AnalysedSequence("XXhalAo3wieGehts3dirheute3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "halAo3wieGehts3dirheute3";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDelitionTest() {
    AnalysedSequence sequence =
        new AnalysedSequence("XXhallo3Gehts3dirheute3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hallo3Gehts3dirheute3";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorInsertTest() {
    AnalysedSequence sequence =
        new AnalysedSequence("XXhallo3wiewieGehts3dirheute3XXX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hallo3wiewieGehts3dirheute3";
    
    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDammagedBeginTest() {
    AnalysedSequence sequence =
        new AnalysedSequence("XXhaXlo3wieGehts3dirheute3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "haXlo3wieGehts3dirheute3";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDammagedEND() {
    AnalysedSequence sequence =
        new AnalysedSequence("XXXhallo3wieGehts3dirheute3ERDXYX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3wieGehts3dirheute3END", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hallo3wieGehts3dirheute3ERD";
    
    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDammagedBeginAndEND() {
    AnalysedSequence sequence =
        new AnalysedSequence("XYXBELhallo3wieGehts3dirheute3ERDASDF", null, null, null, 0.0);
    Gene gen = new Gene("BEGhallo3wieGehts3dirheute3END", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "BELhallo3wieGehts3dirheute3ERD";


    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorEndMissingTest() {
    AnalysedSequence sequence = new AnalysedSequence("XXhallow", null, null, null, 0.0);
    Gene gen = new Gene("hallowiegeht", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "hallow";



    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorBeginMissingTest() {
    AnalysedSequence sequence =
        new AnalysedSequence("wieGehts3dirheute3ABCABCENDYXYX", null, null, null, 0.0);
    Gene gen = new Gene("BEGhallo3wieGehts3dirheute3ABCABCEND", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();

    String expected = "wieGehts3dirheute3ABCABCEND";
    assertTrue(expected.equals(test));
    assertEquals(9, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorTimingTest() {

    AnalysedSequence randomSequence = getRandomSequence();
    Gene randomgen = getRandomGen(randomSequence);

    Double time = (double) System.nanoTime();

    StringAnalysis.trimVector(randomSequence, randomgen);
    time = ((System.nanoTime() - time)) / 1000000000.0;// calsulate time needet and convert to
                                                       // seconds

    assertTrue(time < 5);
  }


  /**
   * generates a random gene out of a rendom sequence
   * 
   * @param Sequence
   * @return
   */
  public static Gene getRandomGen(AnalysedSequence Sequence) {
    int end = Sequence.length() - (int) (Math.random() * Sequence.length() / 4);
    end -= end % 3;
    int begin = (int) (Math.random() * Sequence.length() / 4);
    begin += (3 - (begin % 3));
    Sequence.setOffset(begin);
    return new Gene(Sequence.getSequence().substring(begin, end), 0, "FN", "Coincidence");
  }

  /**
   * generates a random sequence WARNING no qualities are set
   * 
   * @return
   */
  public static AnalysedSequence getRandomSequence() {
    StringBuilder sequenceBuilder = new StringBuilder("");
    for (int i = 0; i < 2000; i++) {
      int rand = (int) (Math.random() * 4);
      sequenceBuilder.append(getRandomNucleotide(rand));
    }
    AnalysedSequence seq =
        new AnalysedSequence(sequenceBuilder.toString(), "Coincidence", "FN", null, 0.0);
    seq.setReferencedGene(getRandomGen(seq));
    seq.setSequence(randomMutation(seq.getSequence(), seq.getOffset(),
        seq.getOffset() + seq.getReferencedGene().getSequence().length()));
    return seq;
  }

  /**
   * returns a rendom nucleotide (as char) 0 = A 1 = T 2 = C 3 = G everything else will result in an
   * error 'e'
   * 
   * @param id
   * @return
   */
  public static char getRandomNucleotide(int id) {
    switch (id) {
      case 0:
        return 'A';
      case 1:
        return 'T';
      case 2:
        return 'C';
      case 3:
        return 'G';
      default:
        System.err.println("ERROR in random Gen");
        return 'e';
    }
  }

  /**
   * randomly mutates the given sequence in range from begin to and it puts up to 10 mutation into
   * one sequence
   * 
   * @param sequence
   * @param begin
   * @param end
   * @return
   */
  public static String randomMutation(String sequence, int begin, int end) {
    int numMutations = (int) ((Math.random() * 10) + 1);
    StringBuilder sequenceBuilder = new StringBuilder(sequence);
    for (int i = 0; i < numMutations; i++) {
      int index = begin + ((int) (Math.random() * (sequence.length() - (begin + end + 1))));
      sequenceBuilder.setCharAt(index, getRandomNucleotide((int) (Math.random() * 4)));

    }
    return sequenceBuilder.toString();
  }

}
