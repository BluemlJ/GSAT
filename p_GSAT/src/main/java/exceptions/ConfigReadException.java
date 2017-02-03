package exceptions;

/**
 * This exception is thrown if a field in the config file is not found or corrupted.
 * 
 * @author lovisheindrich
 *
 */
public class ConfigReadException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -6005703763452334990L;
  public String field;

  public ConfigReadException(String field) {
    super("Error while reading " + field + " from config");
    this.field = field;
  }
}
