package analysis;

import exceptions.CorruptedSequenceException;
import io.ProblematicComment;

/**
 * This class contains the logic of analyzing the quality of sequences. Thus, it is one of the main
 * parts of the analyzing pipeline. The quality checks are performed before searching for mutations.
 * 
 * @author Jannis Blueml
 * @author Lovis Heindrich
 */
public class QualityAnalysis {

  /**
   * This parameter sets the minimal quality to start a sequence. This can be changed by the user.
   * The default value is 30.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int avgApproximationStart = 30;


  /**
   * This parameter sets the minimal quality to end a sequence. It can be changed by the user. The
   * default value is 25.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int avgApproximationEnd = 25;


  /**
   * This variable represents how many bad quality nucleotide are allowed before the sequence gets
   * cut off. This can be changed by the user. The default value is 9.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int breakcounter = 9;


  /**
   * Number of Nucleotides which will be used for the average Quality calculations. This can be
   * changed by the user. The default value is 20.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int numAverageNucleotides = 20;


  /**
   * This variable represents how many good quality nucleotides are needed before the sequence's
   * start gets detected. This serves as a sanity check (in case the quality is low, a start is not
   * 'sure' to be found). This can be changed by the user. The default value is 3.
   */
  private static int startcounter = 3;


  /**
   * This method is called before analysing a sequence, but after trimming them. It checks if any
   * 'X' symbols are within the trimmed sequence. This would mean the sequence is corrupt and can't
   * be analysed.
   * 
   * @param toAnalyse the sequence to check if there is any corrupt nucleotide
   * 
   * @throws CorruptedSequenceException if there is any corruption then throw this exception.
   * 
   * @author Jannis Blueml
   */
  public static void checkIfSequenceIsClean(AnalysedSequence toAnalyse)
      throws CorruptedSequenceException {
    for (char c : toAnalyse.getSequence().toCharArray()) {
      if (c == 'X') {
        throw new CorruptedSequenceException(toAnalyse.getSequence().indexOf('X'), c,
            toAnalyse.getSequence());
      }
    }
  }


  /**
   * This method checks the nucleotide string and finds a position to trim the low quality part of
   * the sequence. This is necessary in order to base the following steps on reliable data. It
   * returns an array with three entries: The first indicates the start position to trim (counting
   * from the first nucleotide), the second indicates the end position to trim (counting from the
   * first nucleotide), and the third one corresponds to the start position on amino acid basis
   * (counting from the first amino acid).
   * 
   * @param sequence the sequence obtained from the AB1 file
   * 
   * @see #getAverageTrimmingPosition(int[], int)
   * 
   * @return an array indicating where to trim
   * 
   * @author Jannis Blueml
   * @author Lovis Heindrich
   * 
   */
  public static int[] findLowQuality(AnalysedSequence sequence) {

    int[] qualities = sequence.getQuality();

    int[] trimmingPosition = {sequence.length(), sequence.length(), 0};
    int countertoBreak = 0;
    int countertoStart = 0;
    boolean startfound = false;
    int counter = 0;

    // check every quality value and find start and end by counting
    for (int quality : qualities) {
      // counting start
      if (!startfound) {
        if (quality > avgApproximationStart) {
          countertoStart++;
        } else {
          counter += countertoStart + 1;
          countertoStart = 0;
        }
        // found start
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
        // found end
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
   * This method provides an additional quality measurement by detecting nucleotide substrings with
   * an average low quality.
   * 
   * @param qualities the sequence which needs to be trimmed
   * @param startPosition the first array index which will be used
   * 
   * @return The position where a sequence of average low qualities starts
   * 
   * @author Lovis Heindrich
   */
  public static int getAverageTrimmingPosition(int[] qualities, int startPosition) {
    int endPosition = qualities.length;

    // if sequence is too short for analysis, return the default case
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

            return endCandidate + ((3 - (endCandidate % 3)) % 3);
          }
        }
        return qualities.length;
      }
    }

    // default case if no bad quality sequence has been found
    return qualities.length;
  }



  /**
   * This method calculate the percentage of how many nucleotides have been trimmed away by the
   * quality analysis. It also reacts on high percentages by adding comments to the AnalysedSequence
   * object passed to it (in case 70 or more percent are discarded).
   * 
   * @param lengthBefore the length before the sequence is trimmed.
   * @param toAnalyse the sequence after trimming.
   * 
   * @return a percentage between 0 and 100, indicating which percentage of nucleotides have been
   *         trimmed away
   * 
   * @author Jannis Blueml
   */
  public static int percentageOfTrimQuality(int lengthBefore, AnalysedSequence toAnalyse) {

    int lengthNow = toAnalyse.getSequence().length();
    double percentage = (lengthBefore - lengthNow) / ((double) lengthBefore);
    int percentageInt = (int) (percentage * 100);

    // react on high percentage
    if (percentageInt >= 90) {
      toAnalyse.addProblematicComment(ProblematicComment.NINETY_PERCENT_QUALITY_TRIM);
    } else if (percentageInt >= 70) {
      toAnalyse.addComments("70% or more (but less than 90%) of the processed "
          + "sequence got trimmed away by the quality analysis.");
    }

    return percentageInt;
  }


  /**
   * This method trims a sequence by removing the low quality end of the sequence. It reduced the
   * nucleotide sequence or the AnalysedSequence object passed to it.
   * 
   * @param toAnalyse the sequence to trim
   * 
   * @see #findLowQuality(AnalysedSequence)
   * 
   * @author Jannis Blueml
   */
  public static void trimLowQuality(AnalysedSequence toAnalyse) {

    int[] trimmingpositions = QualityAnalysis.findLowQuality(toAnalyse);
    toAnalyse.setOffset(toAnalyse.getOffset() + trimmingpositions[2]);
    toAnalyse.trimSequence(trimmingpositions[0], trimmingpositions[1] - 1);
  }


  /**
   * Gets the average quality in a score between 0 and 100 by getting all phred scores computing the
   * average.
   * 
   * @return The average quality between 0 and 100.
   * 
   * @author Jannis Blueml
   */
  public static int getAvgQuality(AnalysedSequence sequenceToAnalyse) {

    if (sequenceToAnalyse.getQuality().length == 0) {
      return 0;
    }

    int sum = 0;
    for (int i : sequenceToAnalyse.getQuality()) {
      sum += i;
    }
    return sum / sequenceToAnalyse.getQuality().length;
  }



  // GETTERs and SETTERs:

  public static int getAvgApproximationStart() {
    return avgApproximationStart;
  }

  public static void setAvgApproximationStart(int avgApproximationStart) {
    QualityAnalysis.avgApproximationStart = avgApproximationStart;
  }



  public static int getAvgApproximationEnd() {
    return avgApproximationEnd;
  }

  public static void setAvgApproximationEnd(int avgApproximationEnd) {
    QualityAnalysis.avgApproximationEnd = avgApproximationEnd;
  }



  public static int getNumAverageNucleotides() {
    return numAverageNucleotides;
  }

  public static void setNumAverageNucleotides(int numAverageNucleotides) {
    QualityAnalysis.numAverageNucleotides = numAverageNucleotides;
  }



  public static int getStartcounter() {
    return startcounter;
  }

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
