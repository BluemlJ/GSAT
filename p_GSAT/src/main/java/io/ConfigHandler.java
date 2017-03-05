package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import exceptions.ConfigNotFoundException;
import exceptions.UnknownConfigFieldException;


/**
 * This class is responsible for reading the configuration file and storing its values
 * 
 * @author lovisheindrich, jannis blueml
 *
 */
public class ConfigHandler {
  private static String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "config.txt";
  private static String researcher = "-";
  private static String[] researcherList = {"-"};
  private static String srcPath = "";

  // DB connection values
  private static String dbUrl = "130.83.37.145";
  private static String dbUser = "gsatadmin";
  private static String dbPass = "none";
  private static int dbPort = 3306;

  // quality parameter
  private static int[] defaultValues = {30, 25, 9, 20, 3};
  private static int avgApproximationStart = defaultValues[0];
  private static int avgApproximationEnd = defaultValues[1];
  private static int breakcounter = defaultValues[2];
  private static int numAverageNucleotides = defaultValues[3];
  private static int startcounter = defaultValues[4];

  // date format
  private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

  /**
   * char used for separating values in the configuration file
   */
  public static final char SEPARATOR_CHAR = ';';
  public static final char MANUAL_CHECKED_YES = 'y';
  public static final char MANUALCHECKED_NO = 'n';

