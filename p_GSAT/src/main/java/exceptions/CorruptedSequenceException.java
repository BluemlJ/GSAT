package exceptions;

/**
 * An instance of this class is thrown if a char, which is not one of 'A', 'T', 'C' or 'G', is
 * detected in a sequence.
 * 
 * @author Ben Kohr
 */
public class CorruptedSequenceException extends Exception {

  /**
   * The index of the observed wrong character (starting with 0).
   */
  public int index;

  /**
   * The nucleotide list in which the problem was detected.
   */
  public String nucleotides;

  /**
   * The problematic character.
   */
  public char problem;


  /**
   * Constructor specifies the position, the type of wrong character in an observed sequence and the
   * nucleotide String. It also specifies an error message.
   * 
   * @param i The index of the wrong character
   * @param problem The wrong character
   * @param nucleotides The nucleotide String which is corrupted
   * 
   * @author Ben Kohr
   */
  public CorruptedSequenceException(int i, char problem, String nucleotides) {
    super("Problem in observed nucleotide sequence: Index " + i + " is '" + problem
        + "', but should be 'A', 'T', 'C' or 'G'.");

    this.index = i;
    this.problem = problem;
    this.nucleotides = nucleotides;
  }

}
