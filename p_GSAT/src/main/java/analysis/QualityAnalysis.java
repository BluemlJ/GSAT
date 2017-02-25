package analysis;

import exceptions.CorruptedSequenceException;

/**
 * This class contains the logic of analyzing the quality of sequences (poin) Thus, it is one of the
 * main parts of the analyzing pipeline.
 * 
 * @category DNA.Utils
 * @author Jannis Blueml, Lovis Heindrich
 */
public class QualityAnalysis {

  /**
   * this parameter sets the minimal quality to start a sequence. This can be changed by the user.
   * The default value is 30.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int avgApproximationStart = 30;
  /**
   * this parameter sets the minimal quality to end a sequence. This can be changed by the user. The
   * default value is 25.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int avgApproximationEnd = 25;

  /**
   * this parameter sets the average quality to calculate the quality of the whole sequence. This
   * can be changed by the user. The default value is 30.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int avgQualityEdge = 30;
  /**
   * This variable represents how many bad quality nucleotide are allowed before the sequence gets
   * cut off. Changing this may cause tests to fail. This can be changed by user.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */

  private static int breakcounter = 9;

  /**
   * Number of Nucleotides which will be used for the average Quality calculations. This can be
   * changed by user.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int numAverageNucleotides = 20;

  // TODO by lovis
  private static int startcounter = 3;

  /**
   * This method is called before analysing a sequence but after the trimming them. It checks if any
   * 'X' are in the trimmed sequence. This would mean the sequence is corrupt and cant be analysed.
   * 
   * @see analysis.MutationAnalysis
   * @param toAnalyse the sequence to check if there is any corrupt nucleotide.
   * @throws CorruptedSequenceException if there is any corruption then throw this exception.
   * @author jannis blueml
   */
  public static void checkIfSequenceIsClean(AnalysedSequence toAnalyse)
      throws CorruptedSequenceException {
    for (char c : toAnalyse.getSequence().toCharArray()) {
      if (c == 'X') {
        throw new CorruptedSequenceException(toAnalyse.getSequence().indexOf('X'), c, toAnalyse.getSequence());
      }
    }
  }

  /**
   * This method checks the nucleotide string and finds a position to trim the low quality part at
   * the end of the sequence.
   * 
   * @param sequence the sequence getting from the abi file
   * @return an Integer, that gives you the position at the end of the sequence to trim the low
   *         quality part.
   * 
   * 
   * @author jannis blueml, Lovis Heindrich
   * 
   */
  public static int[] findLowQuality(AnalysedSequence sequence) {
    // the qualityscaling of the sequence in form of Integers between 0 and
    // 128. See phred scale for
    // more informations.
    int[] qualities = sequence.getQuality();

    // init some parameters
    int[] trimmingPosition = {sequence.length(), sequence.length(), 0};
    int countertoBreak = 0;
    int countertoStart = 0;
    boolean startfound = false;
    int counter = 0;

    // checks for every quality value and find start and end by counting.
    for (int quality : qualities) {
      // counting start
      if (!startfound) {
        if (quality > avgApproximationStart) {
          countertoStart++;
        } else {
          counter += countertoStart + 1;
          countertoStart = 0;
        }
        // fount start
        if (countertoStart == startcounter) {
          trimmingPosition[0] = counter + ((3 - (counter % 3)) % 3);
          startfound = true;
          trimmingPosition[2] = trimmingPosition[0] / 3;
          counter += startcounter;
        }
      } else {
        // counting end
        if (quality < avgApproximationEnd) {
          countertoBreak++;
        } else {
          counter += countertoBreak + 1;
          countertoBreak = 0;
        }
        // found ending
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
   * low quality.
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

  /**
   * This method calculate the percentage of trimmed nucleotides by quality of parameters.
   *
   * @param toAnalyse the trimmed sequence to get the quality for.
   * @return a value between 0 and 100. This is a percentage.
   * @author jannis blueml
   */
  public static double getQualityPercentage(AnalysedSequence toAnalyse) {
    // checks if sequence is null or empty
    if (toAnalyse == null || toAnalyse.getQuality().length == 0) {
      return 0;
    }
    // counter for calculation
    double counter = 0;

    for (int phred : toAnalyse.getQuality()) {
      if (phred > avgQualityEdge) {
        counter++;
      }
    }

    return (int) (counter / toAnalyse.getQuality().length * 100);
  }

  /**
   * This method calculate the percentage how much are trimmed away.
   * 
   * @param lengthBefore this gives the length before the sequence is trimmed.
   * @param toAnalyse the sequence after trimming.
   * @return a percentage between 0 and 100.
   * @author jannis blueml
   */
  public static int percentageOfTrimQuality(int lengthBefore, AnalysedSequence toAnalyse) {

    int lengthNow = toAnalyse.getSequence().length();
    double percentage = (lengthBefore - lengthNow) / ((double) lengthBefore);
    return (int) (percentage * 100);
  }

  /**
   * This method trims a sequence by removing the low quality end of the sequence.
   * 
   * @param toAnalyse the sequence to trim
   * @author jannis blueml
   */
  public static void trimLowQuality(AnalysedSequence toAnalyse) {
    int[] trimmingpositions = QualityAnalysis.findLowQuality(toAnalyse);
    toAnalyse.setOffset(toAnalyse.getOffset() + trimmingpositions[2]);
    toAnalyse.trimSequence(trimmingpositions[0], trimmingpositions[1] - 1);
  }

  /**
   * @return the avgApproximationStart
   */
  public static int getAvgApproximationStart() {
    return avgApproximationStart;
  }

  /**
   * @param avgApproximationStart the avgApproximationStart to set
   */
  public static void setAvgApproximationStart(int avgApproximationStart) {
    QualityAnalysis.avgApproximationStart = avgApproximationStart;
  }

  /**
   * @return the avgApproximationEnd
   */
  public static int getAvgApproximationEnd() {
    return avgApproximationEnd;
  }

  /**
   * @param avgApproximationEnd the avgApproximationEnd to set
   */
  public static void setAvgApproximationEnd(int avgApproximationEnd) {
    QualityAnalysis.avgApproximationEnd = avgApproximationEnd;
  }

  /**
   * @return the avgQualityEdge
   */
  public static int getAvgQualityEdge() {
    return avgQualityEdge;
  }

  /**
   * @param avgQualityEdge the avgQualityEdge to set
   */
  public static void setAvgQualityEdge(int avgQualityEdge) {
    QualityAnalysis.avgQualityEdge = avgQualityEdge;
  }

  /**
   * @return the numAverageNucleotides
   */
  public static int getNumAverageNucleotides() {
    return numAverageNucleotides;
  }

  /**
   * @param numAverageNucleotides the numAverageNucleotides to set
   */
  public static void setNumAverageNucleotides(int numAverageNucleotides) {
    QualityAnalysis.numAverageNucleotides = numAverageNucleotides;
  }

  /**
   * @return the startcounter
   */
  public static int getStartcounter() {
    return startcounter;
  }

  /**
   * @param startcounter the startcounter to set
   */
  public static void setStartcounter(int startcounter) {
    QualityAnalysis.startcounter = startcounter;
  }

  public static void setBreakcounter(int breakcounter) {
    QualityAnalysis.breakcounter = breakcounter;

  }

  public static int getBreakcounter() {
    return breakcounter;

  }

}
