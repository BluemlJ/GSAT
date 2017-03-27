package analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import exceptions.CorruptedSequenceException;
import io.GeneHandler;
import io.ProblematicComment;

/**
 * This class contains the logic of analyzing sequence strings. This class serves for
 * MutationAnaysis by providing useful String Matching methods.
 * 
 * @category DNA.Utils
 * @author jannis blueml, kevin otto
 */
public class StringAnalysis {

  /**
   * A Map with all possible DNA codons with the matched AminoAcid in short form and the not so
   * short but not the full name form.
   */
  public static final Map<String, Pair<String, String>> AMINO_ACID_LOOKUP;

  static {
    Hashtable<String, Pair<String, String>> tmp = new Hashtable<String, Pair<String, String>>();

    // DNA
    tmp.put("TTT", new Pair<String, String>("F", "Phe"));
    tmp.put("TTC", new Pair<String, String>("F", "Phe"));
    tmp.put("TTA", new Pair<String, String>("L", "Leu"));
    tmp.put("TTG", new Pair<String, String>("L", "Leu"));
    tmp.put("TCT", new Pair<String, String>("S", "Ser"));
    tmp.put("TCC", new Pair<String, String>("S", "Ser"));
    tmp.put("TCA", new Pair<String, String>("S", "Ser"));
    tmp.put("TCG", new Pair<String, String>("S", "Ser"));
    tmp.put("TAT", new Pair<String, String>("Y", "Tyr"));
    tmp.put("TAC", new Pair<String, String>("Y", "Tyr"));
    tmp.put("TAA", new Pair<String, String>("#", "STOP"));
    tmp.put("TAG", new Pair<String, String>("#", "STOP"));
    tmp.put("TGT", new Pair<String, String>("C", "Cys"));
    tmp.put("TGC", new Pair<String, String>("C", "Cys"));
    tmp.put("TGA", new Pair<String, String>("#", "STOP"));
    tmp.put("TGG", new Pair<String, String>("W", "Trp"));

    tmp.put("CTT", new Pair<String, String>("L", "Leu"));
    tmp.put("CTC", new Pair<String, String>("L", "Leu"));
    tmp.put("CTA", new Pair<String, String>("L", "Leu"));
    tmp.put("CTG", new Pair<String, String>("L", "Leu"));
    tmp.put("CCT", new Pair<String, String>("P", "Pro"));
    tmp.put("CCC", new Pair<String, String>("P", "Pro"));
    tmp.put("CCA", new Pair<String, String>("P", "Pro"));
    tmp.put("CCG", new Pair<String, String>("P", "Pro"));
    tmp.put("CAT", new Pair<String, String>("H", "His"));
    tmp.put("CAC", new Pair<String, String>("H", "His"));
    tmp.put("CAA", new Pair<String, String>("Q", "Gln"));
    tmp.put("CAG", new Pair<String, String>("Q", "Gln"));
    tmp.put("CGT", new Pair<String, String>("R", "Arg"));
    tmp.put("CGC", new Pair<String, String>("R", "Arg"));
    tmp.put("CGA", new Pair<String, String>("R", "Arg"));
    tmp.put("CGG", new Pair<String, String>("R", "Arg"));

    tmp.put("ATT", new Pair<String, String>("I", "Ile"));
    tmp.put("ATC", new Pair<String, String>("I", "Ile"));
    tmp.put("ATA", new Pair<String, String>("I", "Ile"));
    tmp.put("ATG", new Pair<String, String>("M", "Met"));
    tmp.put("ACT", new Pair<String, String>("T", "Thr"));
    tmp.put("ACC", new Pair<String, String>("T", "Thr"));
    tmp.put("ACA", new Pair<String, String>("T", "Thr"));
    tmp.put("ACG", new Pair<String, String>("T", "Thr"));
    tmp.put("AAT", new Pair<String, String>("N", "Asn"));
    tmp.put("AAC", new Pair<String, String>("N", "Asn"));
    tmp.put("AAA", new Pair<String, String>("K", "Lys"));
    tmp.put("AAG", new Pair<String, String>("K", "Lys"));
    tmp.put("AGT", new Pair<String, String>("S", "Ser"));
    tmp.put("AGC", new Pair<String, String>("S", "Ser"));
    tmp.put("AGA", new Pair<String, String>("R", "Arg"));
    tmp.put("AGG", new Pair<String, String>("R", "Arg"));

    tmp.put("GTT", new Pair<String, String>("V", "Val"));
    tmp.put("GTC", new Pair<String, String>("V", "Val"));
    tmp.put("GTA", new Pair<String, String>("V", "Val"));
    tmp.put("GTG", new Pair<String, String>("V", "Val"));
    tmp.put("GCT", new Pair<String, String>("A", "Ala"));
    tmp.put("GCC", new Pair<String, String>("A", "Ala"));
    tmp.put("GCA", new Pair<String, String>("A", "Ala"));
    tmp.put("GCG", new Pair<String, String>("A", "Ala"));
    tmp.put("GAT", new Pair<String, String>("D", "Asp"));
    tmp.put("GAC", new Pair<String, String>("D", "Asp"));
    tmp.put("GAA", new Pair<String, String>("E", "Glu"));
    tmp.put("GAG", new Pair<String, String>("E", "Glu"));
    tmp.put("GGT", new Pair<String, String>("G", "Gly"));
    tmp.put("GGC", new Pair<String, String>("G", "Gly"));
    tmp.put("GGA", new Pair<String, String>("G", "Gly"));
    tmp.put("GGG", new Pair<String, String>("G", "Gly"));
    AMINO_ACID_LOOKUP = Collections.unmodifiableMap(tmp);
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
   * @author kevin
   */
  public static String appendStringToLength(String input, int length) {
    // calculate number of characters needed to reach required length
    int difference = length - input.length();

    // if string has to be appendet:
    if (difference > 0) {
      StringBuilder builder = new StringBuilder();
      // append String with given number of spaces
      while (difference > 0) {
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
    return codonsToAminoAcids(nucleotides, true);
  }

  /**
   * Changes the sequence representation from nucleotides to aminoacid (shortform)
   * 
   * @param nucleotides the sequence presented by nucleotides
   * @return the sequence presented by aminoAcid (shorts)
   * 
   * @author jannis blueml
   */
  public static String codonsToAminoAcids(String nucleotides, boolean oneChar) {
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
        String aminoacid = "";
        if (oneChar) {
          if (AMINO_ACID_LOOKUP.get(codon) != null) {
            aminoacid = AMINO_ACID_LOOKUP.get(codon).first;
          } else {
            aminoacid = null;
          }
        } else {
          if (AMINO_ACID_LOOKUP.get(codon) != null) {
            aminoacid = AMINO_ACID_LOOKUP.get(codon).second;
          } else {
            aminoacid = null;
          }
        }


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
  public static Gene findRightGene(AnalysedSequence toAnalyze) {
    return findRightGene(toAnalyze, GeneHandler.getGeneList());
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

      if (AMINO_ACID_LOOKUP.get(aminoAcid) != null && AMINO_ACID_LOOKUP.get(aminoAcid).first != null
          && AMINO_ACID_LOOKUP.get(aminoAcid).first.equals("#")) {
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
    // create levenstein matrix:
    int[][] matrix = calculateLevenshteinMatrix(first, second);
    return getLevenshteinIndex(matrix);
  }

  /**
   * cuts of the vector of the given sequence (trim vector) and sets the given gene as reference
   * Gene
   * 
   * @see trimvector
   * @param toAlign
   * @param gene
   * @author kevin
   */
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

    newSequence = newSequence.substring(Math.max(toAlign.getOffset(), 0));
    // alsow cut quality array to fit newSequence
    toAlign.trimQualityArray(Math.max(toAlign.getOffset(), 0), toAlign.length());

    // set vector and correct offset
    toAlign.setOffset(Math.min(toAlign.getOffset(), 0));
    toAlign.setOffset(-toAlign.getOffset());
    toAlign.setSequence(newSequence);
  }

  /**
   * calculates the offset and writes it into the sequence WARNING: does change Offset value!
   * returns false if begin of sequence was not found Returning false may be an indicator for bad
   * sequence, but may also be perfectly fine
   * 
   * @param sequence
   * @return
   * @author kevin
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
      sequence.addProblematicComment(ProblematicComment.SEQUENCE_TO_SHORT);
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
        sequence.addProblematicComment(ProblematicComment.NO_MATCH_FOUND);
        offsetNotFound = false;
      }
    }
    return false;
  }

}
