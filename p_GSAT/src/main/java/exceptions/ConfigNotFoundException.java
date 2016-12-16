package exceptions;

/**
 * This exception is thrown when the config file could not be found.
 * 
 * @author lovisheindrich
 *
 */
public class ConfigNotFoundException extends Exception {
  public String path;

  public ConfigNotFoundException(String path) {
    super("Config at path: " + path + " could not be found");
    this.path = path;
  }
}
