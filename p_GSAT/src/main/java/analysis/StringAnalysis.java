package analysis;

import java.util.LinkedList;

/**
 * This class contains the logic of analyzing sequence strings. This class serves for
 * MutationAnaysis by providing useful String Matching methods.
 * 
 */
public class StringAnalysis {

  
  /**
   * Compares to sequences and returns their similarity without finding the exact differences. 
   * 
   * @param first The first sequence
   * @param second The second sequence
   * 
   * @return Similarity measure
   */
  public static double checkSimilarity(Sequence first, Sequence second) {
    return 0.0;
  }

  
  /**
   * note: return with nops!
   * 
   * @param sOne
   * @param sTwo
   * @return
   */
  private static LinkedList<String> needlemanWunsch(String sOne, String sTwo) {

    // TODO find appropriate gap Penalty
    int gabPenalty = -5;

    int matrixWidth = sOne.length() + 1;
    int matrixHeight = sTwo.length() + 1;

    // create empty Needleman Wunsch Matrix
    int[][] wunschMatrix = new int[matrixWidth][matrixHeight];

    // fill first line from 1 to |first|
    for (int i = 1; i < matrixWidth; i++) {
      wunschMatrix[i][0] = gabPenalty * i;
    }
    // fill first row from 1 to |second|
    for (int j = 1; j < matrixHeight; j++) {
      wunschMatrix[0][j] = gabPenalty * j;
    }

    int cost = 0;// variable to save difference in characters
    // iterate over 2D array
    for (int i = 1; i < matrixWidth; i++) {
      for (int j = 1; j < matrixHeight; j++) {
        int deletion = wunschMatrix[i - 1][j] + gabPenalty;
        int insertion = wunschMatrix[i][j - 1] + gabPenalty;
        int match = wunschMatrix[i - 1][j - 1] + Similarity(sOne.charAt(i), sTwo.charAt(j));
        wunschMatrix[i][j] = Math.max(Math.max(deletion, insertion), match);
      }
    }
    return findNeedlemanWunschPath(wunschMatrix);
  }


  
  private static LinkedList<String> findNeedlemanWunschPath(int[][] wunschMatrix) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private static int Similarity(char a, char b) {
    // TODO Implement
    return 0;
  }
  
  /**
   * Calculates the Levensthein Matrix of two Strings. The Matrix gives information about the
   * differences of the two Strings and the best way to transform them into another.
   * 
   * for more information: https://en.wikipedia.org/wiki/Levenshtein_distance
   * 
   * @param first The first String
   * @param second The second String
   * @return
   * @author Kevin Otto
   */
  public static int[][] calculateLevenshteinMatrix(String first, String second) {

    int matrixHeight = first.length() + 1;
    int matrixWidth = second.length() + 1;

    int[][] levenMatrix = new int[matrixHeight][matrixWidth];// create empty Levenshtein Matrix

    // fill first line from 1 to |first|
    for (int i = 1; i < matrixHeight; i++) {
      levenMatrix[i][0] = i;
    }
    // fill first row from 1 to |second|
    for (int j = 1; j < matrixWidth; j++) {
      levenMatrix[0][j] = j;
    }

    int cost = 0;// variable to save difference in characters
    // iterate over 2D array
    for (int j = 1; j < matrixWidth; j++) {
      for (int i = 1; i < matrixHeight; i++) {
        // if characters are equal cost for replacement = 0
        if (first.charAt(i - 1) == second.charAt(j - 1)) {
          cost = 0;
        } else {
          cost = 1;
        }
        levenMatrix[i][j] = Math.min(
            Math.min((levenMatrix[i - 1][j] + 1), /* deletion of char */
                (levenMatrix[i][j - 1] + 1)), /* insertion of char */
            (levenMatrix[i - 1][j - 1] + cost));/* change of char */
      }
    }
    return levenMatrix;
  }
  
  
}
