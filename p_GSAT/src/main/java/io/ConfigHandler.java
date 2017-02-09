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
public class ConfigHandler {
  private static String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "config.txt";
  private static String researcher = "-";
  private static String[] researchers = {"-"};

  // DB connection values
  private static String dbUrl = "130.83.37.145";
  private static String dbUser = "gsatadmin";
  private static String dbPass  = "none";
  private static int dbPort = 3306;

  // quality parameter
  private static int avgApproximationStart = 30;
  private static int avgApproximationEnd = 25;
  private static int avgQualityEdge = 30;
  private static int breakcounter = 9;
  private static int numAverageNucleotides = 20;
  private static int startcounter = 3;

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
      configReader = new BufferedReader(new FileReader(ConfigHandler.path));
    } catch (FileNotFoundException e) {
      throw new ConfigNotFoundException(path);
    }
    String line = null;
    while ((line = configReader.readLine()) != null) {
      String[] elements = line.split(SEPARATOR);
      switch (elements[0]) {
        case "researcher":
          ConfigHandler.researcher = elements[1].trim();
          break;
        case "researchers":
          ConfigHandler.researchers = Arrays.copyOfRange(elements, 1, elements.length);
          for (int i = 0; i < ConfigHandler.researchers.length; i++) {
            ConfigHandler.researchers[i] = ConfigHandler.researchers[i].trim();
          }
          break;
          
        // DB Login
        case "dbUser":
          ConfigHandler.setDbUser(elements[1].trim());
          break;
        case "dbPass":
          ConfigHandler.setDbPass(elements[1].trim());
          break;
        case "dbUrl":
          ConfigHandler.setDbUrl(elements[1].trim());
          break;
        case "dbPort":
          ConfigHandler.setDbPort(Integer.parseInt(elements[1].trim()));
          break;
          
        //Quality analysis parameters  
        case "avgApproximationStart":
        	ConfigHandler.setAvgApproximationStart(Integer.parseInt(elements[1].trim()));
        	break;
        case "avgApproximationEnd":
        	ConfigHandler.setAvgApproximationEnd(Integer.parseInt(elements[1].trim()));
        	break;
        case "avgQualityEdge":
        	ConfigHandler.setAvgQualityEdge(Integer.parseInt(elements[1].trim()));
        	break;

        case "breakcounter":
        	ConfigHandler.setBreakcounter(Integer.parseInt(elements[1].trim()));
        	break;
        case "numAverageNucleotides":
        	ConfigHandler.setNumAverageNucleotides(Integer.parseInt(elements[1].trim()));
        	break;
        case "startcounter":
        	ConfigHandler.setStartcounter(Integer.parseInt(elements[1].trim()));
        	break;
        default:
          throw new ConfigReadException(elements[0]);

      }
    }

    configReader.close();

    // Arrays.sort(researchers);
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
    ConfigHandler.path = path;
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
    configWriter.write(System.getProperty("line.separator"));
    
    //write DB data
    
    configWriter.write("dbUrl" + SEPARATOR + dbUrl);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("dbUser" + SEPARATOR + dbUser);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("dbPass" + SEPARATOR + dbPass);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("dbPort" + SEPARATOR + dbPort);
    configWriter.write(System.getProperty("line.separator"));

    //write quality parameter

    configWriter.write("avgApproximationStart" + SEPARATOR + avgApproximationStart);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("avgApproximationEnd" + SEPARATOR + avgApproximationEnd);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("avgQualityEdge" + SEPARATOR + avgQualityEdge);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("breakcounter" + SEPARATOR + breakcounter);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("numAverageNucleotides" + SEPARATOR + numAverageNucleotides);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("startcounter" + SEPARATOR + startcounter);
    configWriter.write(System.getProperty("line.separator"));
    
    configWriter.close();
  }

  public static String[] getSortedResearchers() {
    Arrays.sort(researchers);
    return researchers;
  }

  public static String[] getResearchers() {
    return researchers;
  }

  public static void setResearchers(String[] researchers) {
    ConfigHandler.researchers = researchers;
  }

  public static void setResearchers(String researcher, int i) {
    ConfigHandler.researchers[i] = researcher;
  }

  public static String getResearcher() {
    return researcher;
  }

  public static void setResearcher(String researcher) {
    ConfigHandler.researcher = researcher;
  }

  /**
   * adds a new researcher to the list does not write it directly to configuration file
   * 
   * @param name name of the new researcher
   */
  public static void addResearcher(String name) {
    String newResearchers[] = new String[researchers.length + 1];
    for (int i = 0; i < researchers.length; i++) {
      newResearchers[i] = researchers[i];
    }
    newResearchers[researchers.length] = name;
    researchers = newResearchers;

    // sort researchers
    // Arrays.sort(researchers);
  }

  /**
   * deletes a researcher from the list does not write it directly to configuration file
   * 
   * @param name name of the researcher which will be deleted
   */
  public static void deleteResearcher(String name) {
    String newResearchers[] = new String[researchers.length - 1];
    int j = 0;
    for (int i = 0; i < researchers.length; i++) {
      if (!researchers[i].equals(name)) {
        newResearchers[j] = researchers[i];
        j++;
      }
    }
    researcher = "";
    researchers = newResearchers;
  }

  // TODO @Lovis
  public static String[] getParameters() {
    return null;
  }

  public static String getDbUrl() {
    return dbUrl;
  }

  public static void setDbUrl(String dbUrl) {
    ConfigHandler.dbUrl = dbUrl;
  }

  public static String getDbUser() {
    return dbUser;
  }

  public static void setDbUser(String dbUser) {
    ConfigHandler.dbUser = dbUser;
  }

  public static String getDbPass() {
    return dbPass;
  }

  public static void setDbPass(String dbPass) {
    ConfigHandler.dbPass = dbPass;
  }

  public static int getDbPort() {
    return dbPort;
  }

  public static void setDbPort(int dbPort) {
    ConfigHandler.dbPort = dbPort;
  }

public static int getAvgApproximationStart() {
	return avgApproximationStart;
}

public static void setAvgApproximationStart(int avgApproximationStart) {
	ConfigHandler.avgApproximationStart = avgApproximationStart;
}

public static int getAvgApproximationEnd() {
	return avgApproximationEnd;
}

public static void setAvgApproximationEnd(int avgApproximationEnd) {
	ConfigHandler.avgApproximationEnd = avgApproximationEnd;
}

public static int getAvgQualityEdge() {
	return avgQualityEdge;
}

public static void setAvgQualityEdge(int avgQualityEdge) {
	ConfigHandler.avgQualityEdge = avgQualityEdge;
}

public static int getBreakcounter() {
	return breakcounter;
}

public static void setBreakcounter(int breakcounter) {
	ConfigHandler.breakcounter = breakcounter;
}

public static int getNumAverageNucleotides() {
	return numAverageNucleotides;
}

public static void setNumAverageNucleotides(int numAverageNucleotides) {
	ConfigHandler.numAverageNucleotides = numAverageNucleotides;
}

public static int getStartcounter() {
	return startcounter;
}

public static void setStartcounter(int startcounter) {
	ConfigHandler.startcounter = startcounter;
}

}
