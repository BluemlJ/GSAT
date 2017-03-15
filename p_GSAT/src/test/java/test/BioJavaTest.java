package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.biojava.bio.BioError;
import org.biojava.bio.program.abi.ABITrace;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests to check the correct integration of the BioJava framework in our
 * project.
 * 
 * @author Lovis Heindrich
 *
 */
@Ignore
public class BioJavaTest {
	private File myFile;
	private ABITrace myTrace;
	private Chromatogram croma;

	@Ignore
	@Before
	public void initializeABIData() throws IOException {
		myFile = new File("resources/ab1/Tk_Gs40Hits/Forward/95EI60.ab1");
		myTrace = new ABITrace(myFile);
		croma = ChromatogramFactory.create(myFile);
	}

	/**
	 * This Test checks whether the BioJava Legacy Framework is usable
	 * 
	 * @throws IOException
	 * 
	 * @author Lovis Heindrich
	 * @throws IllegalSymbolException
	 * @throws BioError
	 * @throws IllegalAlphabetException
	 */
	@Ignore
	@Test
	public void testABIRead() throws IOException, IllegalSymbolException, IllegalAlphabetException, BioError {
		// scale has to be 2
		/*
		 * BufferedImage chromaImg = myTrace.getImage(1000, 2); File outputfile
		 * = new File("/home/bluemlj/Desktop/chroma.png");
		 * ImageIO.write(chromaImg, "png", outputfile);
		 */
		int[] aTrace = myTrace.getTrace(DNATools.a());
		assertTrue(aTrace != null);
		int[] cTrace = myTrace.getTrace(DNATools.c());
		assertTrue(cTrace != null);
		int[] gTrace = myTrace.getTrace(DNATools.g());
		assertTrue(gTrace != null);
		int[] tTrace = myTrace.getTrace(DNATools.t());
		assertTrue(tTrace != null);
		int[] basecalls = myTrace.getBasecalls();
		assertTrue(basecalls != null);
	}

	/**
	 * This test checks, if the nucleotidestring from BioJava and Jillion are
	 * identical
	 * 
	 * @throws IOException
	 *             if the file is not fount
	 * 
	 * @author bluemlj
	 */
	
	@Ignore
	@Test
	public void testABIReadJillionBioJava() throws IOException {

		String testString1 = myTrace.getSequence().seqString();
		String testString2 = croma.getNucleotideSequence().toString();

		testString1 = testString1.toLowerCase();
		testString2 = testString2.toLowerCase();

		assertTrue(testString1.equals(testString2));
	}

}
