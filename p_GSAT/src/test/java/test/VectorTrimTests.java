package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
    String test = StringAnalysis.findBestMatchFast("XXhalloXX", "hallo").value;
    String expected = "hallo";
    assertTrue(expected.equals(test));
  }
  
  
  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastMutatedTest() {
    String test = StringAnalysis.findBestMatchFast("XXhalAoXX", "hallo").value;
    String expected = "halAo";
    assertTrue(expected.equals(test));
  }
  
  
  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastDelTest() {
    String test = StringAnalysis.findBestMatchFast("XXhalloWieGetsXX", "halloWieGehts").value;
    String expected = "halloWieGets";

    assertTrue(expected.equals(test));
  }
  
  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastInsertTest() {
    String test = StringAnalysis.findBestMatchFast("XXhalAloXX", "hallo").value;
    String expected = "halAlo";
    assertTrue(expected.equals(test));
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastEndMissingTest() {
    String test = StringAnalysis.findBestMatchFast("XXhallowie", "hallowiegehts").value;
    String expected = "hallowie";
    assertTrue(expected.equals(test));
  }

  /**
   * 
   * 
   * @author Kevin Otto
   */
  @Test
  public void findBestmatchFastBeginMissingTest() {
    
    String test = StringAnalysis.findBestMatchFast("wiegehts", "hallowiegehts").value;
    String expected = "wiegehts";

    assertTrue(expected.equals(test));
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
    
    Double time = (double) System.nanoTime();
    StringAnalysis.findBestMatchFast(randomSequence, randomgen);
    System.out.println(((System.nanoTime()-time))/1000000000.0);
  }

  public static String getRandomGen(String Sequence){
    
    return Sequence.substring(234,1830);
  }
  
  public static String getRandomSequence(){
      String sequence = "";
      for (int i = 0; i < 2000; i++) {
        int rand = (int) (Math.random()*3);
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
