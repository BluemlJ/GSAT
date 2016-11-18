package analysis;

import java.util.LinkedList;

/**
 * This class contains the logic of analyzing DNA sequences. Thus, it is the 
 * main part of the analyzing pipeline.
 * 
 */
public class DNAUtils {

    /**
     * This method finds the point in given sequence from which on the sequence is 
     * not reliable anymore. 
     * The nucleotide at the point itself is the last "reliable" nucleotide.
     * 
     * @param sequence The sequence for which the end of reliability should be determined.
     * 
     * @return the index of the sequence String from which on it is considered unreliable
     */
    public int findEndOfTrustworthyness(AnalyzedSequence sequence) {
	return 0;
    }
    
    
    
    
    /**
     * Measures the quality of a sequence or of one of its parts which is used to
     * find the end of trustwothyness.
     * 
     * @return The quality measure for the given sequence (may also be a part of a reference sequence)
     */
    private static double measureQuality(AnalyzedSequence seq) {
	return 0.0;
    }
    
    
    
    /**
     * Compares a sequence to a gene to find mutations. Returns the list of mutations, denoted as described by
     * the department of organic chemistry.
     * 
     * @param toAnalyze The sequence to be analyzed (which may have mutations)
     * @param reference The referenced gene (used to compare the sequence against it)
     * 
     * @return A List of Mutations, represented as Strings in the given format
     */
    public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze, Gene reference) {
	return null;
    }
    
    
    /**
     * Compares a sequence to its own referenced gene.
     * 
     * @param toAnalyse The sequence to be analyzed
     * 
     * @return A List of Mutations, represented as Strings in the given format
     * 
     * @see #findMutations(AnalyzedSequence, Gene)
     * 
     * @author Ben Kohr
     */
    public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze) {
	
	LinkedList<String> mutations = findMutations(toAnalyze, toAnalyze.getReferencedGene());
	return mutations;
    }
    
    
    
    /**
     * Finds the gene that fits best to a given sequence by comparing it to all given genes.
     * Known genes can be found in the database.
     * 
     * @param toAnalyze Sequence to be analyzed (i.e. for which the fitting gene is to find)
     * 
     * @return The found gene.
     */
    public static Gene findRightGene(AnalyzedSequence toAnalyze) {
	return null;
    }
    
    
    
    /**
     * Compares to sequences and returns their differences as a list (represented as Strings). 
     * The order of the input sequences is 
     * irrelevant.
     * 
     * @param sOne The first sequence
     * @param sTwo The second sequence.
     * 
     * @return A list of differences (represented as Strings)
     */
    private static LinkedList<String> reportDifferences(Sequence sOne, Sequence sTwo) {
	return null;
	
    }
    
    
    
    
    /**
     * Compares to sequences and returns their similarity without finding the exact differences.
     * The order of the input sequences is irrelevant.
     * 
     * @param first The first sequence
     * @param second The second sequence
     * 
     * @return Similarity measure
     */
    private static double compare(Sequence first, Sequence second) {
	return 0.0;
    }
    
    
    
    
}
