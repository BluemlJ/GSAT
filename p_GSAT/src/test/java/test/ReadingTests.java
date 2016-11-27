package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.junit.Test;

import analysis.AnalyzedSequence;
import exceptions.FileReadingException;
import io.SequenceReader;

/**
 * This class tests the behavior of reading AB1 files and converting them into sequences.
 *
 */
public class ReadingTests {

  /**
   * This tests checks if it is possible to read a DNA Sequence from an .abi file
   * 
   * @throws IllegalSymbolException
   * @throws FileReadingException
   * @throws IOException
   */
  @Test
  public void SequenceReadTest() throws IllegalSymbolException, FileReadingException, IOException {
    // TODO local file path
    SequenceReader
        .configurePath("D:/Dokumente/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1");
    AnalyzedSequence testSequence = SequenceReader.convertFileIntoSequence();
    assertEquals(testSequence.getSequence().substring(0, 6), "gagttt");
  }
}
