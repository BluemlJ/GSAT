package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import analysis.AnalysedSequence;
import exceptions.*;

/**
 * This class tests the behavior of the exceptions - whether they contain the right information when
 * they are thrown.
 * 
 * @author Ben Kohr
 *
 */
public class ExceptionTests {


  /**
   * An AnalyzedSequence object for test purposes.
   */
  static AnalysedSequence seq1 =
      new AnalysedSequence("ATRGATCG", "2016-11-28", "Kurt Hafer", "sequence1.ab1", "No comments", null);


  /**
   * This test checks if the exception indicating a wrong character in a sequence works correctly
   * (given an AnalyzedSequenceObject).
   * 
   * @see exceptions.CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testCorruptedSequenceExceptionWithSequence() {

    try {
      throw new CorruptedSequenceException(2, 'R', seq1);

    } catch (CorruptedSequenceException e) {

      // Check if the error message is correctly produced
      assertEquals(
          "Problem in observed AnalyzedSequence: Index 2 is 'R', but should be 'A', 'T', 'C' or 'G'.",
          e.getMessage());

      // Check whether the fields are set correctly
      assertEquals(2, e.index);
      assertEquals('R', e.problem);
      assertSame(seq1, e.sequence);
      assertEquals(seq1.getSequence(), e.nucleotides);

    }

  }



  /**
   * This test checks if the exception indicating a wrong character in a sequence works correctly
   * (given a nucleotide sequence only).
   * 
   * @see exceptions.CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testCorruptedSequenceExceptionWithNucleotideString() {

    try {
      throw new CorruptedSequenceException(7, 'L', "ATUCGGCL");

    } catch (CorruptedSequenceException e) {

      // Check if the error message is correctly produced
      assertEquals(
          "Problem in observed nucleotide sequence: Index 7 is 'L', but should be 'A', 'T', 'C' or 'G'.",
          e.getMessage());

      // Check whether the fields are set correctly
      assertEquals(7, e.index);
      assertEquals('L', e.problem);

      // Only a String is given. Therefore, no Sequence should be found in the exception
      assertNull(e.sequence);
      assertEquals("ATUCGGCL", e.nucleotides);

    }

  }

  /**
   * This test checks if the exception that is thrown if the database connection could not be set up
   * works correctly.
   * 
   * @see exceptions.DatabaseConnectionException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testDatabaseConnectionException() {

    try {
      throw new DatabaseConnectionException();

    } catch (DatabaseConnectionException e) {

      // Check if the error message is correctly produced
      assertEquals("Connection to the database could not be established.", e.getMessage());

    }

  }


  /**
   * This test checks if the exception indicating and error while working with the database works
   * correctly.
   * 
   * @see exceptions.DatabaseErrorException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testDatabaseErrorException() {

    try {
      throw new DatabaseErrorException();

    } catch (DatabaseErrorException e) {

      // Check if the error message is correctly produced
      assertEquals("Error while database processing.", e.getMessage());

    }
  }


  /**
   * This test checks if the exception indicating a reading error works correctly.
   * 
   * @see exceptions.FileReadingException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testFileReadingException() {

    try {
      throw new FileReadingException("myFile.ab1");
    } catch (FileReadingException e) {

      // Check if the error message is correctly produced
      assertEquals("Error while reading file myFile.ab1.", e.getMessage());

      // Check if the name is also stored in the exception object
      assertEquals("myFile.ab1", e.filename);

    }
  }


  /**
   * This test checks if the exception that is thrown if a missing path is detected works correctly.
   * 
   * @see exceptions.MissingPathException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testMissingPathException() {

    // Check with a missing path for writing files locally.
    try {
      throw new MissingPathException(PathUsage.WRITING);
    } catch (MissingPathException e) {

      // Check if the error message is correctly produced
      assertEquals("Missing path detected (Usage: WRITING).", e.getMessage());

      // Check if the usage's name is also stored in the exception object
      assertEquals(PathUsage.WRITING, e.usage);

    }

    // Same check for the database path.
    try {
      throw new MissingPathException(PathUsage.DATABASE);
    } catch (MissingPathException e) {

      // Check if the error message is correctly produced
      assertEquals("Missing path detected (Usage: DATABASE).", e.getMessage());

      // Check if the usage's name is also stored in the exception object
      assertEquals(PathUsage.DATABASE, e.usage);

    }

    // Same check for the reading path.
    try {
      throw new MissingPathException(PathUsage.READING);
    } catch (MissingPathException e) {

      // Check if the error message is correctly produced
      assertEquals("Missing path detected (Usage: READING).", e.getMessage());

      // Check if the usage's name is also stored in the exception object
      assertEquals(PathUsage.READING, e.usage);

    }

  }


  /**
   * This test checks if the exception indicating an unknown mutation type is working correctly.
   * 
   * @see exceptions.CorruptedSequenceException
   * 
   * @author Ben Kohr
   */
  @Test
  public void testUndefinedTypeOfMutationException() {

    try {
      throw new UndefinedTypeOfMutationException("y");
    } catch (UndefinedTypeOfMutationException e) {

      // Check if the error message is correctly produced
      assertEquals(
          "Problem in mutation result: The String y was observed but is not a valid mutation identifier.",
          e.getMessage());

      // Check if the mutations String is stored in the exception object
      assertEquals("y", e.mutationString);

    }
  }



}
