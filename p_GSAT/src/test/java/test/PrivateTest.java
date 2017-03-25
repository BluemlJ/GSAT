package test;

import java.io.IOException;

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
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.SequenceReader;


public class PrivateTest {

  // TO CHANGE: ---------------------------------------------------

  private static String[] paths =
      new String[] {"C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/",
          "C:\\Users\\Business\\Dropbox\\BP_GSAT\\Materialien\\Dateien\\Bsp\\AB\\",
          "/Users/lovisheindrich/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/", "C:/GSAT Tests/",
          "/home/jannis/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/",
          "/home/kevin/Documents/GSAT_Tests/",
          "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 2 17.1 Privat/Sequences/",
          "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 3 12.16 oeffentlich/ab1/Tk_Gs40Hits/Forward/",
          "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 3 12.16 oeffentlich/ab1/Tk_Gs40Hits/Reverse/",
          "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 3 12.16 oeffentlich/ab1/Tk40Hits050215/",
          "/home/jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/plasmidmix2/"};

  private static String pathToUse;

  // --------------------------------------------------------------

  /*
   * Datenset 1 0 Jannis, 1 Ben, 2 Lovis, 3 Kevin, 4 Jannis' Laptop, 5 Kevins Laptop
   * 
   * Datenset 2 6 Jannis
   * 
   * Datenset 3 7-9 Jannis
   * 
   * Datenset 4 10 Jannis' Laptop
   */
  private static int userNr = 4;

  
  @BeforeClass
  public static void setup() {
    pathToUse = paths[userNr];
    System.out.println("Start");
  }

  
  // Name of the file
  private String fileName = "93GH02_D08.ab1";

  
  // _____________________________________________________________________

