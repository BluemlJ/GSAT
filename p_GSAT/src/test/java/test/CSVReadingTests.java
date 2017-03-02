package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Test;

import analysis.AnalysedSequence;
import exceptions.DuplicateGeneException;
import io.FileRetriever;
import io.GeneHandler;

public class CSVReadingTests {

  
  public static String path = "resources" + File.separator + "readingtests" + File.separator;
  
  
  @Test
  public void testReadingCSV() throws IOException, DuplicateGeneException {
    GeneHandler.addGene("FSA", "AATAAT", "ecoli", "comment");
    LinkedList<AnalysedSequence> result = FileRetriever.convertFilesToSequences(path);
    
    result.sort((s1, s2) -> {return s1.getFileName().compareTo(s2.getFileName()); });
    
    // there are only three sequences
    assertTrue(result.size() == 3);
    
    // first sequence
    
    AnalysedSequence test = result.pop();
    
    assertEquals("x.ab1", test.getFileName());
    assertEquals("FSA", test.getReferencedGene().getName());
    
    assertEquals("A4R (CGC)", test.getMutations().getFirst());
    assertTrue(test.getMutations().size() == 1);
    
    assertEquals("", test.getComments());
    assertEquals("testresearcher", test.getResearcher());
    assertEquals("04/03/17", test.getAddingDate());
    assertEquals("3.555", test.returnAvgQuality() + "");
    assertEquals(22, (int) test.getTrimPercentage());
    assertEquals("ATC", test.getSequence());
    assertEquals("AAA", test.getLeftVector());
    assertEquals("null", test.getRightVector());
    assertEquals("none", test.getPrimer());
    assertEquals(-1, test.getHisTagPosition());
    assertEquals(true, test.isManuallyChecked());
    
    
    // second sequence
    
    test = result.pop();
    
    assertEquals("y.ab1", test.getFileName());
    assertEquals("FSA", test.getReferencedGene().getName());
    
    assertEquals("R6F (AAA)", test.getMutations().getFirst());
    assertTrue(test.getMutations().size() == 1);
    
    assertEquals("comment", test.getComments());
    assertEquals("-", test.getResearcher());
    assertEquals("02/03/17", test.getAddingDate());
    assertEquals("99.9997646690603", test.returnAvgQuality() + "");
    assertEquals(55, (int) test.getTrimPercentage());
    assertEquals("ATC", test.getSequence());
    assertEquals("CCC", test.getLeftVector());
    assertEquals("null", test.getRightVector());
    assertEquals("none", test.getPrimer());
    assertEquals(3, test.getHisTagPosition());
    assertEquals(false, test.isManuallyChecked());
    
    // third sequence
    
    test = result.pop();
    
    assertEquals("z.ab1", test.getFileName());
    assertEquals("FSA", test.getReferencedGene().getName());
    
    assertEquals("F6R (ACT)", test.getMutations().getFirst());
    assertTrue(test.getMutations().size() == 1);
    
    assertEquals("c", test.getComments());
    assertEquals("-", test.getResearcher());
    assertEquals("02/03/17", test.getAddingDate());
    assertEquals("99.99988905066168", test.returnAvgQuality() + "");
    assertEquals(49, (int) test.getTrimPercentage());
    assertEquals("AATATC", test.getSequence());
    assertEquals("TTC", test.getLeftVector());
    assertEquals("A", test.getRightVector());
    assertEquals("none", test.getPrimer());
    assertEquals(-1, test.getHisTagPosition());
    assertEquals(true, test.isManuallyChecked());
    
  }
  
  
}
