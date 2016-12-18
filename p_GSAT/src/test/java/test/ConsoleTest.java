package test;

import static org.junit.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exceptions.CorruptedSequenceException;
import io.ConsoleIO;

public class ConsoleTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @After
  public void cleanUpStreams() {
    System.setOut(null);
    System.setErr(null);
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
