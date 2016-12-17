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
   * Calculates the Levensthein Matrix of two Strings. The Matrix gives information about the
   * differences of the two Strings and the best way to transform them into another.
   * 
   * for more information: https://en.wikipedia.org/wiki/Levenshtein_distance
   * 
   * @param horizontal The first String
   * @param vertical The second String
   * @return
   * @author Kevin Otto
   */
  public static int[][] calculateLevenshteinMatrix(String horizontal, String vertical) {

    int matrixHeight = horizontal.length() + 1;
    int matrixWidth = vertical.length() + 1;

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
        if (horizontal.charAt(i - 1) == vertical.charAt(j - 1)) {
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
   * same as find Best Match but faster
   * 
   * @param toAlign
   * @param template
   * @return
   */
  public static void trimVectorDEPRECATED(AnalysedSequence toAlign, Gene template) {
    int[][] levenMatrix = calculateLevenshteinMatrix(template.sequence, toAlign.sequence);

    // begin and end possition of final string
    int begin = 0;
    int end = toAlign.length();
    boolean endFound = false;
    boolean potentialBegin = false;
    int potentialBeginPosition = 0;

    int row = levenMatrix.length - 1;// gen
    int line = levenMatrix[0].length - 1;// sequence
    int originalBegin = 0;
    // System.out.println();
    // ConsoleIO.printIntMatrix(levenMatrix);
    // System.out.println();
    while (row > 0 && line > 0) {
      if (levenMatrix[row - 1][line - 1] <= levenMatrix[row - 1][line]
          && levenMatrix[row - 1][line - 1] <= levenMatrix[row][line - 1]) {
        row--;

        line--;
        endFound = true;

        potentialBegin = true;
        potentialBeginPosition = line;
        originalBegin = row;

      } else if (levenMatrix[row - 1][line] < levenMatrix[row][line - 1]) {
        row--;

      } else {
        if (!endFound) {
          end--;
        }
        line--;
      }
    }
    boolean beginOutOfRange = false;
    if (potentialBeginPosition > 1 && potentialBegin) {
      begin = potentialBeginPosition;

    } else {
      begin = 0;
      beginOutOfRange = true;
    }
    String result = toAlign.sequence.substring(begin, end);

    String startCodon = template.sequence.substring(0, 3);
    if (result.contains(startCodon)) {
      int codonIndex = result.indexOf(startCodon);
      String alternativ = result.substring(codonIndex);
      // System.err.println(alternativ + " # " + result);
      if (checkSimilarity(template.sequence, alternativ) <= checkSimilarity(template.sequence,
          result)) {
        // System.out.println("BESTMATCH: Start Codon found at " + (begin + codonIndex));
        result = alternativ;
        originalBegin = 0;
        begin = begin + codonIndex;
      }
    }

    // TODO EXPLAIN
    begin = begin + ((3 - (originalBegin % 3)) % 3);
    if (beginOutOfRange && begin > 0) {
      begin--;
    }
    // TODO remove syso
    // System.out.println("begin = " + begin + " end = " + end);
    result = toAlign.sequence.substring(begin, (end - ((end - begin) % 3)));

    toAlign.setSequence(result);
    // sequence.setOffset(begin);ORIGINAL
    toAlign.setOffset(originalBegin + ((3 - (originalBegin % 3)) % 3));
    // System.out.println(toAlign.getOffset() + " = OFFSET");
  }
  
  public static void trimVector(AnalysedSequence toAlign, Gene gene) {
    toAlign.setReferencedGene(gene);
    trimVector(toAlign);
  }
  
  public static void trimVector(AnalysedSequence toAlign) {
    //**********simple Vector Cutting*****************
    findOffset(toAlign);
    Gene gene = toAlign.getReferencedGene();
    
    //calculate the end of the sequence (as long as the gene if possible els till end)
    String newSequence = toAlign.sequence;
        
    //if necessary cut off begin
    if (toAlign.getOffset() < 0) {
      String leftVector = newSequence.substring(0, -toAlign.getOffset());
      newSequence = newSequence.substring(-toAlign.getOffset());
      toAlign.setLeftVector(leftVector);
    }
    
    int sequenceEnd = Math.min(newSequence.length(),gene.sequence.length());
    String rightVector = newSequence.substring(sequenceEnd);
    newSequence = newSequence.substring(0, sequenceEnd);
    toAlign.setRightVector(rightVector);
        
    toAlign.setSequence(newSequence);
    
    //**********complex Vector Cutting*****************
    //TODO implement
    
    //**********modulo Cutting*****************
    //TODO implement
    
  }

  public static void findOffset(AnalysedSequence seqence) {

    // get gene and sequence as String
    String gene = seqence.getReferencedGene().getSequence();
    String seq = seqence.getSequence();

    // part of the sequence that will be testet.

    String toTest = seq.substring(0, (seq.length() / 3));
    int testIndex = 0;

    if (toTest.length() < 9) {
      System.err.println("Usable part of Sequence might be too short for good Results");
    }

    boolean offsetNotFound = true;
    boolean emrgencyMode = false;

    while (offsetNotFound) {
      
      // index of toTest is gene
      int targetIndex = gene.indexOf(toTest);

      // test if toTest was found and if it was found only once
      if (targetIndex >= 0 && targetIndex == gene.lastIndexOf(toTest)) {
        //OFFSET found:
        offsetNotFound = false;
       
        //Set offset
        seqence.setOffset(targetIndex-testIndex);
        
      } else if(!emrgencyMode){
        if (testIndex + toTest.length() * 2 > seq.length()) {
          testIndex = 0;
          toTest = seq.substring(0, toTest.length() - 1);
          if (toTest.length() < 9) {
            emrgencyMode = true;
          }
        } else {
          testIndex += toTest.length();
          toTest = seq.substring(testIndex, testIndex + toTest.length());
        }
      }else {
        //EMERGENCY MODE
        System.err.println("EMERGENCY MODE REQUIRED");
        //TODO Implement
        offsetNotFound = true;//TODO REMOVE
      }
    }
  }

  /**
   * 
   * @param toAlign the String to search in
   * @param template the String to search for
   * @return
   * 
   * @author Kevin
   */
  public static Pair<Integer, String> findBestMatch(String toAlign, String template) {

    TreeMap<Double, Pair<Integer, String>> matches = new TreeMap<>();

    // go throu every supString

    // begin at every position
    for (int begin = 0; begin < (toAlign.length() - 1); begin++) {

      // end at every position
      for (int end = begin + 1; end < toAlign.length() + 1
          && (end - begin) - 1 < template.length() + 1; end++) {

        // get supString
        String canditate = toAlign.substring(begin, end);
        canditate = appentStringToLength(canditate, template.length());

        // calculate similarity
        Double rating = checkSimilarity(canditate, template);
        canditate = canditate.trim();
        // System.out.println(rating + " # " + canditate);
        // check if two strings have the same Similarity
        if (matches.containsKey(rating)) {

          // if yes, take the one that is nearer to original
          String doubleHit = matches.get(rating).second;
          if (Math.abs(doubleHit.trim().length() - template.length()) > Math
              .abs(canditate.trim().length() - template.length())) {
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

  public static String appentStringToLength(String input, int length) {
    if (length - input.length() > 0) {
      String expand = new String(new char[length - input.length()]).replace('\0', ' ');
      input = input + expand;
    }
    return input;
  }

}
