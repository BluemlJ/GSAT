package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

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
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastBasicTest() {
    AnalysedSequence sequence = new AnalysedSequence("XXhallo3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "hallo3";
    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastMutatedTest() {
    AnalysedSequence sequence = new AnalysedSequence("XXhalAo3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();
    String expected = "halAo3";
    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastDelTest() {
    AnalysedSequence sequence = new AnalysedSequence("XXhalloWieGetsXX", null, null, null, 0.0);
    Gene gen = new Gene("halloWieGehts", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "halloWieGets";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastInsertTest() {
    AnalysedSequence sequence = new AnalysedSequence("XXhalAAAlo3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "halAAAlo3";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastDammagedBegin() {
    AnalysedSequence sequence = new AnalysedSequence("XXhxllo3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hxllo3";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastDammagedEND() {
    AnalysedSequence sequence = new AnalysedSequence("XXhall33XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hall33";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastDammagedBeginAndEND() {
    AnalysedSequence sequence = new AnalysedSequence("XXhxll33YasdlkjfhY", null, null, null, 0.0);
    Gene gen = new Gene("hallo3", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);

    String test = sequence.getSequence();
    String expected = "hxll33";

    assertTrue(expected.equals(test));
    assertEquals(0, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastEndMissingTest() {
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
  public void findBestmatchFastBeginMissingTest() {
    AnalysedSequence sequence = new AnalysedSequence("wiegeh", null, null, null, 0.0);
    Gene gen = new Gene("hallo3wiegeht", 0, null, null);

    StringAnalysis.trimVector(sequence, gen);
    String test = sequence.getSequence();

    String expected = "wiegeh";
    assertTrue(expected.equals(test));
    assertEquals(6, sequence.getOffset());
  }


  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastTimingTest() {

    AnalysedSequence randomSequence = getRandomSequence();
    Gene randomgen = getRandomGen(randomSequence);



    Double time = (double) System.nanoTime();

    StringAnalysis.trimVector(randomSequence, randomgen);
    System.out.println("Time for matching:" + ((System.nanoTime() - time)) / 1000000000.0);
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findOffsetTest() {
    for (int i = 0; i < 100; i++) {

      AnalysedSequence randomSequence = getRandomSequence();
      Gene randomgene = randomSequence.getReferencedGene();

      int realOffset = -randomSequence.getOffset();
      randomSequence.setOffset(-1);
      StringAnalysis.findOffset(randomSequence);
      // System.out.println(realOffset);
      // System.err.println(randomSequence.getOffset());
      assertEquals(realOffset, randomSequence.getOffset());

      // System.out.println(StringAnalysis.appentStringToLength("", randomSequence.getOffset()) +
      // randomgene);
      // System.out.println(randomSequence);
    }
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findPositivOffsetTest() {
    // supress prints:
    // creates an print stream that does nothing on print
    PrintStream originalOUTStream = System.out;
    PrintStream originalERRStream = System.err;


    PrintStream dummyStream = new PrintStream(new OutputStream() {
      public void write(int b) {
        // NO-OP
      }
    });
    System.setOut(dummyStream);
    System.setErr(dummyStream);
    

    AnalysedSequence randomSequence = new AnalysedSequence("wiegehts", "", "", null, 0);
    Gene randomgene = new Gene("hallowiegehts", 0, "", "");
    randomSequence.setReferencedGene(randomgene);

    StringAnalysis.findOffset(randomSequence);
    // System.out.println(realOffset);
    // System.err.println(randomSequence.getOffset());
    assertEquals(5, randomSequence.getOffset());

    // System.out.println(StringAnalysis.appentStringToLength("", randomSequence.getOffset()) +
    // randomgene);
    // System.out.println(randomSequence);
    // unsupress prints
    System.setOut(originalOUTStream);
    System.setErr(originalERRStream);
  }
  
  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findNegativOffsetTest() {
    // supress prints:
    // creates an print stream that does nothing on print
    PrintStream originalOUTStream = System.out;
    PrintStream originalERRStream = System.err;

    PrintStream dummyStream = new PrintStream(new OutputStream() {
      public void write(int b) {
        // NO-OP
      }
    });
    System.setOut(dummyStream);
    System.setErr(dummyStream);
    

    AnalysedSequence randomSequence = new AnalysedSequence("ZZZZZZZwiegehts", "", "", null, 0);
    Gene randomgene = new Gene("hallowiegehts", 0, "", "");
    randomSequence.setReferencedGene(randomgene);

    StringAnalysis.findOffset(randomSequence);
    // System.out.println(realOffset);
    // System.err.println(randomSequence.getOffset());
    assertEquals(-2, randomSequence.getOffset());

    // System.out.println(StringAnalysis.appentStringToLength("", randomSequence.getOffset()) +
    // randomgene);
    // System.out.println(randomSequence);
    // unsupress prints
    System.setOut(originalOUTStream);
    System.setErr(originalERRStream);
  }


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
