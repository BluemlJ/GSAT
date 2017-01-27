package analysis;

import exceptions.CorruptedSequenceException;

/**
 * This class contains the logic of analyzing the quality of sequences (poin) Thus, it is one of the
 * main parts of the analyzing pipeline.
 * 
 * @author Jannis Blueml, Lovis Heindrich
 */
public class QualityAnalysis {

  private static int avgApproximationEnd = 25;
  private static int avgApproximationStart = 30;

  private static int avgQualityEdge = 30;
  /**
   * This variable represents how many bad quality nucleotide are allowed before the sequence gets
   * cut off. Changing this may cause tests to fail.
   */
  // TODO needs to be adjusted to achieve reasonable values on sample ab1
  // data, 10-40 looks promising
  private static int breakcounter = 9;
  /*
   * Number of Nucleotides which will be used for the average Quality calculations
   */
  private static int numAverageNucleotides = 20;

  private static int startcounter = 3;

  public static boolean checkIfSequenceIsClean(AnalysedSequence toAnalysedSequence)
      throws CorruptedSequenceException {
    for (char c : toAnalysedSequence.getSequence().toCharArray()) {
      if (c == 'X') throw new CorruptedSequenceException();
    }
    return true;
  }

  /**
   * This method checks the nucleotidestring and finds a position to trim the low quality part at
   * the end of the sequence.
   * 
   * @param sequence the sequence getting from the abi file
   * @return an Integer, that gives you the position at the end of the sequence to trim the low
   *         quality part.
   * 
   * 
   * @author bluemlj, Lovis Heindrich
   * 
   */
  public static int[] findLowQuality(AnalysedSequence sequence) {
    int[] qualities = sequence.getQuality();

    int trimmingPosition[] = {sequence.length(), sequence.length(), 0};
    int countertoBreak = 0;
    int countertoStart = 0;
    boolean startfound = false;
    int counter = 0;

    for (int quality : qualities) {
      if (!startfound) {
        if (quality > avgApproximationStart) {
          countertoStart++;
        } else {
          counter += countertoStart + 1;

          countertoStart = 0;
        }
        if (countertoStart == startcounter) {
          trimmingPosition[0] = counter + ((3 - (counter % 3)) % 3);
          startfound = true;
          trimmingPosition[2] = trimmingPosition[0] / 3;
          counter += startcounter;
        }
      } else {

        if (quality < avgApproximationEnd)
          countertoBreak++;
        else {
          counter += countertoBreak + 1;
          countertoBreak = 0;
        }
        if (countertoBreak == breakcounter) {
          trimmingPosition[1] = counter + ((3 - (counter % 3)) % 3);
          break;
        }
      }
    }

    // get the trimming position for trimming by average quality
    int trimmingPositionAverageEnd = getAverageTrimmingPosition(qualities, trimmingPosition[0]);
    // use the position that trims earlier
    if (trimmingPositionAverageEnd < trimmingPosition[1]) {
      trimmingPosition[1] = trimmingPositionAverageEnd;
    }
    return trimmingPosition;
  }

  /**
   * This method provides an additional quality measurement by detecting substrings with an average
   * low quality
   * 
   * @param qualities the sequence which needs to be trimmed
   * @param startPosition the first array index which will be used
   * @return
   * @author Lovis Heindrich
   */
  public static int getAverageTrimmingPosition(int[] qualities, int startPosition) {
    int endPosition = qualities.length;

    // if sequence is too short for average analysis return the default case
    if (qualities.length - startPosition <= numAverageNucleotides) {
      return endPosition;
    }

    // calculate initial average quality
    double averageQuality = 0;
    for (int i = startPosition; i < startPosition + numAverageNucleotides; i++) {
      averageQuality += qualities[i];
    }

    // move to next quality frame by deleting the first quality and adding a
    // new one at the end
    for (int i = startPosition + 1; i < qualities.length - numAverageNucleotides + 1; i++) {

      // remove first element in quality frame
      averageQuality -= qualities[i - 1];
      // add new quality to frame
      averageQuality += qualities[i + numAverageNucleotides - 1];

      // check if the subsequence has bad quality
      if (averageQuality / numAverageNucleotides < avgApproximationEnd) {
        endPosition = i;
        // use first nucleotide below avgApproximationEnd as actual
        // cutting position in bad quality
        // subsequence
        for (int endCandidate = endPosition; endCandidate < qualities.length; endCandidate++) {
          if (qualities[endCandidate] < avgApproximationEnd) {
            // math magic
            return endCandidate + ((3 - (endCandidate % 3)) % 3);
          }
        }
        // should never be called
        return qualities.length;
      }
    }

    // default case if no bad quality sequence has been found
    return qualities.length;
  }

  public static int getBreakcounter() {
    return breakcounter;
  }

  /**
   * 
   * @param toAnalyse
   * @return
   * @author Jannis
   */
  public static double getQualityPercentage(AnalysedSequence toAnalyse) {
    if (toAnalyse.getQuality().length == 0) return 0;

    double counter = 0;
    for (int phred : toAnalyse.getQuality()) {
      if (phred > avgQualityEdge) counter++;
    }
    double tmp = (int) (counter / toAnalyse.getQuality().length * 1000);
    return tmp / 1000;
  }

  public static double percentageOfTrimQuality(int lengthBefore, AnalysedSequence toAnalyse) {
    // rounds result to promille
    double tmp = (int) ((double) toAnalyse.getSequence().length() / (double) lengthBefore * 1000);
    return tmp = 1 - tmp / 1000;

  }

  public static void setBreakcounter(int update) {
    breakcounter = update;
  }

  /**
   * This method trims a sequence by removing the low quality end of the sequence.
   */
  public static void trimLowQuality(AnalysedSequence toAnalyse) {
    int[] trimmingpositions = QualityAnalysis.findLowQuality(toAnalyse);
    toAnalyse.setOffset(toAnalyse.getOffset() + trimmingpositions[2]);
    toAnalyse.trimSequence(trimmingpositions[0], trimmingpositions[1] - 1);

  }

}
