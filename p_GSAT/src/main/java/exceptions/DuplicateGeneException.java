package exceptions;

/**
 * This exception is thrown when the user tries to add a gene which already exists in our database
 * 
 * @author Lovis
 *
 */
public class DuplicateGeneException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -1471737516955714928L;



  public DuplicateGeneException(String name) {
    super("Gene " + name + " already exists");
  }

}
