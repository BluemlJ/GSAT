package analysis;

import exceptions.CorruptedSequenceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;



/**
 * This class contains the logic of analyzing sequence strings. This class serves for
 * MutationAnaysis by providing useful String Matching methods.
 * 
 * @category DNA.Utils
 * @author jannis blueml, kevin otto
 */
public class StringAnalysis {

  /**
   * A Map with all possible RNA and DNA codons with the matched AminoAcid in short form.
   */
  public static final Map<String, String> AMINO_ACID_SHORTS;

  static {
    Hashtable<String, String> tmp = new Hashtable<String, String>();
    // RNA
    tmp.put("UUU", "F");
    tmp.put("UUC", "F");
    tmp.put("UUA", "L");
    tmp.put("UUG", "L");
    tmp.put("UCU", "S");
    tmp.put("UCC", "S");
    tmp.put("UCA", "S");
    tmp.put("UCG", "S");
    tmp.put("UAU", "Y");
    tmp.put("UAC", "Y");
    tmp.put("UAA", "#");
    tmp.put("UAG", "#");
    tmp.put("UGU", "C");
    tmp.put("UGC", "C");
    tmp.put("UGA", "#");
    tmp.put("UGG", "W");

    tmp.put("CUU", "L");
    tmp.put("CUC", "L");
    tmp.put("CUA", "L");
    tmp.put("CUG", "L");
    tmp.put("CCU", "P");
    tmp.put("CCC", "P");
    tmp.put("CCA", "P");
    tmp.put("CCG", "P");
    tmp.put("CAU", "H");
    tmp.put("CAC", "H");
    tmp.put("CAA", "Q");
    tmp.put("CAG", "Q");
    tmp.put("CGU", "R");
    tmp.put("CGC", "R");
    tmp.put("CGA", "R");
    tmp.put("CGG", "R");

    tmp.put("AUU", "I");
    tmp.put("AUC", "I");
    tmp.put("AUA", "I");
    tmp.put("AUG", "M");
    tmp.put("ACU", "T");
    tmp.put("ACC", "T");
    tmp.put("ACA", "T");
    tmp.put("ACG", "T");
    tmp.put("AAU", "N");
    tmp.put("AAC", "N");
    tmp.put("AAA", "K");
    tmp.put("AAG", "K");
    tmp.put("AGU", "S");
    tmp.put("AGC", "S");
    tmp.put("AGA", "R");
    tmp.put("AGG", "R");

    tmp.put("GUU", "V");
    tmp.put("GUC", "V");
    tmp.put("GUA", "V");
    tmp.put("GUG", "V");
    tmp.put("GCU", "A");
    tmp.put("GCC", "A");
    tmp.put("GCA", "A");
    tmp.put("GCG", "A");
    tmp.put("GAU", "D");
    tmp.put("GAC", "D");
    tmp.put("GAA", "E");
    tmp.put("GAG", "E");
    tmp.put("GGU", "G");
    tmp.put("GGC", "G");
    tmp.put("GGA", "G");
    tmp.put("GGG", "G");

    // DNA
    tmp.put("TTT", "F");
    tmp.put("TTC", "F");
    tmp.put("TTA", "L");
    tmp.put("TTG", "L");
    tmp.put("TCT", "S");
    tmp.put("TCC", "S");
    tmp.put("TCA", "S");
    tmp.put("TCG", "S");
    tmp.put("TAT", "Y");
    tmp.put("TAC", "Y");
    tmp.put("TAA", "#");
    tmp.put("TAG", "#");
    tmp.put("TGT", "C");
    tmp.put("TGC", "C");
    tmp.put("TGA", "#");
    tmp.put("TGG", "W");

    tmp.put("CTT", "L");
    tmp.put("CTC", "L");
    tmp.put("CTA", "L");
    tmp.put("CTG", "L");
    tmp.put("CCT", "P");
    tmp.put("CCC", "P");
    tmp.put("CCA", "P");
    tmp.put("CCG", "P");
    tmp.put("CAT", "H");
    tmp.put("CAC", "H");
    tmp.put("CAA", "Q");
    tmp.put("CAG", "Q");
    tmp.put("CGT", "R");
    tmp.put("CGC", "R");
    tmp.put("CGA", "R");
    tmp.put("CGG", "R");

    tmp.put("ATT", "I");
    tmp.put("ATC", "I");
    tmp.put("ATA", "I");
    tmp.put("ATG", "M");
    tmp.put("ACT", "T");
    tmp.put("ACC", "T");
    tmp.put("ACA", "T");
    tmp.put("ACG", "T");
    tmp.put("AAT", "N");
    tmp.put("AAC", "N");
    tmp.put("AAA", "K");
    tmp.put("AAG", "K");
    tmp.put("AGT", "S");
    tmp.put("AGC", "S");
    tmp.put("AGA", "R");
    tmp.put("AGG", "R");

    tmp.put("GTT", "V");
    tmp.put("GTC", "V");
    tmp.put("GTA", "V");
    tmp.put("GTG", "V");
    tmp.put("GCT", "A");
    tmp.put("GCC", "A");
    tmp.put("GCA", "A");
    tmp.put("GCG", "A");
    tmp.put("GAT", "D");
    tmp.put("GAC", "D");
    tmp.put("GAA", "E");
    tmp.put("GAG", "E");
    tmp.put("GGT", "G");
    tmp.put("GGC", "G");
    tmp.put("GGA", "G");
    tmp.put("GGG", "G");
    AMINO_ACID_SHORTS = Collections.unmodifiableMap(tmp);
  }

