package exceptions;

/**
 * This exception is thrown if a field in the configuration file is unknown.
 * 
 * @author lovisheindrich
 * @author Ben Kohr
 *
 */
public class UnknownConfigFieldException extends Exception {
  

  /**
   * The name of the unknown field which was found in the configuration file.
   */
  public String unknownField;

  
  /**
   * The constructor sets the unknown field name and constructs an error message.
   * 
   * @param unknownField the name of the unknown field which was found
   * 
   * @author lovisheindrich
   * @author Ben Kohr
   */
  public UnknownConfigFieldException(String unknownField) {
    super("Error while reading field '" + unknownField + "' from the configuration file.");
    
    this.unknownField = unknownField;
  }
}
