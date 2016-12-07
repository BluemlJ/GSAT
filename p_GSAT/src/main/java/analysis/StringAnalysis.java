package analysis;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.lang.model.element.Element;
import javax.xml.ws.Action;

/**
 * This class contains the logic of analyzing sequence strings. This class
 * serves for MutationAnaysis by providing useful String Matching methods.
 * 
 */
public class StringAnalysis {

	/**
	 * Compares to sequences and returns their similarity without finding the
	 * exact differences.
	 * 
	 * @param first
	 *            The first sequence
	 * @param second
	 *            The second sequence
	 * 
	 * @return Similarity measure
	 * @author Kevin
	 */
	public static double checkSimilarity(Sequence first, Sequence second) {
		return checkSimilarity(first.sequence, second.sequence);
	}

	/**
	 * Compares to String and returns their similarity without finding the exact
	 * differences.
	 * 
	 * @param first
	 *            The first String
	 * @param second
	 *            The second String
	 * 
	 * @return Similarity measure
	 * @author Kevin
	 */
	public static double checkSimilarity(String first, String second) {
		double levenshteinIndex = getLevenshteinIndex(first, second);
		double avgLength = (first.length() + second.length()) / 2;
		return Math.max(0, 100 - (levenshteinIndex / (avgLength / 100)));
	}

	/**
	 * calculates Levensthein Matrix of first and second using
	 * calculateLevenshteinMatrix(first, second) and returns the Levenshtein
	 * Index
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
	 * @param matrix
	 *            the Levenshtein Matrix
	 * @return
	 * @author Kevin
	 */
	public static int getLevenshteinIndex(int[][] matrix) {
		// get levensthein index out of matrix
		return matrix[matrix.length - 1][matrix[matrix.length - 1].length - 1];
	}

	/**
	 * note: return with nops!
	 * 
	 * @param sOne
	 * @param sTwo
	 * @return
	 * @author Kevin
	 */
	private static LinkedList<String> needlemanWunsch(String sOne, String sTwo) {

		// TODO find appropriate gap Penalty
		int gabPenalty = -5;

		int matrixWidth = sOne.length() + 1;
		int matrixHeight = sTwo.length() + 1;

		// create empty Needleman Wunsch Matrix
		int[][] wunschMatrix = new int[matrixWidth][matrixHeight];

		// fill first line from 1 to |first|
		for (int i = 1; i < matrixWidth; i++) {
			wunschMatrix[i][0] = gabPenalty * i;
		}
		// fill first row from 1 to |second|
		for (int j = 1; j < matrixHeight; j++) {
			wunschMatrix[0][j] = gabPenalty * j;
		}

		int cost = 0;// variable to save difference in characters
		// iterate over 2D array
		for (int i = 1; i < matrixWidth; i++) {
			for (int j = 1; j < matrixHeight; j++) {
				int deletion = wunschMatrix[i - 1][j] + gabPenalty;
				int insertion = wunschMatrix[i][j - 1] + gabPenalty;
				int match = wunschMatrix[i - 1][j - 1] + Similarity(sOne.charAt(i), sTwo.charAt(j));
				wunschMatrix[i][j] = Math.max(Math.max(deletion, insertion), match);
			}
		}
		return findNeedlemanWunschPath(wunschMatrix);
	}

	private static LinkedList<String> findNeedlemanWunschPath(int[][] wunschMatrix) {
		// TODO Auto-generated method stub
		return null;
	}

	private static int Similarity(char a, char b) {
		// TODO Implement
		return 0;
	}

	/**
	 * Calculates the Levensthein Matrix of two Strings. The Matrix gives
	 * information about the differences of the two Strings and the best way to
	 * transform them into another.
	 * 
	 * for more information: https://en.wikipedia.org/wiki/Levenshtein_distance
	 * 
	 * @param first
	 *            The first String
	 * @param second
	 *            The second String
	 * @return
	 * @author Kevin Otto
	 */
	public static int[][] calculateLevenshteinMatrix(String first, String second) {

		int matrixHeight = first.length() + 1;
		int matrixWidth = second.length() + 1;

		int[][] levenMatrix = new int[matrixHeight][matrixWidth];// create empty
																	// Levenshtein
																	// Matrix

		// fill first line from 1 to |first|
		for (int i = 1; i < matrixHeight; i++) {
			levenMatrix[i][0] = i;
		}
		// fill first row from 1 to |second|
		for (int j = 1; j < matrixWidth; j++) {
			levenMatrix[0][j] = j;
		}

		int cost = 0;// variable to save difference in characters
		// iterate over 2D array
		for (int j = 1; j < matrixWidth; j++) {
			for (int i = 1; i < matrixHeight; i++) {
				// if characters are equal cost for replacement = 0
				if (first.charAt(i - 1) == second.charAt(j - 1)) {
					cost = 0;
				} else {
					cost = 1;
				}
				levenMatrix[i][j] = Math.min(Math.min(
						(levenMatrix[i - 1][j] + 1), /* deletion of char */
						(levenMatrix[i][j - 1] + 1)), /* insertion of char */
						(levenMatrix[i - 1][j - 1] + cost));/* change of char */
			}
		}
		return levenMatrix;
	}

	/**
	 * cuts out the Vector from a given sequence
	 * @param sequence
	 * @param gen
	 * @author Kevin
	 */
	public static void cutVector(Sequence sequence, Gene gen){
		Pair<Integer, String> match = findBestMatch(sequence.sequence, gen.sequence);
		String newSequence =sequence.sequence.substring(match.key, match.value.length());
		sequence.setSequence(newSequence);
	}
	
	/**
	 * 
	 * @param longString
	 *            the String to search in
	 * @param toFind
	 *            the String to search for
	 * @return
	 * 
	 * @author Kevin
	 */
	public static Pair findBestMatch(String longString, String toFind) {

		/*
		 * Comparator<String> similarity = new Comparator<String>() {
		 * 
		 * @Override public int compare(String o1, String o2) { double
		 * similarityOne = checkSimilarity(o1, longString); double similarityTwo
		 * = checkSimilarity(o2, longString); if (similarityOne > similarityTwo)
		 * { return (int) (similarityOne * 100); } else if (similarityOne <
		 * similarityTwo) { return (int) (similarityTwo * 100); } else { if
		 * (o1.length() > o2.length()) { return (int) (similarityOne * 100); }
		 * else { return (int) (similarityTwo * 100); } } } };
		 */

		TreeMap<Double, Pair<Integer, String>> matches = new TreeMap<>();

		//go throu every supString
		
		//begin at every possition
		for (int begin = 0; begin < (longString.length() - 1); begin++) {
			
			//end at every possition
			for (int end = begin + 1; end < longString.length(); end++) {
				//get supString
				String canditate = longString.substring(begin, end);
				//calculate similarity
				Double rating = checkSimilarity(canditate, toFind);

				//check if two strings have the same Similarity
				if (matches.containsKey(rating)) {
					//if yes, take the longer one
					String doubleHit = matches.get(rating).value;
					if (doubleHit.length() < canditate.length()) {
						matches.put(rating, new Pair<Integer, String>(begin, canditate));
					}
				} else {
					matches.put(rating, new Pair<Integer, String>(begin, canditate));
				}
			}
		}
		return matches.pollFirstEntry().getValue();
	}

	/**
	 * Helper class to store Pairs
	 * @author Kevin
	 *
	 * @param <Key>
	 * @param <Value>
	 */
	public static class Pair<Key, Value> {

		public Key key;
		public Value value;

		public Pair(Key k, Value v) {
			key = k;
			value = v;
		}
	}
}
