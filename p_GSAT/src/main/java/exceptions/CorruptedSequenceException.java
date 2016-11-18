package exceptions;

import analysis.Sequence;

/**
 * An instance of this class is thrown if a char, which is not one of 'A', 'T', 'C' or 'G', is
 * detected in a sequence.
 * 
 * @author Ben Kohr
 *
 */
public class CorruptedSequenceException extends Exception {

	/**
	 * The index of the observed wrong character
	 */
	public int index;

	/**
	 * The problematic character.
	 */
	public char problem;



	/**
	 * Constructor specifies the position and the type of wrong character in an observed
	 * sequence object. It also sets them in the object itself.
	 * 
	 * @param i The index of the wrong character
	 * @param problem The wrong character
	 * 
	 * @author Ben Kohr
	 */
	public CorruptedSequenceException(int i, char problem) {
		super("Problem in observed sequence: Index " + i + " is '" + problem
				+ "', but should be 'A', 'T', 'C' or 'G'.");

		this.index = i;
		this.problem = problem;
	}

}
