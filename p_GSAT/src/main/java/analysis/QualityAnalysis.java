package analysis;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;

/**
 * This class contains the logic of analyzing the quality of sequences (poin) 
 * Thus, it is one of the main parts of the analyzing pipeline.
 * 
 */
public class QualityAnalysis {

  
  
  /**
   * This method checks the nucleotidestring and finds a position to trim the
   * low quality part at the end of the sequence.
   * 
   * @param file
   *            The .abi file to read
   * @return an Integer, that gives you the position in the sequence to trim
   *         the low quality part.
   * 
   * @throws IOException
   * 
   * @author bluemlj
   * 
   * */
  public static int findLowQualityClippingPosition(AnalysedSequence sequence) throws IOException {
      

      byte[] qualities = sequence.getQuality();
      double average = sequence.getAvgQuality();
      int trimmingPosition = 0;
      int countertoBreak = 0;

      for (byte b : qualities) {
          int i = b;
          if (i < average)
              countertoBreak++;
          else {
              trimmingPosition += countertoBreak + 1;
              countertoBreak = 0;
          }
          if (countertoBreak == 5) {
              return trimmingPosition;
          }
      }
      return trimmingPosition;
  }
  
  
}
