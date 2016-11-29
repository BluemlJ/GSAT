package test;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.biojava.bio.BioError;
import org.biojava.bio.program.abi.ABITrace;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
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
   * This test checks whether the BioJavaFramework 4.2 is usable (i.e. it is configured correctly)
   * by referring to classes from the framework.
   * 
   * @throws CompoundNotFoundException
   * 
   * @author Lovis Heindrich
   */
  @Test
  public void testIntegration() throws CompoundNotFoundException {
    DNASequence seq = new DNASequence("GTAC");
    assertTrue(seq.getLength() == 4);
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
  /*@Test
  public void testABIRead() throws IOException, IllegalSymbolException, IllegalAlphabetException, BioError {
    // placeholder - real test needs local files
    assertTrue(true);
    File myFile = new
    //File("D:/Dokumente/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1");
    		File ("/Users/lovisheindrich/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A04.ab1");
    // assertTrue("File access worked", myFile.canRead());
    ABITrace myTrace = new ABITrace(myFile);
    // ABIFParser myParser = new ABIFParser(myFile);
    // ABIFChromatogram myChroma = new ABIFChromatogram();

    // The map has no particular order and so cannot be relied on to iterate over records in the
    // same order they were read from the file.
    /*
     * Map<String,ABIFParser.TaggedDataRecord> dataMap = myParser.getAllDataRecords(); for
     * (Map.Entry<String,ABIFParser.TaggedDataRecord> entry : dataMap.entrySet()) {
     * System.out.println(entry.getKey() + ", " + entry.getValue()); }
     */
    
    /*
    //scale has to be 2
    BufferedImage chromaImg = myTrace.getImage(1000, 2);
    File outputfile = new File("/Users/lovisheindrich/Desktop/chroma.png");
    ImageIO.write(chromaImg, "png", outputfile);
    int[] aTrace = myTrace.getTrace(DNATools.a());
    int[] cTrace = myTrace.getTrace(DNATools.c());
    int[] gTrace = myTrace.getTrace(DNATools.g());
    int[] tTrace = myTrace.getTrace(DNATools.t());
    int[] basecalls = myTrace.getBasecalls();
    
    for(int i =0; i<basecalls.length; i++){
    	//System.out.println(basecalls[i]);
    }
    System.out.println(tTrace.length);
    System.out.println(basecalls[basecalls.length-1]);
    System.out.println((double) basecalls[basecalls.length-1] / (double) tTrace.length );

    // System.out.println(myTrace.getSequenceLength());
    // SymbolList mySequence = myTrace.getSequence();
    // assertEquals(mySequence.subStr(1, 6), "gagttt");
    // System.out.println(mySequence.seqString());
  }
*/
}
