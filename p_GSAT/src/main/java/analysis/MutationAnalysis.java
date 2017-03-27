package analysis;

import java.util.Arrays;
import java.util.LinkedList;

import exceptions.CorruptedSequenceException;
import exceptions.UndefinedTypeOfMutationException;

/**
 * This class contains the logic of analyzing mutations in sequences. Thus, it is one of the main
 * parts of the analyzing pipeline.
 * 
 * @author jannis blueml, kevin otto
 * @category DNA.Utils
 */
public class MutationAnalysis {

  /**
   * boolean if there is a possible reading frame error is detected
   */
  public static boolean readingFrameError = false;
  /**
   * how many bad qualtity nucleotides before we stop analysing
   */
  public static int readingFrameErrorBorder = 100;
  /**
   * A Integer, that specifies the border for the "Reading Frame Error"
   */
  public static int warningReadingFrameError = 10;

  /**
   * Compares a sequence to a gene to find mutations. Returns a boolean if there was a reading frame
   * error.
   * 
   * @param toAnalyze The sequence to be analyzed (which may have mutations)
   * 
   * @return A boolean, if there was a reading frame error
   * 
   * @author jannis blueml
   * @throws CorruptedSequenceException if sequence of toAnalyze is damaged or corrupt
   * @throws UndefinedTypeOfMutationException if reportDifferences gives me a mutation thats not
   *         s,i,d
   */
  public static boolean findMutations(AnalysedSequence toAnalyze)
      throws UndefinedTypeOfMutationException, CorruptedSequenceException {

    // the gene references by the mutated Sequence
    Gene reference = toAnalyze.getReferencedGene();
    // the sequence to analyze
    String mutatedSequence = toAnalyze.getSequence();
    // the gene sequence
    String originalSequence = reference.getSequence();

    warningReadingFrameError = mutatedSequence.length() / 8;
    // because we work on amino acids
    readingFrameErrorBorder = mutatedSequence.length() / 3;

    // a List of all differences in form of s|12|d|e
    LinkedList<String> differenceList = reportDifferences(toAnalyze, true);
    // counter for reading frame error detection
    int checkFrameerrorCounter = 0;
    // shift created by insertions and deletions
    int shift = 0;
    // check all differences and add them to the sequence.
    // The new form will be -> D6E (ATA), +1/-1 T26 (TTT)
    for (int i = 0; i < differenceList.size(); i++) {

      // checks reading frame error and warnings
      if (checkFrameerrorCounter == warningReadingFrameError) {
        if (checkFrameerrorCounter == readingFrameErrorBorder) {
          readingFrameError = true;
          return false;
        }
      }

      // difference in form of s|12|e|d
      String difference = differenceList.get(i);
      // type of mutation (s,i,d)
      String typeOfMutations = difference.split("\\|")[0];
      // position relative to mutatedSequence (of animoAcids)
      int position = Integer.parseInt(difference.split("\\|")[1]) + toAnalyze.getOffset();
      // the amino acid in the mutated sequence
      String newAminoAcid;
      // the amino acid found in the gene at the same position
      String oldAminoAcid;
      // represents the codon of newAminoAcid
      String codonsOfNew;



      int firstNucleotidePos = (position - 1) * 3;

      // how to proceed mutation
      switch (typeOfMutations) {
        // s = substitution, normal mutation of one aminoAcid
        case "s":
          // get information out of difference
          newAminoAcid = difference.split("\\|")[2];
          oldAminoAcid = difference.split("\\|")[3];
          // checks length before getting the codon out of gene
          if (toAnalyze.getSequence().length() > firstNucleotidePos + 3) {
            // get codon out of gene
            codonsOfNew = toAnalyze.getSequence().substring(firstNucleotidePos + shift * 3,
                firstNucleotidePos + shift * 3 + 3);
            // add mutation to sequence
            toAnalyze
                .addMutation(oldAminoAcid + position + newAminoAcid + " (" + codonsOfNew + ")");
          } else {
            toAnalyze.addMutation(oldAminoAcid + position + newAminoAcid);
          }

          checkFrameerrorCounter++;

          break;
        // i = injection, inject of an new amino acid (aminoAcid short form)
        case "i":
          // set shift and get informations
          shift++;
          newAminoAcid = difference.split("\\|")[2];
          position++;

          // write mutation into sequence (see substitution)
          if (toAnalyze.getSequence().length() > firstNucleotidePos + 3) {
            codonsOfNew = toAnalyze.getSequence().substring(firstNucleotidePos + shift * 3,
                firstNucleotidePos + shift * 3 + 3);
            toAnalyze.addMutation("+1" + newAminoAcid + position + " (" + codonsOfNew + ")");
          } else {
            toAnalyze.addMutation("+1" + newAminoAcid + position);
          }

          checkFrameerrorCounter++;
          break;
        // d = deletion, deletion of an amino acid (see insertion and
        // substitution for comment)
        case "d":
          // increment shift + get informations
          shift--;
          oldAminoAcid = difference.split("\\|")[2];

          // write informations in sequence
          if (toAnalyze.getSequence().length() > firstNucleotidePos + 3) {
            codonsOfNew = originalSequence.substring(firstNucleotidePos + shift * 3 + 3,
                firstNucleotidePos + shift * 3 + 6);
            toAnalyze.addMutation("-1" + oldAminoAcid + position + " (" + codonsOfNew + ")");
          } else {
            toAnalyze.addMutation("-1" + oldAminoAcid + position);
          }
          checkFrameerrorCounter++;
          break;

        // if report differences find a mutation which is not s,i or d
        default:
          throw new UndefinedTypeOfMutationException(typeOfMutations);
      }
    }
    // the following code finds silentmutations in sequence and add them to the list of mutations

    // a tempoary shift, needed for insertions and deletions and the change in reading frame
    int tmpshift = 0;
    // boolean to say if the position has allready a normal mutation, if so, there cant be a
    // silentmutation
    boolean isMutation = false;

    // check every position in the sequence
    for (int j = 0; j < toAnalyze.length(); j++) {
      // checks if position has allready a mutation
      for (String diff : differenceList) {
        int tmpPosition = Integer.parseInt(diff.split("\\|")[1]) + toAnalyze.getOffset();
        String tom = diff.split("\\|")[0];
        // checks position is equal to j+1, difference because of start by 0 instead of 1
        if (tmpPosition == j + 1) {
          if (tom.equals("s")) {
            isMutation = true;
          }
        }
        // insertions and deletions gives the position before the mutation, so check against j
        if (tmpPosition == j) {
          if (tom.equals("d")) {
            tmpshift--;
          }
          if (tom.equals("i")) {
            tmpshift++;
          }
          isMutation = true;

        }
      }
      // checks if silent mutation
      if (!isMutation) {
        // checks boundaries
        if ((j + tmpshift) * 3 + toAnalyze.getOffset() * 3 + 3 > originalSequence.length()
            || Math.max(j + tmpshift, j) * 3 + 3 > mutatedSequence.length()
            || (j + tmpshift) * 3 + toAnalyze.getOffset() * 3 < 0) {
          break;
        }
        // gets nucleotide triple
        String oldAcid = originalSequence.substring(j * 3 + toAnalyze.getOffset() * 3,
            j * 3 + toAnalyze.getOffset() * 3 + 3);
        String newAcid = mutatedSequence.substring((j + tmpshift) * 3, (j + tmpshift) * 3 + 3);
        // checks if original and toAnalyse are not equal
        if (!oldAcid.equals(newAcid)) {
          j++;
          toAnalyze.addMutation(oldAcid + j + newAcid);
          j--;
        }
      }
      // restart process
      isMutation = false;
    }
    return true;
  }


