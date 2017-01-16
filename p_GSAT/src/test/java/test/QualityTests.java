package test;

import static org.junit.Assert.assertEquals;

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
   * This test confirms expected quality trimming behaviour for a synthetic sequence (Userstory 008
   * - Expected behavior)
   */
  @Test
  public void qualityTestA() throws IOException {
    // quality cutoff after 8 nucleotide
    int[] qualitiesA =
        {100, 100, 100, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    AnalysedSequence testSequenceA =
        new AnalysedSequence("aaatttgggaaaaaaaaaaaa", "", "", qualitiesA);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceA);
    assertEquals(trim[0], 0);
    assertEquals(trim[1], 9);
    QualityAnalysis.trimLowQuality(testSequenceA);
    assertEquals(testSequenceA.getSequence(), "AAATTTGGG");
  }

  /**
   * This test confirms expected quality trimming behaviour for a synthetic sequence (Userstory 008
   * - Unusual behavior)
   */
  @Test
  public void qualityTestB() throws IOException {
    // whole sequence is perfect quality
    int[] qualitiesB = {100, 100, 100, 100, 100, 100, 100, 100, 100};
    AnalysedSequence testSequenceB = new AnalysedSequence("aaatttggg", "", "", qualitiesB);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceB);
    assertEquals(trim[1], 9);
    assertEquals(trim[0], 0);
    QualityAnalysis.trimLowQuality(testSequenceB);
    assertEquals(testSequenceB.getSequence(), "AAATTTGGG");
  }

  /**
   * This test confirms expected quality trimming behaviour for a synthetic sequence (Userstory 008
   * - Unusual behavior)
   */
  @Test
  public void qualityTestC() throws IOException {
    // whole sequence is bad quality
    int[] qualitiesC = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    AnalysedSequence testSequenceC = new AnalysedSequence("aaatttggg", "", "", qualitiesC);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceC);
    assertEquals(trim[1], 9);
    assertEquals(trim[0], 9);
    QualityAnalysis.trimLowQuality(testSequenceC);
    assertEquals(testSequenceC.getSequence(), "");
  }

  /**
   * This test confirms expected quality trimming behaviour for a synthetic sequence (Userstory 008
   * - Expected behavior)
   */
  @Test
  public void qualityTestD() throws IOException {
    // start is bad quality, end is low quality
    int[] qualitiesD = {0, 0, 0, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    AnalysedSequence testSequenceD =
        new AnalysedSequence("aaatttgggaaattt", "", "", qualitiesD);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceD);
    assertEquals(trim[1], 9);
    assertEquals(trim[0], 3);
    QualityAnalysis.trimLowQuality(testSequenceD);
    assertEquals(testSequenceD.getSequence(), "TTTGGG");
  }

  /**
   * This test confirms expected quality trimming behaviour for a synthetic sequence (Userstory 008
   * - Expected behavior)
   */
  @Test
  public void qualityTestE() throws IOException {
    // start is bad quality, end is low quality
    int[] qualitiesE = {0, 0, 0, 100, 100, 100, 100, 100, 100};
    AnalysedSequence testSequenceE = new AnalysedSequence("aaatttggg", "", "", qualitiesE);
    int[] trim = QualityAnalysis.findLowQuality(testSequenceE);
    assertEquals(trim[1], 9);
    assertEquals(trim[0], 3);
    QualityAnalysis.trimLowQuality(testSequenceE);
    assertEquals(testSequenceE.getSequence(), "TTTGGG");
  }

  /**
   * Tests if the quality information is accessible (Userstory 008 - Expected behavior)
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
   * @throws FileReadingException
   */
  @Test
  public void testQualityTrim() throws IOException, FileReadingException {
    // Debug code for adjusting QualityAnalysis.BREAKCOUNTER
    /*
     * testSequence = SequenceReader.convertFileIntoSequence(new
     * File("D:/Dokumente/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_G05.ab1"));
     * System.out.println("Gesamtlaenge der Sequenz: " + testSequence.length()); System.out
     * .println("Anfang der hohen Qualitaet: " + QualityAnalysis.findLowQuality(testSequence)[0]);
     * System.out .println("Ende der hohen Qualitaet " +
     * QualityAnalysis.findLowQuality(testSequence)[1]);
     */
  }

  /**
   * Tests if the average Quality Trim detects a regular bad quality ending (Userstory xxx -
   * Expected Behavior)
   */
  @Test
  public void testAverageQualityTrimA() {
    int[] qualities = {0, 0, 0, 0, 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(QualityAnalysis.getAverageTrimmingPosition(qualities, 6), 18);
  }

  /**
   * Tests if the average Quality Trim detects a bad quality ending which would not have been
   * detected by findLowQuality (Userstory xxx - Expected Behavior)
   */
  @Test
  public void testAverageQualityTrimB() {
    int[] qualities = {0, 0, 0, 0, 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
        0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 30, 0, 0};
    assertEquals(QualityAnalysis.getAverageTrimmingPosition(qualities, 6), 18);
  }

  /**
   * Tests if a short sequence string produces the default case as output (normal findLowQuality
   * will handle it) (Userstory xxx - Unusual Behavior)
   */
  @Test
  public void testAverageQualityTrimC() {
    int[] qualities = {0, 0, 0};
    assertEquals(QualityAnalysis.getAverageTrimmingPosition(qualities, 0), 3);
  }

  /**
   * This tests checks if the average Quality calculation is correctly integrated in findLowQuality
   * by using testAverageQualityTrimB (Userstory xxx - Expected Behavior)
   */
  @Test
  public void testAverageQualityOnMainQualityFunction() {
    int[] qualities = {0, 0, 0, 0, 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
        0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 30, 0, 0};
    AnalysedSequence testSequence =
        new AnalysedSequence("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "", "", qualities);
    int[] trim = QualityAnalysis.findLowQuality(testSequence);
    assertEquals(trim[1], 18);
  }
}
