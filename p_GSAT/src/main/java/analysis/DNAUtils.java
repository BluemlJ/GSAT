package analysis;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;

import core.Main;
import io.ConsoleIO;

/**
 * This class contains the logic of analyzing DNA sequences. Thus, it is the
 * main part of the analyzing pipeline.
 * 
 */
public class DNAUtils {

	/**
	 * This method finds the point in given sequence from which on the sequence
	 * is not reliable anymore. The nucleotide at the point itself is the last
	 * "reliable" nucleotide.
	 * 
	 * @param sequence
	 *            The sequence for which the end of reliability should be
	 *            determined.
	 * 
	 * @return the index of the sequence String from which on it is considered
	 *         unreliable
	 */
	public int findEndOfTrustworthyness(AnalyzedSequence sequence) {
		return 0;
	}

	/**
	 * Measures the quality of a sequence or of one of its parts which is used
	 * to find the end of trustwothyness.
	 * 
	 * @return The quality measure for the given sequence (may also be a part of
	 *         a reference sequence)
	 */
	private static double measureQuality(AnalyzedSequence seq) {
		return 0.0;
	}

	/**
	 * Compares a sequence to a gene to find mutations. Returns the list of
	 * mutations, denoted as described by the department of organic chemistry.
	 * 
	 * @param toAnalyze
	 *            The sequence to be analyzed (which may have mutations)
	 * @param reference
	 *            The referenced gene (used to compare the sequence against it)
	 * 
	 * @return A List of Mutations, represented as Strings in the given format
	 * 
	 * @author Jannis Blüml
	 */
	public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze, Gene reference) {
		// list to return
		LinkedList<String> mutations = new LinkedList<>();
		// the sequence to analyze
		String mutatedSequence = toAnalyze.getSequence();
		// the gene sequence
		String originalSequence = reference.getSequence();
		// list of differences in form like C|12
		LinkedList<String> differences = reportDifferences(toAnalyze, reference);
		// the shiftdifference between mutated and original because of
		// injections/deletions
		int shift = 0;

		if (originalSequence.length() % 3 != 0 || mutatedSequence.length() % 3 != 0) {
			differences.set(0, "E|2");
		}

		for (String difference : differences) {
			// type of mutation (C,I,D,E)
			String typeOfMutations = difference.split("|")[0];
			// position relative to mutatedSequence
			int position = Integer.parseInt(difference.split("|")[1]);
			// position of mutation relative to the amino acid
			int positionInTriple = position % 3;
			// Strings for the amino acid in nukleotideTriple-form
			String mutatedAminoAcid = "", originalAminoAcid = "";

			switch (typeOfMutations) {
			// C = Change, normal mutation of one nukleotide
			case "C":
				// get amino acid nukleotide triple
				switch (positionInTriple) {
				case 0:
					mutatedAminoAcid += mutatedSequence.substring(position, position + 2);
					originalAminoAcid += originalSequence.substring(position + shift, position + 2 + shift);
					break;
				case 1:
					mutatedAminoAcid += mutatedSequence.substring(position - 1, position + 1);
					originalAminoAcid += originalSequence.substring(position - 1 + shift, position + 1 + shift);
					break;
				case 2:
					mutatedAminoAcid += mutatedSequence.substring(position - 2, position);
					originalAminoAcid += originalSequence.substring(position - 2 + shift, position + shift);
					break;
				default:
					// TODO hier eine execption werfen
					break;
				}
				String newAminoAcid = getAminoAcidName(mutatedAminoAcid);
				String oldAminoAcid = getAminoAcidName(originalAminoAcid);

				if (newAminoAcid.equals(oldAminoAcid)) {
					// SILENT MUTATION
				} else {
					mutations.add(reference.getName() + "   " + oldAminoAcid + position + newAminoAcid);
				}
				break;
			// I = injection, inject of an new amino acid (nukleotide triple
			// form)
			case "I":
				shift -= 3;
				mutatedAminoAcid += mutatedSequence.substring(position, position + 2);
				mutations.add(reference.getName() + "  +1" + getAminoAcidName(mutatedAminoAcid) + position);
				break;
			// D = deletion, deletion of an amino acid
			case "D":
				originalAminoAcid += originalSequence.substring(position, position + 2 + shift);
				shift += 3;
				mutations.add(reference.getName() + "  -1" + getAminoAcidName(originalAminoAcid) + position);
				break;
			// E = error, if an injection or a deletion has more or less then 3
			// nukleotides or the sequence at all are not "% 3 = 0"
			case "E":
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

	/**
	 * This method gets the nukleotideTriple and returns the Name of the amino
	 * acid
	 * 
	 * @param AminoAcidTriple
	 *            a nukleotideTriple to test
	 * @return the name of the amino acid in short form
	 * @author Jannis Blüml
	 */
	private static String getAminoAcidName(String AminoAcidTriple) {
		switch (AminoAcidTriple) {
		case "UUU":
			return "F";
		case "UUC":
			return "F";
		case "UUA":
			return "L";
		case "UUG":
			return "L";
		case "UCU":
			return "S";
		case "UCC":
			return "S";
		case "UCA":
			return "S";
		case "UCG":
			return "S";
		case "UAU":
			return "Y";
		case "UAC":
			return "Y";
		case "UAA":
			return "STOP";
		case "UAG":
			return "STOP";
		case "UGU":
			return "C";
		case "UGC":
			return "C";
		case "UGA":
			return "STOP";
		case "UGG":
			return "W";
		case "CUU":
			return "L";
		case "CUC":
			return "L";
		case "CUA":
			return "L";
		case "CUG":
			return "L";
		case "CCU":
			return "P";
		case "CCC":
			return "P";
		case "CCA":
			return "P";
		case "CCG":
			return "P";
		case "CAU":
			return "H";
		case "CAC":
			return "H";
		case "CAA":
			return "Q";
		case "CAG":
			return "Q";
		case "CGU":
			return "R";
		case "CGC":
			return "R";
		case "CGA":
			return "R";
		case "CGG":
			return "R";
		case "AUU":
			return "I";
		case "AUC":
			return "I";
		case "AUA":
			return "I";
		case "AUG":
			return "M";
		case "ACU":
			return "T";
		case "ACC":
			return "T";
		case "ACA":
			return "T";
		case "ACG":
			return "T";
		case "AAU":
			return "N";
		case "AAC":
			return "N";
		case "AAA":
			return "K";
		case "AAG":
			return "K";
		case "AGU":
			return "S";
		case "AGC":
			return "S";
		case "AGA":
			return "R";
		case "AGG":
			return "R";
		case "GUU":
			return "V";
		case "GUC":
			return "V";
		case "GUA":
			return "V";
		case "GUG":
			return "V";
		case "GCU":
			return "A";
		case "GCC":
			return "A";
		case "GCA":
			return "A";
		case "GCG":
			return "A";
		case "GAU":
			return "D";
		case "GAC":
			return "D";
		case "GAA":
			return "E";
		case "GAG":
			return "E";
		case "GGU":
			return "G";
		case "GGC":
			return "G";
		case "GGA":
			return "G";
		case "GGG":
			return "G";
		default:
			//TODO exception unknown aminoacid
			break;
		}
		return null;
	}

	/**
	 * Compares a sequence to its own referenced gene.
	 * 
	 * @param toAnalyse
	 *            The sequence to be analyzed
	 * 
	 * @return A List of Mutations, represented as Strings in the given format
	 * 
	 * @see #findMutations(AnalyzedSequence, Gene)
	 * 
	 * @author Ben Kohr
	 */
	public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze) {

		LinkedList<String> mutations = findMutations(toAnalyze, toAnalyze.getReferencedGene());
		return mutations;
	}

	/**
	 * Finds the gene that fits best to a given sequence by comparing it to all
	 * given genes. Known genes can be found in the database.
	 * 
	 * @param toAnalyze
	 *            Sequence to be analyzed (i.e. for which the fitting gene is to
	 *            find)
	 * 
	 * @return The found gene.
	 */
	public static Gene findRightGene(AnalyzedSequence toAnalyze) {
		return null;
	}

	/**
	 * Compares to sequences and returns the differences as a list (represented
	 * by the positions). The order of the input sequences is irrelevant.
	 * 
	 * @param sOne
	 *            The first sequence
	 * @param sTwo
	 *            The second sequence.
	 * 
	 * @return A list of differences (represented as String)
	 */
	private static LinkedList<String> reportDifferences(Sequence sOne, Sequence sTwo) {
		//get Levenshtein Result
		LevenshteinResult lev = extendetLeventhsein(sOne.sequence, sTwo.sequence);
		
		//runs while no result was found
		int i = 0;
		int j = 0;
		while (i<lev.m && j<lev.n) {
			//TODO implement
			
		}
		
		return null;//TODO return
	}

	/**
	 * Compares to sequences and returns their similarity without finding the
	 * exact differences. The order of the input sequences is irrelevant.
	 * 
	 * @param first
	 *            The first sequence
	 * @param second
	 *            The second sequence
	 * 
	 * @return Similarity measure
	 */
	private static double compare(Sequence first, Sequence second) {
		return 0.0;
	}
	
	/**
	 * Calculates the Levensthein Matrix of two Strings.
	 * The Matrix gives information about the differences of the two Strings and the best way to transform them into another.
	 * @param first The first String
	 * @param second The second String
	 * @return
	 */
	public static int[][] leventhsein(String first, String second){
		
		int m = first.length()+1;
		int n = second.length()+1;
		
		int[][] levenMatrix = new int[m][n];//create empty Levenshtein Matrix
		
		//fill first line from 1 to |first|
		for (int i = 1; i < m; i++) {
			levenMatrix[i][0] = i;
		}
		//fill first row from 1 to |second|
		for (int j = 1; j < n; j++) {
			levenMatrix[0][j] = j;
		}
		
		int cost = 0;//variable to save difference in characters
		//iterate over 2D array
		for (int j = 1; j < n; j++) {
			for (int i = 1; i < m; i++) {
				if (first.charAt(i-1) == second.charAt(j-1)) {//if characters are equal cost for replacement = 0
					cost = 0;
				}else {
					cost = 1;
				}
				levenMatrix[i][j] = Math.min(Math.min((
						levenMatrix[i-1][j]+1),/*deletion of char*/
						(levenMatrix[i][j-1]+1)),/*insertion of char*/
						(levenMatrix[i-1][j-1]+cost));/*change of char*/
			}
		}
		return levenMatrix;
	}
	
	/**
	 * Calculates the Levensthein Matrix of two Strings.
	 * The Matrix gives information about the differences of the two Strings and the best way to transform them into another.
	 * In this version the matrix contains symbols for the difrent operations
	 * 
	 * 'd' for deletion 
	 * 'i' for insertion
	 * 'c' for change
	 * 'n' for no change
	 * 
	 * 'e' for error (should never happen!)
	 * 
	 * @param first The first String
	 * @param second The second String
	 * @return
	 */
	public static LevenshteinResult extendetLeventhsein(String first, String second){
		
		int m = first.length()+1;
		int n = second.length()+1;
		
		int[][] levenMatrix = new int[m][n];//create empty Levenshtein Matrix
		char[][] levenOperations = new char[m][n];//create empty Levenshtein Matrix
		
		levenOperations[0][0] = ' ';
				
		//fill first line from 1 to |first|
		for (int i = 1; i < m; i++) {
			levenMatrix[i][0] = i;
			levenOperations[i][0]='c';
		}
		//fill first row from 1 to |second|
		for (int j = 1; j < n; j++) {
			levenMatrix[0][j] = j;
			levenOperations[0][j]='c';
		}
		
		int cost = 0;//variable to save difference in characters
		//iterate over 2D array
		for (int j = 1; j < n; j++) {
			for (int i = 1; i < m; i++) {
				if (first.charAt(i-1) == second.charAt(j-1)) {//if characters are equal cost for replacement = 0
					cost = 0;
				}else {
					cost = 1;
				}
				levenMatrix[i][j] = Math.min(Math.min((
						levenMatrix[i-1][j]+1),/*deletion of char*/
						(levenMatrix[i][j-1]+1)),/*insertion of char*/
						(levenMatrix[i-1][j-1]+cost));/*change of char*/
				
				//put char in Operation Matrix
				if (levenMatrix[i][j] == levenMatrix[i-1][j]+1) {
					levenOperations[i][j]='d';//deletion of char
				}else if (levenMatrix[i][j] == levenMatrix[i][j-1]+1) {
					levenOperations[i][j]='i';//insertion of char
				}else if (levenMatrix[i][j] == levenMatrix[i-1][j-1]+cost) {
					if (cost == 1) {
						levenOperations[i][j]='c';	//change of char
					}else {
						levenOperations[i][j]='n';	//no change of char
					}					
				}else {
					levenOperations[i][j]='e'; //error
					System.err.println("Fatal Levenshtein Error");
				}
			}
		}
		return new LevenshteinResult(levenMatrix, levenOperations);
	}
	
	public static void main(String[] args) {
		ConsoleIO.printCharMatrix(extendetLeventhsein("allo", "hallo").levenshteinOperations);
		System.out.println();
		ConsoleIO.printIntMatrix(extendetLeventhsein("allo", "hallo").levenshteinMatrix);
	}
}
