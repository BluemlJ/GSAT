package exceptions;

/**
 * An instance of this class is thrown if a problem with the database connection is observed.
 * 
 * @author Ben Kohr
 *
 */
public class DatabaseConnectionException extends Exception {

  /**
   * Constructor calling the super constructor.
   * 
   * @author Ben Kohr
   */
  public DatabaseConnectionException() {
    super("Connection to the database could not be established.");
  }

}
