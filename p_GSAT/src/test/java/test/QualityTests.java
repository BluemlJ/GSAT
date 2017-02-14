package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
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

  // store main vars to avoid interfering with other tests
  private static int avgApproximationStart = 30;
  private static int avgApproximationEnd = 25;
  private static int avgQualityEdge = 30;
  private static int breakcounter = 9;
  private static int numAverageNucleotides = 20;
  private static int startcounter = 3;

  // local test values
  private static int avgApproximationStartTest = 30;
  private static int avgApproximationEndTest = 25;
  private static int avgQualityEdgeTest = 30;
  private static int breakcounterTest = 9;
  private static int numAverageNucleotidesTest = 20;
  private static int startcounterTest = 3;



  /**
   * Initializes a sample file using SequenceReader
   */
  @Before
  public void initializeSequence() throws FileReadingException, IOException {
    // set SequenceReader file path
    SequenceReader
        .configurePath(new File("resources/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getAbsolutePath());
    testSequence = SequenceReader.convertFileIntoSequence();

    avgApproximationStart = QualityAnalysis.getAvgApproximationStart();
    avgApproximationEnd = QualityAnalysis.getAvgApproximationEnd();
    avgQualityEdge = QualityAnalysis.getAvgQualityEdge();
    breakcounter = QualityAnalysis.getBreakcounter();
    numAverageNucleotides = QualityAnalysis.getNumAverageNucleotides();
    startcounter = QualityAnalysis.getStartcounter();

    QualityAnalysis.setAvgApproximationStart(avgApproximationStartTest);
    QualityAnalysis.setAvgApproximationEnd(avgApproximationEndTest);
    QualityAnalysis.setAvgQualityEdge(avgQualityEdgeTest);
    QualityAnalysis.setBreakcounter(breakcounterTest);
    QualityAnalysis.setNumAverageNucleotides(numAverageNucleotidesTest);
    QualityAnalysis.setStartcounter(startcounterTest);
  }


  @After
  public void resetQualityParameters() {
    QualityAnalysis.setAvgApproximationStart(avgApproximationStart);
    QualityAnalysis.setAvgApproximationEnd(avgApproximationEnd);
    QualityAnalysis.setAvgQualityEdge(avgQualityEdge);
    QualityAnalysis.setBreakcounter(breakcounter);
    QualityAnalysis.setNumAverageNucleotides(numAverageNucleotides);
    QualityAnalysis.setStartcounter(startcounter);
  }


  @Test
  public void percentageGoodQualityTest1() {
    int[] qualities = {0, 0, 0, 0, 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
        0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 40, 0, 0};
    AnalysedSequence testSequence =
        new AnalysedSequence("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "", "", qualities);
    double avg = QualityAnalysis.getQualityPercentage(testSequence);
    assertTrue(Math.abs(avg - 36) < 0.05);
  }


  @Test
  public void percentageGoodQualityTest2() {
    int[] qualities = {30, 31, 40, 57, 57, 57, 6, 7, 8, 4, 3, 12};
    AnalysedSequence testSequence = new AnalysedSequence("aaaaaaaaaaaa", "", "", qualities);
    double avg = QualityAnalysis.getQualityPercentage(testSequence);
    assertTrue(Math.abs(avg - 41) < 0.05);
  }


  @Test
  public void percentageGoodQualityTestUnusual() {
    int[] qualities = {};
    AnalysedSequence testSequence = new AnalysedSequence("", "", "", qualities);
    double avg = QualityAnalysis.getQualityPercentage(testSequence);
    assertTrue(avg == 0);
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
    AnalysedSequence testSequenceD = new AnalysedSequence("aaatttgggaaattt", "", "", qualitiesD);
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
   * This tests checks if the average Quality calculation is correctly integrated in findLowQuality
   * by using testAverageQualityTrimB (Userstory 029 - Expected Behavior)
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


  /**
   * Tests if the average Quality Trim detects a regular bad quality ending (Userstory 029 -
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
   * detected by findLowQuality (Userstory 029 - Expected Behavior)
   */
  @Test
  public void testAverageQualityTrimB() {
    int[] qualities = {0, 0, 0, 0, 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
        0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 30, 0, 0, 0, 30, 0, 0};
    assertEquals(QualityAnalysis.getAverageTrimmingPosition(qualities, 6), 18);
  }


  /**
   * Tests if a short sequence string produces the default case as output (normal findLowQuality
   * will handle it) (Userstory 029 - Unusual Behavior)
   */
  @Test
  public void testAverageQualityTrimC() {
    int[] qualities = {0, 0, 0};
    assertEquals(QualityAnalysis.getAverageTrimmingPosition(qualities, 0), 3);
  }


  @Test
  public void testPercentageOfTrim1() {
    int[] qualities = {0, 0, 0, 100, 100, 100, 100, 100, 100};
    AnalysedSequence toAnalyze = new AnalysedSequence("aaatttggg", "", "", qualities);
    int tmp = toAnalyze.getSequence().length();
    QualityAnalysis.trimLowQuality(toAnalyze);
    System.out.println(QualityAnalysis.percentageOfTrimQuality(tmp, toAnalyze));
    assertTrue(Math.abs(QualityAnalysis.percentageOfTrimQuality(tmp, toAnalyze) - 33) < 0.05);
  }


  @Test
  public void testPercentageOfTrim2() {
    int[] qualities = {0, 0, 0, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    AnalysedSequence toAnalyze = new AnalysedSequence("aaatttgggaaatttaaaaa", "", "", qualities);
    int tmp = toAnalyze.getSequence().length();
    QualityAnalysis.trimLowQuality(toAnalyze);
    System.out.println(QualityAnalysis.percentageOfTrimQuality(tmp, toAnalyze));
    assertTrue(Math.abs(QualityAnalysis.percentageOfTrimQuality(tmp, toAnalyze) - 70) < 0.05);
  }


  @Test
  public void testPercentageOfTrimUnsusual() {
    int[] qualities = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    AnalysedSequence toAnalyze = new AnalysedSequence("aaataaattt", "", "", qualities);
    int tmp = toAnalyze.getSequence().length();
    QualityAnalysis.trimLowQuality(toAnalyze);
    System.out.println(QualityAnalysis.percentageOfTrimQuality(tmp, toAnalyze));
    assertTrue(QualityAnalysis.percentageOfTrimQuality(tmp, toAnalyze) == 100);
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
     * testSequence = SequenceReader.convertFileIntoSequence(new File(
     * "D:/Dokumente/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_G05.ab1" ));
     * System.out.println("Gesamtlaenge der Sequenz: " + testSequence.length()); System.out
     * .println("Anfang der hohen Qualitaet: " + QualityAnalysis.findLowQuality(testSequence)[0]);
     * System.out .println("Ende der hohen Qualitaet " +
     * QualityAnalysis.findLowQuality(testSequence)[1]);
     */
  }

}
