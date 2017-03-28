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
 * This class reads reference genes from the genes.txt and stores them.
 * 
 * @author Lovis Heindrich
 * @author Jannis Blueml
 *
 */
public class GeneHandler {

  /**
   * Arraylist containing all known genes.
   */
  private static ArrayList<Gene> geneList;

  /**
   * Path where the gene data will be stored.
   */
  private static String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "genes.txt";

  /**
   * Separator character.
   */
  private static final String SEPARATOR = ConfigHandler.SEPARATOR_CHAR + "";

  /**
   * Add a new gene if it does not exist yet. Also writes it to the genes.txt.
   * 
   * @param geneName Name of the gene.
   * @param geneSequence Sequence string of the gene.
   * @throws DuplicateGeneException Gene already exists.
   * @throws IOException Error while writing the file
   * 
   * @author Lovis Heindrich
   */
  public static void addGene(String geneName, String geneSequence)
      throws DuplicateGeneException, IOException {
    addGene(path, geneName, geneSequence);
  }

  /**
   * Add a new gene if it does not exist yet. Also writes it to the genes.txt.
   * 
   * @param genePath Path of the genes.txt file.
   * @param geneName Name of the gene.
   * @param geneSequence Sequence string of the gene.
   * @throws DuplicateGeneException Gene already exists.
   * @throws IOException Error while writing the file
   * @author Lovis Heindrich
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
   * Add a new gene if it does not exist yet. Also writes it to the genes.txt.
   * 
   * @param geneName Name of the gene.
   * @param geneSequence Sequence string of the gene.
   * @param organism Organism of the gene.
   * @param comment User comment for the gene.
   * @throws DuplicateGeneException Gene already exists.
   * @throws IOException Error while writing the file
   * @author Lovis Heindrich
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
   * Adds a gene to the list if the gene is not known yet. Also writes the updated List to
   * genes.txt.
   * 
   * @param gene The gene which will be added.
   * @return False if the gene already exists. True otherwise.
   * @throws IOException Error writing gene file.
   * @author Lovis Heindrich
   */
  public static boolean addGene(Gene gene) throws IOException {
    readGenes();

    if (checkGene(gene.getName(), gene.getOrganism()) != null) {
      return false;
    }

    geneList.add(gene);

    writeGenes();
    return true;
  }

  /**
   * Deletes a gene and rewrites the local file.
   * 
   * @param newpath Path of the gene file.
   * @param geneName Name of the gene.
   * @throws IOException Error while writing the file.
   * @author Lovis Heindrich
   */
  public static void deleteGene(String newpath, String geneName) throws IOException {

    for (int i = 0; i < geneList.size(); i++) {
      if (geneList.get(i).getName().equals(geneName) && geneList.get(i).getOrganism() == null) {
        geneList.remove(i);
      }
    }
    writeGenes(newpath);
  }

  /**
   * Deletes a gene and rewrites the local file.
   * 
   * @param string Name and id of the file.
   * @throws IOException Error while writing the file.
   * @author Lovis Heindrich
   */
  public static void deleteGene(String string) throws IOException {
    if (string.split(" ").length > 1) {
      deleteGene(path, string.split(" ")[0],
          string.split(" ")[1].substring(1, string.split(" ")[1].length() - 1));
    } else {
      deleteGene(path, string.split(" ")[0]);
    }
  }

  /**
   * Deletes a gene and rewrites the local file.
   * 
   * @param newpath Path of the gene file.
   * @param geneName Name of the gene.
   * @param organism Organism of the gene.
   * @throws IOException Error while writing the file.
   * @author Lovis Heindrich
   */
  public static void deleteGene(String newpath, String geneName, String organism)
      throws IOException {

    for (int i = 0; i < geneList.size(); i++) {
      if (geneList.get(i).getName().equals(geneName)
          && geneList.get(i).getOrganism().equals(organism)) {
        geneList.remove(i);
      }
    }
    writeGenes(newpath);
  }

  /**
   * Clears the gene.txt and rewrites all known genes.
   * 
   * @param genePath Path of the gene file.
   * @throws IOException Error writing local file.
   * @author Lovis Heindrich
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
      geneWriter.write(geneString.toString());
    }

    geneWriter.close();
    readGenes(genePath);
  }

  /**
   * Clears the gene.txt and rewrites all known genes.
   * 
   * @throws IOException Error writing local file.
   * @author Lovis Heindrich
   */
  public static void writeGenes() throws IOException {
    writeGenes(path);

  }

