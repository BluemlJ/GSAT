package analysis;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import exceptions.CorruptedSequenceException;

/**
 * This class contains the logic of analyzing sequence strings. This class serves for
 * MutationAnaysis by providing useful String Matching methods.
 * 
 */
public class StringAnalysis {

  // A Map with all possible RNA and DNA codons with the matched AminoAcid in
  // shortform.
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
    tmp.put("UAA", "STOP");
    tmp.put("UAG", "STOP");
    tmp.put("UGU", "C");
    tmp.put("UGC", "C");
    tmp.put("UGA", "STOP");
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
    tmp.put("TAA", "STOP");
    tmp.put("TAG", "STOP");
    tmp.put("TGT", "C");
    tmp.put("TGC", "C");
    tmp.put("TGA", "STOP");
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
   * Changes the sequence representation from nucleotides to aminoacid (shortform)
   * 
   * @param nucleotides the sequence presented by nucleotides
   * @return the sequence presented by aminoAcid (shorts)
   * 
   * @author bluemlj
   */
  public static String codonsToAminoAcids(String nucleotides) throws CorruptedSequenceException {
    nucleotides = nucleotides.toUpperCase();

    // check for empty parameter
    if (nucleotides.isEmpty()) return "empty nucleotides";


    StringBuilder builder = new StringBuilder();

    // checks if the nucleotides is % 3 = 0 because if not, there will be an error at the end
    if (nucleotides.length() % 3 == 0) {
      // changes the nucleotides to aminoacids in shortform by using the Map
      for (int i = 0; i < nucleotides.length(); i = i + 3) {
        String codon = nucleotides.substring(i, i + 3);
        String aminoacid = AMINO_ACID_SHORTS.get(codon);


        if (aminoacid != null)
          builder.append(aminoacid);
        // get the index of the corruptedSequenceException
        else {
          int index;
          if (!codon.matches("[ATCGU]..")) {
            index = 0;
          } else if (!codon.matches(".[ATCGU].")) {
            index = 1;
          } else {
            index = 2;
          }

          throw new CorruptedSequenceException(i + index, codon.charAt(index), nucleotides);
        }
      }
    } else
      return "nucleotides not modulo 3, so not convertable";
    return builder.toString();
  }

  /**
   * Finds the gene that fits best to a given sequence by comparing it to all given genes.
   * 
   * @param toAnalyze The sequence, we compare with the list of genes
   * @param listOfGenes A list of all genes, we want to compare with the sequence
   * @return the gene, that has the best similarity
   * @author bluemlj
   */
  public static Pair<Gene, Double> findRightGene(AnalysedSequence toAnalyze,
      LinkedList<Gene> listOfGenes) {
    Gene bestgene = null;
    double bestSimilarity = 0;

    for (Gene gene : listOfGenes) {
      double similarity = StringAnalysis.checkSimilarity(toAnalyze, gene);
      if (similarity > bestSimilarity) {
        bestSimilarity = similarity;
        bestgene = gene;
      }
    }

    return new Pair<Gene, Double>(bestgene, bestSimilarity);
  }

  /**
   * Finds the gene that fits best to a given sequence by comparing it to all given genes. Known
   * genes can be found in the local dataset.
   * 
   * @param toAnalyze Sequence to be analyzed (i.e. for which the fitting gene is to find)
   * 
   * @return The found gene.
   * 
   * @author bluemlj
   */
  public static Pair<Gene, Double> findRightGeneLocal(AnalysedSequence toAnalyze) {
    // TODO call findRightGene(sequence, listOfGenes) with listOfGenes with a database sysout export
    return null;
  }