  String ECDERA =
      "ATGACTGATCTGAAAGCAAGCAGCCTGCGTGCACTGAAATTGATGGACCTGACCACCCTG"
      + "AATGACGACGACACCGACGAGAAAGTGATCGCCCTGTGTCATCAGGCCAAAACTCCGG"
      + "TCGGCAATACCGCCGCTATCTGTATCTATCCTCGCTTTATCCCGATTGCTCGCAA"
      + "AACTCTGAAAGAGCAGGGCACCCCGGAAATCCGTATCGCTACGGTAACCAACTTCC"
      + "CACACGGTAACGACGACATCGACATCGCGCTGGCAGAAACCCGTGCGGCAATCGCC"
      + "TACGGTGCTGATGAAGTTGACGTTGTGTTCCCGTACCGCGCGCTGATGGCGGGTAACG"
      + "AGCAGGTTGGTTTTGACCTGGTGAAAGCCTGTAAAGAGGCTTGCGCGGCAGCGAATGT"
      + "ACTGCTGAAAGTGATCATCGAAACCGGCGAACTGAAAGACGAAGCGCTGATCCGTAA"
      + "AGCGTCTGAAATCTCCATCAAAGCGGGTGCGGACTTCATCAAAACCTCTACCGGTAAA"
      + "GTGGCTGTGAACGCGACGCCGGAAAGCGCGCGCATCATGATGGAAGTGATCCGTGATA"
      + "TGGGCGTAGAAAAAACCGTTGGTTTCAAACCGGCGGGCGGCGTGCGTACTGCGGAAGA"
      + "TGCGCAGAAATATCTCGCCATTGCAGATGAACTGTTCGGTGCTGACTGGGCAGATGCG"
      + "CGTCACTACCGCTTTGGCGCTTCCAGCCTGCTGGCAAGCCTGCTGAAAGCG"
      + "CTGGGTCACGGCGACGGTAAGAGCGCCAGCAGCTACCTCGAGCACCACCACCACCACCACTGA";

  
  String FSA =
      "ATGGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCT"
      + "GGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCT"
      + "TCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACT"
      + "GCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAA"
      + "GTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACG"
      + "CTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATAT"
      + "GTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCG"
      + "ACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCA"
      + "GCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAAT"
      + "TACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCC"
      + "GGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTT"
      + "GGCAGAACGTCGATTTAA";

  
  String TKGS =
      "ATGGCGCATTCAATCGAGGAGTTGGCGATTACGACGATTCGAACGCTGTCGATTGACGCGAT" 
      + "CGAAAAAGCGAAATCCGGGCATCCGGGCATGCCGATGGGCGCGGC"
      + "GCCAATGGCGTACACGCTTTGGACGAAATTTATGAATCATAACCCGGCGAATCCAAAC" 
      + "TGGTTCAACCGCGACCGTTTTGTCTTGTCAGCCGGGCACGGGTCGATG"
      + "TTATTGTACAGCTTGCTTCATTTAAGCGGCTACGACGTATCGATGGATGATTTGAAAC" 
      + "AATTCCGTCAATGGGGAAGCAAAACGCCGGGCCATCCGGAATACGGCC"
      + "ATACGCCGGGCGTGGAAGCGACGACCGGCCCACTCGGCCAAGGGATTGCGATGGCGG" 
      + "TCGGCATGGCGATGGCGGAACGGCATTTGGCCGCTACATACAACCGCGA"
      + "CGGGTTTGAGATTATCAATCATTATACGTACGCCATTTGCGGCGATGGCGATTTGA" 
      + "TGGAAGGAGTGGCGAGCGAAGCTGCGTCACTCGCCGGCCACTTGAAGCTC"
      + "GGTCGACTGATCGTCCTGTATGACTCGAACGACATTTCGCTGGACGGGGAGCTCAA" 
      + "CCTGTCGTTCTCGGAAAACGTCGCCCAACGTTTCCAAGCATACGGCTGGCA"
      + "ATATTTGCGCGTTGAGGACGGCAACAATATTGAAGAAATCGCCAAAGCGCTGGAGGA" 
      + "GGCGCGGGCGGACCTCAGCCGGCCGACGCTCATTGAAGTAAAAACGACGA"
      + "TTGGCTACGGCGCGCCAAATAAAGCGGGCACGTCCGGCGTCCACGGTGCTCCGCTCGG" 
      + "CGCCCAAGAGGCGAAGCTGACGAAAGAGGCGTATCGTTGGACATTTGCGG"
      + "AAGATTTTTACGTGCCAGAAGAAGTGTACGCCCACTTCCGTGCGACGGTGCAAGAGC" 
      + "CGGGAGCGAAAAAAGAGGCGAAATGGAATGAGCAGCTCGCCGCCTATGAACA"
      + "GGCCCATCCGGAACTGGCCGCCCAATTGAAGCGAGCGATCGAAGGCAAACTTCCAG" 
      + "ATGGATGGGAAGCTTCTTTGCCGGTATACGAAGCAGGCAAAAGCTTGGCAACCC"
      + "GCTCATCGTCCGGGGAAGTGATCAACGCCATCGCCAAAGCGGTACCGCAATTGTTT" 
      + "GGCGGTTCGGCGGACTTGGCAAGCTCGAATAAAACGCTCATCAAAGGCGGCGGCA"
      + "ACTTCTTCCCGGGCAGCTACGAAGGGCGCAACGTTTGGTTTGGCGTGCGCGAGTTTG" 
      + "CCATGGGGGCGGCGCTGAACGGCATGGCGCTTCACGGCGGGCTGAAAGTGTTCG"
      + "GCGGCACGTTCTTCGTGTTCTCTGACTATTTGCGTCCGGCGATCCGCTTGGCGGCGC" 
      + "TGATGGGCTTACCGGTCATCTACGTCTTGACGCACGACAGCATCGCCGTCGGCG"
      + "AAGACGGGCCGACGCACGAGCCGATCGAACAGCTAGCTTCGCTTCGGGCGATGCCGA" 
      + "ACTTGTCGGTCATCCGTCCGGCTGACGCAAACGAAACGGCGGCAGCATGGCGGCT"
      + "GGCGCTCGAATCGACGGACAAGCCGACTGCGCTCGTCTTGACGCGTCAAGATGTGCCG" 
      + "ACGTTGGCGGCAACAGCTGAGTTGGCGTATGAAGGCGTGAAAAAAGGTGCATAC"
      + "GTCGTTTCACCGGCGAAAAACGGCGCTCCGGAGGCGCTGTTGTTGGCGACTGGCTCGG" 
      + "AAGTCGGTCTGGCCGTAAAAGCGCAAGAAGCGCTCGCCGCTGAGGGCATCCATGT"
      + "CTCCGTCATCAGCATGCCATCGTGGGACCGCTTCGAAGCGCAGCCAAAATCGTACCGC" 
      + "GATGAAGTGCTTCCGCCGGCCGTGACGAAGCGGCTCGCCATTGAAATGGGCGCGT"
      + "CGCTCGGTTGGGAGCGCTACGTCGGCGCCGAGGGCGACATTTTGGCCATCGACCGATT" 
      + "CGGTGCTTCCGCTCCGGGAGAGAAAATCATGGCCGAGTATGGCTTTACGGTTGAC"
      + "AACGTCGTCCGCCGCACAAAAGCGCTGCTCGGCAAGTAA";

