import static org.junit.Assert.*;
import org.biojava.nbio.core.sequence.*;
import org.junit.Test;

public class BioJavaTest {

    @Test
    public void testIntegration() {
	DNASequence seq = new DNASequence("GTAC"); 
	assertTrue("seq should be initialized", (seq instanceof DNASequence));
    }

}
