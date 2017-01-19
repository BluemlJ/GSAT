package exceptions;

import analysis.AnalysedSequence;

/**
 * An instance of this class is thrown if a char, which is not one of 'A', 'T', 'C' or 'G', is
 * detected in a sequence.
 * 
 * @author Ben Kohr
 *
 */
public class CorruptedSequenceException extends Exception {

  /**
   * The index of the observed wrong character
   */
  public int index;


  /**
   * The nucleotide list.
   */
  public String nucleotides;


  /**
   * The problematic character.
   */
  public char problem;


  /**
   * If available, the corrupt sequence is referenced. If only a nucleotide String is given, this
   * will be a null reference.
   */
  public AnalysedSequence sequence;



  /**
   * This Constructor specifies the position, the type of wrong character in an observed sequence
   * object and the object itself.
   * 
   * 
   * @author Ben Kohr, Jannis Blueml (17.01. Update)
   */
  public CorruptedSequenceException() {
    super(
        "Problem in observed AnalyzedSequence: The Sequence has corrupted nucleptides, which means there are not 'A','C','G','T' or 'U'.");
  }



  /**
   * Constructor specifies the position, the type of wrong character in an observed sequence and the
   * nucleotide String.
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
