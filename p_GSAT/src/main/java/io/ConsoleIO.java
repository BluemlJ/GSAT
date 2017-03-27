package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class to handle Console outputs
 * 
 * @author Kevin Otto
 *
 */
public class ConsoleIO {

  /**
   * WARNING: Highly experimental and still untested Needs To be testet in builded Verion (as JAR)
   * WARNING: Black Magic incoming Should clear Console OS independently
   */
  public static void clearConsole() {
    try {
      // get OS name
      final String os = System.getProperty("os.name");

      // check if OS is Windows
      if (os.contains("Windows")) {
        // Black Windows Magic
        // (cmd command for clear console)
        Runtime.getRuntime().exec("cmd /c cls");
      } else {
        // Black Linux Magic
        // (ANSI escape commant for clear console)
        System.out.print("\033[H\033[2J");
        System.out.flush();
      }
    } catch (final Exception e) {
      // no idea what could possibly go wrong (may be anything)
      System.err.println("Error duringing console clearing.");
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
   * Asks the User to type in a Number (Integer) and returns it as int
   * 
   * @param message the message displayed to the User
   * @return
   * @throws IOException
   */
  public static int readInt(String message) throws IOException {
    // create reader
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int intput = 0;

    boolean notValid = true;

    // while input is not a number, ask again.
    while (notValid) {
      System.out.print(message);
      System.out.println();
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

  /**
   * Asks the User to type in a Number in range from min to max (Integer) and returns it as int
   * Queston will be repeated if input is not a Number or to big or to small Warning: an Error is
   * printen when min and max are switched
   * 
   * @param message the message displayed to the User
   * @return
   * @throws IOException
   */
  public static int readInt(String message, int min, int max) throws IOException {

    // check for min and max flip
    if (min > max) {
      // print error
      System.err.println("ERROR: min > max!");

      // corect flip to prevent endless loop
      int tmp = min;
      min = max;
      max = tmp;
    }

    // create reader
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int intput = 0;

    boolean notValid = true;

    // while input is not valid, again
    while (notValid) {
      System.out.print(message);
      System.out.println();
      try {
        intput = Integer.parseInt(br.readLine());
        if (max >= intput && intput >= min) {
          notValid = false;
        } else {
          throw new NumberFormatException();
        }
      } catch (NumberFormatException nfe) {
        notValid = true;
        System.err.println("Please Enter a Valid Number in range from " + min + " to " + max);
      }
    }
    return intput;
  }

  /**
   * Asks the User to Type text into console and returns the text as String.
   * 
   * @param message the message displayed to the User
   * @return
   * @throws IOException
   */
  public static String readLine(String message) throws IOException {
    // create reader
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    // print message and go to next line
    System.out.print(message);
    System.out.println();

    // read input from user
    String input = br.readLine();
    return input;
  }
}
