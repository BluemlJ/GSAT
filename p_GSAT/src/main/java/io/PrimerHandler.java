package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import analysis.Primer;
import gui.GUIUtils;
import javafx.scene.control.Alert.AlertType;

/**
 * This class is responsible for storing primer data. Primers get synchronised to a csv file which
 * is stored in the user directory.
 * 
 * @author Lovis Heindrich
 *
 */
public class PrimerHandler {

  /**
   * Arraylist containing all primers.
   */
  private static ArrayList<Primer> primerList;

  /**
   * Path to where the csv file is stored.
   */
  private static String path =
      System.getProperty("user.home") + File.separator + "gsat" + File.separator + "primer.txt";

  /**
   * Separator character.
   */
  private static final String SEPARATOR = ConfigHandler.SEPARATOR_CHAR + "";

  /**
   * Reads all data stored in the primer csv file.
   * 
   * @param primerPath The path to the csv file.
   * @throws NumberFormatException Error with the number format.
   * @throws IOException Error while reading file.
   * @author Lovis Heindrich
   */
  public static void readPrimer(String primerPath) throws NumberFormatException, IOException {

    // open primer.txt or initialize it
    path = primerPath;
    initPrimer();

    // read all primer from the file
    primerList = new ArrayList<Primer>();
    BufferedReader primerReader = new BufferedReader(new FileReader(primerPath));
    String line;
    // for each line
    while ((line = primerReader.readLine()) != null) {
      // Format: name;sequence;researcher;meltingpoint;id
      String[] sepLine = line.split(SEPARATOR);
      String name = sepLine[0];
      String sequence = sepLine[1];
      String researcher = sepLine[2];
      int meltingPoint = Integer.parseInt(sepLine[3]);
      String id = sepLine[4];
      String comment = sepLine[5];

      primerList.add(new Primer(sequence, researcher, meltingPoint, id, name, comment));
    }

    primerReader.close();

  }

  /**
   * Reads all data stored in the primer csv file.
   * 
   * @throws NumberFormatException Error with the number format.
   * @throws IOException Error while reading file.
   * @author Lovis Heindrich
   */
  public static void readPrimer() throws NumberFormatException, IOException {
    initPrimer();
    readPrimer(path);
  }

  /**
   * Initializes the primer.txt file. Checks if the file exists and creates it otherwise.
   * 
   * @throws IOException Error while reading file.
   * @author Lovis Heindrich
   */
  public static void initPrimer() throws IOException {
    if (!exists()) {
      File primerFile = new File(path);
      primerFile.getParentFile().mkdirs();
      primerFile.createNewFile();

      BufferedWriter geneWriter = new BufferedWriter(new FileWriter(path));
      geneWriter.write("DummyPrimer" + SEPARATOR + "ATG" + SEPARATOR + "none" + SEPARATOR + "0"
          + SEPARATOR + "none" + SEPARATOR + "none");
      geneWriter.close();

    }
  }

  /**
   * Gets all primers as a single string.
   * 
   * @return A string containing all primer names and ids.
   * @author Lovis Heindrich
   */
  public static String[] getPrimerListWithIdAsString() {
    String[] ret = new String[primerList.size()];
    for (int i = 0; i < primerList.size(); i++) {
      ret[i] = primerList.get(i).getName() + " (" + primerList.get(i).getId() + ")";
    }

    Arrays.sort(ret, (s1, s2) -> {
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    });

    return ret;
  }

  /**
   * Deletes a primer from the primerlist and updates the txt file.
   * 
   * @param newpath Path of the txt file.
   * @param name Name of the primer to be deleted.
   * @throws IOException Error while updating the txt file.
   * @author Lovis Heindrich
   */
  public static void deletePrimer(String newpath, String name) throws IOException {

    for (int i = 0; i < primerList.size(); i++) {
      if (primerList.get(i).getName().equals(name)) {
        primerList.remove(i);
      }
    }
    writePrimer(newpath);
  }

  /**
   * Deletes a primer from the primerlist.
   * 
   * @param name Name of the primer to be deleted.
   * @throws IOException Error while updating the txt file.
   * @author Jannis Blueml
   */
  public static void deletePrimer(String name) throws IOException {
    if (!name.split(" ")[1].isEmpty()) {
      deletePrimer(path, name.split(" ")[0],
          name.split(" ")[1].substring(1, name.split(" ")[1].length() - 1));
    }
  }

  /**
   * Deletes a primer from the primerlist and updates the txt file.
   * 
   * @param newpath Path of the txt file.
   * @param name Name of the primer to be deleted.
   * @param id Id of the primer.
   * @throws IOException Error while updating the txt file.
   * @author Lovis Heindrich
   */
  public static void deletePrimer(String newpath, String name, String id) throws IOException {

    for (int i = 0; i < primerList.size(); i++) {
      if (primerList.get(i).getName().equals(name) && primerList.get(i).getId().equals(id)) {
        primerList.remove(i);
      }
    }
    writePrimer(newpath);
  }

