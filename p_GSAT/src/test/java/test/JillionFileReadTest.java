package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.experimental.primer.PrimerUtil;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;
import org.junit.Test;

public class JillionFileReadTest {
  
  @Test
  public void testBioJavaAccessibility(){
    //assertEquals(DNATools.a().getName(), "adenine");
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
  
  @Test
  public void testBioJavaRead() throws IOException{
    //File testFile = new File(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
    //ABITrace myTrace = new ABITrace(testFile);
    //assertEquals(myTrace.getSequenceLength(), 1482);
  }
  
  @Test
  public void testJillionRead() throws IOException{
    File testFile = new File(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
    Chromatogram chroma = ChromatogramFactory.create(testFile);
    assertEquals(chroma.getNucleotideSequence().getLength(), 1482);
  }
}


