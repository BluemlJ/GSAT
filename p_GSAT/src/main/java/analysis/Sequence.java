package analysis;

/**
 * Models a DNA sequence which could be a reference gene or a given sequence to analyze.
 * Encapsulates the shared behavior of genes and sequences in analysis.
 * 
 * @author Ben Kohr
 *
 */
public abstract class Sequence {

    /**
     * The sequence of nucleotides.
     */
    protected String sequence;
    
    
    /**
     * Constructor setting the attribute (used for inheriting classes).
     * 
     * @param sequence the sequence of nucleotides
     */
    public Sequence(String sequence) {
	this.sequence = sequence;
    }
    
    
    /**
     * Returns the reversed version of this sequence, i.e. 
     * the nucleotide sequence is inverted
     * 
     * @return A new reversed sequence object (including the inverted nucleotide sequence)
     * 
     * @author Ben Kohr
     */
    public AnalyzedSequence getReversedSequence() {
	
	StringBuilder builder = new StringBuilder(sequence);
	builder.reverse();
	String reversedSequence = builder.toString();
	
	return new AnalyzedSequence(reversedSequence);
    }
    
    
    
    /**
     * Returns the complementary version of this sequence, i.e. all A nucleotides in the 
     * nucleotide sequence are replaced with T nucleotides (and vice versa) and all C nucleotides 
     * are replaced by G nucleotides (and vice versa).
     * 
     *  @return A new complementary sequence object (including the complementary nucleotide sequence)
     *  
     *  @author Ben Kohr
     */
    public AnalyzedSequence getComplementarySequence() {

	StringBuilder complSeqBuilder = new StringBuilder();
	int stringLength = sequence.length();
	
	for (int i = 0; i < stringLength; i++){
	    switch (sequence.charAt(i)) {
	    	case 'A':  complSeqBuilder.append('T');
	    	case 'T':  complSeqBuilder.append('A');
	    	case 'C':  complSeqBuilder.append('G');
	    	default:   complSeqBuilder.append('C');
	    }
	}
	
	String complSequence = complSeqBuilder.toString();
	
	return new AnalyzedSequence(complSequence);
	    
	}
	
    }
    
    
    
}
