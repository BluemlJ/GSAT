package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.biojava.bio.program.abi.ABIFChromatogram;
import org.biojava.bio.program.abi.ABIFParser;
import org.biojava.bio.program.abi.ABITrace;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.junit.Test;

/**
 * Tests to check the correct integration of the BioJava framework in our project.
 * 
 * @author Lovis Heindrich
 *
 */
public class BioJavaTest {

	/**
	 * This test checks whether the BioJavaFramework 4.2 is usable (i.e. it is configured
	 * correctly) by referring to classes from the framework.
	 * 
	 * @throws CompoundNotFoundException
	 * 
	 * @author Lovis Heindrich
	 */
	@Test
	public void testIntegration() throws CompoundNotFoundException {
		DNASequence seq = new DNASequence("GTAC");
		assertTrue("seq should be initialized", (seq instanceof DNASequence));
	}
	
	/**
	 * This Test checks whether the BioJava Legacy Framework is usable
	 * @throws IOException 
	 * 
	 * @author Lovis Heindrich	 
	 */
	@Test
	public void testABIRead() throws IOException{
		File myFile = new File("D:/Dokumente/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1");
		assertTrue("File access worked", myFile.canRead());
		ABITrace myTrace = new ABITrace(myFile);
		//ABIFParser myParser = new ABIFParser(myFile);
		//ABIFChromatogram myChroma = new ABIFChromatogram();
		
		//The map has no particular order and so cannot be relied on to iterate over records in the same order they were read from the file.
		/*Map<String,ABIFParser.TaggedDataRecord> dataMap = myParser.getAllDataRecords();
		for (Map.Entry<String,ABIFParser.TaggedDataRecord> entry : dataMap.entrySet()) {
		    System.out.println(entry.getKey() + ", " + entry.getValue());
		}*/
		
		
		//System.out.println(myTrace.getSequenceLength());
		SymbolList mySequence = myTrace.getSequence();
		assertEquals(mySequence.subStr(1, 6), "gagttt");
		//System.out.println(mySequence.seqString());
	}

}
