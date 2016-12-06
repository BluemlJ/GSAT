package analysis;

/**
 * This class contains the logic of analyzing the quality of sequences (poin)
 * Thus, it is one of the main parts of the analyzing pipeline.
 * 
 */
public class QualityAnalysis {

	/**
	 * This variable represents how many bad quality nucleotide are allowed
	 * before the sequence gets cut off. Changing this may cause tests to fail.
	 */
	// TODO needs to be adjusted to achieve reasonable values on sample ab1
	// data, 10-40 looks promising
	private static int BREAKCOUNTER = 5;

	/**
	 * This method checks the nucleotidestring and finds a position to trim the
	 * low quality part at the end of the sequence.
	 * 
	 * @param file
	 *            The .abi file to read
	 * @return an Integer, that gives you the position in the sequence to trim
	 *         the low quality part.
	 * 
	 * 
	 * @author bluemlj
	 * 
	 */
	public static int findLowQualityEnd(AnalysedSequence sequence) {
		int start = findLowQualityStart(sequence);
		if (start == sequence.getSequence().length()) {
			return 0;
		}
		int[] qualities = sequence.getQuality();
		double average = sequence.getAvgQuality();
		int trimmingPosition = 0;
		int countertoBreak = 0;

		for (int i = start; i < qualities.length; i++) {
			int quality = qualities[i];
			if (quality < average / 2 || quality < 30) {
				countertoBreak++;
			} else {
				trimmingPosition += countertoBreak + 1;
				countertoBreak = 0;
			}
			if (countertoBreak == BREAKCOUNTER) {
				return trimmingPosition + start;
			}
		}
		return trimmingPosition + start;
	}

	public static int findLowQualityStart(AnalysedSequence sequence) {

		int[] qualities = sequence.getQuality();
		double average = sequence.getAvgQuality();
		int trimmingPosition = 0;
		int countertoBreak = 0;

		for (int i : qualities) {
			if (i > average / 2 || i > 30)
				countertoBreak++;
			else {
				trimmingPosition += countertoBreak + 1;
				countertoBreak = 0;
			}
			if (countertoBreak == BREAKCOUNTER) {
				return trimmingPosition;
			}
		}
		return trimmingPosition;

	}

}