  /**
   * searches for plasmidmixes and add a comment to sequences with possitive results. Gives
   * canditates by analysing the quality of single nucleotides.
   * 
   * @param sequence the sequence to analyse for plasmidmixes
   */
  public static void findPlasmidMix(AnalysedSequence sequence) {

    // List of candidates
    LinkedList<String> mixPositions = new LinkedList<>();

    // Quality arrays
    int[] qualityA = sequence.getChannelA();
    int[] qualityC = sequence.getChannelC();
    int[] qualityG = sequence.getChannelG();
    int[] qualityT = sequence.getChannelT();

    int counter = 0;
    boolean found = false;

    for (int i = 0; i < sequence.length(); i++) {
      // Array of four qualities from four traces
      int[] tmp = {qualityA[i], qualityG[i], qualityC[i], qualityT[i]};
      Arrays.sort(tmp);


      // find equal qualities
      if (sequence.getQuality()[i] < 15) {
        found = true;
      } else {
        found = false;
      }

      // if found and quality is broken, we got a mix
      if (found) {
        counter++;
        if (counter == 3) {
          int pos = i / 3 + 1;
          mixPositions.add("" + pos);
          counter = 0;
        }
      } else {
        counter = 0;
      }

    }
    if (!mixPositions.isEmpty()) {
      if (mixPositions.size() > 1) {
        sequence.setComments(
            sequence.getComments() + "There are possible plasmidmixes at the positions ");
        for (String string : mixPositions) {
          sequence.setComments(sequence.getComments() + string + ", ");
        }
      } else {
        sequence.setComments(sequence.getComments()
            + "There is a possible plasmidmix at the position " + mixPositions.getFirst() + "  ");
      }
      sequence.setComments(
          sequence.getComments().substring(0, sequence.getComments().length() - 2) + ". ");
    }
  }

