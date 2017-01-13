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

  /**
   * This test tries to access a sample file from our repository by using a relative path
   */
  @Test
  public void testFileAccessibility() {
    File testFile =
        new File(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
    assertTrue(testFile.exists());
  }

  /**
   * This test confirms the jillion framework is accessible by calling a static method from the
   * framework
   */
  @Test
  public void testJillionAccessibility() {
    assertEquals(PrimerUtil.M13_FORWARD_PRIMER.toString(), "TGTAAAACGACGGCCAGT");
  }

  /**
   * This test checks if a sample .abi file is readable using jillion
   * 
   * @throws IOException
   */
  @Test
  public void testJillionRead() throws IOException {
    File testFile =
        new File(getClass().getResource("/ab1/Tk_Gs40Hits/Forward/95EI60.ab1").getFile());
    Chromatogram chroma = ChromatogramFactory.create(testFile);
    assertEquals(chroma.getNucleotideSequence().getLength(), 1482);
  }
}
