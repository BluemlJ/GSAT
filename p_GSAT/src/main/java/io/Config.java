package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;

/**
 * This class is responsible for reading the configuration file and storing its values
 * 
 * @author lovisheindrich
 *
 */
public class Config {
  public static String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "config.txt";
  public static String researcher;
  private static String[] researchers;



  /**
   * char used for separating values in the configuration file
   */
  private final static String SEPARATOR = ";";



  /**
   * read the content of the configuration file and store its values locally
   * 
   * @throws IOException
   * @throws ConfigReadException
   * @throws ConfigNotFoundException
   */
  public static void readConfig() throws ConfigReadException, ConfigNotFoundException, IOException {
    initConfig();
    BufferedReader configReader;
    try {
      configReader = new BufferedReader(new FileReader(Config.path));
    } catch (FileNotFoundException e) {
      throw new ConfigNotFoundException(path);
    }
    String line = null;
    while ((line = configReader.readLine()) != null) {
      String[] elements = line.split(SEPARATOR);
      switch (elements[0]) {
        case "researcher":
          Config.researcher = elements[1].trim();
          break;
        case "researchers":
          Config.researchers = Arrays.copyOfRange(elements, 1, elements.length);
          for (int i = 0; i < Config.researchers.length; i++) {
            Config.researchers[i] = Config.researchers[i].trim();
          }
          break;
        default:
          break;
      }
    }
    /*
     * String researcherLine = configReader.readLine(); if (researcherLine == null ||
     * researcherLine.length() < 12 || !researcherLine.substring(0, 11).equals("researcher:")) {
     * configReader.close(); throw new ConfigReadException("researcher"); } Config.researcher =
     * researcherLine.substring(11).trim();
     */
    configReader.close();
  }

  /**
   * read the content of the configuration file in the given path and store its values locally
   * 
   * @param path the folder in which the config.ini is
   * @throws IOException
   * @throws ConfigReadException
   * @throws ConfigNotFoundException
   */
  public static void readConfig(String path)
      throws ConfigReadException, ConfigNotFoundException, IOException {
    setPath(path);
    readConfig();
  }


  /**
   * check if a config.ini file exists at the given path
   * 
   * @return true if a config.ini exists
   */
  public static boolean exists() {
    File config = new File(path);
    return config.exists();
  }

  /**
   * create a new configuration file in the user home directory in a folder named gsat
   * 
   * @throws IOException
   * 
   */
  public static void initConfig() throws IOException {
    if (!exists()) {
      File configFile = new File(path);
      configFile.getParentFile().mkdirs();
      configFile.createNewFile();
    }
  }

  /**
   * sets the path of the configuration file
   * 
   * @param path
   */
  public static void setPath(String path) {
    Config.path = path;
  }

  public static String getPath() {
    return path;
  }

  /**
   * writes all parameters in the configuration file
   * 
   * @throws IOException
   */
  public static void writeConfig() throws IOException {
    BufferedWriter configWriter = new BufferedWriter(new FileWriter(path));

    // write researcher
    configWriter.write("researcher" + SEPARATOR + researcher);

    configWriter.write(System.getProperty("line.separator"));

    // write researchers
    configWriter.write("researchers");
    for (String res : researchers) {
      configWriter.write(SEPARATOR);
      configWriter.write(res);
    }

    configWriter.close();
  }

  public static String[] getResearchers() {
    return researchers;
  }

  public static void setResearchers(String[] researchers) {
    Config.researchers = researchers;
  }

  public static void setResearchers(String researcher, int i) {
    Config.researchers[i] = researcher;
  }
  
  public static String getResearcher() {
    return researcher;
  }
  
  public static void setResearcher(String researcher) {
    Config.researcher = researcher;
  }

}
