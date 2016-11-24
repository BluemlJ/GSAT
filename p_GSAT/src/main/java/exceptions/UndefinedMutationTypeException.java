package exceptions;


/**
 * This exception is thrown if a String was used for representing a mutation but is not 
 * valid (i.e. its none of the following: 'i' (insertion), 's' (substitution), 'd' (deletion)
 * or 'e' (known error; this may happen and is not problematic).
 * 
 * @author Ben Kohr
 *
 */
public class UndefinedMutationTypeException extends Exception {

	
	
	/**
	 * The problematic mutation String is stored.
	 */
	public String mutationString;
	
	
	/**
	 * Constructor specifies the String that was used to encode a mutation but is not valid.
	 * 
	 * @param i The index of the wrong character
	 * @param problem The wrong character
	 * 
	 * @author Ben Kohr
	 */
	public UndefinedMutationTypeException(String mutationString) {
		super("Problem in mutation result: The String " + mutationString + 
				" was observed but is not a valid mutation identifier.");

		this.mutationString = mutationString;
	}
	
}
