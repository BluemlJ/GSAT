package analysis;

/**
 * Class for containing results of Levenshtein Distance calculations
 * 
 * 
 * 
 * 
 * Variables for matrix with and height
 * int m
 * int n
 * 
 * example: matrix[m][n]
 *  
 * @author Kevin Otto
 *
 */
public class LevenshteinResult {
	public int[][] levenshteinMatrix;//Levenshtein Distance Matrix
	public char[][] levenshteinOperations;//Levenshtein operation Matrix containing needed operations for transformation

	public int  m;//matrix with
	public int  n;//matrix high
	
	/**
	 * default constructor
	 * @param levenshteinMatrix
	 * @param levenshteinOperations
	 */
	public LevenshteinResult(int[][] levenshteinMatrix, char[][] levenshteinOperations) {
		super();
		this.levenshteinMatrix = levenshteinMatrix;
		this.levenshteinOperations = levenshteinOperations;
		this.m = levenshteinMatrix.length;
		this.n = levenshteinMatrix[0].length;
	}
}