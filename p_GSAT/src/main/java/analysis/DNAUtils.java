package analysis;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import core.Main;
import io.ConsoleIO;
import test.BioJavaTest;
import org.biojava.*;

/**
 * This class contains the logic of analyzing DNA sequences. Thus, it is the main part of the
 * analyzing pipeline.
 * 
 */
public class DNAUtils {

  public static final Map<String, String> aminoAcidShorts;
  static {
    Hashtable<String, String> tmp = new Hashtable<String, String>();
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
   * @author Jannis Blueml
   */
  public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze) {

    Gene reference = toAnalyze.getReferencedGene();

    // list to return
    LinkedList<String> mutations = new LinkedList<>();

    // the sequence to analyze
    String mutatedSequence = toAnalyze.getSequence();

    // the gene sequence
    String originalSequence = reference.getSequence();

    // list of differences in form like s|12
    LinkedList<String> differences = reportDifferences(toAnalyze, reference);

    // the shiftdifference between mutated and original because of injections/deletions
    int shift = 0;


    for (String difference : differences) {
      // type of mutation (s,i,d,e,n)
      String typeOfMutations = difference.split("|")[0];

      // position relative to mutatedSequence (of animoAcids)
      int position = Integer.parseInt(difference.split("|")[1]);


      // position of mutation relative the nukleotides
      int positionInSequence = position * 3;

      // Strings for the amino acid in nukleotideTriple-form
      String mutatedAminoAcid = "";
      String originalAminoAcid = "";

      String oldAminoAcid;
      String newAminoAcid;

      switch (typeOfMutations) {
        // s = substitution, normal mutation of one nukleotide
        case "s":
          oldAminoAcid = difference.split("|")[2];
          newAminoAcid = difference.split("|")[3];
          mutations.add(reference.getName() + "   " + oldAminoAcid + position + newAminoAcid);
          break;

        // i = injection, inject of an new amino acid (nukleotide triple
        // form)
        case "i":
          shift -= 3;
          newAminoAcid = difference.split("|")[2];
          mutations.add(reference.getName() + "  +1" + newAminoAcid + position);
          break;
        // d = deletion, deletion of an amino acid
        case "d":
          shift += 3;
          oldAminoAcid = difference.split("|")[2];
          mutations.add(reference.getName() + "  +1" + oldAminoAcid + position);
          // e = error, if an injection or a deletion has more or less
          // then 3
          // nukleotides or the sequence at all are not "% 3 = 0"
        case "e":
          mutations.clear();
          mutations.add(" - reading frame");
          return mutations;
        default:
          // TODO exeption werfen
          break;
      }

    }

    return mutations;
  }



  private static LinkedList<String> checkSilentMutation(String tripple) {
    return null;
  }


  /**
   * Finds the gene that fits best to a given sequence by comparing it to all given genes. Known
   * genes can be found in the database.
   * 
   * @param toAnalyze Sequence to be analyzed (i.e. for which the fitting gene is to find)
   * 
   * @return The found gene.
   */
  public static Gene findRightGene(AnalyzedSequence toAnalyze) {
    return null;
  }

  /**
   * Compares to sequences and returns the differences as a list (represented by the positions). The
   * order of the input sequences is irrelevant.
   * 
   * the returned list contains String of the following syntax:
   * 
   * x|y|n
   * 
   * where: x is element of {s,i,d} where s stands for substitution i for insertion and d for
   * deletion
   * 
   * y is element of all chars contained in sOne and sTwo
   * 
   * n is the index of the char in sOne
   *
   * insertions take place between the given index and the next index
   * 
   * @param sOne The first sequence
   * @param sTwo The second sequence.
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
    return null;
  }


  /**
   * Compares to sequences and returns the differences as a list (represented by the positions). The
   * order of the input sequences is irrelevant.
   * 
   * the returned list contains String of the following syntax:
   * 
   * x|y|n
   * 
   * where: x is element of {s,i,d} where s stands for substitution i for insertion and d for
   * deletion
   * 
   * y is element of all chars contained in sOne and sTwo
   * 
   * n is the index of the char in sOne
   *
   * insertions take place between the given index and the next index
   * 
   * @param sOne The first sequence
   * @param sTwo The second sequence.
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
        if (lev[row - 1][column - 1] == lev[row][column] - 1) {// Diagonal smaller -> Substitution
          // SUBSTITUTION
          result.addFirst("s|" + sTwo.charAt(column - 1) + "|" + row);
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
          result.addFirst("d|" + sOne.charAt(row - 1) + "|" + row);
        }

        row--;
      } else {
        // up smaller -> insertion
        // INSERTION
        if (lev[row][column - 1] == lev[row][column] - 1) {
          result.addFirst("i|" + sTwo.charAt(column - 1) + "|" + row);
        }
        column--;
      }
    }

    // special cases:

    // insertion at begin
    if (column > 0) {
      for (; column > 0; column--) {
        result.addFirst("i|" + sTwo.charAt(column - 1) + "|" + row);
      }
    }

    // deletion at begin
    if (row > 0) {
      for (; row > 0; row--) {
        result.addFirst("d|" + sOne.charAt(row - 1) + "|" + row);
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
  private static double compare(Sequence first, Sequence second) {
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
        if (first.charAt(i - 1) == second.charAt(j - 1)) {// if
                                                          // characters
                                                          // are equal
                                                          // cost for
                                                          // replacement
                                                          // = 0
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
    //org.biojava.nbio.core.sequence.io.util.IOUtils.
    org.

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
