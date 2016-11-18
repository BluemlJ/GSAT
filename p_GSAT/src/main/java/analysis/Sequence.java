package analysis;

import exceptions.CorruptedSequenceException;

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
	 * Returns the reversed version of this object's nucleotide sequence, i.e. the nucleotide
	 * sequence is inverted
	 * 
	 * @return The reversed nucleotide sequence as a String
	 * 
	 * @author Ben Kohr
	 */
	public String getReversedSequence() {

		StringBuilder builder = new StringBuilder(sequence);
		builder.reverse();
		String reversedSequence = builder.toString();

		return reversedSequence;
	}


	/**
	 * Returns the complementary version of this object's nucleotide sequence, i.e. all A
	 * nucleotides in the original sequence are replaced with T nucleotides (and vice versa)
	 * and all C nucleotides are replaced by G nucleotides (and vice versa).
	 * 
	 * @return The complementary nucleotide sequence as a String
	 * 
	 * @author Ben Kohr
	 */
	public String getComplementarySequence() throws CorruptedSequenceException {

		StringBuilder complSeqBuilder = new StringBuilder();
		int stringLength = sequence.length();

		for (int i = 0; i < stringLength; i++) {

			switch (sequence.charAt(i)) {
			case 'A':
				complSeqBuilder.append('T');
				break;
			case 'T':
				complSeqBuilder.append('A');
				break;
			case 'C':
				complSeqBuilder.append('G');
				break;
			case 'G':
				complSeqBuilder.append('C');
				break;
			default:
				char problem = sequence.charAt(i);
				throw new CorruptedSequenceException(i, problem);
			}
		}

		String complSequence = complSeqBuilder.toString();

		return complSequence;

	}

}
