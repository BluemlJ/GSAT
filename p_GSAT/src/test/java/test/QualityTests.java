package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.junit.Before;
import org.junit.Test;

import analysis.AnalysedSequence;
import exceptions.FileReadingException;
import io.SequenceReader;

public class QualityTests {
  
  AnalysedSequence testSequence;
  
  @Before
  public void initializeSequence() throws IllegalSymbolException, FileReadingException, IOException{
    // set SequenceReader file path
    SequenceReader.configurePath(
      getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
      //"./src/main/resources/ab1/Tk_Gs40Hits/Forward/95EI60.ab1");
    testSequence = SequenceReader.convertFileIntoSequence();
  }
  
  /**
   * Tests if the quality information is accessible
   */
  @Test
  public void testQualityAccessibility(){
    // test if average quality information is accessible
    assertEquals((int) testSequence.getAvgQuality(), 36);
    // test if the quality array is accessible
    assertEquals(testSequence.getQuality()[0], 16);
  }
}
