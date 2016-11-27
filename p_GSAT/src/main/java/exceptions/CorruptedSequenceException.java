package exceptions;

import analysis.AnalyzedSequence;

/**
 * An instance of this class is thrown if a char, which is not one of 'A', 'T', 'C', 'G' or 'U', is
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
   * The problematic character.
   */
  public char problem;


  /**
   * If available, the corrupt sequence is referenced. If only a nucleotide String is given, this
   * will be a null reference.
   */
  public AnalyzedSequence sequence;


  /**
   * The nucleotide list.
   */
  public String nucleotides;



  /**
   * This Constructor specifies the position, the type of wrong character in an observed sequence
   * object and the object itself.
   * 
   * @param i The index of the wrong character
   * @param problem The wrong character
   * @param sequence The problematic sequence object
   * 
   * @author Ben Kohr
   */
  public CorruptedSequenceException(int i, char problem, AnalyzedSequence sequence) {
    super("Problem in observed AnalyzedSequence: Index " + i + " is '" + problem
        + "', but should be 'A', 'T', 'C', 'G' or 'U'.");

    this.index = i;
    this.problem = problem;
    this.sequence = sequence;
    this.nucleotides = sequence.getSequence();

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
        + "', but should be 'A', 'T', 'C', 'G' or 'U'.");

    this.index = i;
    this.problem = problem;
    this.nucleotides = nucleotides;
  }

}
