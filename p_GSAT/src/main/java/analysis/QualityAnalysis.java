package analysis;

/**
 * This class contains the logic of analyzing the quality of sequences (poin)
 * Thus, it is one of the main parts of the analyzing pipeline.
 * 
 * @author Lovis Heindrich
 */
public class QualityAnalysis {

	/**
	 * This variable represents how many bad quality nucleotide are allowed
	 * before the sequence gets cut off. Changing this may cause tests to fail.
	 */
	// TODO needs to be adjusted to achieve reasonable values on sample ab1
	// data, 10-40 looks promising
	private static int breakcounter = 5;

	/**
	 * This method checks the nucleotidestring and finds a position to trim the
	 * low quality part at the end of the sequence.
	 * 
	 * @param file
	 *            The .abi file to read
	 * @return an Integer, that gives you the position at the end of the
	 *         sequence to trim the low quality part.
	 * 
	 * 
	 * @author bluemlj
	 * 
	 */
	public static int findLowQuality(AnalysedSequence sequence) {
		int[] qualities = sequence.getQuality();
		double average = sequence.getAvgQuality();
		int trimmingPosition = 0;
		int countertoBreak = 0;

		for (int quality : qualities) {
			if (quality < (average + 50) / 2)
				countertoBreak++;
			else {
				trimmingPosition += countertoBreak + 1;
				countertoBreak = 0;
			}
			if (countertoBreak == breakcounter) {
				return trimmingPosition;
			}
		}
		return trimmingPosition;
	}

	
	/**
	 * This method trims a sequence by removing the low quality end of the sequence.
	 */
	public static void trimLowQuality(AnalysedSequence toAnalyse) {
		int end = QualityAnalysis.findLowQuality(toAnalyse);
		toAnalyse.discardRest(end -1);
	}

	public static void setBreakcounter(int update) {
		breakcounter = update;
	}

	public static int getBreakcounter() {
		return breakcounter;
	}

}
