package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
  private static final String SEPARATOR = ConfigHandler.SEPARATOR_CHAR + "";

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

    readGenes();

    // check if the new gene already exists
    if (GeneHandler.containsGene(geneName)) {
      throw new DuplicateGeneException(geneName);
    }

    geneList.add(new Gene(geneSequence, 0, geneName, ConfigHandler.getResearcher()));

    writeGenes(genePath);
  }

  /**
   * add a new gene to genes.txt
   * 
   * @param geneName name of the gene
   * @param geneSequence sequence string of the gene
   * @param organism more informations about the gene sequence
   * @param comment specific comment to this gene sequence
   * @throws DuplicateGeneException gene name already exists
   * @throws IOException error while writing the file
   */
  public static boolean addGene(String geneName, String geneSequence, String organism,
      String comment) throws DuplicateGeneException, IOException {

    readGenes();

    if (checkGene(geneName, organism) != null) {
      return false;
    }

    geneList
        .add(new Gene(geneSequence, 0, geneName, ConfigHandler.getResearcher(), organism, comment));

    writeGenes();
    return true;
  }

  /**
   * 
   * @param newpath
   * @param geneName
   * @throws IOException
   */
  public static void deleteGene(String newpath, String geneName) throws IOException {

    for (int i = 0; i < geneList.size(); i++) {
      if (geneList.get(i).getName().equals(geneName)) {
        geneList.remove(i);
      }
    }
    writeGenes(newpath);
  }

  /**
   * 
   * @param string
   * @throws IOException
   */
  public static void deleteGene(String string) throws IOException {
    deleteGene(path, string.split(" ")[0]);

  }

  /**
   * clears gene.txt and writes all known genes.
   * 
   * @param genePath
   * @throws IOException
   */
  public static void writeGenes(String genePath) throws IOException {
    // clears all genes from file
    BufferedWriter geneWriter = new BufferedWriter(new FileWriter(genePath));
    // write all previously known genes
    for (Gene gene : geneList) {
      StringBuilder geneString = new StringBuilder();
      geneString.append(gene.getName());
      geneString.append(SEPARATOR);
      geneString.append(gene.getSequence());
      geneString.append(SEPARATOR);
      if (gene.getOrganism() != null && !gene.getOrganism().equals("")) {
        geneString.append(gene.getOrganism());
      } else {
        geneString.append("none");
      }
      geneString.append(SEPARATOR);
      if (gene.getComment() != null && !gene.getComment().equals("")) {
        geneString.append(gene.getComment());
      } else {
        geneString.append("none");
      }
      geneString.append(System.getProperty("line.separator"));
      // old writing code
      /*
       * geneWriter.write( gene.getName() + SEPARATOR + gene.getSequence() + SEPARATOR +
       * gene.getOrganism()); } else { geneWriter.write(gene.getName() + SEPARATOR +
       * gene.getSequence() + SEPARATOR + "none"); }
       * geneWriter.write(System.getProperty("line.separator"));
       */
      geneWriter.write(geneString.toString());
    }

    geneWriter.close();
    readGenes(genePath);
  }


  public static void writeGenes() throws IOException {
    writeGenes(path);

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
        System.out.println("found");
        return geneList.get(i);
      }
    }
    return null;
  }

  /**
   * 
   * @param geneName
   * @return
   */
  public static Gene checkGene(String geneName, String organism) {
    if (organism != null && !organism.isEmpty()) {
      for (int i = 0; i < geneList.size(); i++) {
        if (geneList.get(i).getName().equals(geneName)) {
          
          String tmp;
          if (geneList.get(i).getOrganism() == null) {
            tmp = "none";
          } else {
            tmp = geneList.get(i).getOrganism();
          }
          
          if (tmp.equals(organism)) {
            return geneList.get(i);
          }
        }
      }
    } else {
      System.out.println("noorga");
      return getGene(geneName);
    }
    System.out.println("nofound");
    return null;
  }

  public static Gene getGeneAt(int index) {
    return geneList.get(index);
  }

  public static ArrayList<Gene> getGeneList() {
    return geneList;
  }

  /**
   * 
   * @return
   */
  public static String[] getGeneNames() {
    String[] names = new String[geneList.size()];
    for (int i = 0; i < geneList.size(); i++) {
      names[i] = geneList.get(i).getName();
    }

    Arrays.sort(names);
    return names;
  }

  /**
   * 
   * @return
   */
  public static String[] getGeneNamesAndOrganisms() {
    String[] names = new String[geneList.size()];
    for (int i = 0; i < geneList.size(); i++) {
      names[i] = geneList.get(i).getName();
      if (geneList.get(i).getOrganism() != null && !geneList.get(i).getOrganism().equals("none")) {
        names[i] = names[i] + " (" + geneList.get(i).getOrganism() + ")";
      }
    }

    Arrays.sort(names);
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

    // open genes.txt or initialize it
    path = genePath;
    initGenes();

    // read all genes from the file
    geneList = new ArrayList<Gene>();
    BufferedReader geneReader = new BufferedReader(new FileReader(genePath));
    String line;
    int id = 0;
    // for each line
    while ((line = geneReader.readLine()) != null) {
      // format "name=atgAAT..."
      String[] sepLine = line.split(SEPARATOR);
      String name = sepLine[0];
      String gene = sepLine[1];
      String organism = sepLine[2];
      String comment = sepLine[3];

      if (organism.equals("none")) {
        organism = null;
      }

      geneList.add(new Gene(gene, id, name, ConfigHandler.getResearcher(), organism, comment));
      id++;
    }

    geneReader.close();


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

      BufferedWriter geneWriter = new BufferedWriter(new FileWriter(path));
      geneWriter.write("FSA" + SEPARATOR
          + "ATGGAACTGTATCTGGATACTTCAGACGTTGTTGCGGTGAAGGCGCTGTCACGTATTTTTCCGCTGGCGGGTGTGAC"
          + "CACTAACCCAAGCATTATCGCCGCGGGTAAAAAACCGCTGGATGTTGTGCTTCCGCAACTTCATGAAGCGATGGGCGGT"
          + "CAGGGGCGTCTGTTTGCCCAGGTAATGGCTACCACTGCCGAAGGGATGGTTAATGACGCGCTTAAGCTGCGTTCTATTAT"
          + "TGCGGATATCGTGGTGAAAGTTCCGGTGACCGCCGAGGGGCTGGCAGCTATTAAGATGTTAAAAGCGGAAGGGATTCCGA"
          + "CGCTGGGAACCGCGGTATATGGCGCAGCACAAGGGCTGCTGTCGGCGCTGGCAGGTGCGGAATATGTTGCGCCTTACGT"
          + "TAATCGTATTGATGCTCAGGGCGGTAGCGGCATTCAGACTGTGACCGACTTACACCAGTTATTGAAAATGCATGCGCCG"
          + "CAGGCGAAAGTGCTGGCAGCGAGTTTCAAAACCCCGCGTCAGGCGCTGGACTGCTTACTGGCAGGATGTGAATCAATTA"
          + "CTCTGCCACTGGATGTGGCACAACAGATGATTAGCTATCCGGCGGTTGATGCCGCTGTGGCGAAGTTTGAGCAGGACTG"
          + "GCAGGGAGCGTTTGGCAGAACGTCGATTTAA" + SEPARATOR + "none" + SEPARATOR + "none");
      geneWriter.close();

    }
  }



  // GETTERs and SETTERs:


  public static void setPath(String path) {
    GeneHandler.path = path;
  }

  public static String getPath() {
    return GeneHandler.path;
  }



}
