package analysis;

/**
 * This class contains the logic of analyzing the quality of sequences (poin) Thus, it is one of the
 * main parts of the analyzing pipeline.
 * 
 * @author Jannis Blueml
 */
public class QualityAnalysis {

  /**
   * This variable represents how many bad quality nucleotide are allowed before the sequence gets
   * cut off. Changing this may cause tests to fail.
   */
  // TODO needs to be adjusted to achieve reasonable values on sample ab1
  // data, 10-40 looks promising
  private static int breakcounter = 10;
  private static int startcounter = 5;

  private static int avgApproximationEnd = 25;
  private static int avgApproximationStart = 30;

  /**
   * This method checks the nucleotidestring and finds a position to trim the low quality part at
   * the end of the sequence.
   * 
   * @param sequence the sequence getting from the abi file
   * @return an Integer, that gives you the position at the end of the sequence to trim the low
   *         quality part.
   * 
   * 
   * @author bluemlj
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
        if (quality > (avgApproximationStart)) {
          countertoStart++;
        } else {
          counter += countertoStart + 1;

          countertoStart = 0;
        }
        if (countertoStart == startcounter) {
          trimmingPosition[0] = counter + (3 - (counter % 3) % 3);
          startfound = true;
          trimmingPosition[2] = trimmingPosition[0] / 3;
          counter += startcounter;
        }
      } else {

        if (quality < (avgApproximationEnd))
          countertoBreak++;
        else {
          counter += countertoBreak + 1;
          countertoBreak = 0;
        }
        if (countertoBreak == breakcounter) {
          trimmingPosition[1] = counter + (3 - (counter % 3) % 3);
          break;
        }
      }
    }

    return trimmingPosition;
  }

  /**
   * This method trims a sequence by removing the low quality end of the sequence.
   */
  public static void trimLowQuality(AnalysedSequence toAnalyse) {
    int[] trimmingpositions = QualityAnalysis.findLowQuality(toAnalyse);
    toAnalyse.setOffset(toAnalyse.getOffset() + trimmingpositions[2]);
    toAnalyse.trimSequence(trimmingpositions[0], trimmingpositions[1] - 1);
  }

  public static void setBreakcounter(int update) {
    breakcounter = update;
  }

  public static int getBreakcounter() {
    return breakcounter;
  }

}
