package exceptions;

/**
 * This exception is thrown if a String was used for representing a mutation but is not valid.
 * 
 * @author Ben Kohr
 *
 */
public class UndefinedTypeOfMutationException extends Exception {

  /**
   * The problematic mutation String is stored.
   */
  public String mutationString;


  /**
   * Constructor specifies the String that was used to encode a mutation but is not valid.
   * 
   * @param mutationString The invalid String
   * 
   * @author Ben Kohr
   */
  public UndefinedTypeOfMutationException(String mutationString) {
    super("Problem in mutation result: The String " + mutationString
        + " was observed but is not a valid mutation identifier.");

    this.mutationString = mutationString;
  }

}
