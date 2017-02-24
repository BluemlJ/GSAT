package analysis;

import java.util.LinkedList;

/**
 * Models a sequence under analysis (i.e. obtained from an AB1 file), which may have mutations. The
 * Sequence class defines it's basic behavior.
 * 
 * @category object
 * @author Ben Kohr, Jannis Blueml
 * 
 */
public class AnalysedSequence extends Sequence {

  /**
   * Information to be stored in the database together with the sequence. Can be entered by the
   * user.
   */
  private String comments = "";

  /**
   * The name of the file this sequence was obtained from. This is used to create the name of the
   * output file.
   */
  private String fileName;

  /**
   * The left vector to be stored with this sequence, i.e. the nucleotides at the left side of the
   * sequence that corresponds to the gene.
   */
  private String leftVector;

  /**
   * Indicates whether the results of this analysis have been checked by a researcher.
   */
  private boolean manuallyChecked = false;

  /**
   * A list of discovered mutations to be stored. Each of them is encoded as a String.
   */
  private LinkedList<String> mutations = new LinkedList<String>();

  /**
   * Indicates how the nucleotides corresponding to the original gene are shifted in the complete
   * sequence.
   */
  private int offset = 0;

  /**
   * The id of the primer which generated the sequence. May be added by the user. The value -1
   * indicates that there is no primer to be associated with the sequence.
   */
  private int primerId = -1;

  /**
   * Array containing the quality information for the sequence (i.e. for each nucleotide position).
   */
  private int[] qualities;

  /**
   * The gene this sequence was formed from.
   */
  private Gene referencedGene;

  /**
   * The right vector to be stored with this sequence, i.e. the nucleotides at the right hand side
   * of the sequence that corresponds to the gene.
   */
  private String rightVector;

  /**
   * The percentage of trimmed nucleotides due to the quality trim.
   */
  private double trimPercentage;

  /**
   * Specifies the position (starting with 0) where a HIS tag is found in the analysed sequence.
   * It's -1 if there is no such tag found.
   */
  private int hisTagPosition = -1;

  /**
   * Constructor calling the super constructor (which sets all given attributes).
   * 
   * @param sequence the nucleotide sequence as a String
   * @param researcher the researcher's name
   * @param fileName the name of the file this sequence was obtained from
   * @param qualities the int-array of quality measurements
   * 
   * @author Ben Kohr
   */
  public AnalysedSequence(String sequence, String researcher, String fileName, int[] qualities) {
    super(sequence, researcher);
    this.fileName = fileName;
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
   * get average Quality in a score between 0 and 100 by getting all phread scores and setting them
   * in the phread function.
   * 
   * @return the average quality between 0 and 100.
   * @author bluemlj
   */
  public double getAvgQuality() {
    int sum = 0;
    for (int i : qualities) {
      sum += i;
    }
    double tmp = sum / (1.0 * qualities.length);
    return 100 - Math.pow(10, -tmp / 10);
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

  /**
   * This method reverses the Qualityarray and set it new.
   * 
   * @author bluemlj
   */
  public void reverseQuality() {
    if (qualities == null) {
      qualities = new int[0];
    }
    int[] qualities2 = new int[qualities.length];
    for (int i = qualities.length - 1; i >= 0; i--) {
      qualities2[qualities.length - 1 - i] = qualities[i];
    }
    this.qualities = qualities2;
  }

  /**
   * This method trims a quality array, i.e. it cuts out the desired part of the nucleotide sequence
   * out of it. It keeps the quality for the start index character, and all following characters
   * including the end index character. The rest is discarded. The result is stored within the
   * object.
   * 
   * @param startIndex The start of the array to be cut off
   * @param endIndex The end of the array to be cut off
   * 
   * @author Jannis Blueml
   */
  public void trimQualityArray(int startIndex, int endIndex) {
    int[] trimmed = new int[endIndex - startIndex];
    for (int i = startIndex; i < Math.min(endIndex, this.getQuality().length); i++) {
      trimmed[i - startIndex] = qualities[i];
    }

    this.qualities = trimmed;
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
   * @return the comments
   */
  public String getComments() {
    return comments;
  }

  /**
   * @param comments the comments to set
   */
  public void setComments(String comments) {
    this.comments = comments;
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return the leftVector
   */
  public String getLeftVector() {
    return leftVector;
  }

  /**
   * @param leftVector the leftVector to set
   */
  public void setLeftVector(String leftVector) {
    this.leftVector = leftVector;
  }

  /**
   * @return the manuallyChecked
   */
  public boolean isManuallyChecked() {
    return manuallyChecked;
  }

  /**
   * @param manuallyChecked the manuallyChecked to set
   */
  public void setManuallyChecked(boolean manuallyChecked) {
    this.manuallyChecked = manuallyChecked;
  }

  /**
   * @return the mutations
   */
  public LinkedList<String> getMutations() {
    return mutations;
  }

  /**
   * @param mutations the mutations to set
   */
  public void setMutations(LinkedList<String> mutations) {
    this.mutations = mutations;
  }

  /**
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * @param offset the offset to set
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * @return the primer
   */
  public int getPrimerId() {
    return primerId;
  }

  /**
   * @param primerId the primerId to set
   */
  public void setPrimerId(int primerId) {
    this.primerId = primerId;
  }

  /**
   * @return the qualities
   */
  public int[] getQuality() {
    return qualities;
  }

  /**
   * @param qualities the qualities to set
   */
  public void setQuality(int[] qualities) {
    this.qualities = qualities;
  }

  /**
   * @return the referencedGene
   */
  public Gene getReferencedGene() {
    return referencedGene;
  }

  /**
   * @param referencedGene the referencedGene to set
   */
  public void setReferencedGene(Gene referencedGene) {
    this.referencedGene = referencedGene;
  }

  /**
   * @return the rightVector
   */
  public String getRightVector() {
    return rightVector;
  }

  /**
   * @param rightVector the rightVector to set
   */
  public void setRightVector(String rightVector) {
    this.rightVector = rightVector;
  }

  /**
   * @return the trimPercentage
   */
  public double getTrimPercentage() {
    return trimPercentage;
  }

  /**
   * @param trimPercentage the trimPercentage to set
   */
  public void setTrimPercentage(double trimPercentage) {
    this.trimPercentage = trimPercentage;
  }

  /**
   * @return the hisTagPosition
   */
  public int getHisTagPosition() {
    return hisTagPosition;
  }

  /**
   * @param hisTagPosition the hisTagPosition to set
   */
  public void setHisTagPosition(int hisTagPosition) {
    this.hisTagPosition = hisTagPosition;
  }

}