  /**
   * Appents a string to the given lenght by adding space characters at the end if the String is
   * already long e or longe nothing is done.
   * 
   * @param input
   * 
   *        the length it should be appended to
   * @param length
   * @return
   */
  public static String appendStringToLength(String input, int length) {
    // calculate number of characters needed to reach required length
    int difference = length - input.length();

    // if string has to be appendet:
    if (difference > 0) {
      StringBuilder builder = new StringBuilder();
      while (difference > 0) { // append String with given number of spaces
        builder.append(' ');
        difference--;
      }
      String expand = builder.toString();
      input = input + expand;
    }
    return input;
  }

  /**
   * Calculates the Levensthein Matrix of two Strings. The Matrix gives information about the
   * differences of the two Strings and the best way to transform them into another.
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
   * This method checks if the complementary and/or the reverse gene has a better similarity with
   * the mutated sequence the the original gene. If this so set reference gene of sequence to the
   * best similarity.
   * 
   * @see CorruptedSequenceException
   * 
   * @param toAnalyse the sequence to check
   * @throws CorruptedSequenceException if complementary cant be build
   * @author jannis blueml
   * 
   */
  public static void checkComplementAndReverse(AnalysedSequence toAnalyse)
      throws CorruptedSequenceException {
    // initialize with standard sequence
    double bestSimilarity = checkSimilarity(toAnalyse, toAnalyse.getReferencedGene());
    // checks complementary sequence
    double toTest = checkSimilarity(toAnalyse.getComplementarySequence(),
        toAnalyse.getReferencedGene().getSequence());
    if (toTest > bestSimilarity) {
      System.out.println("complementary the sequence");
      toAnalyse.setSequence(toAnalyse.getComplementarySequence());
      bestSimilarity = toTest;
    }
    // checks reverse sequence
    toTest = checkSimilarity(toAnalyse.getReversedSequence(),
        toAnalyse.getReferencedGene().getSequence());
    if (toTest > bestSimilarity) {
      System.out.println("reverse the sequence");
      toAnalyse.setSequence(toAnalyse.getReversedSequence());
      toAnalyse.reverseQuality();
      bestSimilarity = toTest;
    }
    // checks complementary and reversed sequence
    toTest = checkSimilarity(toAnalyse.getComplementarySequence(toAnalyse.getReversedSequence()),
        toAnalyse.getReferencedGene().getSequence());
    if (toTest > bestSimilarity) {
      System.out.println("reverse + complementary the sequence");
      toAnalyse.setSequence(toAnalyse.getComplementarySequence(toAnalyse.getReversedSequence()));
      toAnalyse.reverseQuality();
      bestSimilarity = toTest;
    }

  }

