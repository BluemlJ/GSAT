package test;

import java.io.IOException;

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

	
	@Test
	public void testLocalFile() throws FileReadingException, IOException, UndefinedTypeOfMutationException, CorruptedSequenceException {
		// TODO set local path
		SequenceReader.configurePath("/home/bluemlj/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A07.ab1");
		System.out.println("Path set");
		AnalysedSequence testSeq = SequenceReader.convertFileIntoSequence();
		System.out.println("File read");
		QualityAnalysis.trimLowQuality(testSeq);
		System.out.println("Quality trimmed");
		// TODO add gene
		String gene = "atgGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTtaa";
		Gene fsa = new Gene(gene, 0, "FSA", "");
		System.out.println("Gene constructed");
		System.out.println(testSeq.getSequence());
		StringAnalysis.trimVector(testSeq, fsa);
		// String test =
		// StringAnalysis.findBestMatch(testSeq.getSequence(),fsa.getSequence()).value;
		// System.out.println(test);
		System.out.println("Vector trimmed");
		testSeq.setReferencedGene(fsa);
		MutationAnalysis.findMutations(testSeq);
		for (String s : testSeq.getMutations()) {
			System.out.println(s);
		}
	}
}
