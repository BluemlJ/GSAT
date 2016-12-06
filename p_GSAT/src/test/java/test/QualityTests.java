package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.QualityAnalysis;
import exceptions.FileReadingException;
import io.SequenceReader;

public class QualityTests {

	AnalysedSequence testSequence;

	/**
	 * Initializes a sample file using SequenceReader
	 */
	@Before
	public void initializeSequence() throws FileReadingException, IOException {
		// set SequenceReader file path
		SequenceReader.configurePath(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
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
		assertTrue(QualityAnalysis.findLowQualityEnd(testSequence) >= 0);
	}

	/*
	 * This test confirms expected quality trimming behaviour for a synthetic
	 * sequence
	 */
	@Test
	public void qualityTestA() throws IOException {
		// quality cutoff after 3 nucleotide
		int[] qualitiesA = { 100, 100, 100, 100, 100, 100, 100, 100, 0 };
		AnalysedSequence testSequenceA = new AnalysedSequence("aaatttggg", "", "", "", "", qualitiesA, 33.3);
		assertEquals(QualityAnalysis.findLowQualityEnd(testSequenceA), 8);
		assertEquals(QualityAnalysis.findLowQualityStart(testSequenceA), 0);
	}

	/*
	 * This test confirms expected quality trimming behaviour for a synthetic
	 * sequence
	 */
	@Test
	public void qualityTestB() throws IOException {
		// whole sequence is perfect quality
		int[] qualitiesB = { 100, 100, 100, 100, 100, 100, 100, 100, 100 };
		AnalysedSequence testSequenceB = new AnalysedSequence("aaatttggg", "", "", "", "", qualitiesB, 100);
		assertEquals(QualityAnalysis.findLowQualityEnd(testSequenceB), 9);
		assertEquals(QualityAnalysis.findLowQualityStart(testSequenceB), 0);
	}

	/*
	 * This test confirms expected quality trimming behaviour for a synthetic
	 * sequence
	 */
	@Test
	public void qualityTestC() throws IOException {
		// whole sequence is bad quality
		int[] qualitiesC = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		AnalysedSequence testSequenceC = new AnalysedSequence("aaatttggg", "", "", "", "", qualitiesC, 33.3);
		assertEquals(QualityAnalysis.findLowQualityEnd(testSequenceC), 0);
		assertEquals(QualityAnalysis.findLowQualityStart(testSequenceC), 9);
	}
	
	/*
	 * This test confirms expected quality trimming behaviour for a synthetic
	 * sequence
	 */
	@Test
	public void qualityTestD() throws IOException {
		// start is bad quality, end is low quality
		int[] qualitiesD = { 0, 0, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0};
		AnalysedSequence testSequenceD = new AnalysedSequence("aaatttggg", "", "", "", "", qualitiesD, 33.3);
		System.out.println(QualityAnalysis.findLowQualityEnd(testSequenceD));
		assertEquals(QualityAnalysis.findLowQualityEnd(testSequenceD), 7);
		assertEquals(QualityAnalysis.findLowQualityStart(testSequenceD), 2);
	}
	
	/*
	 * This test confirms expected quality trimming behaviour for a synthetic
	 * sequence
	 */
	@Test
	public void qualityTestE() throws IOException {
		// start is bad quality, end is low quality
		int[] qualitiesE = { 0, 0, 100, 100, 100, 100, 100, 100, 100 };
		AnalysedSequence testSequenceE = new AnalysedSequence("aaatttggg", "", "", "", "", qualitiesE, 33.3);
		System.out.println(QualityAnalysis.findLowQualityEnd(testSequenceE));
		assertEquals(QualityAnalysis.findLowQualityEnd(testSequenceE), 9);
		assertEquals(QualityAnalysis.findLowQualityStart(testSequenceE), 2);
	}
}
