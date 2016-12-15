package analysis;

import java.util.LinkedList;
import java.util.TreeMap;

import io.ConsoleIO;


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
   * @author Kevin
   */
  public static double checkSimilarity(Sequence first, Sequence second) {
    return checkSimilarity(first.sequence, second.sequence);
  }

  /**
   * Compares to String and returns their similarity without finding the exact differences.
   * 
   * @param first The first String
   * @param second The second String
   * 
   * @return Similarity measure
   * @author Kevin
   */
  public static double checkSimilarity(String first, String second) {
    double levenshteinIndex = getLevenshteinIndex(first, second);
    double avgLength = (first.length() + second.length()) / 2.0;
    return Math.max(0, 100 - (levenshteinIndex / (avgLength / 100)));
  }

  /**
   * calculates Levensthein Matrix of first and second using calculateLevenshteinMatrix(first,
   * second) and returns the Levenshtein Index
   * 
   * @param first
   * @param second
   * @return
   * @author Kevin
   */
  public static int getLevenshteinIndex(String first, String second) {
    int[][] matrix = calculateLevenshteinMatrix(first, second);
    return getLevenshteinIndex(matrix);
  }

  /**
   * gets Levenshtein index out of Levenshtein Matrix
   * 
   * @param matrix the Levenshtein Matrix
   * @return
   * @author Kevin
   */
  public static int getLevenshteinIndex(int[][] matrix) {
    // get levensthein index out of matrix
    return matrix[matrix.length - 1][matrix[matrix.length - 1].length - 1];
  }

  /**
   * note: return with nops!
   * 
   * @param sOne
   * @param sTwo
   * @return
   * @author Kevin
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

    // variable to save difference in characters
    int cost = 0;

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

    // create empty Levenshtein matrix
    int[][] levenMatrix = new int[matrixHeight][matrixWidth];



    // fill first line from 1 to |first|
    for (int i = 1; i < matrixHeight; i++) {
      levenMatrix[i][0] = i;
    }
    // fill first row from 1 to |second|
    for (int j = 1; j < matrixWidth; j++) {
      levenMatrix[0][j] = j;
    }

    // variable to save difference in characters
    int cost = 0;

    // iterate over 2D array
    for (int j = 1; j < matrixWidth; j++) {
      for (int i = 1; i < matrixHeight; i++) {
        // if characters are equal cost for replacement = 0
        if (first.charAt(i - 1) == second.charAt(j - 1)) {
          cost = 0;
        } else {
          cost = 1;
        }
        // deletion of char, insertion of char, change of char:
        levenMatrix[i][j] = Math.min(Math.min(levenMatrix[i - 1][j] + 1, levenMatrix[i][j - 1] + 1),
            levenMatrix[i - 1][j - 1] + cost);
      }
    }
    return levenMatrix;
  }

  /**
   * cuts out the Vector from a given sequence
   * 
   * @param sequence
   * @param gen
   * @author Kevin
   */
  public static void trimVector(AnalysedSequence sequence, Gene gen) {
    findBestMatchFast(sequence, gen);
  }

  /**
   * same as find Best Match but faster
   * @param sequence
   * @param gen
   * @return
   */
  public static void findBestMatchFast(AnalysedSequence sequence, Gene gen){
    int[][] levenMatrix = calculateLevenshteinMatrix(gen.sequence, sequence.sequence);
    
    //begin and end possition of final string
    int begin = 0;
    int end = sequence.length();
    boolean endFound = false;
    boolean potentialBegin = false;
    int potentialBeginPosition = 0;
    
    int row = levenMatrix.length-1;//gen
    int line = levenMatrix[0].length-1;//sequence
    int originalBegin = 0;
    //System.out.println();
    //ConsoleIO.printIntMatrix(levenMatrix);
    //System.out.println();
    while (row > 0 && line > 0) {
      if (levenMatrix[row-1][line-1] <= levenMatrix[row-1][line] && levenMatrix[row-1][line-1] <= levenMatrix[row][line-1]) {
        row--;
        
        line--;
        endFound = true;

        potentialBegin = true;
        potentialBeginPosition = line;
        originalBegin = row;
        
      }else if(levenMatrix[row-1][line] < levenMatrix[row][line-1]){
        row--;

      }else {
        if (!endFound) {
          end--;
        }
        line--;
      }
    }
    boolean neg = false;
    if (potentialBeginPosition > 1 && potentialBegin) {
      begin = potentialBeginPosition;
      
    }else {
      begin = 0;
      neg = true;
    }
    String result = sequence.sequence.substring(begin, end);
 
    String startCodon = gen.sequence.substring(0,3);
    if (result.contains(startCodon)) {
      int codonIndex = result.indexOf(startCodon);
      String alternativ = result.substring(codonIndex);
      //System.err.println(alternativ + " # " + result);
      if (checkSimilarity(gen.sequence, alternativ) <= checkSimilarity(gen.sequence,result)) {
        System.out.println("BESTMATCH: Start Codon found at " +(begin + codonIndex));
        result = alternativ;
        originalBegin = 0;
        begin = begin+codonIndex;
      }
    }
    
    
    begin = begin+((3-(originalBegin%3))%3);
    if (neg && begin > 0) {
      begin--;
    }
    
    System.out.println("begin = " + begin + " end = " + end);
    result = sequence.sequence.substring(begin,(end-((end-begin)%3)));
    
    sequence.setSequence(result);
    //sequence.setOffset(begin);ORIGINAL
    sequence.setOffset(originalBegin+((3-(originalBegin%3))%3));
    System.out.println(sequence.getOffset() + " = OFFSET");
  }

  /**
   * 
   * @param sequence the String to search in
   * @param gen the String to search for
   * @return
   * 
   * @author Kevin
   */
  public static Pair<Integer, String> findBestMatch(String sequence, String gen) {

    TreeMap<Double, Pair<Integer, String>> matches = new TreeMap<>();

    // go throu every supString

    // begin at every position
    for (int begin = 0; begin < (sequence.length() - 1); begin++) {

      // end at every position
      for (int end = begin + 1; end < sequence.length() + 1
          && (end - begin) - 1 < gen.length() + 1; end++) {

        // get supString
        String canditate = sequence.substring(begin, end);
        canditate = appentStringToLength(canditate, gen.length());

        // calculate similarity
        Double rating = checkSimilarity(canditate, gen);
        canditate = canditate.trim();
       // System.out.println(rating  + " # " +  canditate);
       // check if two strings have the same Similarity
        if (matches.containsKey(rating)) {

          // if yes, take the one that is nearer to original
          String doubleHit = matches.get(rating).value;
          if (Math.abs(doubleHit.trim().length() - gen.length()) > Math
              .abs(canditate.trim().length() - gen.length())) {
            matches.put(rating, new Pair<Integer, String>(begin, canditate));
          }
        } else {
          matches.put(rating, new Pair<Integer, String>(begin, canditate));
        }
      }
    }

    /*
     * while (!matches.isEmpty()) { System.out.println(matches.pollFirstEntry().getValue().value);
     * 
     * }
     */

    return matches.pollLastEntry().getValue();
  }

  public static String appentStringToLength(String input, int Length) {
    if (Length - input.length() > 0) {
      String expand = new String(new char[Length - input.length()]).replace('\0', ' ');
      input = input + expand;
    }
    return input;
  }

  /**
   * Helper class to store Pairs
   * 
   * @author Kevin
   *
   * @param <Key>
   * @param <Value>
   */
  public static class Pair<Key, Value> {

    public Key key;
    public Value value;

    public Pair(Key k, Value v) {
      key = k;
      value = v;
    }
  }
}
