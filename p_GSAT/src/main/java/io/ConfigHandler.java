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
 * This class is responsible for reading the configuration file and storing its values. The
 * configuration file is stored in home/gsat/config.txt.
 * 
 * @author Lovis Heindrich, jannis blueml
 *
 */
public class ConfigHandler {

  /**
   * The path where the config file is stored. This is set to the OS dependent home directory.
   */
  private static String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "config.txt";

  /**
   * The active researcher which will be written in analysis results. This is set to "-" as a
   * placeholder to avoid null pointer.
   */
  private static String researcher = "-";

  /**
   * This is a list of all known researchers which is used for selecting an active researcher. Set
   * to "-" to avoid null pointer.
   */
  private static String[] researcherList = {"-"};

  /**
   * The source path for .abi files to speed up opening files from the GUI.
   */
  private static String srcPath = "";

  /**
   * The server address for the database, set to the address from the TU Darmstadt database.
   */
  private static String dbUrl = "130.83.37.145";

  /**
   * The username for the database, set to the username from the TU Darmstadt database.
   */
  private static String dbUser = "gsatadmin";

  /**
   * The password for the database, "none" is a placeholder which can be replaced by setting the
   * correct password from the configure database GUI.
   */
  private static String dbPass = "none";

  /**
   * The port for the database, set to the standard port of 3306.
   */
  private static int dbPort = 3306;

  /**
   * Default values for all quality parameters, used to reset user settings.
   */
  private static int[] defaultValues = {30, 25, 9, 20, 3};

  /**
   * this parameter sets the minimal quality to start a sequence. This can be changed by the user.
   * The default value is 30.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int avgApproximationStart = defaultValues[0];

  /**
   * this parameter sets the minimal quality to end a sequence. This can be changed by the user. The
   * default value is 25.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int avgApproximationEnd = defaultValues[1];

  /**
   * This variable represents how many bad quality nucleotide are allowed before the sequence gets
   * cut off. Changing this may cause tests to fail. This can be changed by user.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int breakcounter = defaultValues[2];

  /**
   * Number of Nucleotides which will be used for the average Quality calculations. This can be
   * changed by user.
   * 
   * @see io.ConfigHandler
   * @see gui.ParameterWindow
   */
  private static int numAverageNucleotides = defaultValues[3];

  /**
   * This variable represents how many good quality nucleotides are needed before the sequence start
   * gets detected. Changing this may cause tests to fail. This can be changed by user.
   */
  private static int startcounter = defaultValues[4];

  /**
   * The date format which is used throughout the program.
   */
  private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

  /**
   * The char used for separating values in the configuration file.
   */
  public static final char SEPARATOR_CHAR = ';';

  /**
   * The char which is used to represent a file that has been manually checked.
   */
  public static final char MANUAL_CHECKED_YES = 'y';

  /**
   * The char which is used to represent a file that has not been manually checked.
   */
  public static final char MANUAL_CHECKED_NO = 'n';

  /**
   * Reads the content of the configuration file and stores its values locally.
   * 
   * @throws IOException An error during file handling occured.
   * @throws UnknownConfigFieldException The config file contains unknown fields.
   * @throws ConfigNotFoundException The config file can not be found.
   * @author Lovis Heindrich
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
   * Reads the content of the configuration file in the given path and stores its values locally.
   * 
   * @param path The folder in which the config.txt is.
   * @throws IOException An error during file handling occured.
   * @throws UnknownConfigFieldException The config file contains unknown fields.
   * @throws ConfigNotFoundException The config file can not be found.
   * @author Lovis Heindrich
   */
  public static void readConfig(String path)
      throws UnknownConfigFieldException, ConfigNotFoundException, IOException {
    setPath(path);
    readConfig();
  }

  /**
   * This method checks if a config.txt file exists at the given path.
   * 
   * @return true if a config.txt exists
   * @author Lovis Heindrich
   */
  public static boolean exists() {
    File config = new File(path);
    return config.exists();
  }

  /**
   * This method creates a new configuration file in the user home directory in a folder named gsat.
   * 
   * @throws IOException
   * @author Lovis Heindrich
   */
  public static void initConfig() throws IOException {
    if (!exists()) {
      File configFile = new File(path);
      configFile.getParentFile().mkdirs();
      configFile.createNewFile();
    }
  }

  /**
   * Checks if a researcher is in the researcher list.
   * 
   * @param researcher Name of the researcher.
   * @return True if researcher is already known
   * @author Lovis Heindrich
   */
  public static boolean containsResearcher(String researcher) {
    for (String res : researcherList) {
      if (researcher.equals(res)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Writes all local parameters in the configuration file.
   * 
   * @throws IOException IO Error while writing.
   * @author Lovis Heindrich
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

  /**
   * Sorts and returns the list of researchers.
   * 
   * @return An array which contains the sorted researchers.
   * @author Jannis Blueml
   */
  public static String[] getSortedResearcherList() {
    Arrays.sort(researcherList);
    return researcherList;
  }

  /**
   * Writes a researcher in the researcher list. May overwrite an existing researcher.
   * 
   * @param researcher The name of the researcher.
   * @param i The array index where the new researcher will be added.
   * @author Lovis Heindrich
   */
  public static void setResearcherInResearcherList(String researcher, int i) {
    ConfigHandler.researcherList[i] = researcher;
  }

  /**
   * Adds a new researcher to the list. Does not write it directly to configuration file.
   * 
   * @param name The name of the new researcher.
   * @author Lovis Heindrich
   */
  public static void addResearcher(String name) {
    String[] newResearchers = new String[researcherList.length + 1];
    for (int i = 0; i < researcherList.length; i++) {
      newResearchers[i] = researcherList[i];
    }
    newResearchers[researcherList.length] = name;
    researcherList = newResearchers;
  }

  /**
   * Deletes a researcher from the list. Does not write it directly to the configuration file.
   * 
   * @param name The name of the researcher which will be deleted.
   * @author Lovis Heindrich
   */
  public static void deleteResearcher(String name) {

    if (researcherList.length == 1) {
      return;
    }

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

  public static int[] getDefaultValues() {
    return defaultValues;
  }

  public static void setDefaultValues(int[] defaultValues) {
    ConfigHandler.defaultValues = defaultValues;
  }

  public static DateFormat getDateFormat() {
    return dateFormat;
  }

  public static void setDateFormat(DateFormat dateFormat) {
    ConfigHandler.dateFormat = dateFormat;
  }

  public static void setSrcPath(String srcPath) {
    ConfigHandler.srcPath = srcPath;
  }

  public static String getSrcPath() {
    return srcPath;
  }
}
