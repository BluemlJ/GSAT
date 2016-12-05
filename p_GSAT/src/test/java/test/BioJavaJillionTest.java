package test;

import static org.junit.Assert.*;

import org.biojava.bio.seq.DNATools;
import org.jcvi.jillion.experimental.primer.PrimerUtil;
import org.junit.Test;

public class BioJavaJillionTest {
  
  @Test
  public void testBioJavaAccessibility(){
    assertEquals(DNATools.a().getName(), "adenine");
  }
  
  @Test
  public void testJillionAccessibility(){
    assertEquals(PrimerUtil.M13_FORWARD_PRIMER.toString(), "TGTAAAACGACGGCCAGT");
  }
}


