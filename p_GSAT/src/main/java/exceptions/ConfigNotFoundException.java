package exceptions;

/**
 * This exception is thrown when the config file could not be found.
 * 
 * @author lovisheindrich
 *
 */
public class ConfigNotFoundException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -4800961894140016693L;
  public String path;



  public ConfigNotFoundException(String path) {
    super("Config at path: " + path + " could not be found");
    this.path = path;
  }
}
