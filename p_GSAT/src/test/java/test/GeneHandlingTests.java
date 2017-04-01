package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import exceptions.DuplicateGeneException;
import io.GeneHandler;

/**
 * Tests for the automatic reading of reference genes from a file
 * 
 * @author lovisheindrich
 *
 */
public class GeneHandlingTests {

  private String path;
  private String writePath;

  /**
   * initialize the geneReader with a sample gene file
   * 
   * @throws IOException
   */
  @Before
  public void initGeneReader() throws IOException {
    // path = getClass().getResource("/GeneData/Genes.txt").getFile();
    path = new File("resources/GeneData/Genes.txt").getAbsolutePath();
    writePath = new File("resources/GeneData/GenesWriteTest.txt").getAbsolutePath();
    GeneHandler.readGenes(path);

  }

  /**
   * This tests checks if a list of genes is created after reading
   * 
   */
  @Test
  public void testGeneListNotEmpty() {
    assertEquals(GeneHandler.getGeneList().isEmpty(), false);
  }

  /**
   * This test checks if it is possible to access a gene by its name
   */
  @Test
  public void testGetGeneFromName() {
    assertEquals(GeneHandler.getGene("FSA").getSequence(),
        "atgGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTtaa"
            .toUpperCase());
  }

  /**
   * This test tries to write a single new Gene and then tries to read this gene from the file
   * 
   * @throws DuplicateGeneException
   * @throws IOException
   */
  @Test
  public void testWriteGene() throws DuplicateGeneException, IOException {
    GeneHandler.clearTxtFile(writePath);
    GeneHandler.addGene(writePath, "testGene", "aaatttaaaggg");
    assertEquals(GeneHandler.getGene("testGene").getSequence(), "aaatttaaaggg".toUpperCase());
  }

  /**
   * This test tries to write multiple new genes and then tries to read the genes from the file
   * 
   * @throws DuplicateGeneException
   * @throws IOException
   */
  @Test
  public void testWriteGenes() throws DuplicateGeneException, IOException {
    GeneHandler.clearTxtFile(writePath);
    GeneHandler.addGene(writePath, "testGene", "aaatttaaaggg");
    GeneHandler.addGene(writePath, "testGene2", "aaatttaaaggg");
    assertEquals(GeneHandler.getGene("testGene").getSequence(), "aaatttaaaggg".toUpperCase());
    assertEquals(GeneHandler.getGene("testGene2").getSequence(), "aaatttaaaggg".toUpperCase());
  }

  /**
   * This test checks if a deleted gene can´t be accessed anymore (Userstory 023 - Expected
   * behavior)
   * 
   * @throws IOException
   * @throws DuplicateGeneException
   */
  @Test
  public void testGeneDelete() throws IOException, DuplicateGeneException {
    GeneHandler.clearTxtFile(writePath);
    GeneHandler.addGene(writePath, "testGene", "aaatttaaaggg");
    GeneHandler.addGene(writePath, "testGene2", "aaatttaaaggg");
    GeneHandler.addGene(writePath, "testGene3", "aaatttaaaggg");
    assertEquals(GeneHandler.getGene("testGene").getSequence(), "aaatttaaaggg".toUpperCase());
    assertEquals(GeneHandler.getGene("testGene2").getSequence(), "aaatttaaaggg".toUpperCase());
    assertEquals(GeneHandler.getGene("testGene3").getSequence(), "aaatttaaaggg".toUpperCase());
    GeneHandler.deleteGene(writePath, "testGene2");
    assertTrue(GeneHandler.getGene("testGene") != null);
    assertEquals(GeneHandler.getGene("testGene2"), null);
    assertTrue(GeneHandler.getGene("testGene3") != null);
  }

  /**
   * This test checks if adding a gene that already exists causes a duplicate gene exception
   * (Userstory 023 - Unusual behavior)
   * 
   * @throws IOException
   */
  @Test
  public void writeDuplicateGene() throws IOException {
    GeneHandler.clearTxtFile(writePath);
    try {
      GeneHandler.addGene(writePath, "testGene", "aaatttaaaggg");
      GeneHandler.addGene(writePath, "testGene", "aaatttaaaggg");
      fail();
    } catch (DuplicateGeneException e) {
      assertEquals(e.getMessage(), "Gene testGene already exists.");
    }
  }

  /**
   * This test tries to write multiple new genes including comment and organism and then tries to
   * read the genes from the file
   * 
   * @throws DuplicateGeneException
   * @throws IOException
   */
  @Test
  public void testWriteGenesCommentOrganism() throws DuplicateGeneException, IOException {
    String oldPath = GeneHandler.getPath();
    GeneHandler.setPath(writePath);
    GeneHandler.clearTxtFile(writePath);

    GeneHandler.addGene("testGene", "aaatttaaaggg", "organism1", "comment1");
    GeneHandler.addGene("testGene2", "aaatttaaaggg", "organism2", "comment2");
    GeneHandler.writeGenes(writePath);
    assertEquals(GeneHandler.getGene("testGene").getSequence(), "aaatttaaaggg".toUpperCase());
    assertEquals(GeneHandler.getGene("testGene2").getSequence(), "aaatttaaaggg".toUpperCase());

    assertEquals(GeneHandler.getGene("testGene").getOrganism(), "organism1");
    assertEquals(GeneHandler.getGene("testGene2").getOrganism(), "organism2");

    assertEquals(GeneHandler.getGene("testGene").getComment(), "comment1");
    assertEquals(GeneHandler.getGene("testGene2").getComment(), "comment2");
    GeneHandler.setPath(oldPath);
  }

  /**
   * This test checks if initGenes makes a new file if none exists and if the placeholder gene is
   * written to the new file
   * 
   * @throws IOException
   */
  @Test
  public void initGeneTestNoFile() throws IOException {
    String oldPath = GeneHandler.getPath();
    GeneHandler.setPath(writePath);

    File geneFile = new File(writePath);
    geneFile.delete();

    GeneHandler.initGenes();

    GeneHandler.readGenes();

    assertTrue(GeneHandler.getGene("fsa") != null);

    GeneHandler.setPath(oldPath);
  }

  /**
   * This test verifies that initGenes does not overwrite any existing genes if the genes.txt file
   * already exists
   * 
   * @throws IOException
   * @throws DuplicateGeneException
   */
  @Test
  public void initGeneTestFileExists() throws IOException, DuplicateGeneException {
    String oldPath = GeneHandler.getPath();
    GeneHandler.setPath(writePath);

    GeneHandler.clearTxtFile(writePath);

    GeneHandler.addGene("testGene", "aaatttaaaggg", "organism1", "comment1");
    GeneHandler.readGenes();
    assertEquals(GeneHandler.getGene("testGene").getSequence(), "aaatttaaaggg".toUpperCase());

    GeneHandler.initGenes();
    GeneHandler.readGenes();

    assertEquals(GeneHandler.getGene("testGene").getSequence(), "aaatttaaaggg".toUpperCase());
    GeneHandler.setPath(oldPath);
  }

  /**
   * this test verifies that an exception is thrown when an invalid path is set
   * 
   * @throws IOException
   */
  @Test(expected = NullPointerException.class)
  public void initGeneInvalidPath() throws IOException {
    String oldPath = GeneHandler.getPath();
    GeneHandler.setPath("aaaa");
    GeneHandler.initGenes();
    GeneHandler.setPath(oldPath);
  }
}
