package analysis;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.abi.AbiChromatogram;

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


  private int[] ChannelA;
  private int[] ChannelC;
  private int[] ChannelG;
  private int[] ChannelT;

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

  private String primer = "";

  private double avgQuality;


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


  public AnalysedSequence() {
    super("", null);
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
   * This methods sorts the detected plasmid mixes into the list of already found mutations. In case
   * a plasmid mix has the same position as a normal mutation, the normal mutation is replaced by
   * the mix. Afterwards, the complete list is stored in the mutation list field. The list is sorted
   * by increasing mutation positions.
   * 
   * @param plasmidMixes A list of Strings (each String describes a plasmid mix)
   * 
   * @see #numberOfMutation(String)
   * 
   * @author Ben Kohr
   */
  @Deprecated // wird nie verwendet und nicht mehr benötigt
  public void sortInPlasmidmixes(LinkedList<String> plasmidMixes) {

    Comparator<String> comp = (s1, s2) -> {
      return numberOfMutation(s1) - numberOfMutation(s2);
    };

    plasmidMixes.sort(comp);
    mutations.sort(comp);

    for (String mix : plasmidMixes) {
      for (int i = 0; i < mutations.size(); i++) {
        String normalMutation = mutations.get(i);
        if (numberOfMutation(normalMutation) == numberOfMutation(mix)) {
          mutations.remove(i);
          mutations.add(i, mix);
          break;
        } else if (numberOfMutation(normalMutation) > numberOfMutation(mix)) {
          mutations.add(i, mix);
          break;
        }
      }
      if (!mutations.contains(mix)) {
        mutations.addLast(mix);
      }
    }

  }



  /**
   * This method extracts the position of a mutation from the mutation String.
   * 
   * @param mutationString the String which describes a mutation
   * 
   * @return the position of this mutation as an int value
   * 
   * @author Ben Kohr
   */
  private int numberOfMutation(String mutationString) {

    char[] chars = mutationString.toCharArray();

    int end = chars.length - 1;
    int start = chars.length - 1;
    while (!String.valueOf(chars[end]).matches("[0-9]")) {
      end--;
    }
    start = end;
    while (start >= 0 && String.valueOf(chars[start]).matches("[0-9]")) {
      start--;
    }

    char[] numberChars = Arrays.copyOfRange(chars, start + 1, end + 1);
    String numberString = new String(numberChars);

    int number = Integer.parseInt(numberString);

    return number;

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


  // GETTERs and SETTERs:

  public String getComments() {
    return comments;
  }


  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getFileName() {
    return fileName;
  }


  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  public String getLeftVector() {
    return leftVector;
  }


  public void setLeftVector(String leftVector) {
    this.leftVector = leftVector;
  }


  public boolean isManuallyChecked() {
    return manuallyChecked;
  }


  public void setManuallyChecked(boolean manuallyChecked) {
    this.manuallyChecked = manuallyChecked;
  }


  public LinkedList<String> getMutations() {
    return mutations;
  }


  public void setMutations(LinkedList<String> mutations) {
    this.mutations = mutations;
  }


  public int getOffset() {
    return offset;
  }


  public void setOffset(int offset) {
    this.offset = offset;
  }



  public int[] getQuality() {
    return qualities;
  }


  public void setQuality(int[] qualities) {
    this.qualities = qualities;
  }

  public double getAvgQuality() {
    return avgQuality;
  }

  public void setAvgQuality(double avgQuality) {
    this.avgQuality = avgQuality;
  }


  public void setPrimer(String primer) {
    this.primer = primer;
  }

  public String getPrimer() {
    return primer;
  }


  public Gene getReferencedGene() {
    return referencedGene;
  }


  public void setReferencedGene(Gene referencedGene) {
    this.referencedGene = referencedGene;
  }


  public String getRightVector() {
    return rightVector;
  }


  public void setRightVector(String rightVector) {
    this.rightVector = rightVector;
  }



  public double getTrimPercentage() {
    return trimPercentage;
  }


  public void setTrimPercentage(double trimPercentage) {
    this.trimPercentage = trimPercentage;
  }


  public int getHisTagPosition() {
    return hisTagPosition;
  }


  public void setHisTagPosition(int hisTagPosition) {
    this.hisTagPosition = hisTagPosition;
  }


  /**
   * @return the channelA
   */
  public int[] getChannelA() {
    return ChannelA;
  }


  /**
   * @param channelA the channelA to set
   */
  public void setChannelA(int[] channelA) {
    ChannelA = channelA;
  }


  /**
   * @return the channelC
   */
  public int[] getChannelC() {
    return ChannelC;
  }


  /**
   * @param channelC the channelC to set
   */
  public void setChannelC(int[] channelC) {
    ChannelC = channelC;
  }


  /**
   * @return the channelG
   */
  public int[] getChannelG() {
    return ChannelG;
  }


  /**
   * @param channelG the channelG to set
   */
  public void setChannelG(int[] channelG) {
    ChannelG = channelG;
  }


  /**
   * @return the channelT
   */
  public int[] getChannelT() {
    return ChannelT;
  }


  /**
   * @param channelT the channelT to set
   */
  public void setChannelT(int[] channelT) {
    ChannelT = channelT;
  }


}