  /**
   * This is the private test method, used e.g. on Thursdays to check the performance
   * on the given AB1 files.  
   */
  
  @Test
  public void testLocalFile() throws FileReadingException, IOException,
      UndefinedTypeOfMutationException, CorruptedSequenceException, MissingPathException {
    
    // Configure path to file
    SequenceReader.configurePath(pathToUse + fileName);
    System.out.println("Path set");
    
    
    // Read in the file
    AnalysedSequence testSeq = SequenceReader.convertFileIntoSequence();
    System.out.println("File read");

    // Quality insights
    for (int i : testSeq.getQuality()) {
      System.err.print(i + ", ");
    }
    System.out.println("\n");
    for (int i : QualityAnalysis.findLowQuality(testSeq)) {
      System.err.println("ENDEN:    " + i);
    }

    // Gene selection
    String gene = FSA;
    Gene fsa = new Gene(gene, 0, "FSA", "");
    testSeq.setReferencedGene(fsa);
    System.out.println("Gene constructed");
    System.err.println("Gene                " + testSeq.getReferencedGene().getSequence());
    System.out.println("Seq before Vector   " + testSeq.getSequence());
    
    // Complement and reverse check
    StringAnalysis.checkComplementAndReverse(testSeq);
    
    // Vector trimming
    StringAnalysis.trimVector(testSeq, fsa);
    System.out.println("Offset    " + testSeq.getOffset());
    System.out.println("Vector trimmed");
    System.out.println("Seq after Vector    " + testSeq.getSequence());
    
    // Quality trimming
    int i = testSeq.getSequence().length();
    QualityAnalysis.trimLowQuality(testSeq);
    System.out.println("Quality trimmed");
    System.out.println("Offset    " + testSeq.getOffset());
    System.out.println("Seq after Quality   " + testSeq.getSequence());
    
    // Stopcodon search
    if (StringAnalysis.findStopcodonPosition(testSeq) != -1) {
      testSeq.trimSequence(0, StringAnalysis.findStopcodonPosition(testSeq) * 3 + 2);
      System.out.println("FIND STOPCODON");
    }

    // Quality trim percentage
    System.out.println("" + QualityAnalysis.percentageOfTrimQuality(i, testSeq));

    // Sanity check
    QualityAnalysis.checkIfSequenceIsClean(testSeq);
    
    // HIS tags
    testSeq.setHisTagPosition(StringAnalysis.findHisTag(testSeq));
    System.out.println("");
    System.err
        .println(testSeq.getReferencedGene().getSequence().substring(testSeq.getOffset() * 3));
    System.out.println(testSeq.getSequence());
    System.out.println("_________________________________________________________");

    // Mutations 
    MutationAnalysis.findMutations(testSeq);
    MutationAnalysis.findPlasmidMix(testSeq);
    for (String s : testSeq.getMutations()) {
      System.out.println(s);
    }

    // Constructed comment String
    System.out.println("-------\n" + testSeq.getComments());
  }
}
