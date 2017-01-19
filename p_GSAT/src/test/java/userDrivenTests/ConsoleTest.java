package userDrivenTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.ConsoleIO;


public class ConsoleTest {


  /**
   * runns printCharMatrix with test matrix
   * 
   * expecting to see matrix in console
   * 
   * @author Kevin Otto
   */
  @Test
  public void printCharMatrixEmptyTest() {
    System.out.println("empty matrix:");
    char[][] mat = {{}};
    ConsoleIO.printCharMatrix(mat);
  }

  /**
   * runns printCharMatrix with test matrix
   * 
   * expecting to see matrix in console
   * 
   * @author Kevin Otto
   */
  @Test
  public void printCharMatrixTest() {
    char[][] mat = {{'h', 'h'}, {'i', 'o'}};
    ConsoleIO.printCharMatrix(mat);
  }

  /**
   * runns printCharMatrix with test matrix
   * 
   * expecting to see matrix in console
   * 
   * @author Kevin Otto
   */
  @Test
  public void printCharMatrixUnevenTest() {
    char[][] mat = {{'h', 'h', 'h'}, {'i', 'o', 'u'}};
    ConsoleIO.printCharMatrix(mat);
  }

  /**
   * runns printCharMatrix with test matrix
   * 
   * expecting to see matrix in console
   * 
   * @author Kevin Otto
   */
  @Test
  public void printIntMatrixEmptyTest() {
    System.out.println("empty matrix:");
    int[][] mat = {{}};
    ConsoleIO.printIntMatrix(mat);
  }

  /**
   * runns printCharMatrix with test matrix
   * 
   * expecting to see matrix in console
   * 
   * @author Kevin Otto
   */
  @Test
  public void printIntMatrixTest() {
    int[][] mat = {{1, 2}, {3, 4}};
    ConsoleIO.printIntMatrix(mat);
  }

  /**
   * runns printCharMatrix with test matrix
   * 
   * expecting to see matrix in console
   * 
   * @author Kevin Otto
   */
  @Test
  public void printIntMatrixUnevenTest() {
    int[][] mat = {{1, 2, 3}, {4, 5, 6}};
    ConsoleIO.printIntMatrix(mat);
  }

  /**
   * awayts given userimput
   * 
   * 
   * @author Kevin Otto
   * @throws IOException
   */
  @Test
  public void readbiggerIntTest() throws IOException {
    int read = ConsoleIO.readInt("Please Type \"5000\"");
    assertEquals(read, 5000);
  }

  /**
   * awayts given userimput
   * 
   * 
   * @author Kevin Otto
   * @throws IOException
   */
  @Test
  public void readIntminMaxTest() throws IOException {
    int read = ConsoleIO.readInt("Please Type a number between \"5\" and \"10\" ", 5, 10);
    assertTrue(read >= 5 && read <= 10);
  }

  /**
   * awayts given userimput
   * 
   * 
   * @author Kevin Otto
   * @throws IOException
   */
  @Test
  public void readIntTest() throws IOException {
    int read = ConsoleIO.readInt("Please Type \"5\"");
    assertEquals(read, 5);
  }

  /**
   * awayts given userimput
   * 
   * 
   * @author Kevin Otto
   * @throws IOException
   */
  @Test
  public void readLineTest() throws IOException {
    String read = ConsoleIO.readLine("type \"hello\"");
    assertEquals(read, "hello");
  }

  /**
   * awayts given userimput
   * 
   * 
   * @author Kevin Otto
   * @throws IOException
   */
  @Test
  @Ignore
  public void readNoIntTest() throws IOException {
    // int read = ConsoleIO.readInt("Please press enter");
  }

  @Before
  @After
  public void seperator() throws InterruptedException {
    Thread.sleep(100);
    System.err.println();
    System.err.println("***********************************************");
    System.err.println();
  }



}
