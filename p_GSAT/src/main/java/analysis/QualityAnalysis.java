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
	private static int startcounter = 5;
	private static int avgApproximation = 50;

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
	public static int[] findLowQuality(AnalysedSequence sequence) {
		int[] qualities = sequence.getQuality();
		double average = sequence.getAvgQuality();
		int trimmingPosition[] = { sequence.length(), sequence.length() };
		int countertoBreak = 0;
		int countertoStart = 0;
		boolean startfound = false;
		int counter = 0;

		for (int quality : qualities) {
			if (!startfound) {
				if (quality > (average + avgApproximation) / 2)
					countertoStart++;
				else {
					counter += countertoStart + 1;
					countertoStart = 0;
				}
				if (countertoStart == startcounter) {
					trimmingPosition[0] = counter;
					startfound = true;
					counter += startcounter;
				}
			} else {

				if (quality < (average + avgApproximation) / 2)
					countertoBreak++;
				else {
					counter += countertoBreak + 1;
					countertoBreak = 0;
				}
				if (countertoBreak == breakcounter) {
					trimmingPosition[1] = counter;
				}
			}
		}
		
		return trimmingPosition;
	}

	/**
	 * This method trims a sequence by removing the low quality end of the
	 * sequence.
	 */
	public static void trimLowQuality(AnalysedSequence toAnalyse) {
		int[] trimmingpositions = QualityAnalysis.findLowQuality(toAnalyse);
		toAnalyse.trimSequence(trimmingpositions[0], trimmingpositions[1]-1);;
	}

	public static void setBreakcounter(int update) {
		breakcounter = update;
	}

	public static int getBreakcounter() {
		return breakcounter;
	}

}
