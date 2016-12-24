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

  @Ignore
  @Test
  public void testLocalFile() throws FileReadingException, IOException,
      UndefinedTypeOfMutationException, CorruptedSequenceException {
    // TODO set local path
    SequenceReader
        .configurePath("/home/bluemlj/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1");
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



    QualityAnalysis.trimLowQuality(testSeq);
    System.out.println("Quality trimmed");
    String gene =
        "atgGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTtaa";
    Gene fsa = new Gene(gene, 0, "FSA", "");
    System.out.println("Gene constructed");
    System.out.println(testSeq.getSequence());
    StringAnalysis.trimVector(testSeq, fsa);
    System.err.println(testSeq.getSequence());
    System.out.println("Vector trimmed");
    testSeq.setReferencedGene(fsa);
    MutationAnalysis.findMutations(testSeq);
    for (String s : testSeq.getMutations()) {
      System.out.println(s);
    }
  }


  //@Ignore
  @Test
  public void kevinLocalTest() throws FileReadingException, IOException,
      UndefinedTypeOfMutationException, CorruptedSequenceException {
    // TODO set local path
    SequenceReader.configurePath("C:/GSAT Tests/93GH02_A01.ab1");

    AnalysedSequence sequence = SequenceReader.convertFileIntoSequence();

    String gene =
        "atgGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTtaa";
    Gene fsa = new Gene(gene.toUpperCase(), 0, "FSA", "");
    sequence.setReferencedGene(fsa);
    
    System.out.println(sequence);
    
    //StringAnalysis.trimVector(sequence, fsa);
    StringAnalysis.trimVector(sequence);
    System.out.println(sequence.getOffset());
    //System.out.println(sequence.getOffset());
    
    System.err.println(sequence);
    System.out.println();
    System.out.println(fsa);
    
  }
  
}
