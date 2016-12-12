package test;

import java.io.IOException;

import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import exceptions.FileReadingException;
import exceptions.UndefinedTypeOfMutationException;
import io.SequenceReader;

public class privateTest {

	@Test
	public void testLocalFile() throws FileReadingException, IOException, UndefinedTypeOfMutationException {
		SequenceReader.configurePath("/Users/lovisheindrich/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1");
		System.out.println("Path set");
		AnalysedSequence testSeq = SequenceReader.convertFileIntoSequence();
		System.out.println("File read");
		QualityAnalysis.trimLowQuality(testSeq);
		System.out.println("Quality trimmed");
		String gene = "ACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTtaa".toUpperCase();
		Gene fsa = new Gene(gene, 0, "FSA", "");
		System.out.println("Gene constructed");
		System.out.println(testSeq.getSequence());
		//StringAnalysis.trimVector(testSeq, fsa);
		//String test = StringAnalysis.findBestMatch(testSeq.getSequence(),fsa.getSequence()).value;
		//System.out.println(test);
		System.out.println("Vector trimmed");
		testSeq.setReferencedGene(fsa);
		MutationAnalysis.findMutations(testSeq);
		for (String s : testSeq.getMutations()) {
			System.out.println(s);
		}
	}
}
