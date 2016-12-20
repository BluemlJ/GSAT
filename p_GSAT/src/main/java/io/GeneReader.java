package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class reads reference genes from a txt and stores them
 * @author lovisheindrich
 *
 */
public class GeneReader {
  private static String path;
  private static HashMap<String, String> geneList;
  
  /**
   * reads a gene.txt from a given path
   * @param genePath
   * @throws IOException
   */
  public static void readGenes(String genePath) throws IOException {
    geneList = new HashMap<String, String>();
    BufferedReader geneReader = new BufferedReader(new FileReader(genePath));
    String line;
    // for each line
    while ((line = geneReader.readLine()) != null) {
      // format "name=atgAAT..."
      String sepLine[] = line.split("=");
      String name = sepLine[0];
      String gene = sepLine[1];
      geneList.put(name, gene);
      
    }

    geneReader.close();
  }
  
  /**
   * reads genes from gene file at the locally stored path
   * @throws IOException
   */
  public static void readGenes() throws IOException {
    readGenes(path);
  }
  
  /**
   * set the path of the gene.txt file
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
  public static String getGene(String geneName){
    return geneList.get(geneName);
  }
  
  public static HashMap getGeneList(){
    return geneList;
  }

}

