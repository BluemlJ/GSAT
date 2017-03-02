package analysis;

import exceptions.CorruptedSequenceException;
import exceptions.UndefinedTypeOfMutationException;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * This class contains the logic of analyzing mutations in sequences. Thus, it is one of the main
 * parts of the analyzing pipeline.
 * 
 * @author jannis blueml
 * @category DNA.Utils
 * @since 11.2.17
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

    warningReadingFrameError = mutatedSequence.length() / 8;
    // because we work on amino acids
    readingFrameErrorBorder = mutatedSequence.length() / 3;

    // a List of all differences in form of s|12|d|e
    LinkedList<String> differenceList = reportDifferences(toAnalyze, true);
    // the last position we found a mutation. Necessary for silent mutation
    // detection.
    int lastposition = 0;
    // counter for reading frame error detection
    int checkFrameerrorCounter = 0;
    // shift created by insertions and deletions
    int shift = 0;
    // a temporary counter. Necessary for silent mutation detection.
    int tmpshift = 0;

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
      // the amino acid in the mutated seqeunce
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
            codonsOfNew =
                toAnalyze.getSequence().substring(firstNucleotidePos, firstNucleotidePos + 3);
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
          shift--;
          newAminoAcid = difference.split("\\|")[2];

          // write mutation into sequence (see substitution)
          if (toAnalyze.getSequence().length() > firstNucleotidePos + 3) {
            codonsOfNew =
                toAnalyze.getSequence().substring(firstNucleotidePos, firstNucleotidePos + 3);
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
          shift++;
          oldAminoAcid = difference.split("\\|")[2];
          // write informations in sequence
          if (toAnalyze.getSequence().length() > firstNucleotidePos + 3) {
            codonsOfNew =
                toAnalyze.getSequence().substring(firstNucleotidePos, firstNucleotidePos + 3);
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

      // in case that between to mutations are more then zero aminoacids,
      // we check if there is any
      // silent mutation in them.

      // if the step between to mutations is greater 1 or we are the last
      // for iteration
      if (position > lastposition + 1 || i == differenceList.size() - 1) {

        // starts by the last mutation and test evera amino acid between
        // there and the actual amino
        // acid
        int tempPosition = lastposition + 1;
        while (tempPosition < position - toAnalyze.getOffset()) {

          tempPosition = position;

          // checks boundaries
          if ((tempPosition + tmpshift) * 3 + toAnalyze.getOffset() * 3 + 3 > originalSequence
              .length()
              || Math.max(tempPosition + tmpshift, tempPosition) * 3 + 3 > mutatedSequence
                  .length()) {
            break;
            // checks amino acid
          } else {
            String oldAcid = originalSequence.substring(
                (tempPosition + tmpshift) * 3 + toAnalyze.getOffset() * 3,
                (tempPosition + tmpshift) * 3 + toAnalyze.getOffset() * 3 + 3);
            String newAcid = mutatedSequence.substring(tempPosition * 3, tempPosition * 3 + 3);

            // if there is any silent mutation add them to sequence
            if (!oldAcid.equals(newAcid)) {
              tempPosition += toAnalyze.getOffset() + 1;
              toAnalyze.addMutation(oldAcid + tempPosition + newAcid);
            }
          }

          // set new lastposition and increment tempP. for next run
          lastposition = position;
          tempPosition++;
        }

        // didn't increment tempP. because of insertion or deletion,
        // instead set tmpshift. Because
        // the actual shift references to actual position not to the
        // room between actual and
        // lastPosition.

      } else {
        tmpshift = shift;
        if (typeOfMutations.charAt(0) == 's') {
          lastposition = position;
        }
      }
    }

    // if there is no mutation at all, check all (ignore shift)
    if (differenceList.size() == 0) {

      // init Position
      int tempPosition = 0;
      while (tempPosition < mutatedSequence.length()) {
        // check boundaries
        if (tempPosition * 3 + toAnalyze.getOffset() * 3 + 3 > originalSequence.length()
            || tempPosition * 3 + 3 > mutatedSequence.length()) {
          break;
          // actual testing
        } else {
          String oldAcid = originalSequence.substring(tempPosition * 3 + toAnalyze.getOffset() * 3,
              tempPosition * 3 + toAnalyze.getOffset() * 3 + 3);
          String newAcid = mutatedSequence.substring(tempPosition * 3, tempPosition * 3 + 3);
          // if silent mutation, add them. tempPosition must adding
          // the offset before adding to
          // sequence.
          if (!oldAcid.equals(newAcid)) {

            tempPosition += toAnalyze.getOffset() + 1;
            toAnalyze.addMutation(oldAcid + tempPosition + newAcid);
            tempPosition -= toAnalyze.getOffset() + 1;
          }
        }
        tempPosition++;
      }

    }
    return true;
  }

  public static void findMixes(AnalysedSequence toAnalyze) {

    // return list of all mixes
    LinkedList<String> ret = new LinkedList<>();
    // the gene references by the mutated Sequence
    Gene reference = toAnalyze.getReferencedGene();
    // the sequence to analyze
    StringBuilder mutatedSequence = new StringBuilder();
    mutatedSequence.append(toAnalyze.getSequence());
    // the gene sequence
    String originalSequence = reference.getSequence();

    // list of all candidates in form of p|12|AG, p|3|GCA...
    LinkedList<String> candidates = findPlacmidMixCanditates(toAnalyze);

    // check every candidate and add it to the return list
    for (String string : candidates) {
      // all parameters of the candidate
      String[] params = string.split("\\|");
      // first nucleotide of AminoAcid
      int position = Integer.parseInt(params[1]) - (Integer.parseInt(params[1]) % 3);

      // AminoAcid in Gene
      String oldAcid =
          StringAnalysis.AMINO_ACID_SHORTS.get(originalSequence.substring(position, position + 3));

      // the possible nucleotides
      char[] theOther = params[2].toCharArray();

      // start a Mix with H25, P84, ...
      StringBuilder retString = new StringBuilder();
      retString.append(oldAcid + params[1]);

      // Add all possible Mixes
      for (int i = 0; i < theOther.length; i++) {
        mutatedSequence.setCharAt(Integer.parseInt(params[1]), theOther[i]);
        retString.append(
            StringAnalysis.AMINO_ACID_SHORTS.get(mutatedSequence.substring(position, position + 3))
                + "/");
      }
      retString.subSequence(0, retString.length() - 1);
      ret.add(retString.toString());
    }
    //add them to sequence
    toAnalyze.setPlasmidmixes(ret);
  }

  public static LinkedList<String> findPlacmidMixCanditates(AnalysedSequence sequence) {

    // List of candidates
    LinkedList<String> ret = new LinkedList<>();

    // Channels
    org.jcvi.jillion.trace.chromat.Channel cA = sequence.getChannels().getAChannel();
    org.jcvi.jillion.trace.chromat.Channel cG = sequence.getChannels().getGChannel();
    org.jcvi.jillion.trace.chromat.Channel cC = sequence.getChannels().getCChannel();
    org.jcvi.jillion.trace.chromat.Channel cT = sequence.getChannels().getTChannel();

    // Qualities
    byte[] qATemp = cA.getQualitySequence().toArray();
    byte[] qGTemp = cG.getQualitySequence().toArray();
    byte[] qCTemp = cC.getQualitySequence().toArray();
    byte[] qTTemp = cT.getQualitySequence().toArray();

    int[] qAi = new int[qATemp.length];
    int[] qGi = new int[qGTemp.length];
    int[] qCi = new int[qCTemp.length];
    int[] qTi = new int[qTTemp.length];

    // byte[] to int[]
    for (int i = 0; i < qATemp.length; i++) {
      qAi[i] = qATemp[i];
      qGi[i] = qGTemp[i];
      qCi[i] = qCTemp[i];
      qTi[i] = qTTemp[i];
    }

    for (int i = 0; i < sequence.length(); i++) {
      // Array of four qualities from four traces
      int[] tmp = {qAi[i], qGi[i], qCi[i], qTi[i]};
      // Counter to count maximum mix (all four possibilities
      int cnt = 0;
      // the candidate (a String in form of ACG,AT,...)
      StringBuilder candidate = new StringBuilder();
      Arrays.sort(tmp);
      // find equal qualities
      while (tmp[0] - 10 < tmp[1] && cnt < 4) {
        Arrays.sort(tmp);
        if (tmp[0] == qAi[i]) {
          candidate.append("A");
          tmp[0] = -11;
          cnt++;
        } else if (tmp[0] == qGi[i]) {
          candidate.append("G");
          tmp[0] = -11;
          cnt++;
        } else if (tmp[0] == qCi[i]) {
          candidate.append("C");
          tmp[0] = -11;
          cnt++;
        } else if (tmp[0] == qTi[i]) {
          candidate.append("T");
          tmp[0] = -11;
          cnt++;

        }
      }
      candidate.append(tmp[0]);

      // if you find a canditate with more then one One codon and the quality is broken, we got a mix
      if (candidate.length() != 1
          && sequence.getQuality()[i] < (sequence.getQuality()[i - 1] / 2)) {
        ret.add("p|" + i + "|" + candidate);
      }
      candidate = new StringBuilder();
    }
    return ret;
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
   * @param type 0 = if the we work on nucleotides, 1 = if we work on aminoacids; =======
   * @param gene The mutated sequence
   * @param sequence The gene
   * 
   * @return A list of differences (represented as String)
   * @author Kevin Otto, Jannis Blueml
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

  public static int IntegerMax(int... n) {
    int i = 0;
    int max = n[i];

    while (++i < n.length)
      if (n[i] > max) max = n[i];

    return max;
  }

}
