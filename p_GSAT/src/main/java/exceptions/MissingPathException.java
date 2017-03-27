package exceptions;

/**
 * This exception is thrown if a path is needed but not set.
 * 
 * @author Ben Kohr
 */
public class MissingPathException extends Exception {

  /**
   * This field indicates whether the path is used for reading or writing.
   */
  public PathUsage usage;

  /**
   * Constructor calling the super constructor. It also specifies the error message.
   * 
   * @param usage The usage of the path which is not set (reading or writing).
   * 
   * @author Ben Kohr
   */
  public MissingPathException(PathUsage usage) {
    super("Missing path detected (Usage: " + usage + ").");
    this.usage = usage;
  }

}
