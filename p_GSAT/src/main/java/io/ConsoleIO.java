package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.Main;

/**
 * Class to handle Console outputs
 * 
 * @author Kevin Otto
 *
 */
public class ConsoleIO {

	/**
	 * outputs a Integer Matrix (int[][]) to Console and Formating it for better
	 * readability
	 * 
	 * @param mat
	 *            matrix (int[][]) to print
	 */
	public static void printIntMatrix(int[][] mat) {
		for (int j = 0; j < mat[0].length; j++) {
			for (int i = 0; i < mat.length; i++) {
				System.out.print(mat[i][j] + " ");
			}
			System.out.println();
		}
	}

	/**
	 * outputs a Char Matrix (char[][]) to Console and Formating it for better
	 * readability
	 * 
	 * @param mat
	 *            matrix (char[][]) to print
	 */
	public static void printCharMatrix(char[][] mat) {
		for (int j = 0; j < mat[0].length; j++) {
			for (int i = 0; i < mat.length; i++) {
				System.out.print(mat[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static int consoleReadInt(String message, int max, int min) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int intput = 0;

		boolean notValid = true;

		while (notValid) {
			System.out.print(message);
			try {
				intput = Integer.parseInt(br.readLine());
				if (max >= intput && intput >= min) {
					notValid = false;
				}
			} catch (NumberFormatException nfe) {
				System.err.println("Please Enter a Valid Number in range from" + min + " to " + max);
			}
		}
		return intput;
	}
	
	public static int consoleReadInt(String message) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int intput = 0;

		boolean notValid = true;

		while (notValid) {
			System.out.print(message);
			try {
				intput = Integer.parseInt(br.readLine());
				notValid = false;
			} catch (NumberFormatException nfe) {
				notValid = true;
				System.err.println("Please Enter a Valid Number");
			}
		}
		return intput;
	}

	public static String consoleReadLine(String message) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print(message);
		String input = br.readLine();
		return input;
	}
	
	public static void main(String[] args) {
		try {
			consoleReadLine("text bitte");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
