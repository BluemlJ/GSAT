package analysis;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import exceptions.CorruptedSequenceException;
import exceptions.UndefinedTypeOfMutationException;
import io.ConsoleIO;

/**
 * This class contains the logic of analyzing mutations in sequences. 
 * Thus, it is one of the main parts of the analyzing pipeline.
 * 
 */
public class MutationAnalysis {

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
  public static boolean findMutations(AnalysedSequence toAnalyze)
      throws UndefinedTypeOfMutationException {

    Gene reference = toAnalyze.getReferencedGene();

    
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
          toAnalyze.addMutation(oldAminoAcid + position + newAminoAcid);
          break;
        // i = injection, inject of an new amino acid (aminoAcid short form)
        case "i":
          shift--;
          newAminoAcid = difference.split("\\|")[2];
          toAnalyze.addMutation("+1" + newAminoAcid + position);
          break;
        // d = deletion, deletion of an amino acid
        case "d":
          shift++;
          oldAminoAcid = difference.split("\\|")[2];
          toAnalyze.addMutation("-1" + oldAminoAcid + position);
          break;
        // in case of a nop, we test a silent mutation and add it if the
        // test has a positive match
        case "n":
          String oldAcid =
              originalSequence.substring((position + shift) * 3, (position + shift) * 3 + 2);
          String newAcid = mutatedSequence.substring(position * 3, position * 3 + 2);

          if (!oldAcid.equals(newAcid)) {
        	  toAnalyze.addMutation(oldAcid + position + newAcid);
          }
          break;
        // in case of an error, we clear the return list and add a reading
        // frame error
        case "e":
          return false;
        default:
          throw new UndefinedTypeOfMutationException(typeOfMutations);
      }

    }
    //TODO mutationis to sequence
    return true;
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

    if (nucleotides.isEmpty()) return "empty nucleotides";
    
    StringBuilder builder = new StringBuilder();
    
    if (nucleotides.length() % 3 == 0) {
      for (int i = 0; i < nucleotides.length(); i = i + 3) {
        String codon = nucleotides.substring(i, i + 3);
        String aminoacid = aminoAcidShorts.get(codon);

        
        if (aminoacid != null)
          builder.append(aminoacid);
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
  public static Gene findRightGene(AnalysedSequence toAnalyze, LinkedList<Gene> listOfGenes) {
    Gene bestgene = null;
    double bestSimilarity = 0;

    for (Gene gene : listOfGenes) {
      double similarity = StringAnalysis.checkSimilarity(toAnalyze, gene);
      if (similarity > bestSimilarity) {
        bestSimilarity = similarity;
        bestgene = gene;
      }
    }
    
    if(bestSimilarity>0.8)
    return bestgene;
    else return null;
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
  public static Gene findRightGene(AnalysedSequence toAnalyze) {
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
   * m is the new amino acid placed in the mutated sequence insertions take place between the given
   * index and the next index
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
   * @param sOne The mutated sequence
   * @param sTwo The gene
   * 
   * @return A list of differences (represented as String)
   * @author Kevin Otto
   */
  public static LinkedList<String> reportDifferences(String sOne, String sTwo) {
    // get Levenshtein Result
    int[][] lev = StringAnalysis.calculateLevenshteinMatrix(sOne, sTwo);

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
          result.addFirst("s|" + row + "|" + sTwo.charAt(column - 1) + "|" + sOne.charAt(row - 1));
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
          result.addFirst("d|" + row + "|" + sOne.charAt(row - 1));
        }

        row--;
      } else {
        // up smaller -> insertion
        // INSERTION
        if (lev[row][column - 1] == lev[row][column] - 1) {
          result.addFirst("i|" + row + "|" + sTwo.charAt(column - 1) + "|");
        }
        column--;
      }
    }

    // special cases:

    // insertion at begin
    if (column > 0) {
      for (; column > 0; column--) {
        result.addFirst("i|" + row + "|" + sTwo.charAt(column - 1) + "|");
      }
    }

    // deletion at begin
    if (row > 0) {
      for (; row > 0; row--) {
        result.addFirst("d|" + row + "|" + "|" + sOne.charAt(row - 1));
      }
    }
    return result;
  }
}