  /**
   * Writes all primers in the primerlist to the local txt file.
   * 
   * @param primerPath Path to the primer file.
   * @throws IOException Error while writing the local file.
   * @author Lovis Heindrich
   */
  public static void writePrimer(String primerPath) throws IOException {
    // clears all primers from file
    BufferedWriter primerWriter = new BufferedWriter(new FileWriter(primerPath));
    // write all previously known primers
    for (Primer primer : primerList) {
      // Format: name;sequence;researcher;meltingpoint;id
      StringBuilder primerString = new StringBuilder();
      primerString.append(primer.getName());
      primerString.append(SEPARATOR);
      if (primer.getSequence() == null || primer.getSequence().equals("")) {
        primerString.append("none");
      } else {
        primerString.append(primer.getSequence());
      }
      primerString.append(SEPARATOR);
      if (primer.getResearcher() == null || primer.getResearcher().equals("")) {
        primerString.append("none");
      } else {
        primerString.append(primer.getResearcher());
      }
      primerString.append(SEPARATOR);
      primerString.append(primer.getMeltingPoint());
      primerString.append(SEPARATOR);
      if (primer.getId() == null || primer.getId().equals("")) {
        primerString.append("none");
      } else {
        primerString.append(primer.getId());
      }
      primerString.append(SEPARATOR);
      if (primer.getComment() == null || primer.getComment().equals("")) {
        primerString.append("none");
      } else {
        primerString.append(primer.getComment());
      }
      primerString.append(System.getProperty("line.separator"));
      primerWriter.write(primerString.toString());
    }

    primerWriter.close();
  }

  /**
   * Writes all primers in the primerlist to the local txt file.
   * 
   * @throws IOException Error while writing the local file.
   * @author Lovis Heindrich
   */
  public static void writePrimer() throws IOException {
    writePrimer(path);
  }

  /**
   * Checks if the primer txt file exists.
   * 
   * @return Boolean which indicates if the file exists.
   * @author Lovis Heindrich
   */
  private static boolean exists() {
    File config = new File(path);
    return config.exists();
  }

  /**
   * Adds a primer to the list if it does not exist yet. Writes all primers to the txt file
   * afterwards.
   * 
   * @param primer The primer to be added.
   * @author Lovis Heindrich
   */
  public static boolean addPrimer(Primer primer) {
    // only add if primer doesn't exist yet
    try {
      readPrimer();
    } catch (NumberFormatException | IOException e) {
      GUIUtils.showInfo(AlertType.ERROR, "Primer reading error", "Error while reading primers occurred.");
    }

    if (getPrimer(primer.getName(), primer.getId()) == null) {
      primerList.add(primer);

      try {
        writePrimer();
      } catch (IOException e) {
        GUIUtils.showInfo(AlertType.ERROR, "Primer writing error", "Error while writing primers occurred.");
      }

      return true;
    }
    return false;
  }

  /**
   * Returns a primer identified by name and id from the primerList.
   * 
   * @param name Name of the primer.
   * @param primerId If of the primer.
   * @return The primer identified by name and primerId or null.
   * @author Lovis Heindrich
   */
  public static Primer getPrimer(String name, String primerId) {
    for (Primer primer : primerList) {
      if (primer.getName().equals(name) && primer.getId().equals(primerId)) {
        return primer;
      }
    }
    return null;
  }

  /**
   * Returns a primer identified by name from the primerList.
   * 
   * @param name The name of the primer.
   * @return The primer identified by name or null.
   * @author jannis blueml
   */
  public static Primer getPrimer(String name) {
    if (name.split(" ")[1].isEmpty()) {
      for (Primer primer : primerList) {
        if (primer.getName().equals(name)) {
          return primer;
        }
      }
    } else {
      return getPrimer(name.split(" ")[0],
          name.split(" ")[1].substring(1, name.split(" ")[1].length() - 1));
    }
    return null;

  }

  /**
   * Clears the txt file at a given path.
   * 
   * @throws IOException Error while writing file.
   * @author Lovis Heindrich
   */
  public static void clearTxtFile() throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    writer.write("");
    writer.close();
  }

  /**
   * Gets all primer names from the primerlist.
   * 
   * @return A string containing all primer names.
   * @author Lovis Heindrich
   */
  public static String[] getPrimerListAsString() {
    String[] ret = new String[primerList.size()];
    for (int i = 0; i < primerList.size(); i++) {
      ret[i] = primerList.get(i).getName();
    }

    return ret;
  }

  // Getter and Setter

  public static ArrayList<Primer> getPrimerList() {
    return primerList;
  }

  public static void setPrimerList(ArrayList<Primer> primerList) {
    PrimerHandler.primerList = primerList;
  }

  public static String getPath() {
    return path;
  }

  public static void setPath(String path) {
    PrimerHandler.path = path;
  }
}
