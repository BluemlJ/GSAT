package exceptions;

/**
 * This exception is thrown when the user tries to add a gene whose name
 * already exists in the gene file.
 * 
 * @author lovisheindrich
 */
public class DuplicateGeneException extends Exception {

  /**
   * The constructor specifies the error message.
   * 
   * @param name The name of the gene which is already in the gene file.
   * 
   * @author lovisheindrich
   */
  public DuplicateGeneException(String name) {
    super("Gene " + name + " already exists.");
  }

}
