package exceptions;

/**
 * This exception is thrown if a path is needed but not set.
 * 
 * @author Ben Kohr
 *
 */
public class MissingPathException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -6175068560785854772L;

  public PathUsage usage;



  /**
   * Constructor calling the super constructor. It also specifies the type of path missing.
   * 
   * @author Ben Kohr
   */
  public MissingPathException(PathUsage usage) {
    super("Missing path detected (Usage: " + usage + ").");
    this.usage = usage;
  }

}
