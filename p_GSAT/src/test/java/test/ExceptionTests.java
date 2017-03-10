package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import analysis.AnalysedSequence;
import analysis.Gene;
import exceptions.CorruptedSequenceException;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseErrorException;
import exceptions.DissimilarGeneException;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.PathUsage;
import exceptions.UndefinedTypeOfMutationException;

/**
 * This class tests the behavior of the exceptions - whether they contain the
 * right information when they are thrown.
 * 
 * @author Ben Kohr
 *
 */
public class ExceptionTests {

	/**
	 * An AnalyzedSequence object for test purposes.
	 */
	static AnalysedSequence seq1 = new AnalysedSequence("ATRGATCG", "Kurt Hafer", "sequence1.ab1", null);

	/**
	 * This test checks if the exception indicating a wrong character in a
	 * sequence works correctly (given a nucleotide sequence only).
	 * 
	 * @see exceptions.CorruptedSequenceException
	 * 
	 * @author Ben Kohr
	 */
	@Test
	public void testCorruptedSequenceException() {

		try {
			throw new CorruptedSequenceException(7, 'L', "ATUCGGCL");

		} catch (CorruptedSequenceException e) {

			// Check if the error message is correctly produced
			assertEquals("Problem in observed nucleotide sequence: Index 7 is 'L', but should be 'A', 'T', 'C' or 'G'.",
					e.getMessage());

			// Check whether the fields are set correctly
			assertEquals(7, e.index);
			assertEquals('L', e.problem);

			assertEquals("ATUCGGCL", e.nucleotides);

		}

	}

	/**
	 * This test checks if the exception that is thrown if the database
	 * connection could not be set up works correctly.
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
	 * This test checks if the exception indicating and error while working with
	 * the database works correctly.
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
	 * This test checks if the exception indicating a badly fitting gene works.
	 * 
	 * @see exceptions.DissimilarGeneException
	 * 
	 * @author Ben Kohr
	 */
	@Test
	public void testDissimilarGeneException() {

		AnalysedSequence toAnalyse = new AnalysedSequence("TCTCTAGAGC", "Klaus Hafer", "sequence.ab1", null);
		Gene bestGene = new Gene("AATC", 1, "nicht FSA", "Karl Mueller");

		try {
			throw new DissimilarGeneException(toAnalyse, bestGene, 20.3);
		} catch (DissimilarGeneException e) {

			// Check if the error message is correctly produced
			assertEquals(
					"Best found gene for given sequence (nicht FSA) has a similarity of only 20.3 for the given sequence (file: sequence.ab1).",
					e.getMessage());

			// Check if the fields are set
			assertEquals(toAnalyse, e.toAnalyse);
			assertEquals(bestGene, e.bestGene);
			assertTrue(Math.abs(e.similarity - 20.3) < 0.1);

		}
	}

	/**
	 * This test checks if the exception indicating a reading error works
	 * correctly.
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
	 * This test checks if the exception that is thrown if a missing path is
	 * detected works correctly.
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
	 * This test checks if the exception indicating an unknown mutation type is
	 * working correctly.
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
