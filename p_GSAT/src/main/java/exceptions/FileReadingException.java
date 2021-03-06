package exceptions;

/**
 * An instance of this class is thrown if a file could not be read correctly and could therefore not
 * be transformed into a sequence.
 * 
 * @author Ben Kohr
 */
public class FileReadingException extends Exception {

  /**
   * Name of the file that caused the problem.
   */
  public String filename;

  /**
   * Constructor specifies the message about the problematic file and also stores its name in the
   * object.
   * 
   * @param filename Name of the file which caused the problem
   * 
   * @author Ben Kohr
   */
  public FileReadingException(String filename) {
    super("Error while reading file " + filename + ".");
    this.filename = filename;
  }

}
