package test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsoleTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  private PrintStream oldout = null;
  private PrintStream olderr = null;

  @Before
  public void setUpStreams() {
    olderr = System.err;
    oldout = System.out;
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @After
  public void cleanUpStreams() {
    System.setOut(oldout);
    System.setErr(olderr);
  }


  @Test
  public void testConsoleTest() {
    System.out.print("hello");
    assertEquals("hello", outContent.toString());
  }

  @Test
  public void err() {
    System.err.print("hello again");
    assertEquals("hello again", errContent.toString());
  }
}
