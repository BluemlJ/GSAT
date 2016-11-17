package analysis;

import java.util.LinkedList;

/**
 * Models an sequence under analysis (i.e. obtained from an AB1 file).
 * 
 */
public class AnalyzedSequence extends Sequence {

    /**
     * The gene this sequence was formed from.
     */
    private Gene referencedGene;
    
    /**
     * Information to be stored with the sequence (how the sequence was obtained).
     */
    private String primer;
    
    
    /**
     * A list of discovered mutations.
     */
    private LinkedList<String> mutations;
    
    
    
    /**
     * A list of discovered silent mutations.
     */
    private LinkedList<String> silentMutations;
    
    
    /**
     * The point in the data sequence from which on the sequence itself is 
     * considered unreliable.
     */
    private int endOfTrustworthyness;
    
    
    
    /**
     * Constructor calling the super constructor.
     */
    public AnalyzedSequence(String sequence) {
	super(sequence);
    }
    
    /**
     * Add a discovered (normal) mutation.
     */
    public void addMutation(String mutation) {
	mutations.add(mutation);
    }
    
    /**
     * Adds a discovered silent mutation.
     */
    public void addSilentMutation(String mutation) {
	silentMutations.add(mutation);
    }
    
    
    /**
     * Sets the point of the data starting to be unreliable.
     */
    public void setEndOfThrustworthyness(int endPoint) {
	endOfTrustworthyness = endPoint;
    }
    
    
    /**
     * Updates the sequence (e.g. after trimming off the ends).
     */
    public void updateSequence(String seq) {
	sequence = seq;
    }
    
    
    
    
}
