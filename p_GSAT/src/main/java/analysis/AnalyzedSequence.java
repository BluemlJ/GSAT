package analysis;

import java.util.LinkedList;

/**
 * Models a sequence under analysis (i.e. obtained from an AB1 file).
 * 
 * @author Ben Kohr
 * 
 */
public class AnalyzedSequence extends Sequence {

    /**
     * The gene this sequence was formed from. Useful to compare the sequence with the gene.
     */
    private Gene referencedGene;
    
    /**
     * Information to be stored in the database together
     * with the sequence (indicates how the sequence was obtained).
     */
    private String primer;
    
    
    /**
     * A list of discovered mutations to be stored.
     */
    private LinkedList<String> mutations;
    
    
    
    /**
     * A list of discovered silent mutations to be stored
     */
    private LinkedList<String> silentMutations;
    
    
    /**
     * The point in the data sequence from which on the sequence itself is 
     * considered unreliable. This number is zero-based (index in String).
     */
    private int endOfTrustworthyness;
    
    
    
    /**
     * Constructor calling the super constructor (which sets the only given attribute).
     * 
     * @param sequence The actual sequence of nucleotides as a String
     * 
     * @author Ben Kohr
     */
    public AnalyzedSequence(String sequence) {
	super(sequence);
    }
    
    /**
     * Add a discovered (normal) mutation to the list of already discovered mutations.
     * 
     * @param mutation A discovered mutation (in the given String format)
     * 
     * @author Ben Kohr
     */
    public void addMutation(String mutation) {
	mutations.add(mutation);
    }
    
    /**
     * Adds a discovered silent mutation.
     * 
     * @param mutation A discovered silent mutation (in the given String format)
     * 
     * @author Ben Kohr
     */
    public void addSilentMutation(String mutation) {
	silentMutations.add(mutation);
    }
    
    
    /**
     * Sets the point of the data starting to be unreliable.
     * 
     * @param endPoint The index of the data String from which on the data is considered unreliable
     * 
     * @author Ben Kohr
     */
    public void setEndOfThrustworthyness(int endPoint) {
	endOfTrustworthyness = endPoint;
    }
    
    
    /**
     * Updates the sequence (e.g. after trimming off the ends).
     * 
     * @param seq The new sequence of nucleotides as a String
     * @author Ben Kohr
     */
    public void updateSequence(String seq) {
	sequence = seq;
    }
    
    
    /**
     * Returns the referenced gene.
     * 
     * @return the gene referenced with this sequence
     */
    public Gene getReferencedGene() {
	return referencedGene;
    }
    
    
    
}
