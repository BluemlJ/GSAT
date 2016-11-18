package test;

import static org.junit.Assert.*;
import org.biojava.nbio.core.sequence.*;
import org.biojava.nbio.core.exceptions.*;
import org.junit.Test;

/**
 * Tests to check the correct integration of the BioJava framework in our project.
 * 
 * @author Lovis Heindrich
 *
 */
public class BioJavaTest {

	/**
	 * This test checks whether the BioJavaFramework is usable (i.e. it is configured
	 * correctly) by referring to classes from the framework.
	 * 
	 * @throws CompoundNotFoundException
	 * 
	 * @author Lovis Heindrich
	 */
	@Test
	public void testIntegration() throws CompoundNotFoundException {
		DNASequence seq = new DNASequence("GTAC");
		assertTrue("seq should be initialized", (seq instanceof DNASequence));
	}

}
