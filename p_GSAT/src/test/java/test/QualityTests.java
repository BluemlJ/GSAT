package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.QualityAnalysis;
import exceptions.FileReadingException;
import io.SequenceReader;

/**
 * This class contains tests for analysing the quality of an analysedSequence
 * 
 * @author Lovis Heindrich
 *
 */
public class QualityTests {

  AnalysedSequence testSequence;

  /**
   * Initializes a sample file using SequenceReader
   */
  @Before
  public void initializeSequence() throws FileReadingException, IOException {
    // set SequenceReader file path
    SequenceReader
        .configurePath(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
    testSequence = SequenceReader.convertFileIntoSequence();
  }

  /**
   * Tests if the quality information is accessible
   */
  @Test
  public void testQualityAccessibility() {
    // test if average quality information is accessible
    assertEquals((int) testSequence.getAvgQuality(), 36);
    // test if the quality array is accessible
    assertEquals(testSequence.getQuality()[0], 16);

  }

  /**
   * Tests if the quality trim function is usable
   * 
   * @throws IOException
   */
  @Test
  public void testQualityTrim() throws IOException {
    // Debug code for adjusting QualityAnalysis.BREAKCOUNTER
    /*
     * System.out.println("Gesamtlaenge der Sequenz: " + testSequence.length());
     * System.out.println("Anfang der hohen Qualitaet: " +
     * QualityAnalysis.findLowQualityStart(testSequence));
     * System.out.println("Ende der hohen Qualitaet " +
     * QualityAnalysis.findLowQualityEnd(testSequence));
     */
  }

  /*
   * This test confirms expected quality trimming behaviour for a synthetic sequence
   */
  @Test
  public void qualityTestA() throws IOException {
    // quality cutoff after 8 nucleotide
    int[] qualitiesA = {100, 100, 100, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0};
    AnalysedSequence testSequenceA =
        new AnalysedSequence("aaatttgggaaaaaa", "", "", qualitiesA, 33.3);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceA);
    assertEquals(trim[0], 0);
    assertEquals(trim[1], 8);
    QualityAnalysis.trimLowQuality(testSequenceA);
    assertEquals(testSequenceA.getSequence(), "aaatttgg");
  }

  /*
   * This test confirms expected quality trimming behaviour for a synthetic sequence
   */
  @Test
  public void qualityTestB() throws IOException {
    // whole sequence is perfect quality
    int[] qualitiesB = {100, 100, 100, 100, 100, 100, 100, 100, 100};
    AnalysedSequence testSequenceB = new AnalysedSequence("aaatttggg", "", "", qualitiesB, 100);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceB);
    assertEquals(trim[1], 9);
    assertEquals(trim[0], 0);
    QualityAnalysis.trimLowQuality(testSequenceB);
    assertEquals(testSequenceB.getSequence(), "aaatttggg");
  }

  /*
   * This test confirms expected quality trimming behaviour for a synthetic sequence
   */
  @Test
  public void qualityTestC() throws IOException {
    // whole sequence is bad quality
    int[] qualitiesC = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    AnalysedSequence testSequenceC = new AnalysedSequence("aaatttggg", "", "", qualitiesC, 33.3);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceC);
    assertEquals(trim[1], 9);
    assertEquals(trim[0], 9);
    QualityAnalysis.trimLowQuality(testSequenceC);
    assertEquals(testSequenceC.getSequence(), "");
  }

  /*
   * This test confirms expected quality trimming behaviour for a synthetic sequence
   */
  @Test
  public void qualityTestD() throws IOException {
    // start is bad quality, end is low quality
    int[] qualitiesD = {0, 0, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0};
    AnalysedSequence testSequenceD =
        new AnalysedSequence("aaatttgggaaatt", "", "", qualitiesD, 33.3);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceD);
    assertEquals(trim[1], 7);
    assertEquals(trim[0], 2);
    QualityAnalysis.trimLowQuality(testSequenceD);
    assertEquals(testSequenceD.getSequence(), "atttg");
  }

  /*
   * This test confirms expected quality trimming behaviour for a synthetic sequence
   */
  @Test
  public void qualityTestE() throws IOException {
    // start is bad quality, end is low quality
    int[] qualitiesE = {0, 0, 100, 100, 100, 100, 100, 100, 100};
    AnalysedSequence testSequenceE =
        new AnalysedSequence("aaatttggg", "", "", qualitiesE, 33.3);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceE);
    assertEquals(trim[1], 9);
    assertEquals(trim[0], 2);
    QualityAnalysis.trimLowQuality(testSequenceE);
    assertEquals(testSequenceE.getSequence(), "atttggg");
  }
}
