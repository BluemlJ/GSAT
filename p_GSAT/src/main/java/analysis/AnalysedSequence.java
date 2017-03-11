package analysis;

import java.util.LinkedList;

import io.ProblematicComment;

/**
 * Models a sequence under analysis (i.e. obtained from an AB1 file), which may
 * have mutations. The Sequence class defines it's basic behavior.
 * 
 * @category object
 * @author Ben Kohr, Jannis Blueml
 * 
 */
public class AnalysedSequence extends Sequence {

	/**
	 * Information to be stored in the database together with the sequence. Can
	 * be entered by the user.
	 */
	private String comments = "";

	private LinkedList<ProblematicComment> problems = new LinkedList<ProblematicComment>();

	/**
	 * Channels with informations about the positions of the four single Traces.
	 * Dont mess this up with the quality values. Quality is in "phread scala",
	 * this channel in "pixel" or something else.
	 */
	private int[] channelA;
	private int[] channelC;
	private int[] channelG;
	private int[] channelT;

	private int[] baseCalls;

	/**
	 * The name of the file this sequence was obtained from. This is used to
	 * create the name of the output file.
	 */
	private String fileName;

	/**
	 * Indicates whether the results of this analysis have been checked by a
	 * researcher.
	 */
	private boolean manuallyChecked = false;

	/**
	 * A list of discovered mutations to be stored. Each of them is encoded as a
	 * String.
	 */
	private LinkedList<String> mutations = new LinkedList<String>();

	/**
	 * Indicates how the nucleotides corresponding to the original gene are
	 * shifted in the complete sequence.
	 */
	private int offset = 0;

	/**
	 * Array containing the quality information for the sequence (i.e. for each
	 * nucleotide position).
	 */
	private int[] qualities;

	/**
	 * The gene this sequence was formed from.
	 */
	private Gene referencedGene;

	/**
	 * The percentage of trimmed nucleotides due to the quality trim.
	 */
	private double trimPercentage;

	private String primer = "";

	private int avgQuality;

	/**
	 * Specifies the position (starting with 0) where a HIS tag is found in the
	 * analysed sequence. It's -1 if there is no such tag found.
	 */
	private int hisTagPosition = -1;

	/**
	 * Constructor calling the super constructor (which sets all given
	 * attributes).
	 * 
	 * @param sequence
	 *            the nucleotide sequence as a String
	 * @param researcher
	 *            the researcher's name
	 * @param fileName
	 *            the name of the file this sequence was obtained from
	 * @param qualities
	 *            the int-array of quality measurements
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
	 * constructor which sets all values
	 * 
	 * @param gene
	 * @param name
	 * @param sequence
	 * @param date
	 * @param researcher
	 * @param comment
	 * @param manualcheck
	 * @param primer2
	 * @param vecLeft
	 * @param vecRight
	 * @param trimpercent
	 * @param histag
	 * @param avgquality2
	 * 
	 * @author Lovis Heindrich
	 */
	public AnalysedSequence(Gene gene, LinkedList<String> mutations, String name, String sequence, java.util.Date date,
			String researcher, String comment, boolean manuallyChecked, String primer2, int trimpercent, int histag,
			int avgquality2) {
		super(sequence, researcher, date);
		this.referencedGene = gene;
		this.mutations = mutations;
		this.fileName = name;
		this.comments = comment;
		this.manuallyChecked = manuallyChecked;
		this.primer = primer2;
		this.trimPercentage = trimpercent;
		this.hisTagPosition = histag;
		this.avgQuality = avgquality2;
	}

	/**
	 * Add a discovered, String-encoded mutation to the list of already
	 * discovered mutations.
	 * 
	 * @param mutation
	 *            A discovered mutation
	 * 
	 * @author Ben Kohr
	 */
	public void addMutation(String mutation) {
		mutations.add(mutation);
	}

	public void addProblematicComment(ProblematicComment comment) {
		problems.add(comment);
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
	 * This method trims a quality array, i.e. it cuts out the desired part of
	 * the nucleotide sequence out of it. It keeps the quality for the start
	 * index character, and all following characters including the end index
	 * character. The rest is discarded. The result is stored within the object.
	 * 
	 * @param startIndex
	 *            The start of the array to be cut off
	 * @param endIndex
	 *            The end of the array to be cut off
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
	 * This method trims a sequence, i.e. it cuts out the desired part of the
	 * nucleotide sequence. It keeps the start index character, and all
	 * following characters including the end index character. The rest is
	 * discarded. The result is stored within the object.
	 * 
	 * @param startIndex
	 *            The start of the sequence to be cut off
	 * @param endIndex
	 *            The end of the sequence to be cut off
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

	/**
	 * adds comment to existing comments
	 * 
	 * @author Kevin
	 * @param comments
	 */
	public void addComments(String comments) {
		if (this.comments.length() > 0) {
			this.comments = this.comments + ", " + comments;
		} else {
			setComments(comments);
		}

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public int getAvgQuality() {
		return avgQuality;
	}

	public void setAvgQuality(int avgQuality) {
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
		return channelA;
	}

	/**
	 * @param channelA
	 *            the channelA to set
	 */
	public void setChannelA(int[] channelA) {
		this.channelA = channelA;
	}

	/**
	 * @return the channelC
	 */
	public int[] getChannelC() {
		return channelC;
	}

	/**
	 * @param channelC
	 *            the channelC to set
	 */
	public void setChannelC(int[] channelC) {
		this.channelC = channelC;
	}

	/**
	 * @return the channelG
	 */
	public int[] getChannelG() {
		return channelG;
	}

	/**
	 * @param channelG
	 *            the channelG to set
	 */
	public void setChannelG(int[] channelG) {
		this.channelG = channelG;
	}

	/**
	 * @return the channelT
	 */
	public int[] getChannelT() {
		return channelT;
	}

	/**
	 * @param channelT
	 *            the channelT to set
	 */
	public void setChannelT(int[] channelT) {
		this.channelT = channelT;
	}

	public LinkedList<ProblematicComment> getProblematicComments() {
		return problems;
	}

	public void setBaseCalls(int[] basecalls) {
		this.baseCalls = basecalls;
	}

	public int[] getBaseCalls() {
		return baseCalls;
	}

}
