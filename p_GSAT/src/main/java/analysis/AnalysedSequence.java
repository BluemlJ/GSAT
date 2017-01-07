package analysis;

import java.util.LinkedList;

/**
 * Models a sequence under analysis (i.e. obtained from an AB1 file), which may have mutations.
 * The Sequence class defines it's basic behavior.
 * 
 * @author Ben Kohr
 * @author Jannis Blueml
 * 
 */
public class AnalysedSequence extends Sequence {

  /**
   * The name of the file this sequence was obtained from. This is used to create the name of the
   * output file.
   */
  private String fileName;

  /**
   * The gene this sequence was formed from.
   */
  private Gene referencedGene;

  /**
   * Information to be stored in the database together with the sequence. Can be entered by the user.
   */
  private String comments = "";

  /**
   * Indicates whether the results of this analysis have been checked by a researcher.
   */
  private boolean manuallyChecked = false;

  /**
   * The left vector to be stored with this sequence, i.e. the nucleotides at the left side of the 
   * sequence that corresponds to the gene.
   */
  private String leftVector;

  /**
   * The right vector to be stored with this sequence, i.e. the nucleotides at the right hand side of the 
   * sequence that corresponds to the gene.
   */
  private String rightVector;

  /**
   * The promotor which generated the sequence. May be added by the user.
   */
  private String promotor;

  /**
   * A list of discovered mutations to be stored. Each of them is encoded as a String.
   */
  private LinkedList<String> mutations = new LinkedList<String>();

  /**
   * Array containing the quality information for the sequence (i.e. for each nucleotide position).
   */
  private int[] qualities;


  /**
   * Indicates how the nucleotides corresponding to the original gene are shifted in the complete sequence.
   */
  private int offset = 0;

  /**
   * Average quality of the sequence. Measured by the quality analysis.
   */
  private double qualityAvg;

  /**
   * Constructor calling the super constructor (which sets all given attributes).
   *  
   * @param sequence the nucleotide sequence as a String
   * @param researcher the researcher's name
   * @param fileName the name of the file this sequence was obtained from
   * @param qualities the int-array of quality measurements
   * @param average the average quality
   * 
   * @author Ben Kohr
   */
  public AnalysedSequence(String sequence, String researcher, String fileName, int[] qualities,
      double average) {
    super(sequence, researcher);
    this.fileName = fileName;
    this.qualityAvg = average;
    this.qualities = qualities;
  }

  
  /**
   * Add a discovered, String-encoded mutation to the list of already discovered mutations.
   * 
   * @param mutation A discovered mutation
   * 
   * @author Ben Kohr
   */
  public void addMutation(String mutation) {
    mutations.add(mutation);
  }

  
  /**
   * This method trims a sequence, i.e. it cuts out the desired part of the nucleotide sequence. It
   * keeps the start index character, and all following characters including the end index
   * character. The rest is discarded. The result is stored within the object.
   * 
   * @param startIndex The start of the sequence to be cut off
   * @param endIndex The end of the sequence to be cut off
   * 
   * @author Ben Kohr
   */
  public void trimSequence(int startIndex, int endIndex) {
    String trimmed = sequence.substring(startIndex, endIndex + 1);
    this.sequence = trimmed;
  }

  
  /**
   * This method trims a quality array, i.e. it cuts out the desired part of the nucleotide sequence
   * out of it. It keeps the quality for the start index character, and all following characters
   * including the end index character. The rest is discarded. The result is stored within the object.
   * 
   * @param startIndex The start of the array to be cut off
   * @param endIndex The end of the array to be cut off
   * 
   * @return The trimmed quality array
   * 
   * @author Jannis Blueml
   */
  public void trimQualityArray(int startIndex, int endIndex) {
    int[] trimmed = new int[endIndex - startIndex];
    for (int i = startIndex; i < Math.min(endIndex, this.getQuality().length); i++)
      trimmed[i - startIndex] = qualities[i];

    this.qualities = trimmed;
  }

  
  /**
   * Returns the length of the sequence (the number of nucleotides in it).
   * 
   * @return the sequence's length
   * 
   * @author Jannis Blueml
   */
  public int length() {
	    return sequence.length();
  }
  
  

  // GETTERs and SETTERs:
  
  public Gene getReferencedGene() {
	    return referencedGene;
  }

  public void setReferencedGene(Gene gene) {
    referencedGene = gene;
  }


  public String getFileName() {
    return fileName;
  }

  public LinkedList<String> getMutations() {
    return mutations;
  }

  
  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  
  public boolean isManuallyChecked() {
    return manuallyChecked;
  }

  public void setManuallyChecked(boolean manuallyChecked) {
    this.manuallyChecked = manuallyChecked;
  }

  
  public String getLeftVector() {
    return leftVector;
  }

  public void setLeftVector(String vector) {
    this.leftVector = vector;
  }

  
  public String getRightVector() {
    return rightVector;
  }

  public void setRightVector(String vector) {
    this.rightVector = vector;
  }

  
  public String getPromotor() {
    return promotor;
  }

  public void setPromotor(String promotor) {
    this.promotor = promotor;
  }

  
  public int[] getQuality() {
    return qualities;
  }

  public double getAvgQuality() {
    return qualityAvg;
  }


  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

}
