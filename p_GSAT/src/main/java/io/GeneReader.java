package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import analysis.Gene;
import exceptions.DuplicateGeneException;

/**
 * This class reads reference genes from a txt and stores them
 * 
 * @author lovisheindrich
 *
 */
public class GeneReader {
  private static String path;
  private static ArrayList<Gene> geneList;
  private final static String SEPARATOR = ";";

  public static void addGene(String geneName, String geneSequence)
      throws DuplicateGeneException, IOException {
    addGene(path, geneName, geneSequence);
  }

  /**
   * add a new gene to genes.txt
   * 
   * @param genePath path of the genes.txt file
   * @param geneName name of the gene
   * @param geneSequence sequence string of the gene
   * @throws DuplicateGeneException gene name already exists
   * @throws IOException error while writing the file
   */
  public static void addGene(String genePath, String geneName, String geneSequence)
      throws DuplicateGeneException, IOException {
    readGenes(path);
    if (GeneReader.containsGene(geneName)) {
      throw new DuplicateGeneException(geneName);
    }
    path = genePath;
    BufferedWriter geneWriter = new BufferedWriter(new FileWriter(genePath));
    // TODO new genes need to be in a new line, no way to know if file starts with an empty line or

    for (Gene gene : geneList) {
      geneWriter.write(gene.getName() + SEPARATOR + gene.getSequence());
      geneWriter.write(System.getProperty("line.separator"));
    }
    // with an existing line
    geneWriter.write(geneName + SEPARATOR + geneSequence);
    geneWriter.close();
    readGenes(genePath);
  }

  /**
   * clears the txt file at a given path
   * 
   * @param path
   * @throws IOException
   */
  public static void clearTxtFile(String path) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    writer.write("");
    writer.close();
  }

  /**
   * checks if a gene already exists
   * 
   * @param name
   * @return
   */
  public static boolean containsGene(String name) {
    for (int i = 0; i < geneList.size(); i++) {
      if (geneList.get(i).getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @param geneName
   * @return
   */
  public static Gene getGene(String geneName) {
    for (int i = 0; i < geneList.size(); i++) {
      if (geneList.get(i).getName().equals(geneName)) {
        return geneList.get(i);
      }
    }
    return null;
  }

  public static Gene getGeneAt(int index) {
    return geneList.get(index);
  }

  public static ArrayList<Gene> getGeneList() {
    return geneList;
  }

  public static String[] getGeneNames() {
    String[] names = new String[geneList.size()];
    for (int i = 0; i < geneList.size(); i++) {
      names[i] = geneList.get(i).getName();
    }
    return names;
  }

  public static int getNumGenes() {
    return geneList.size();
  }

  /**
   * reads genes from gene file at the locally stored path
   * 
   * @throws IOException
   */
  public static void readGenes() throws IOException {
    readGenes(path);
  }

  /**
   * reads a gene.txt from a given path
   * 
   * @param genePath
   * @throws IOException
   */
  public static void readGenes(String genePath) throws IOException {
    path = genePath;
    geneList = new ArrayList<Gene>();
    BufferedReader geneReader = new BufferedReader(new FileReader(genePath));
    String line;
    int id = 0;
    // for each line
    while ((line = geneReader.readLine()) != null) {
      // format "name=atgAAT..."
      String sepLine[] = line.split(SEPARATOR);
      String name = sepLine[0];
      String gene = sepLine[1];
      if (getGene(name) == null) {
        geneList.add(new Gene(gene, id, name, Config.researcher));
        id++;
      }
    }

    geneReader.close();
  }

  /**
   * set the path of the gene.txt file
   * 
   * @param path
   */
  public static void setPath(String path) {
    GeneReader.path = path;
  }
}

