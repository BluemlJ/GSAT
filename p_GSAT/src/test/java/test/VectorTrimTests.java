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
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastBasicTest() {
    AnalysedSequence sequence = new AnalysedSequence("XXhallo3XX", null, null, null, 0.0);
    Gene gen = new Gene("hallo3", 0, null, null);
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    String test = sequence.getSequence();
    String expected = "hallo3";
    //System.err.println(test);
    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    String test = sequence.getSequence();
    String expected = "halAo3";
    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
    
    StringAnalysis.findBestMatchFast(sequence, gen);

    String test = sequence.getSequence();
    String expected = "halloWieGets";

    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    
    String test = sequence.getSequence();
    String expected = "halAAAlo3";
    //System.out.println(test);
    //System.out.println(test);
    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    
    String test = sequence.getSequence();
    String expected = "hxllo3";
    
    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    
    String test = sequence.getSequence();
    String expected = "hall33";
    
    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    
    String test = sequence.getSequence();
    String expected = "hxll33";
    //System.err.println(test);
    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    String test = sequence.getSequence();
    String expected = "hallow";
    assertTrue(expected.equals(test));
    assertEquals(0,sequence.getOffset());
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
   
    StringAnalysis.findBestMatchFast(sequence, gen);
    String test = sequence.getSequence();
  
    String expected = "wiegeh";
    assertTrue(expected.equals(test));
    assertEquals(6,sequence.getOffset());
  }
  

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastTimingTest() {
    
    String randomSequence = getRandomSequence();
    String randomgen = getRandomGen(randomSequence);
    
    AnalysedSequence sequence = new AnalysedSequence(randomSequence, null, null, null, 0.0);
    Gene gen = new Gene(randomgen, 0, null, null);
   
    
    Double time = (double) System.nanoTime();
    
    StringAnalysis.findBestMatchFast(sequence, gen);
    System.out.println("Time for matching:"+((System.nanoTime()-time))/1000000000.0);
  }

  public static String getRandomGen(String Sequence){
    
    return Sequence.substring(203,1997);
  }
  
  public static String getRandomSequence(){
      String sequence = "";
      for (int i = 0; i < 2000; i++) {
        int rand = (int) (Math.random()*4);
        switch (rand) {
          case 0:
            sequence+="A";
            break;
          case 1:
            sequence+="T";
            break;
          case 2:
            sequence+="C";
            break;
          case 3:
            sequence+="G";
            break;
          default:
            System.err.println("ERROR in random Gen");
            break;
        }
      }
      return sequence;
  }

}
