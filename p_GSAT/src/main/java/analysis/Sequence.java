package analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import exceptions.CorruptedSequenceException;

/**
 * This class models an abstract DNA sequence which could be a reference gene or a given mutated
 * sequence to analyze. It encapsulates the shared behavior of genes and sequences under analysis.
 * 
 * @category object (abstract)
 * @author Ben Kohr
 */
public abstract class Sequence {

  /**
   * The date at which this sequence was created. Useful to find out when a sequence was analyzed.
   */
  protected String addingDate;

  /**
   * The researcher who added this sequence.
   */
  protected String researcher;

  /**
   * The sequence of nucleotides, encoded as a String. It consists of the letters A, C, T, G for the
   * four possible nucleotides adenine, cytosine, thymine and guanine.
   */
  protected String sequence;

  /**
   * Creates a new Sequence object. As an abstract class, only inheriting classes can be created via
   * this constructor.
   * 
   * It sets the nucleotide sequence and the researcher's name as passed via the parameters.
   * Furthermore, it determines the current date and stores internally.
   * 
   * @param sequence the sequence of nucleotides
   * @param researcher the name of the analyzing researcher
   * 
   * @author Ben Kohr
   */
  public Sequence(String sequence, String researcher) {
    setSequence(sequence);

    this.researcher = researcher;

    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Date dateobj = new Date();
    addingDate = df.format(dateobj);
  }

  public String getAddingDate() {
    return addingDate;
  }

  /**
   * Returns the complementary version of this object's nucleotide sequence, i.e. all A nucleotides
   * in the original sequence are replaced with T nucleotides (and vice versa) and all C nucleotides
   * are replaced by G nucleotides (and vice versa).
   * 
   * @return The complementary nucleotide sequence as a String
   * 
   * @throws CorruptedSequenceException If a nucleotide letter different from A, T, C and G is
   *         observed
   * 
   * @author Ben Kohr
   */
  public String getComplementarySequence() throws CorruptedSequenceException {
    return this.getComplementarySequence(this.getSequence());
  }

  /**
   * Returns the complementary version of this object's nucleotide sequence, i.e. all A nucleotides
   * in the original sequence are replaced with T nucleotides (and vice versa) and all C nucleotides
   * are replaced by G nucleotides (and vice versa).
   * 
   * @return The complementary nucleotide sequence as a String
   * 
   * @throws CorruptedSequenceException If a nucleotide letter different from A, T, C and G is
   *         observed
   * 
   * @author Ben Kohr
   */
  public String getComplementarySequence(String sequence) {

    StringBuilder complSeqBuilder = new StringBuilder();
    int stringLength = sequence.length();

    for (int i = 0; i < stringLength; i++) {

      switch (sequence.charAt(i)) {
        case 'A':
          complSeqBuilder.append('T');
          break;
        case 'T':
          complSeqBuilder.append('A');
          break;
        case 'C':
          complSeqBuilder.append('G');
          break;
        case 'G':
          complSeqBuilder.append('C');
          break;
        default:
          complSeqBuilder.append('X');
          // throw new CorruptedSequenceException(i, problem, sequence);
      }
    }

    String complSequence = complSeqBuilder.toString();

    return complSequence;
  }

  public String getResearcher() {
    return researcher;
  }

  // GETTERs and SETTERs:

  /**
   * Returns the reversed version of this object's nucleotide sequence, i.e. the nucleotide sequence
   * is inverted.
   * 
   * @return The reversed nucleotide sequence as a String
   * 
   * @author Ben Kohr
   */
  public String getReversedSequence() {

    StringBuilder builder = new StringBuilder(sequence);
    builder.reverse();
    String reversedSequence = builder.toString();

    return reversedSequence;
  }

  public String getSequence() {
    return sequence;
  }

  public void setAddingDate(String addingDate) {
    this.addingDate = addingDate;
  }

  public void setResearcher(String researcher) {
    this.researcher = researcher;
  }

  /**
   * This methods sets the internal nucleotide representation as a String constisting of the letters
   * A, T, C and G. It also removes all internal whitespace characters and converts in to uppercase.
   * 
   * @param sequence The nucleotide sequence to store in the object
   * 
   * @author Ben Kohr
   */
  public void setSequence(String sequence) {
    sequence = sequence.replaceAll("\\s+", "");
    this.sequence = sequence.toUpperCase();
  }

  /**
   * The toString-Method of Object is used to return the nucleotide sequence.
   * 
   * @return the nucleotide sequence String (as a represenation of this object)
   * 
   * @author Kevin Otto
   */
  @Override
  public String toString() {
    return sequence;
  }

}
