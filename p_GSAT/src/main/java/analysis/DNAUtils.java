package analysis;

import java.util.LinkedList;

/**
 * This class contains the logic of analyzing DNA sequences.
 *
 */
public class DNAUtils {

    /**
     * This method find the point in given sequence from which on the sequence is 
     * not reliable anymore. 
     */
    public int findEndOfTrustworthyness(AnalyzedSequence sequence) {
	return 0;
    }
    
    
    
    
    /**
     * Compares a sequence to a reference sequence (typically, a gene) to find mutations. Returns the list of mutations, denoted as described by
     * the department of organic chemistry.
     */
    public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze, Gene reference) {
	return null;
    }
    
    
    
    
    /**
     * Finds the gene that fits best to a given sequence by comparing it to all given genes.
     * Known genes can be found in the database.
     */
    public static Gene findRightGene(AnalyzedSequence toAnalyze) {
	return null;
    }
    
    
    
    /**
     * Compares to sequences and returns their differences.
     * Note for Kevin: You could probably use the Levenshtein-Algorithm, but maybe (also?)
     * the Needleman-Wunsch-Algorithm or the Smith-Waterman-Algorithm. This method should
     * have some kind of backtracking (to report on changes), while "compare" shouldn't.
     */
    private static LinkedList<String> compareAndReport(Sequence sOne, Sequence sTwo) {
	return null;
	
    }
    
    
    
    
    /**
     * Compares to sequences and returns their similarity.
     */
    private static double compare(Sequence first, Sequence second) {
	return 0.0;
    }
    
}
