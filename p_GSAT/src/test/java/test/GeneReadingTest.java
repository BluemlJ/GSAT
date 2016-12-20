package test;

import java.io.IOException;

import org.junit.Test;

import io.GeneReader;

public class GeneReadingTest {
  
  @Test
  public void testRead() throws IOException{
    GeneReader.readGenes(getClass().getResource("/GeneData/Genes.txt").getFile());
  }
}
