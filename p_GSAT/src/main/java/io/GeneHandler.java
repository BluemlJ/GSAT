package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
public class GeneHandler {
  private static ArrayList<Gene> geneList;
  private static String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "genes.txt";
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

    path = genePath;

    // read all genes from path to avoid losing data while rewriting
    readGenes(path);

    // check if the new gene already exists
    if (GeneHandler.containsGene(geneName)) {
      throw new DuplicateGeneException(geneName);
    }

    geneList.add(new Gene(geneSequence, 0, geneName, ConfigHandler.getResearcher()));

    writeGenes(genePath);
  }

  public static void deleteGene(String newpath, String geneName) throws IOException {
    for (int i = 0; i < geneList.size(); i++) {
      if (geneList.get(i).getName().equals(geneName)) {
        geneList.remove(i);
      }
    }
    writeGenes(newpath);
  }

  public static void writeGenes(String genePath) throws IOException {
    // clears all genes from file
    BufferedWriter geneWriter = new BufferedWriter(new FileWriter(genePath));

    // write all previously known genes
    for (Gene gene : geneList) {
      geneWriter.write(gene.getName() + SEPARATOR + gene.getSequence());
      geneWriter.write(System.getProperty("line.separator"));
    }

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
    initGenes();
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
        geneList.add(new Gene(gene, id, name, ConfigHandler.getResearcher()));
        id++;
      }
    }

    geneReader.close();
    
    
    //check if there are no genes
    if(geneList == null){
      try {
        addGene("FSA", "ATGGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGACCACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGTCAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTATTGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGACGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGTTAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCGCAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTACTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTGGCAGGGAGCGTTTGGCAGAACGTCGATTTAA");
        writeGenes();
      } catch (DuplicateGeneException e) {
        //should never happen
        e.printStackTrace();
      }
    }
  }

  /**
   * check if a genes.txt file exists at the given path
   * 
   * @return true if a gene.txt exists
   */
  public static boolean exists() {
    File config = new File(path);
    return config.exists();
  }

  /**
   * create a new gene file in the user home directory in a folder named gsat
   * 
   * @throws IOException
   * 
   */
  public static void initGenes() throws IOException {
    if (!exists()) {
      File geneFile = new File(path);
      geneFile.getParentFile().mkdirs();
      geneFile.createNewFile();
    }
  }

  /**
   * set the path of the gene.txt file
   * 
   * @param path
   */
  public static void setPath(String path) {
    GeneHandler.path = path;
  }

  public static void writeGenes() throws IOException {
    writeGenes(path);

  }

  public static void deleteGene(String string) throws IOException {
    deleteGene(path, string);

  }
}
