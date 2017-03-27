package exceptions;

/**
 * This exception is thrown in case the configuration file could not be found.
 * 
 * @author lovisheindrich
 * @author Ben Kohr
 */
public class ConfigNotFoundException extends Exception {

  /**
   * The path where the configuration file could not be found.
   */
  public String path;

  /**
   * The constructor sets the path internally and specifies an error message.
   * 
   * @param path A String specifying the path where the configuration file could not be found.
   * 
   * @author lovisheindrich
   * @author Ben Kohr
   */
  public ConfigNotFoundException(String path) {
    super("Configuration file could not be found at path " + path + ".");

    this.path = path;
  }
}
