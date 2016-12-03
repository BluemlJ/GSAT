package analysis;

import java.util.LinkedList;

import org.biojava.bio.program.abi.ABITrace;

/**
 * Models a sequence under analysis (i.e. obtained from an AB1 file), which may
 * have mutations.
 * 
 * @author Ben Kohr
 * 
 */
public class AnalysedSequence extends Sequence {

	/**
	 * The name of the file this sequence was obtained from. This is used to
	 * create the name of the output file.
	 */
	private String fileName;// TODO change from String to File

	/**
	 * The gene this sequence was formed from. Useful to compare the sequence
	 * with the gene.
	 */
	
  /**
   * The gene this sequence was formed from. Useful to compare the sequence with the gene.
   */
  private Gene referencedGene;

  
  /**
   * Information to be stored in the database together with the sequence.
   */
  private String comments;
  
  
  /**
   * Indicates whether the results of this analysis have been checked by a researcher.
   */
  private boolean manuallyChecked = false;
  
  
  /**
   * The vector to be stored with this sequence.
   */
  private String vector;
  
  
  /**
   * The promotor to be stored with this sequence.
   */
  private String promotor;
  
  /**
   * A list of discovered mutations to be stored.
   */
  private LinkedList<String> mutations = new LinkedList<String>();

  
  /**
   * y-Coordinates for four nucleotides corresponding to the x-Value of the position in the array
   */
  private ABITrace abiTrace;
  
  /**
   * Array containing the quality information for the sequence
   */
  private byte[] qualities;
  
  /**
   * average quality of the sequence
   */
  private double qualityAvg;


  /**
   * Constructor calling the super constructor (which sets all given attributes).
   * 
   * @param sequence The actual sequence of nucleotides as a String
   * @param fileName Name of the file this sequence was obtained from
   * @param primer Metadata to be stored with the sequence
   * 
   * @param abiTrace
   * 
   * @author Ben Kohr
   * @param average 
   * @param qualities 
   */
  public AnalysedSequence(String sequence, String addingDate, String researcher, 
                          String fileName, String comments, ABITrace abiTrace, byte[] qualities, double average) {
    super(sequence, addingDate, researcher);
    this.fileName = fileName;
    this.comments = comments;
    this.abiTrace = abiTrace;
    this.qualities = qualities;
    this.qualityAvg = average;
  }


  /**
   * Add a discovered (normal) mutation to the list of already discovered mutations.
   * 
   * @param mutation A discovered mutation (in the given String format)
   * 
   * @author Ben Kohr
   */
  public void addMutation(String mutation) {
    mutations.add(mutation);
  }



  /**
   * This method trims a sequence, i.e. it cuts out the desired part of the nucleotide sequence. It
   * keeps the start index character, and all following characters including the end index
   * character. The rest is discarded.
   * 
   * @param startIndex The start of the sequence to be cut off
   * @param endIndex The end of the sequence to be cut off
   * 
   * @return The trimmed String
   * 
   * @author Ben Kohr
   */
  public String trimSequence(int startIndex, int endIndex) {
    String trimmed = sequence.substring(startIndex, endIndex + 1);
    return trimmed;
  }


  /**
   * Cuts off the end of a sequence. The nucleotide at the given index will the last one of the
   * trimmed sequence.
   * 
   * @param index The index after which all nucleotides are discarded
   * 
   * @return The trimmed String (trimmed at the end)
   * 
   * @see #trimSequence(int, int)
   * 
   * @author Ben Kohr
   */
  public String discardRest(int index) {
    return trimSequence(0, index);
  }


  /**
   * Cuts off the starts of a sequence. The nucleotide at the given index will the first one of the
   * trimmed sequence.
   * 
   * @param index The first index to be kept in the new sequence
   * 
   * @return The trimmed String (trimmed at the beginning)
   * 
   * @see #trimSequence(int, int)
   * 
   * @author Ben Kohr
   */
  public String discardStart(int index) {
    return trimSequence(index, sequence.length() - 1);
  }


  /**
   * Sets the referenced gene (after it is determined).
   * 
   * @param gene the determined reference gene
   */
  public void setReferencedGene(Gene gene) {
    referencedGene = gene;
  }


  /**
   * Returns the referenced gene.
   * 
   * @return the gene referenced with this sequence
   */
  public Gene getReferencedGene() {
    return referencedGene;
  }


  /**
   * Returns the sequence's file name (the file it was obtained from).
   * 
   * @return the file name
   */
  public String getFileName() {
    return fileName;
  }



  /**
   * Returns the list of normal mutations.
   * 
   * @return list of mutations
   */
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


  public String getVector() {
    return vector;
  }


  public void setVector(String vector) {
    this.vector = vector;
  }


  public String getPromotor() {
    return promotor;
  }


  public void setPromotor(String promotor) {
    this.promotor = promotor;
  }


  public byte[] getQuality() {
    return qualities;
  }


  public double getAvgQuality() {
    return qualityAvg;
  }

  
  
}