  /**
   * Finds the gene that fits best to a given sequence by comparing it to all given genes. Known
   * genes can be found in the database.
   * 
   * @param toAnalyze Sequence to be analyzed (i.e. for which the fitting gene is to find)
   * 
   * @return The found gene.
   * 
   * @author bluemlj
   */
  public static Pair<Gene, Double> findRightGene(AnalysedSequence toAnalyze) {
    // TODO call findRightGene(sequence, listOfGenes) with listOfGenes with a database export
    return null;
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
   * 
   * @return
   */
  public static String trimbyLeve(AnalysedSequence toAlign, boolean beginCorect) {
    Gene template = toAlign.getReferencedGene();
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
        // System.out.println("BESTMATCH: Start Codon found at " +
        // (begin + codonIndex));
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

    if (beginCorect) {
      begin = 0;
    }


    result = toAlign.sequence.substring(begin, (end - ((end - begin) % 3)));

    // corect vectors
    toAlign.setLeftVector(toAlign.getLeftVector() + toAlign.sequence.substring(0, begin));
    toAlign.setRightVector(toAlign.getRightVector() + toAlign.sequence.substring(end));

    toAlign.setSequence(result);
    // sequence.setOffset(begin);ORIGINAL
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
   * 
   * @param toAlign
   * @author Kevin
   */
  public static void trimVector(AnalysedSequence toAlign) {
    // **********simple Vector Cutting*****************
    boolean offsetExact = findOffset(toAlign);

    Gene gene = toAlign.getReferencedGene();

    String newSequence = toAlign.sequence;

    // check for stopcodon //TODO edit
    boolean endexact = false;

    String codon = gene.sequence.substring(gene.sequence.length() - 3, gene.sequence.length());
    codon = "false";//TODO fix

    // if found cut at stopcodon
    for (int i = 0; i < 6; i++) {
      int hitIndex = newSequence.indexOf(codon);
      if (hitIndex >= 0 && hitIndex == newSequence.lastIndexOf(codon)) {
        newSequence = newSequence.substring(0, hitIndex + 3);
        endexact = true;
        System.out.println();
        System.err.println("start found " + codon);
        // TODO save end vector
        i = 10;
      }
      switch (i) {
        case 0:
          codon = "UAG";
          break;
        case 1:
          codon = "UAA";
          break;
        case 2:
          codon = "UGA";
          break;
        case 3:
          codon = "TAG";
          break;
        case 4:
          codon = "TAA";
          break;
        case 5:
          codon = "TGA";
          break;
        default:
          break;
      }
    }


    // calculate the end of the sequence (as long as the gene if possible els till end)
    // if necessary cut off begin
    if (offsetExact) {
      // negative offset -> sequence goes over left site of gene
      // -> cut of left of offset
      String leftVector = newSequence.substring(0, Math.abs(toAlign.getOffset()));
      newSequence = newSequence.substring(Math.abs(toAlign.getOffset()));

      // set vector and correct offset
      toAlign.setLeftVector(leftVector);
      toAlign.setOffset(0);

      toAlign.setSequence(newSequence);
    }



    // calculate exact offset if necessary;
    if (!(endexact && offsetExact)) {
      newSequence = trimbyLeve(toAlign, offsetExact);
    }

    // int sequenceEnd = Math.min(newSequence.length(), gene.sequence.length());
    // String rightVector = newSequence.substring(sequenceEnd);
    // newSequence = newSequence.substring(0, sequenceEnd);

    // **********complex Vector Cutting*****************
    // TODO implement

    // **********modulo Cutting*****************
    if (toAlign.getOffset() != 0) {
      int begin = (3 - (toAlign.getOffset() % 3) % 3);
      // newSequence = newSequence.substring(begin);
      // newSequence =
      // newSequence.substring(0,newSequence.length()-(newSequence.length()%3));
    }
    // ******************************************

    // toAlign.setRightVector(rightVector);
    toAlign.setSequence(newSequence);
  }

  /**
   * calculates offset returns true if offset was corecly found returns false if indirect offset was
   * found;
   * 
   * @param seqence
   * @return
   */
  public static boolean findOffset(AnalysedSequence seqence) {

    // get gene and sequence as String
    String gene = seqence.getReferencedGene().getSequence();
    String seq = seqence.getSequence();

    // check for startcodon
    // if found method can return with exact begining possition
    if (seq.contains(gene.substring(0, 3))) {
      String codon = gene.substring(0, 3);
      
      int hitIndex = seq.indexOf(codon);
      if (hitIndex == seq.lastIndexOf(codon)) {
        seqence.setOffset(hitIndex);        
        return true;        
      }
    }


    boolean offsetNotFound = false;
    boolean emrgencyMode = false;

    // part of the sequence that will be testet.
    String toTest = seq.substring(0, (seq.length() / 3));
    int testIndex = 0;
    

    // warn if sequence is to short for testing
    if (toTest.length() < 9) {
      System.err.println("Usable part of Sequence might be too short for good Results");
    }

    // if begin was not found
    // intense search begins
    while (offsetNotFound) {

      // index of toTest is gene
      int targetIndex = gene.indexOf(toTest);

      // test if toTest was found and if it was found only once
      if (targetIndex >= 0 && targetIndex == gene.lastIndexOf(toTest)) {
        // OFFSET found:
        offsetNotFound = false;

        // Set offset
        seqence.setOffset(targetIndex - testIndex);

      } else if (!emrgencyMode) {
        // check if next step is to big
        if (testIndex + toTest.length() * 2 > seq.length()) {
          testIndex = 0;
          toTest = seq.substring(0, toTest.length() - 1);
          if (toTest.length() < 9) {
            emrgencyMode = true;
          }
          // else begin with smaller step size
        } else {
          testIndex += toTest.length();
          toTest = seq.substring(testIndex, testIndex + toTest.length());
        }
      } else {
        // EMERGENCY MODE
        System.err.println("EMERGENCY MODE REQUIRED");
        // TODO Implement
        offsetNotFound = false;// TODO REMOVE
      }
    }
    return false;
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
