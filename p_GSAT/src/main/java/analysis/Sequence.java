package analysis;

/**
 * Models a DNA sequence which could be a reference gene or a given sequence to analyze.
 *
 */
public abstract class Sequence {

    /**
     * The sequence of nucleotides.
     */
    protected String sequence;
    
    /**
     * Constructor setting the attribute (used for inheriting classes).
     */
    public Sequence(String sequence) {
	this.sequence = sequence;
    }
    
    
    /**
     * Returns the reversed version of this sequence.
     */
    public AnalyzedSequence getReversedSequence() {
	StringBuilder builder = new StringBuilder(sequence);
	
	builder.reverse();
	String reversedSequence = builder.toString();
	
	return new AnalyzedSequence(reversedSequence);
    }
    
    
    
    /**
     * Returns the complementary version of this sequence.
     */
    public Sequence getComplementarySequence() {
	return null;
    }
    
    
}