  /**
   * This method is used when the gene is not the referenced gene in the AnalysedSequence Note: This
   * method overrides the old referenced gene with the given gene
   * 
   * @param toAnalyse the sequence to check
   * @param gene the gene you reference to.
   * @throws CorruptedSequenceException
   * @author jannis blueml
   */
  public static void checkComplementAndReverse(AnalysedSequence toAnalyse, Gene gene)
      throws CorruptedSequenceException {
    toAnalyse.setReferencedGene(gene);
    checkComplementAndReverse(toAnalyse);
  }

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
   * Changes the sequence representation from nucleotides to aminoacid (shortform)
   * 
   * @param nucleotides the sequence presented by nucleotides
   * @return the sequence presented by aminoAcid (shorts)
   * 
   * @author jannis blueml
   */
  public static String codonsToAminoAcids(String nucleotides) {
    nucleotides = nucleotides.toUpperCase();

    // check for empty parameter
    if (nucleotides.isEmpty()) {
      return "empty nucleotides";
    }

    StringBuilder builder = new StringBuilder();

    // checks if the nucleotides is % 3 = 0 because if not, there will be an
    // error at the end
    if (nucleotides.length() % 3 == 0) {
      // changes the nucleotides to aminoacids in shortform by using the
      // Map
      for (int i = 0; i < nucleotides.length(); i = i + 3) {
        String codon = nucleotides.substring(i, i + 3);
        String aminoacid = AMINO_ACID_SHORTS.get(codon);

        if (aminoacid != null) {
          builder.append(aminoacid);
        } else {
          builder.append("X");
        }
      }
    } else {
      return "nucleotides not modulo 3, so not convertable";
    }
    return builder.toString();
  }

