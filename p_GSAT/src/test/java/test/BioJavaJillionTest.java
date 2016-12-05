package test;

import static org.junit.Assert.*;

import java.io.File;

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
  
  @Test
  public void testFileAccessibility(){
    File testFile = new File(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
    assertTrue(testFile.exists());
  }
}


