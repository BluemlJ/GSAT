package io;

/**
 * Class to handle Console outputs
 * 
 * @author Kevin Otto
 *
 */
public class ConsoleIO {

  /**
   * outputs a Integer Matrix (int[][]) to Console and Formating it for better readability
   * 
   * @param mat matrix (int[][]) to print
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
   * outputs a Char Matrix (char[][]) to Console and Formating it for better readability
   * 
   * @param mat matrix (char[][]) to print
   */
  public static void printCharMatrix(char[][] mat) {
    for (int j = 0; j < mat[0].length; j++) {
      for (int i = 0; i < mat.length; i++) {
        System.out.print(mat[i][j] + " ");
      }
      System.out.println();
    }
  }
}