  /**
   * Compares to sequences and returns the differences as a list (represented by the positions). The
   * order of the input sequences is irrelevant.
   * <p>
   * the returned list contains String of the following syntax:
   * </p>
   * <p>
   * x|y|n|m
   * </p>
   * <p>
   * where: x is element of {s,i,d,e,n} where s stands for substitution, i for insertion, d for
   * deletion, n for no Operation, e for ERROR
   * </p>
   * <p>
   * y is the index of the char in sOne
   * </p>
   * <p>
   * n is the old amino acid placed in the gene
   * </p>
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

    // get Levenshtein Matrix
    int[][] lev = StringAnalysis.calculateLevenshteinMatrix(gene, sequence);

    // get matrix dimmensions
    int matrixHeight = lev.length;
    int matrixWidth = lev[0].length;

    // counter variables for actual matrix position
    int row = matrixHeight - 1;
    int column = matrixWidth - 1;

    // add linked list to save diference strings
    LinkedList<String> result = new LinkedList<String>();

    // iterate over levenstein matrix by folowing best path
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

        // if left cell is best
      } else if ((lev[row - 1][column] <= lev[row][column - 1])
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

  /**
   * Compares to sequences and returns the differences as a list (represented by the positions). The
   * order of the input sequences is irrelevant.
   * <p>
   * the returned list contains String of the following syntax:
   * </p>
   * <p>
   * x|y|n|m
   * </p>
   * <p>
   * where: x is element of {s,i,d,e,n} where s stands for substitution i for insertion and d for
   * deletion
   * </p>
   * <p>
   * y is the index of the char in sOne
   * </p>
   * <p>
   * n is the old amino acid placed in the gene
   * </p>
   * m is the new amino acid placed in the mutated sequence insertions take place between the given
   * index and the next index
   * 
   * 
   * @param sOne The mutated sequence
   * @param seq The gene
   * @param type 0 = if the we work on nucleotides, 1 = if we work on aminoacids;
   * @param gene The mutated sequence
   * @param sequence The gene
   * 
   * @return A list of differences (represented as String)
   * @author Kevin Otto, jannis blueml
   * @throws CorruptedSequenceException
   */
  private static LinkedList<String> reportDifferences(AnalysedSequence seq, boolean type)
      throws CorruptedSequenceException {
    String first;
    String second;

    // convert offset and end to Aminoacids
    int begin = seq.getOffset() * 3;
    int end = seq.getOffset() * 3 + seq.length();

    // convert sequences to Aminoacids
    first = StringAnalysis.codonsToAminoAcids(seq.getReferencedGene().sequence.substring(begin,
        Math.min(end, seq.getReferencedGene().getSequence().length())));
    second = StringAnalysis.codonsToAminoAcids(seq.sequence);
    // calculate difrences and return
    return reportDifferences(first.split("#")[0], second.split("#")[0]);
  }
}
