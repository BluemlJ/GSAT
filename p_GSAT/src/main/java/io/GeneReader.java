package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import analysis.Gene;

/**
 * This class reads reference genes from a txt and stores them
 * 
 * @author lovisheindrich
 *
 */
public class GeneReader {
  private static String path;
  private static ArrayList<Gene> geneList;

  /**
   * reads a gene.txt from a given path
   * 
   * @param genePath
   * @throws IOException
   */
  public static void readGenes(String genePath) throws IOException {
    geneList = new ArrayList<Gene>();
    BufferedReader geneReader = new BufferedReader(new FileReader(genePath));
    String line;
    int id = 0;
    // for each line
    while ((line = geneReader.readLine()) != null) {
      // format "name=atgAAT..."
      String sepLine[] = line.split("=");
      String name = sepLine[0];
      String gene = sepLine[1];
      geneList.add(new Gene(gene, id, name, Config.researcher));
      id++;

    }

    geneReader.close();
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
   * set the path of the gene.txt file
   * 
   * @param path
   */
  public static void setPath(String path) {
    GeneReader.path = path;
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
  
  public static Gene getGeneAt(int index){
    return geneList.get(index);
  }
}