  /**
   * read the content of the configuration file and store its values locally
   * 
   * @throws IOException
   * @throws UnknownConfigFieldException
   * @throws ConfigNotFoundException
   */
  public static void readConfig()
      throws UnknownConfigFieldException, ConfigNotFoundException, IOException {
    initConfig();
    BufferedReader configReader;
    try {
      configReader = new BufferedReader(new FileReader(ConfigHandler.path));
    } catch (FileNotFoundException e) {
      throw new ConfigNotFoundException(path);
    }
    String line = null;
    while ((line = configReader.readLine()) != null) {
      String[] elements = line.split(SEPARATOR_CHAR + "");

      String key = elements[0];
      String value = elements[1].trim();

      switch (key) {
        case "researcher":
          ConfigHandler.researcher = elements[1].trim();
          break;
        case "researchers":
          ConfigHandler.researcherList = Arrays.copyOfRange(elements, 1, elements.length);
          for (int i = 0; i < ConfigHandler.researcherList.length; i++) {
            ConfigHandler.researcherList[i] = ConfigHandler.researcherList[i].trim();
          }
          break;
        case "srcPath":
          ConfigHandler.srcPath = elements[1].trim();
          break;


        // DB Login
        case "dbUser":
          ConfigHandler.setDbUser(value);
          break;
        case "dbPass":
          ConfigHandler.setDbPass(value);
          break;
        case "dbUrl":
          ConfigHandler.setDbUrl(value);
          break;
        case "dbPort":
          ConfigHandler.setDbPort(Integer.parseInt(value));
          break;

        // Quality analysis parameters
        case "avgApproximationStart":
          ConfigHandler.setAvgApproximationStart(Integer.parseInt(value));
          break;
        case "avgApproximationEnd":
          ConfigHandler.setAvgApproximationEnd(Integer.parseInt(value));
          break;
        case "breakcounter":
          ConfigHandler.setBreakcounter(Integer.parseInt(value));
          break;
        case "numAverageNucleotides":
          ConfigHandler.setNumAverageNucleotides(Integer.parseInt(value));
          break;
        case "startcounter":
          ConfigHandler.setStartcounter(Integer.parseInt(value));
          break;
        default:
          throw new UnknownConfigFieldException(key);

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
   * @throws UnknownConfigFieldException
   * @throws ConfigNotFoundException
   */
  public static void readConfig(String path)
      throws UnknownConfigFieldException, ConfigNotFoundException, IOException {
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
   * writes all parameters in the configuration file
   * 
   * @throws IOException
   */
  public static void writeConfig() throws IOException {
    BufferedWriter configWriter = new BufferedWriter(new FileWriter(path));

    // write researcher
    configWriter.write("researcher" + SEPARATOR_CHAR + researcher);
    configWriter.write(System.getProperty("line.separator"));

    // write srcPath
    if (srcPath.isEmpty()) {
      srcPath = " ";
    }
    configWriter.write("srcPath" + SEPARATOR_CHAR + srcPath);
    configWriter.write(System.getProperty("line.separator"));

    // write researchers
    configWriter.write("researchers");
    for (String res : researcherList) {
      configWriter.write(SEPARATOR_CHAR);
      configWriter.write(res);
    }
    configWriter.write(System.getProperty("line.separator"));

    // write DB data

    configWriter.write("dbUrl" + SEPARATOR_CHAR + dbUrl);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("dbUser" + SEPARATOR_CHAR + dbUser);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("dbPass" + SEPARATOR_CHAR + dbPass);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("dbPort" + SEPARATOR_CHAR + dbPort);
    configWriter.write(System.getProperty("line.separator"));

    // write quality parameter

    configWriter.write("avgApproximationStart" + SEPARATOR_CHAR + avgApproximationStart);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("avgApproximationEnd" + SEPARATOR_CHAR + avgApproximationEnd);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("breakcounter" + SEPARATOR_CHAR + breakcounter);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("numAverageNucleotides" + SEPARATOR_CHAR + numAverageNucleotides);
    configWriter.write(System.getProperty("line.separator"));
    configWriter.write("startcounter" + SEPARATOR_CHAR + startcounter);
    configWriter.write(System.getProperty("line.separator"));

    configWriter.close();
  }

  public static String getSrcPath() {
    return srcPath;
  }

  public static void setSrcPath(String srcPath) {
    ConfigHandler.srcPath = srcPath;
  }

  public static String[] getSortedResearcherList() {
    Arrays.sort(researcherList);
    return researcherList;
  }


  public static void setResearcherInResearcherList(String researcher, int i) {
    ConfigHandler.researcherList[i] = researcher;
  }

  /**
   * adds a new researcher to the list does not write it directly to configuration file
   * 
   * @param name name of the new researcher
   */
  public static void addResearcher(String name) {
    String[] newResearchers = new String[researcherList.length + 1];
    for (int i = 0; i < researcherList.length; i++) {
      newResearchers[i] = researcherList[i];
    }
    newResearchers[researcherList.length] = name;
    researcherList = newResearchers;

    // sort researchers
    // Arrays.sort(researchers);
  }

  /**
   * deletes a researcher from the list does not write it directly to configuration file
   * 
   * @param name name of the researcher which will be deleted
   */
  public static void deleteResearcher(String name) {
    String[] newResearchers = new String[researcherList.length - 1];
    int j = 0;
    for (int i = 0; i < researcherList.length; i++) {
      if (!researcherList[i].equals(name)) {
        newResearchers[j] = researcherList[i];
        j++;
      }
    }
    researcher = "";
    researcherList = newResearchers;
  }

  // TODO @Lovis
  public static String[] getParameters() {
    return null;
  }



  // GETTERs and SETTERs:

  public static String getPath() {
    return path;
  }


  public static void setPath(String path) {
    ConfigHandler.path = path;
  }


  public static String[] getResearcherList() {
    return researcherList;
  }

  public static void setResearcherList(String[] researchers) {
    ConfigHandler.researcherList = researchers;
  }


  public static String getResearcher() {
    return researcher;
  }

  public static void setResearcher(String researcher) {
    ConfigHandler.researcher = researcher;
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

  /**
   * @return the defaultValues
   */
  public static int[] getDefaultValues() {
    return defaultValues;
  }

  /**
   * @param defaultValues the defaultValues to set
   */
  public static void setDefaultValues(int[] defaultValues) {
    ConfigHandler.defaultValues = defaultValues;
  }

  public static DateFormat getDateFormat() {
    return dateFormat;
  }

  public static void setDateFormat(DateFormat dateFormat) {
    ConfigHandler.dateFormat = dateFormat;
  }

}
