package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GeneReader {
  private static String path;

  
  public static void readGenes(String path) throws IOException{
    
    BufferedReader geneReader = new BufferedReader(new FileReader(path));
    String line;
    while((line = geneReader.readLine()) != null){
      String sepLine[] = line.split("=");
      System.out.println(sepLine[0]);
      System.out.println(sepLine[1]);
    }
      
    geneReader.close();
  }
}
