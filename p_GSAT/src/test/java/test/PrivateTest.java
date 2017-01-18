package test;

import java.io.IOException;
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

    /*
     * Datenset 1 0 Jannis, 1 Ben, 2 Lovis, 3 Kevin, 4 Jannis Laptop, 5 Kevins
     * Laptop
     * 
     * Datenset 2 6 Jannis
     * 
     * Datenset 3 7-9 Jannis
     */
    private static int userNr = 8;

    private static String[] paths = new String[] { "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/",
	    "C:\\Users\\Business\\Dropbox\\BP_GSAT\\Materialien\\Dateien\\Bsp\\AB\\",
	    "/Users/lovisheindrich/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/", "C:/GSAT Tests/",
	    "/home/bluemlj/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/", "/home/kevin/Documents/GSAT_Tests/",
	    "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 2 17.1 Privat/Sequences/",
	    "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 3 12.16 öffentlich/ab1/Tk_Gs40Hits/Forward/",
	    "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 3 12.16 öffentlich/ab1/Tk_Gs40Hits/Reverse/",
	    "C:/Users/Jannis/Dropbox/BP_GSAT/Materialien/Dateien - mehr/SET 3 12.16 öffentlich/ab1/Tk40Hits050215/" };

    // --------------------------------------------------------------

    String ECDERA = "ATGACTGATCTGAAAGCAAGCAGCCTGCGTGCACTGAAATTGATGGACCTGACCACCCTGAATGACGACGACACCGACGAGAAAGTGA"
	    + "TCGCCCTGTGTCATCAGGCCAAAACTCCGGTCGGCAATACCGCCGCTATCTGTATCTATCCTCGCTTTATCCCGATTGCTCGCAAAACTCTGA"
	    + "AAGAGCAGGGCACCCCGGAAATCCGTATCGCTACGGTAACCAACTTCCCACACGGTAACGACGACATCGACATCGCGCTGGCAGAAACCCGTGC"
	    + "GGCAATCGCCTACGGTGCTGATGAAGTTGACGTTGTGTTCCCGTACCGCGCGCTGATGGCGGGTAACGAGCAGGTTGGTTTTGACCTGGTGAAA"
	    + "GCCTGTAAAGAGGCTTGCGCGGCAGCGAATGTACTGCTGAAAGTGATCATCGAAACCGGCGAACTGAAAGACGAAGCGCTGATCCGTAAAGCGTC"
	    + "TGAAATCTCCATCAAAGCGGGTGCGGACTTCATCAAAACCTCTACCGGTAAAGTGGCTGTGAACGCGACGCCGGAAAGCGCGCGCATCATGATGG"
	    + "AAGTGATCCGTGATATGGGCGTAGAAAAAACCGTTGGTTTCAAACCGGCGGGCGGCGTGCGTACTGCGGAAGATGCGCAGAAATATCTCGCCATT"
	    + "GCAGATGAACTGTTCGGTGCTGACTGGGCAGATGCGCGTCACTACCGCTTTGGCGCTTCCAGCCTGCTGGCAAGCCTGCTGAAAGCGCTGGGTCA"
	    + "CACCACCACCACCACCACTGA";

    String FSA = "ATGGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCAT"
	    + "TATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACT"
	    + "GCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGA"
	    + "TGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTA"
	    + "CGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCA"
	    + "GCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCC"
	    + "GGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTTAA";

    String TKGS = "atggcgcattcaatcgaggagttggcgattacgacgattcgaacgctgtcgattgacgcgatcgaaaaagcgaaatccgggcatccgggcatgccgatgggcgcggc"
	    + "gccaatggcgtacacgctttggacgaaatttatgaatcataacccggcgaatccaaactggttcaaccgcgaccgttttgtcttgtcagccgggcacgggtcgatg"
	    + "ttattgtacagcttgcttcatttaagcggctacgacgtatcgatggatgatttgaaacaattccgtcaatggggaagcaaaacgccgggccatccggaatacggcc"
	    + "atacgccgggcgtggaagcgacgaccggcccactcggccaagggattgcgatggcggtcggcatggcgatggcggaacggcatttggccgctacatacaaccgcga"
	    + "cgggtttgagattatcaatcattatacgtacgccatttgcggcgatggcgatttgatggaaggagtggcgagcgaagctgcgtcactcgccggccacttgaagctc"
	    + "ggtcgactgatcgtcctgtatgactcgaacgacatttcgctggacggggagctcaacctgtcgttctcggaaaacgtcgcccaacgtttccaagcatacggctggca"
	    + "atatttgcgcgttgaggacggcaacaatattgaagaaatcgccaaagcgctggaggaggcgcgggcggacctcagccggccgacgctcattgaagtaaaaacgacga"
	    + "ttggctacggcgcgccaaataaagcgggcacgtccggcgtccacggtgctccgctcggcgcccaagaggcgaagctgacgaaagaggcgtatcgttggacatttgcgg"
	    + "aagatttttacgtgccagaagaagtgtacgcccacttccgtgcgacggtgcaagagccgggagcgaaaaaagaggcgaaatggaatgagcagctcgccgcctatgaaca"
	    + "ggcccatccggaactggccgcccaattgaagcgagcgatcgaaggcaaacttccagatggatgggaagcttctttgccggtatacgaagcaggcaaaagcttggcaaccc"
	    + "gctcatcgtccggggaagtgatcaacgccatcgccaaagcggtaccgcaattgtttggcggttcggcggacttggcaagctcgaataaaacgctcatcaaaggcggcggca"
	    + "acttcttcccgggcagctacgaagggcgcaacgtttggtttggcgtgcgcgagtttgccatgggggcggcgctgaacggcatggcgcttcacggcgggctgaaagtgttcg"
	    + "gcggcacgttcttcgtgttctctgactatttgcgtccggcgatccgcttggcggcgctgatgggcttaccggtcatctacgtcttgacgcacgacagcatcgccgtcggcg"
	    + "aagacgggccgacgcacgagccgatcgaacagctagcttcgcttcgggcgatgccgaacttgtcggtcatccgtccggctgacgcaaacgaaacggcggcagcatggcggct"
	    + "ggcgctcgaatcgacggacaagccgactgcgctcgtcttgacgcgtcaagatgtgccgacgttggcggcaacagctgagttggcgtatgaaggcgtgaaaaaaggtgcatac"
	    + "gtcgtttcaccggcgaaaaacggcgctccggaggcgctgttgttggcgactggctcggaagtcggtctggccgtaaaagcgcaagaagcgctcgccgctgagggcatccatgt"
	    + "ctccgtcatcagcatgccatcgtgggaccgcttcgaagcgcagccaaaatcgtaccgcgatgaagtgcttccgccggccgtgacgaagcggctcgccattgaaatgggcgcgt"
	    + "cgctcggttgggagcgctacgtcggcgccgagggcgacattttggccatcgaccgattcggtgcttccgctccgggagagaaaatcatggccgagtatggctttacggttgac"
	    + "aacgtcgtccgccgcacaaaagcgctgctcggcaagtaa";

    // _____________________________________________________________________

    private static String pathToUse;

    @BeforeClass
    public static void setup() {
	pathToUse = paths[userNr];
	System.out.println("Start");
    }

    // Name of the file
    private String fileName = "95EI70.ab1";

    /**
     * 
     * @throws FileReadingException
     * @throws IOException
     * @throws UndefinedTypeOfMutationException
     * @throws CorruptedSequenceException
     */
    @Ignore
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
	String gene = TKGS;

	Gene fsa = new Gene(gene, 0, "FSA", "");
	testSeq.setReferencedGene(fsa);
	System.out.println("Gene constructed");
	System.err.println("Gene                " + testSeq.getReferencedGene().getSequence());
	System.out.println("Seq before Vector   " + testSeq.getSequence());
	StringAnalysis.checkComplementAndReverse(testSeq);
	StringAnalysis.trimVector(testSeq, fsa);
	System.out.println("Offset    " + testSeq.getOffset());
	System.out.println("Vector trimmed");
	System.out.println("Seq after Vector    " + testSeq.getSequence());
	QualityAnalysis.trimLowQuality(testSeq);
	System.out.println("Quality trimmed");
	System.out.println("Offset    " + testSeq.getOffset());
	System.out.println("Seq after Quality   " + testSeq.getSequence());
	QualityAnalysis.checkIfSequenceIsClean(testSeq);
	System.out.println("");
	System.err.println(testSeq.getReferencedGene().getSequence().substring(testSeq.getOffset() * 3));
	System.out.println(testSeq.getSequence());

	System.out.println("_________________________________________________________");

	MutationAnalysis.findMutations(testSeq);
	for (String s : testSeq.getMutations()) {
	    System.out.println(s);
	}
	Assert.assertTrue(true);
    }
}
