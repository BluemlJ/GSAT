package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  /**
   * generates a random gene out of a rendom sequence
   * 
   * @param Sequence
   * @return
   */
  public static Gene getRandomGene(AnalysedSequence Sequence) {
    int end = Sequence.length() - (int) (Math.random() * Sequence.length() / 4);
    end -= end % 3;
    int begin = (int) (Math.random() * Sequence.length() / 4);
    begin += (3 - (begin % 3));
    Sequence.setOffset(begin);
    return new Gene(Sequence.getSequence().substring(begin, end), 0, "FN", "Coincidence");
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
    String seqString = sequenceBuilder.toString();
    int[] qualities = new int[seqString.length()];
    AnalysedSequence seq = new AnalysedSequence(seqString, "Coincidence", "FN", qualities);
    seq.setReferencedGene(getRandomGene(seq));
    seq.setSequence(randomMutation(seq.getSequence(), seq.getOffset(),
        seq.getOffset() + seq.getReferencedGene().getSequence().length()));
    return seq;
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

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorBasicTest() {
    String seq = "XXhallo3wieGehts3dirheute3";
    int[] qualities = new int[seq.length()];

    AnalysedSequence sequence = new AnalysedSequence(seq, "res", "N", qualities);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "hallo3wieGehts3dirheute3";

    System.out.println(test);
    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorBeginMissingTest() {
    String seq = "wieGehts3dirheute3ABCABCENDendeaufgefueltumdasgenlangerzumachen";
    int[] qualities = new int[seq.length()];

    AnalysedSequence sequence = new AnalysedSequence(seq, "res", "N", qualities);
    Gene gen = new Gene("BEGhallo3wieGehts3dirheute3ABCABCENDendeaufgefueltumdasgenlangerzumachen",
        0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();

    System.out.println();
    System.out.println(test);
    System.out.println("Offset = " + sequence.getOffset());
    String expected = "wieGehts3dirheute3ABCABCENDendeaufgefueltumdasgenlangerzumachen";
    System.out.println(expected);

    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(9, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDamagedBeginAndEnd() {

    String seq = "XYXBELhallo3wieGehts3dirheute3mirgehtesgutERD";
    int[] qualities = new int[seq.length()];

    AnalysedSequence sequence = new AnalysedSequence(seq, "res", "N", qualities);
    Gene gen = new Gene("BEGhallo3wieGehts3dirheute3mirgehtesgutEND", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "BELhallo3wieGehts3dirheute3mirgehtesgutERD";

    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDamagedBeginTest() {
    String seq = "XXhaXlo3wieGehts3dirheute3heuheuheuheuheuheuheuheuheuheuheuheu";
    int[] qualities = new int[seq.length()];
    AnalysedSequence sequence = new AnalysedSequence(seq, "Researcher", "NAME", qualities);
    Gene gen =
        new Gene("hallo3wieGehts3dirheute3heuheuheuheuheuheuheuheuheuheuheuheu", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "haXlo3wieGehts3dirheute3heuheuheuheuheuheuheuheuheuheuheuheu";

    System.out.println("Offset = " + sequence.getOffset());
    System.out.println(test);
    System.out.println(expected);
    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDamagedEnd() {
    String seq = "XXXhallo3wieGehts3dirheute3ERD";
    int[] qualities = new int[seq.length()];

    AnalysedSequence sequence = new AnalysedSequence(seq, "res", "N", qualities);
    Gene gen = new Gene("hallo3wieGehts3dirheute3END", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hallo3wieGehts3dirheute3ERD";
    System.err.println(test);
    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorDeletionTest() {
    String seq = "XXhallo3Gehts3dirheute3";
    int[] qualities = new int[seq.length()];

    AnalysedSequence sequence = new AnalysedSequence(seq, "res", "N", qualities);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hallo3Gehts3dirheute3";

    System.out.println(test);
    System.out.println(expected);

    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorEndMissingTest() {

    String seq = "XXhallow";
    int[] qualities = new int[seq.length()];

    AnalysedSequence sequence = new AnalysedSequence("XXhallow", "res", "N", qualities);
    Gene gen = new Gene("hallowiegeht", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "hallow";

    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorInsertTest() {

    String seq = "XXhallo3wiewieGehts3dirheute3";
    int[] qualities = new int[seq.length()];

    AnalysedSequence sequence = new AnalysedSequence(seq, "res", "N", qualities);
    Gene gen = new Gene("hallo3wieGehts3dirheute3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hallo3wiewieGehts3dirheute3";

    assertTrue(expected.toUpperCase().equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorMutatedTest() {
    String seq = "XXhalAo3wieGehts3dirheute3mirgehtesgut".toUpperCase();
    int[] qualities = new int[seq.length()];
    AnalysedSequence sequence = new AnalysedSequence(seq, "Researcher", "NAME", qualities);
    Gene gen = new Gene("hallo3wieGehts3dirheute3mirgehtesgut", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "halAo3wieGehts3dirheute3mirgehtesgut".toUpperCase();

    System.out.println(test);
    System.out.println(expected);
    System.out.println(sequence.getOffset());
    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void trimVectorTimingTest() {

    AnalysedSequence randomSequence = getRandomSequence();
    Gene randomgen = getRandomGene(randomSequence);

    Double time = (double) System.nanoTime();

    StringAnalysis.trimVector(randomSequence, randomgen);
    time = ((System.nanoTime() - time)) / 1000000000.0;// calsulate time
                                                       // needet and
                                                       // convert to
                                                       // seconds

    assertTrue(time < 5);
  }

}