  /**
   * Clears the .txt file at a given path.
   * 
   * @param path Path to the txt file.
   * @throws IOException Error while writing the file.
   * @author Lovis Heindrich
   */
  public static void clearTxtFile(String path) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    writer.write("");
    writer.close();
  }

  /**
   * Checks if a gene already exists.
   * 
   * @param name Name of the gene.
   * @return True if the gene exists, else false.
   * @author Lovis Heindrich
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
   * Gets a gene identified by its name.
   * 
   * @param geneName Name of the gene.
   * @return The correct gene or null.
   * @author Lovis Heindrich
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
   * Gets a gene identified by its name and organism.
   * 
   * @param geneName The name of the gene.
   * @param organism The organism of the gene.
   * @return The correct gene or null.
   * @author Jannis Blueml
   */
  public static Gene checkGene(String geneName, String organism) {
    if (organism != null && !organism.isEmpty() && !organism.equals("none")
        && !organism.equals("null")) {
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
      return getGene(geneName);
    }
    return null;
  }

  /**
   * Gets a gene identified by its index in the gene list.
   * 
   * @param index The index of the gene.
   * @return The correct gene.
   * @author Lovis Heindrich
   */
  public static Gene getGeneAt(int index) {
    return geneList.get(index);
  }

  /**
   * Returns all known genes as a string array.
   * 
   * @return A string array containing all genes.
   * @author Lovis Heindrich
   */
  public static String[] getGeneNames() {
    String[] names = new String[geneList.size()];
    for (int i = 0; i < geneList.size(); i++) {
      names[i] = geneList.get(i).getName();
    }

    Arrays.sort(names, (s1, s2) -> {
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    });
    return names;
  }


  /**
   * Returns all known genes as a string array.
   * 
   * @return A string array containing all genes with their organisms.
   * @author Lovis Heindrich
   */
  public static String[] getGeneNamesAndOrganisms() {
    String[] names = new String[geneList.size()];
    for (int i = 0; i < geneList.size(); i++) {
      names[i] = geneList.get(i).getName();
      if (geneList.get(i).getOrganism() != null && !geneList.get(i).getOrganism().equals("none")) {
        names[i] = names[i] + " (" + geneList.get(i).getOrganism() + ")";
      }
    }

    Arrays.sort(names, (s1, s2) -> {
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    });
    return names;

  }

  /**
   * Reads a gene.txt from the standard path. Stores all parsed genes in the genelist.
   * 
   * @see #readGenes(String)
   * 
   * @throws IOException Error reading file.
   * @author Lovis Heindrich
   */
  public static void readGenes() throws IOException {
    readGenes(path);
  }

  /**
   * Reads a gene.txt from a given path. Stores all parsed genes in the genelist.
   * 
   * @param genePath Path of the gene file.
   * @throws IOException Error reading file.
   * @author Lovis Heindrich
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
   * Checks if a genes.txt file exists at the given path.
   * 
   * @return True if a gene.txt exists.
   * @author Lovis Heindrich
   */
  public static boolean exists() {
    File config = new File(path);
    return config.exists();
  }

  /**
   * Creates a new gene file in the user home directory in a folder named gsat. Writes fsa as a
   * reference gene to avoid null pointers.
   * 
   * @throws IOException Error while writing the file.
   * @author Lovis Heindrich
   */
  public static void initGenes() throws IOException {
    if (!exists()) {
      File geneFile = new File(path);
      geneFile.getParentFile().mkdirs();
      geneFile.createNewFile();

      BufferedWriter geneWriter = new BufferedWriter(new FileWriter(path));
      geneWriter.write("fsa" + SEPARATOR
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

  /**
   * Gets the number of genes in the genelist.
   * 
   * @return The number of genes.
   * @author Lovis Heindrich
   */
  public static int getNumGenes() {
    return geneList.size();
  }

  // GETTERs and SETTERs:

  public static ArrayList<Gene> getGeneList() {
    return geneList;
  }

  public static void setPath(String path) {
    GeneHandler.path = path;
  }

  public static String getPath() {
    return GeneHandler.path;
  }
}