  /**
   * Verry slow, do not use
   * 
   * @param toAlign the String to search in
   * @param template the String to search for
   * @return
   * @deprecated
   * @author Kevin
   */
  @Deprecated
  public static Pair<Integer, String> findBestMatch(String toAlign, String template) {

    TreeMap<Double, Pair<Integer, String>> matches = new TreeMap<>();

    // go through every supString

    // begin at every position
    for (int begin = 0; begin < (toAlign.length() - 1); begin++) {

      // end at every position
      for (int end = begin + 1; end < toAlign.length() + 1
          && (end - begin) - 1 < template.length() + 1; end++) {

        // get supString
        String canditate = toAlign.substring(begin, end);
        canditate = appendStringToLength(canditate, template.length());

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
    return matches.pollLastEntry().getValue();
  }

  /**
   * This method finds the position of the HIS-Tags in the gene.
   * 
   * @param toAnalyze the sequence to analyse
   * @return returns the startposition of the HIS-Tags. This is relative to the beginning of the
   *         sequence.
   * @author jannis blueml
   */
  public static int findHisTag(AnalysedSequence toAnalyze) {
    // initialize result and sequence in form of amino acids
    int result = -1;
    char[] seq = StringAnalysis.codonsToAminoAcids(toAnalyze.getSequence()).toCharArray();
    int counter = 1;
    // start at the end and count the HIS (H) amino acids
    while (seq.length - counter - 1 > 0 && seq[seq.length - counter - 1] == 'H') {
      counter++;
    }
    // if counter greater 5 we got a HISTag.
    if (counter > 5) {
      result = toAnalyze.getSequence().length() - counter * 3;
    }
    return result;
  }

  /**
   * Finds the gene that fits best to a given sequence by comparing it to all given genes. Known
   * genes can be found in the database.
   * 
   * @param toAnalyze Sequence to be analyzed (i.e. for which the fitting gene is to find)
   * 
   * @return The found gene.
   * 
   * @author jannis blueml, lovis heindrich
   */
  public static Pair<Gene, Double> findRightGene(AnalysedSequence toAnalyze) {
    // TODO call findRightGene(sequence, listOfGenes) with listOfGenes with
    // a database export
    return null;
  }

  /**
   * Finds the gene that fits best to a given sequence by comparing it to all given genes.
   * 
   * @param toAnalyze The sequence, we compare with the list of genes
   * @param listOfGenes A list of all genes, we want to compare with the sequence
   * @return the gene, that has the best similarity
   * @author jannis blueml
   */
  public static Gene findRightGene(AnalysedSequence toAnalyze, ArrayList<Gene> listOfGenes) {
    Gene bestgene = listOfGenes.get(0);
    double bestSimilarity = 0;

    for (Gene gene : listOfGenes) {
      double similarity = StringAnalysis.checkSimilarity(toAnalyze, gene);
      if (similarity > bestSimilarity) {
        bestSimilarity = similarity;
        bestgene = gene;
      }
    }

    return bestgene;

  }

  /**
   * 
   * @param toAnalyze
   * @return
   */
  public static int findStopcodonPosition(AnalysedSequence toAnalyze) {
    for (int i = 0; i < toAnalyze.getSequence().length() - 3; i = i + 3) {
      String aminoAcid = toAnalyze.getSequence().substring(i, i + 3);

      if (AMINO_ACID_SHORTS.get(aminoAcid) != null
          && AMINO_ACID_SHORTS.get(aminoAcid).equals("#")) {
        return i / 3;
      }
    }
    return -1;
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
   * same as find Best Match but faster
   * 
   * @param toAlign
   * @param template
   * 
   * @return
   */
  @Deprecated
  public static String trimbyLevevenstein(AnalysedSequence toAlign, boolean isOffsetExact) {

    Gene template = toAlign.getReferencedGene();
    int[][] levenMatrix = calculateLevenshteinMatrix(template.sequence, toAlign.sequence);

    // begin and end possition of final string
    int begin = 0;
    int end = toAlign.length();
    boolean endFound = false;
    boolean potentialBegin = false;
    int potentialBeginPosition = 0;

    int bestBegin = 0;
    double bestScore = 0.0;

    // gen
    int row = levenMatrix.length - 1;
    // sequence
    int line = levenMatrix[0].length - 1;

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
        // TODO try result
        potentialBegin = true;
        potentialBeginPosition = line;
        double score = checkSimilarity(toAlign.getSequence().substring(bestBegin, end),
            toAlign.getSequence().substring(line, end));
        if (bestScore < score) {
          bestScore = score;
          bestBegin = line;

        }

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
    toAlign.trimQualityArray(begin, end);

    String startCodon = template.sequence.substring(0, 3);
    if (result.contains(startCodon)) {
      int codonIndex = result.indexOf(startCodon);
      String alternativ = result.substring(codonIndex);
      // System.err.println(alternativ + " # " + result);
      if (checkSimilarity(template.sequence, alternativ) <= checkSimilarity(template.sequence,
          result)) {
        // System.out.println("BESTMATCH: Start Codon found at " +
        // (begin + codonIndex));
        result = alternativ;
        toAlign.trimQualityArray(codonIndex, toAlign.length());
        originalBegin = 0;
        begin = begin + codonIndex;
      }
    }

    // TODO EXPLAIN
    begin = begin + ((3 - (originalBegin % 3)) % 3);
    if (beginOutOfRange && begin > 0) {
      begin--;
    }

    if (isOffsetExact) {
      begin = 0;
    }

    result = toAlign.sequence.substring(begin, end - ((end - begin) % 3));
    toAlign.trimQualityArray(begin, end - ((end - begin) % 3));
    // corect vectors
    toAlign.setLeftVector(toAlign.getLeftVector() + toAlign.sequence.substring(0, begin));
    toAlign.setRightVector(toAlign.getRightVector() + toAlign.sequence.substring(end));

    // sequence.setOffset(begin);ORIGINAL
    // System.out.println(template.sequence);
    // System.out.println(toAlign.sequence.substring(toAlign.getOffset()));
    // System.err.println(toAlign.sequence);
    /*
     * System.out.println(begin); System.out.println("template    = " +template.sequence);
     * System.out.println("result      = " + result); System.out.println("alinght     = " +
     * toAlign); System.out.println("alternative = " +
     * toAlign.sequence.substring(toAlign.getOffset(),Math.min(end, toAlign.sequence.length()))); if
     * (checkSimilarity(template.sequence,
     * toAlign.sequence.substring(toAlign.getOffset(),Math.min(end, toAlign.sequence.length()))) <=
     * checkSimilarity(template.sequence, result)) { originalBegin = toAlign.getOffset(); result =
     * toAlign.sequence.substring(toAlign.getOffset(),Math.min(end, toAlign.sequence.length())); }
     */
    toAlign.setSequence(result);
    toAlign.setOffset(originalBegin + ((3 - (originalBegin % 3)) % 3));
    // toAlign.setOffset(toAlign.getOffset() + begin);
    return result;
    // System.out.println(toAlign.getOffset() + " = OFFSET");
  }

  public static void trimVector(AnalysedSequence toAlign, Gene gene) {
    toAlign.setReferencedGene(gene);
    trimVector(toAlign);
  }

  /**
   * cuts out the Vector off and writes it into the Left vector of the given sequence Also sets
   * Offset @see findOffset()
   * 
   * @param toAlign
   * @author Kevin
   */
  public static void trimVector(AnalysedSequence toAlign) {
    // **********simple Vector Cutting*****************

    // calculate offset
    findOffset(toAlign);

    // define new sequence
    String newSequence = toAlign.sequence;

    // cut off everything befor begin found by findOffset and write into
    // vector and newSequence
    String leftVector = newSequence.substring(0, Math.max(toAlign.getOffset(), 0));
    newSequence = newSequence.substring(Math.max(toAlign.getOffset(), 0));
    // alsow cut quality array to fit newSequence
    toAlign.trimQualityArray(Math.max(toAlign.getOffset(), 0), toAlign.length());

    // set vector and correct offset
    toAlign.setLeftVector(leftVector);
    toAlign.setOffset(Math.min(toAlign.getOffset(), 0));
    toAlign.setOffset(-toAlign.getOffset());
    toAlign.setSequence(newSequence);

    // TODO Ask Jannis!
    // **********modulo Cutting*****************
    /*
     * if (toAlign.getOffset() != 0) { int begin = (3 - (toAlign.getOffset() % 3) % 3); newSequence
     * = newSequence.substring(begin); int end = newSequence.length()-(newSequence.length()%3);
     * newSequence = newSequence.substring(0,end); toAlign.trimQualityArray(begin,
     * toAlign.getQuality().length); toAlign.trimQualityArray(0, end); }
     */
    // ******************************************

    toAlign.setSequence(newSequence);
  }

  /**
   * calculates the offset and writes it into the sequence WARNING: does change Offset value!
   * returns false if begin of sequence was not found Returning false may be an indicator for bad
   * sequence, but may also be perfectly fine
   * 
   * @param sequence
   * @return
   */
  public static boolean findOffset(AnalysedSequence sequence) {

    // step size for traversing
    int stepSize = 2;
    stepSize *= 3;
    // get gene and sequence as String
    String gene = sequence.getReferencedGene().getSequence();
    String seq = sequence.getSequence();

    // check for startcodon
    // if found method can return with exact begining possition
    if (seq.contains(gene.substring(0, 3))) {
      String codon = gene.substring(0, 3);

      int hitIndex = seq.indexOf(codon);
      if (hitIndex == seq.lastIndexOf(codon)) {
        sequence.setOffset(hitIndex);
        return true;
      }
    }

    boolean offsetNotFound = true;
    boolean emrgencyMode = false;

    // part of the sequence that will be testet.
    String toTest = gene.substring(0, gene.length() / stepSize);
    int testIndex = 0;

    // warn if sequence is to short for testing
    if (toTest.length() < 9) {
      System.err.println("Usable part of Sequence might be too short for good Results");
    }

    // if begin was not found
    // intense search begins
    while (offsetNotFound) {
      // index of toTest is gene
      int targetIndex = seq.indexOf(toTest);

      // test if toTest was found and if it was found only once
      if (targetIndex >= 0 && targetIndex == seq.lastIndexOf(toTest)) {
        // OFFSET found:
        // Set offset
        offsetNotFound = false;

        // changed + to - for test
        sequence.setOffset(targetIndex - testIndex);

        if (testIndex == 0) {
          return true;
        }
        System.err.println("Warning, Trimming may be inexact");
        return false;

      } else if (!emrgencyMode) {
        // check if next step is to big
        if (toTest.length() > 9) {
          toTest = toTest.substring(0, toTest.length() - 3);
        } else {
          // if (testIndex+3 + gene.length()/stepSize < gene.length())
          // {
          if (testIndex + 3 < gene.length() / stepSize) {
            testIndex++;
            toTest = gene.substring(testIndex, gene.length() / stepSize);
          } else {
            emrgencyMode = true;
          }
        }
      } else {
        // EMERGENCY MODE
        System.err.println("EMERGENCY MODE REQUIRED");
        // TODO Implement

        // TODO REMOVE
        offsetNotFound = false;
      }
    }
    return false;
  }

}
