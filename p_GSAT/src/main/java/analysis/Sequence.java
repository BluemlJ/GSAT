package analysis;

import exceptions.CorruptedSequenceException;

/**
 * Models a DNA sequence which could be a reference gene or a given sequence to analyze.
 * Encapsulates the shared behavior of genes and sequences in analysis.
 * 
 * @author Ben Kohr
 *
 */
public abstract class Sequence {

  /**
   * The sequence of nucleotides.
   */
  protected String sequence;

  
  /**
   * The date at which this sequence was added.
   */
  protected String addingDate;


  /**
   * The researcher who added this sequence.
   */
  protected String researcher;
  

  /**
   * Constructor setting the attribute (used for inheriting classes).
   * 
   * @param sequence the sequence of nucleotides
   */
	public Sequence(String sequence, String researcher) {
    this.sequence = sequence;
   // DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
   // Calendar cal = Calendar.getInstance();
   // this.addingDate = df.format(cal);

    this.researcher = researcher;
  }


  /**
   * Returns the reversed version of this object's nucleotide sequence, i.e. the nucleotide sequence
   * is inverted
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


  /**
   * Returns the complementary version of this object's nucleotide sequence, i.e. all A nucleotides
   * in the original sequence are replaced with T nucleotides (and vice versa) and all C nucleotides
   * are replaced by G nucleotides (and vice versa).
   * 
   * @return The complementary nucleotide sequence as a String
   * 
   * @author Ben Kohr
   */
  public String getComplementarySequence() throws CorruptedSequenceException {

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
          char problem = sequence.charAt(i);
          throw new CorruptedSequenceException(i, problem, sequence);
      }
    }

    String complSequence = complSeqBuilder.toString();

    return complSequence;

  }

  /**
   * This Method gives you the nucleotide sequence of this object. *
   * 
   * @return this object's nucleotide sequence
   * 
   * @author bluemlj
   */
  public String getSequence() {
    return sequence;
  }

  /**
   * This Method sets the nucleotide sequence of this object. *
   * 
   * @param sequence sets the nucleotide sequence of this object
   * 
   * @author bluemlj
   */
  public void setSequence(String sequence) {
    this.sequence = sequence;
  }

  
  public String getAddingDate() {
    return addingDate;
  }


  public void setAddingDate(String addingDate) {
    this.addingDate = addingDate;
  }


  public String getResearcher() {
    return researcher;
  }


  public void setResearcher(String researcher) {
    this.researcher = researcher;
  }
  
  
}
