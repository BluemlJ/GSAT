package test;
import static org.junit.Assert.*;
import org.biojava.nbio.core.sequence.*;
import org.biojava.nbio.core.exceptions.*;
import org.junit.Test;

public class BioJavaTest {

    // DOES NOT WORK, probably because the BioJava framework is not globally integrated
    // into the project's resources.
    
    
    @Test
    public void testIntegration() throws CompoundNotFoundException{
	DNASequence seq = new DNASequence("GTAC"); 
	assertTrue("seq should be initialized", (seq instanceof DNASequence));
    }

}
