package exceptions;

/**
 * An instance of this class is thrown if an error occurred during processing data within the
 * database.
 * 
 * @author Ben Kohr
 *
 */
public class DatabaseErrorException extends Exception {

  /**
   * Constructor calling the super constructor.
   * 
   * @author Ben Kohr
   */
  public DatabaseErrorException() {
    super("Error while database processing.");
  }

}
