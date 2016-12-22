package analysis;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.max.MaxCore;

import exceptions.CorruptedSequenceException;
import exceptions.UndefinedTypeOfMutationException;

/**
 * This class contains the logic of analyzing mutations in sequences. Thus, it is one of the main
 * parts of the analyzing pipeline.
 * 
 */
public class MutationAnalysis {

  // A Integer, that specifies the border for the "Reading Frame Error"
  public static int warningReadingErrorFrame = 10;
  public static int readingFrameErrorBorder = 100;



  /**
   * Compares a sequence to a gene to find mutations. Returns a boolean if there was a reading frame
   * error.
   * 
   * @param toAnalyze The sequence to be analyzed (which may have mutations)
   * 
   * @return A boolean, if there was a reading frame error
   * 
   * @author bluemlj
   * @throws CorruptedSequenceException
   */
  public static boolean findMutations(AnalysedSequence toAnalyze)
      throws UndefinedTypeOfMutationException, CorruptedSequenceException {

    // the gene references by the mutated Sequence
    Gene reference = toAnalyze.getReferencedGene();
    // the sequence to analyze
    String mutatedSequence = toAnalyze.getSequence();
    // the gene sequence
    String originalSequence = reference.getSequence();
    // list of differences in form like "s|12|G|H"
    LinkedList<String> differences = reportDifferences(toAnalyze, 0);
    int lastposition = 0;
    int checkFrameerrorCounter = 0;
    int shift = 0;
    int tmpshift = 0;
    for (int i = 0; i < differences.size(); i++) {

      if (checkFrameerrorCounter == warningReadingErrorFrame)
        System.err.println("Warning possible READING FRAME ERROR");
      if (checkFrameerrorCounter == readingFrameErrorBorder) return false;


      String difference = differences.get(i);
      // type of mutation (s,i,d)
      String typeOfMutations = difference.split("\\|")[0];

      // position relative to mutatedSequence (of animoAcids)
      int position = Integer.parseInt(difference.split("\\|")[1]) + toAnalyze.getOffset() / 3;
      String oldAminoAcid;
      String newAminoAcid;

      switch (typeOfMutations) {
        // s = substitution, normal mutation of one aminoAcid
        case "s":
          oldAminoAcid = difference.split("\\|")[2];
          newAminoAcid = difference.split("\\|")[3];
          toAnalyze.addMutation(newAminoAcid + position + oldAminoAcid);
          checkFrameerrorCounter++;

          break;
        // i = injection, inject of an new amino acid (aminoAcid short form)
        case "i":
          shift--;
          newAminoAcid = difference.split("\\|")[2];
          toAnalyze.addMutation("+1" + newAminoAcid + position);
          checkFrameerrorCounter++;
          break;
        // d = deletion, deletion of an amino acid
        case "d":
          shift++;
          oldAminoAcid = difference.split("\\|")[2];
          toAnalyze.addMutation("-1" + oldAminoAcid + position);
          checkFrameerrorCounter++;
          break;


        default:
          throw new UndefinedTypeOfMutationException(typeOfMutations);
      }

      // in case that between to mutations are more then zero aminoacids, we check if there is any
      // silent mutation in them.


      if (position > lastposition + 1 || i == differences.size() - 1) {

        for (int tempPosition = lastposition + 1; tempPosition < position; tempPosition++) {
          if (tempPosition * 3 + toAnalyze.getOffset() + 3 > originalSequence.length()
              || tempPosition * 3 + 3 > mutatedSequence.length()) {
            break;
          } else {
            String oldAcid =
                originalSequence.substring((tempPosition + tmpshift) * 3 + toAnalyze.getOffset(),
                    (tempPosition + tmpshift) * 3 + toAnalyze.getOffset() + 3);
            String newAcid = mutatedSequence.substring(tempPosition * 3, tempPosition * 3 + 3);

            if (!oldAcid.equals(newAcid)) {
              toAnalyze.addMutation(oldAcid + tempPosition + newAcid);
            } else {
              checkFrameerrorCounter = 0;
            }
          }
        }

      } else {
        tmpshift = shift;
        lastposition = position;
      }
    }

    if (differences.size() == 0) {
      for (int tempPosition = 0; tempPosition < mutatedSequence.length(); tempPosition++) {
        if (tempPosition * 3 + toAnalyze.getOffset() + 3 > originalSequence.length()
            || tempPosition * 3 + 3 > mutatedSequence.length()) {
          break;
        } else {
          String oldAcid = originalSequence.substring(tempPosition * 3 + toAnalyze.getOffset(),
              tempPosition * 3 + toAnalyze.getOffset() + 3);
          String newAcid = mutatedSequence.substring(tempPosition * 3, tempPosition * 3 + 3);

          if (!oldAcid.equals(newAcid)) {
            tempPosition++;
            toAnalyze.addMutation(oldAcid + tempPosition + newAcid);
            tempPosition--;
          } else {
            checkFrameerrorCounter = 0;
          }
        }
      }

    }
    return true;
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
   * m is the new amino acid placed in the mutated sequence insertions take place between the given
   * index and the next index
   * 
   * <<<<<<< HEAD
   * 
   * @param sOne The mutated sequence
   * @param seq The gene
   * @param type 0 = if the we work on nucleotides, 1 = if we work on aminoacids; =======
   * @param gene The mutated sequence
   * @param sequence The gene >>>>>>> 2b5f6c1192ca56bd5c615bff808c6f091130fcaf
   * 
   * @return A list of differences (represented as String)
   * @author Kevin Otto, Jannis Blueml
   * @throws CorruptedSequenceException
   */
  private static LinkedList<String> reportDifferences(AnalysedSequence seq, int type)
      throws CorruptedSequenceException {
    String first, second;
    switch (type) {
      case 0:
        first = StringAnalysis
            .codonsToAminoAcids(seq.getReferencedGene().sequence.substring(seq.getOffset()));
        second = StringAnalysis.codonsToAminoAcids(seq.sequence);
        return reportDifferences(first, second);

      default:
        first = seq.getReferencedGene().sequence.substring(seq.getOffset());
        second = seq.sequence;

        return reportDifferences(first, second);

    }

  }



  /**
   * Compares to sequences and returns the differences as a list (represented by the positions). The
   * order of the input sequences is irrelevant.
   * 
   * the returned list contains String of the following syntax:
   * 
   * x|y|n|m
   * 
   * where: x is element of {s,i,d,e,n} where s stands for substitution, i for insertion, d for
   * deletion, n for no Operation, e for ERROR
   * 
   * y is the index of the char in sOne
   *
   * n is the old amino acid placed in the gene
   *
   * m is the new amino acid placed in the mutated sequence * insertions take place between the
   * given index and the next index
   * 
   * @param gene The mutated sequence
   * @param sequence The gene
   * 
   * @return A list of differences (represented as String)
   * @author Kevin Otto
   */
  public static LinkedList<String> reportDifferences(String gene, String sequence) {
    // get Levenshtein Result
    int[][] lev = StringAnalysis.calculateLevenshteinMatrix(gene, sequence);

    int matrixHeight = lev.length;
    int matrixWidth = lev[0].length;

    // counter variables for actual matrix position
    int row = matrixHeight - 1;
    int column = matrixWidth - 1;

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
          result.addFirst(
              "s|" + row + "|" + sequence.charAt(column - 1) + "|" + gene.charAt(row - 1));
        }
        // else -> No Operation
        // go to next diagonal cell
        row--;
        column--;
      }
      // if left cell is best
      else if ((lev[row - 1][column] <= lev[row][column - 1])
          && (lev[row - 1][column] == lev[row][column]
              || lev[row - 1][column] == lev[row][column] - 1)) {
        // left smaller->deletion;
        // DELETION
        if (lev[row - 1][column] == lev[row][column] - 1) {
          result.addFirst("d|" + row + "|" + gene.charAt(row - 1) + "|");
        }

        row--;
      } else {
        // up smaller -> insertion
        // INSERTION
        if (lev[row][column - 1] == lev[row][column] - 1) {
          result.addFirst("i|" + row + "|" + sequence.charAt(column - 1) + "|");
        }
        column--;
      }
    }

    // special cases:

    // insertion at begin
    if (column > 0) {
      for (; column > 0; column--) {
        result.addFirst("i|" + row + "|" + sequence.charAt(column - 1) + "|");
      }
    }

    // deletion at begin
    if (row > 0) {
      for (; row > 0; row--) {
        result.addFirst("d|" + row + "|" + gene.charAt(row - 1) + "|");
      }
    }
    return result;
  }
}
