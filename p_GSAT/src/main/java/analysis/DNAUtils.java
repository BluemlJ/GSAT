package analysis;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import exceptions.CorruptedSequenceException;
import exceptions.UndefinedMutationTypeException;
import io.ConsoleIO;

/**
 * This class contains the logic of analyzing DNA sequences. Thus, it is the main part of the
 * analyzing pipeline.
 * 
 */
public class DNAUtils {

  public static final Map<String, String> aminoAcidShorts;
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
    aminoAcidShorts = Collections.unmodifiableMap(tmp);
  }

  /**
   * This method finds the point in given sequence from which on the sequence is not reliable
   * anymore. The nucleotide at the point itself is the last "reliable" nucleotide.
   * 
   * @param sequence The sequence for which the end of reliability should be determined.
   * 
   * @return the index of the sequence String from which on it is considered unreliable
   */
  public int findEndOfTrustworthyness(AnalyzedSequence sequence) {
    return 0;
  }

  /**
   * Measures the quality of a sequence or of one of its parts which is used to find the end of
   * trustwothyness.
   * 
   * @return The quality measure for the given sequence (may also be a part of a reference sequence)
   */
  private static double measureQuality(AnalyzedSequence seq) {
    return 0.0;
  }

  /**
   * Compares a sequence to a gene to find mutations. Returns the list of mutations, denoted as
   * described by the department of organic chemistry.
   * 
   * @param toAnalyze The sequence to be analyzed (which may have mutations)
   * @param reference The referenced gene (used to compare the sequence against it)
   * 
   * @return A List of Mutations, represented as Strings in the given format
   * 
   * @author bluemlj
   */
  public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze)
      throws UndefinedMutationTypeException {

    Gene reference = toAnalyze.getReferencedGene();

    // list to return
    LinkedList<String> mutations = new LinkedList<>();

    // the sequence to analyze
    String mutatedSequence = toAnalyze.getSequence();

    // the gene sequence
    String originalSequence = reference.getSequence();

    // list of differences in form like "s|12|G|H"
    LinkedList<String> differences = reportDifferences(toAnalyze, reference);

    // the shiftdifference between mutated and original because of
    // injections/deletions
    int shift = 0;

    for (String difference : differences) {
      // type of mutation (s,i,d,e,n)
      String typeOfMutations = difference.split("\\|")[0];

      // position relative to mutatedSequence (of animoAcids)
      int position = Integer.parseInt(difference.split("\\|")[1]);

      String oldAminoAcid;
      String newAminoAcid;

      switch (typeOfMutations) {
        // s = substitution, normal mutation of one aminoAcid
        case "s":
          oldAminoAcid = difference.split("\\|")[2];
          newAminoAcid = difference.split("\\|")[3];
          mutations.add(reference.getName() + "   " + oldAminoAcid + position + newAminoAcid);
          break;
        // i = injection, inject of an new amino acid (aminoAcid short form)
        case "i":
          shift--;
          newAminoAcid = difference.split("\\|")[2];
          mutations.add(reference.getName() + "  +1" + newAminoAcid + position);
          break;
        // d = deletion, deletion of an amino acid
        case "d":
          shift++;
          oldAminoAcid = difference.split("\\|")[2];
          mutations.add(reference.getName() + "  -1" + oldAminoAcid + position);
          break;
        // in case of a nop, we test a silent mutation and add it if the
        // test has a positive match
        case "n":
          String oldAcid =
              originalSequence.substring((position + shift) * 3, (position + shift) * 3 + 2);
          String newAcid = mutatedSequence.substring(position * 3, position * 3 + 2);

          if (!oldAcid.equals(newAcid)) {
            mutations.add(reference.getName() + "   " + oldAcid + position + newAcid);
          }
          break;
        // in case of an error, we clear the return list and add a reading
        // frame error
        case "e":
          mutations.clear();
          mutations.add(" - reading frame");
          return mutations;
        default:
          throw new UndefinedMutationTypeException(typeOfMutations);
      }

    }
    return mutations;
  }

  /**
   * Changes the sequence representation from nukleotides to aminoacid (shortform)
   * 
   * @param nukleotides the sequence presented by nukleotides
   * @return the sequence presented by aminoAcid (shorts)
   * 
   * @author bluemlj
   */
  public static String codonsToAminoAcids(String nukleotides) throws CorruptedSequenceException {
    String aminoAcidString = "";

    if (nukleotides.isEmpty())
      return "empty nukleotides";

    if (nukleotides.length() % 3 == 0)
      for (int i = 0; i < nukleotides.length(); i = i + 3) {
        String codon = nukleotides.substring(i, i + 3);
        String aminoacid = aminoAcidShorts.get(codon);

        if (aminoacid != null)
          aminoAcidString += aminoacid;
        else {

          int index;
          if (!codon.matches("[ATCGU]..")) {
            index = 0;
          } else if (!codon.matches(".[ATCGU].")) {
            index = 1;
          } else {
            index = 2;
          }

          throw new CorruptedSequenceException(i + index, codon.charAt(index), nukleotides);
        }
      }
    else
      return "nukleotides not modulo 3, so not convertable";
    return aminoAcidString.toString();
  }

  /**
   * Finds the gene that fits best to a given sequence by comparing it to all given genes.
   * 
   * @param toAnalyze The sequence, we compare with the list of genes
   * @param listOfGenes A list of all genes, we want to compare with the sequence
   * @return the gene, that has the best similarity
   * @author bluemlj
   */
  public static Gene findRightGene(AnalyzedSequence toAnalyze, LinkedList<Gene> listOfGenes) {
    Gene bestgene = null;
    double bestSimilarity = 0;

    for (Gene gene : listOfGenes) {
      double similarity = checkSimilarity(toAnalyze, gene);
      if (similarity > bestSimilarity) {
        bestSimilarity = similarity;
        bestgene = gene;
      }
    }
    return bestgene;
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
  public static Gene findRightGene(AnalyzedSequence toAnalyze) {
    // TODO call findRightGene(sequence, listOfGenes) with listOfGenes with a database export
    return null;
  }

  /**
   * Compares to sequences and returns the differences as a list (represented by the positions). The
   * order of the input sequences is irrelevant.
   * 
   * the returned list contains String of the following syntax:
   * 
   * x|y|n|m
   * 
   * where: x is element of {s,i,d,e,n} where s stands for substitution i for insertion and d for
   * deletion
   * 
   * y is the index of the char in sOne
   *
   * n is the old amino acid placed in the gene
   *
   * m is the new amino acid placed in the mutated sequence * insertions take place between the
   * given index and the next index
   * 
   * @param sOne The mutated sequence
   * @param sTwo The gene
   * 
   * @return A list of differences (represented as String)
   * @author Kevin Otto
   */
  private static LinkedList<String> reportDifferences(Sequence sOne, Sequence sTwo) {
    LinkedList<String> result = reportDifferences(sOne.sequence, sTwo.sequence);
    return result;
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

    int m = sOne.length() + 1;
    int n = sTwo.length() + 1;

    int[][] wunschMatrix = new int[m][n];// create empty Needleman Wunsch
                                         // Matrix

    // fill first line from 1 to |first|
    for (int i = 1; i < m; i++) {
      wunschMatrix[i][0] = gabPenalty * i;
    }
    // fill first row from 1 to |second|
    for (int j = 1; j < n; j++) {
      wunschMatrix[0][j] = gabPenalty * j;
    }

    int cost = 0;// variable to save difference in characters
    // iterate over 2D array
    for (int i = 1; i < m; i++) {
      for (int j = 1; j < n; j++) {
        int deletion = wunschMatrix[i - 1][j] + gabPenalty;
        int insertion = wunschMatrix[i][j - 1] + gabPenalty;
        int match = wunschMatrix[i - 1][j - 1] + Similarity(sOne.charAt(i), sTwo.charAt(j));
        wunschMatrix[i][j] = Math.max(Math.max(deletion, insertion), match);
      }
    }
    return backTrackNeedlemanWunsch(wunschMatrix);
  }

  private static LinkedList<String> backTrackNeedlemanWunsch(int[][] wunschMatrix) {
    // TODO Auto-generated method stub
    return null;
  }

  private static int Similarity(char a, char b) {
    // TODO Implement
    return 0;
  }

  /**
   * Compares to sequences and returns the differences as a list (represented by the positions). The
   * order of the input sequences is irrelevant.
   * 
   * the returned list contains String of the following syntax:
   * 
   * x|y|n|m
   * 
   * where: x is element of {s,i,d,e,n} where s stands for substitution,
   * i for insertion,
   * d for deletion,
   * n for no Operation,
   * e for ERROR
   * 
   * y is the index of the char in sOne
   *
   * n is the old amino acid placed in the gene
   *
   * m is the new amino acid placed in the mutated sequence * insertions take place between the
   * given index and the next index
   * 
   * @param sOne The mutated sequence
   * @param sTwo The gene
   * 
   * @return A list of differences (represented as String)
   * @author Kevin Otto
   */
  public static LinkedList<String> reportDifferences(String sOne, String sTwo) {
    // get Levenshtein Result
    int[][] lev = leventhsein(sOne, sTwo);

    // m x n matrix
    int m = lev.length;
    int n = lev[0].length;

    // counter variables for actual matrix position
    int row = m - 1;
    int column = n - 1;

    LinkedList<String> result = new LinkedList<String>();

    while (row > 0 && column > 0) {
      // if previous diagonal cell is best (smallest neighbor cell and
      // equal or exactly one smaller)
      if (lev[row - 1][column - 1] <= Math.min(lev[row - 1][column], lev[row][column - 1])
          && (lev[row - 1][column - 1] == lev[row][column]
              || lev[row - 1][column - 1] == lev[row][column] - 1)) {
    	  // Diagonal smaller -> Substitution
    	  if (lev[row - 1][column - 1] == lev[row][column] - 1) {
          // SUBSTITUTION
          result.addFirst("s|" + row  + "|" + sTwo.charAt(column - 1) + "|" + sOne.charAt(row - 1));
        }
        // else -> No Operation
        // go to next diagonal cell
        row--;
        column--;
      }
      // if left cell is best
      else if ((lev[row - 1][column] <= lev[row][column - 1])
          && (lev[row - 1][column] == lev[row][column]
              || lev[row - 1][column] == lev[row][column] - 1)) {// left
        // smaller->deletion;
        // DELETION
        if (lev[row - 1][column] == lev[row][column] - 1) {
          result.addFirst("d|" + row  + "|" + "|" + sOne.charAt(row - 1));
        }

        row--;
      } else {
        // up smaller -> insertion
        // INSERTION
        if (lev[row][column - 1] == lev[row][column] - 1) {
          result.addFirst("i|" + row  + "|" + sTwo.charAt(column - 1) + "|");
        }
        column--;
      }
    }

    // special cases:

    // insertion at begin
    if (column > 0) {
      for (; column > 0; column--) {
        result.addFirst("i|" + row  + "|" + sTwo.charAt(column - 1) + "|");
      }
    }

    // deletion at begin
    if (row > 0) {
      for (; row > 0; row--) {
        result.addFirst("d|" + row  + "|" + "|" + sOne.charAt(row - 1));
      }
    }
    return result;
  }

  /**
   * Compares to sequences and returns their similarity without finding the exact differences. The
   * order of the input sequences is irrelevant.
   * 
   * @param first The first sequence
   * @param second The second sequence
   * 
   * @return Similarity measure
   */
  private static double checkSimilarity(Sequence first, Sequence second) {
    return 0.0;
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
  public static int[][] leventhsein(String first, String second) {

    int m = first.length() + 1;
    int n = second.length() + 1;

    int[][] levenMatrix = new int[m][n];// create empty Levenshtein Matrix

    // fill first line from 1 to |first|
    for (int i = 1; i < m; i++) {
      levenMatrix[i][0] = i;
    }
    // fill first row from 1 to |second|
    for (int j = 1; j < n; j++) {
      levenMatrix[0][j] = j;
    }

    int cost = 0;// variable to save difference in characters
    // iterate over 2D array
    for (int j = 1; j < n; j++) {
      for (int i = 1; i < m; i++) {
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

  /**
   * Calculates the Levensthein Matrix of two Strings. The Matrix gives information about the
   * differences of the two Strings and the best way to transform them into another. In this version
   * the matrix contains symbols for the difrent operations
   * 
   * 'd' for deletion 'i' for insertion 'c' for change 'n' for no change
   * 
   * 'e' for error (should never happen!)
   * 
   * @param first The first String
   * @param second The second String
   * @return
   * @author Kevin Otto
   */
  public static LevenshteinResult extendetLeventhsein(String first, String second) {

    int m = first.length() + 1;
    int n = second.length() + 1;

    int[][] levenMatrix = new int[m][n];// create empty Levenshtein Matrix
    char[][] levenOperations = new char[m][n];// create empty Levenshtein
                                              // Matrix

    levenOperations[0][0] = ' ';

    // fill first line from 1 to |first|
    for (int i = 1; i < m; i++) {
      levenMatrix[i][0] = i;
      levenOperations[i][0] = 'c';
    }
    // fill first row from 1 to |second|
    for (int j = 1; j < n; j++) {
      levenMatrix[0][j] = j;
      levenOperations[0][j] = 'c';
    }

    int cost = 0;// variable to save difference in characters
    // iterate over 2D array
    for (int j = 1; j < n; j++) {
      for (int i = 1; i < m; i++) {
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

        // put char in Operation Matrix
        if (levenMatrix[i][j] == levenMatrix[i - 1][j] + 1) {
          levenOperations[i][j] = 'd';// deletion of char
        } else if (levenMatrix[i][j] == levenMatrix[i][j - 1] + 1) {
          levenOperations[i][j] = 'i';// insertion of char
        } else if (levenMatrix[i][j] == levenMatrix[i - 1][j - 1] + cost) {
          if (cost == 1) {
            levenOperations[i][j] = 'c'; // change of char
          } else {
            levenOperations[i][j] = 'n'; // no change of char
          }
        } else {
          levenOperations[i][j] = 'e'; // error
          System.err.println("Fatal Levenshtein Error");
        }
      }
    }
    return new LevenshteinResult(levenMatrix, levenOperations);
  }

  /**
   * for test reasons, will be removed later TODO remove when no longer necessary
   * 
   * @param args
   * @author Kevin Otto
   */
  public static void main(String[] args) {


    ConsoleIO.printCharMatrix(extendetLeventhsein("allo", "hallo").levenshteinOperations);
    System.out.println();
    ConsoleIO.printIntMatrix(extendetLeventhsein("AACAAB", "AAADDDABA").levenshteinMatrix);

    System.out.println();

    LinkedList<String> list = reportDifferences("AACAAB", "AAADDDAAB");
    for (String string : list) {
      System.out.println(string);
    }
  }
}
