package analysis;

import java.util.LinkedList;

/**
 * Models a sequence under analysis (i.e. obtained from an AB1 file), which may have mutations.
 * 
 * @author Ben Kohr
 * 
 */
public class AnalyzedSequence extends Sequence {

	/**
	 * The name of the file this sequence was obtained from. This is used to create the name of
	 * the output file.
	 */
	private String fileName;

	/**
	 * The gene this sequence was formed from. Useful to compare the sequence with the gene.
	 */
	private Gene referencedGene;

	/**
	 * Information to be stored in the database together with the sequence (indicates how the
	 * sequence was obtained).
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
	 * Constructor calling the super constructor (which sets all given attributes).
	 * 
	 * @param sequence The actual sequence of nucleotides as a String
	 * @param fileName Name of the file this sequence was obtained from
	 * @param primer Metadata to be stored with the sequence
	 * 
	 * @author Ben Kohr
	 */
	public AnalyzedSequence(String sequence, String fileName, String primer) {
		super(sequence);
		this.fileName = fileName;
		this.primer = primer;
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
	 * This method trims a sequence, i.e. it cuts out the desired part of the nucleotide
	 * sequence. It keeps the start index character, and all following characters including the
	 * end index character. The rest is discarded.
	 * 
	 * @param startIndex The start of the sequence to be cut off
	 * @param endIndex The end of the sequence to be cut off
	 * 
	 * @author Ben Kohr
	 */
	public void trimSequence(int startIndex, int endIndex) {
		sequence.substring(startIndex, endIndex + 1);
	}


	/**
	 * Cuts off the end of a sequence. The nucleotide at the given index will the last one of
	 * the trimmed sequence.
	 * 
	 * @param index The index after which all nucleotides are discarded
	 * 
	 * @see #trimSequence(int, int)
	 * 
	 * @author Ben Kohr
	 */
	public void discardRest(int index) {
		trimSequence(0, index);
	}


	/**
	 * Cuts off the starts of a sequence. The nucleotide at the given index will the first one
	 * of the trimmed sequence.
	 * 
	 * @param index The first index to be kept in the new sequence
	 * 
	 * @see #trimSequence(int, int)
	 * 
	 * @author Ben Kohr
	 */
	public void discardStart(int index) {
		trimSequence(index, sequence.length() - 1);
	}


	/**
	 * Sets the referenced gene (after it is determined).
	 * 
	 * @param gene the determined reference gene
	 */
	public void setReferencedGene(Gene gene) {
		referencedGene = gene;
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
