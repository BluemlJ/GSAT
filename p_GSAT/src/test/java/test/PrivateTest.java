package test;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import exceptions.CorruptedSequenceException;
import exceptions.FileReadingException;
import exceptions.UndefinedTypeOfMutationException;
import io.SequenceReader;

public class PrivateTest {

	
	// TO CHANGE: ---------------------------------------------------
	
	/*  0 Jannis, 
	*	1 Ben,
	*	2 Lovis,
	*	3 Kevin,
	*   4 Jannis Laptop,
	*   5 Kevins Laptop
	*/
	private static int userNr = 4;
	
	// Name of the file
	private String fileName = "93GH02_B11.ab1";
	
	// --------------------------------------------------------------
	
	
	private static String[] paths = new String[]{
			"C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/",
			"C:\\Users\\Business\\Dropbox\\BP_GSAT\\Materialien\\Dateien\\Bsp\\AB\\",
			"/Users/lovisheindrich/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/",
			"C:/GSAT Tests/", "/home/bluemlj/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/",
			"/home/kevin/Documents/GSAT_Tests/"};
	
	private static String pathToUse;
	
	@BeforeClass
	public static void setup() {
		pathToUse = paths[userNr];
		System.out.println("Start");
	}
	
	
	/**
	 * 
	 * @throws FileReadingException
	 * @throws IOException
	 * @throws UndefinedTypeOfMutationException
	 * @throws CorruptedSequenceException
	 */
    @Test
    public void testLocalFile()
	    throws FileReadingException, IOException, UndefinedTypeOfMutationException, CorruptedSequenceException {
	  SequenceReader.configurePath(pathToUse + fileName);
	System.out.println("Path set");
	AnalysedSequence testSeq = SequenceReader.convertFileIntoSequence();
	System.out.println("File read");

	for (int i : testSeq.getQuality()) {
	    System.err.print(i + ", ");
	}
	System.out.println("\n");
	for (int i : QualityAnalysis.findLowQuality(testSeq)) {
	    System.err.println("ENDEN:    " + i);
	}

	//
	String gene = "ATGGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTtaa";
	Gene fsa = new Gene(gene, 0, "FSA", "");
	testSeq.setReferencedGene(fsa);
	System.out.println("Gene constructed");
	System.err.println("Gene                " + testSeq.getReferencedGene().getSequence());
	System.out.println("Seq before Vector   " + testSeq.getSequence());
	StringAnalysis.trimVector(testSeq, fsa);
	System.out.println("Offset    " + testSeq.getOffset());
	System.out.println("Vector trimmed");
	System.out.println( "Seq after Vector    " + testSeq.getSequence());
	QualityAnalysis.trimLowQuality(testSeq);
	System.out.println("Quality trimmed");
	System.out.println("Offset    " + testSeq.getOffset());
	System.out.println( "Seq after Quality   " + testSeq.getSequence());
	System.out.println("");
	System.err.println(testSeq.getReferencedGene().getSequence().substring(testSeq.getOffset()*3));
	System.out.println(testSeq.getSequence());
	
	
	System.out.println("__________________________________________________________________________________________________");
	
	MutationAnalysis.findMutations(testSeq);
	for (String s : testSeq.getMutations()) {
	    System.out.println(s);
	}
	Assert.assertTrue(true);
    }
}
