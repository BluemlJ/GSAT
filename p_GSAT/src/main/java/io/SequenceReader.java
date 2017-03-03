package io;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.jcvi.jillion.trace.chromat.ChromatogramFactory;
import org.jcvi.jillion.trace.chromat.abi.AbiChromatogram;

import analysis.AnalysedSequence;
import analysis.Pair;
import exceptions.FileReadingException;



/**
 * This class reads files of the AB1 format and extracts the information into a sequence.
 * 
 * @author Ben Kohr, bluemlj, Lovis Heindrich
 *
 */
public class SequenceReader {

  /**
   * Constructed list of file names in a given folder (in order to not analyze a file twice).
   */
  private static LinkedList<String> files = new LinkedList<String>();

  /**
   * Path the the source of information (folder/file).
   */
  private static String path;

  /**
   * Sets the path to the folder or the file to be used. In case of a folder, also gathers the file
   * names into the list to be able to check that all files are analyzed, and only once.
   * 
   * @param path The given path to the data
   * 
   */
  public static void configurePath(String path) {
    SequenceReader.path = path;
  }

  /**
   * Parses one AB1 file (the only one or the next one in the list) into a sequence. If possible,
   * deletes the first entry of the list. Note: There's no method to read in several files at once,
   * because the files is analyzed one by one.
   * 
   * @throws IOException
   * @throws IllegalSymbolException
   * 
   * @author Lovis Heindrich
   */
  public static AnalysedSequence convertFileIntoSequence()
      throws FileReadingException, IOException {
    return convertFileIntoSequence(new File(path));
  }

  /**
   * Overload taking an input file Parses one AB1 file (the only one or the next one in the list)
   * into a sequence. If possible, deletes the first entry of the list. Note: There's no method to
   * read in several files at once, because the files is analyzed one by one.
   * 
   * @throws IOException
   * @throws IllegalSymbolException
   * 
   * @author Lovis Heindrich
   */
  public static AnalysedSequence convertFileIntoSequence(File file)
      throws FileReadingException, IOException {

    File referencedFile = file;


    AbiChromatogram abifile = (AbiChromatogram) ChromatogramFactory.create(referencedFile);
    String sequence = abifile.getNucleotideSequence().toString();
    byte[] qualities = abifile.getQualitySequence().toArray();

    // TODO Add Primer

    // convert qualities from byte[] to int[]
    int[] qualitiesInt = new int[qualities.length];
    for (int i = 0; i < qualities.length; i++) {
      qualitiesInt[i] = qualities[i];
    }

    AnalysedSequence parsedSequence = new AnalysedSequence(sequence, ConfigHandler.getResearcher(),
        referencedFile.getName(), qualitiesInt);
    parsedSequence.setAbiFile(abifile);
    return parsedSequence;
  }

  public static String getPath() {
    return path;
  }

  /**
   * Indicates whether there is a path set at the moment.
   * 
   * @return Whether a path is set or not
   * 
   * @author Ben Kohr
   */
  public static boolean isPathSet() {
    return path != null;
  }

  /**
   * Returns a list of all AB1 files in the path that was set via configurePath()
   * 
   * @return
   * @author Kevin
   */
  public static Pair<LinkedList<File>, LinkedList<File>> listFiles() {

    // get list of all files and Pathes in given path

    File folder = new File(path);
    File[] allFiles = folder.listFiles();

    if (path.endsWith(".ab1")) {
      allFiles = new File[1];
      allFiles[0] = folder;
    }

    LinkedList<File> ab1Files = new LinkedList<File>();
    LinkedList<File> oddFiles = new LinkedList<File>();

    int lastId;
    if (allFiles != null) {
      lastId = allFiles.length - 1;
    } else {
      lastId = 0;
    }

    // for every files or path
    if (allFiles != null && allFiles.length > 0) {
      for (int fileId = 0; fileId <= lastId; fileId++) {
        File activeFile = allFiles[fileId];
        String fileName = activeFile.getName();
        String fileEnding = fileName.split("\\.")[fileName.split("\\.").length - 1];
        // if it is a File and the fileending is abi or ab1 add file
        if (activeFile.isFile()
            && (fileEnding.toLowerCase().equals("ab1") || fileEnding.toLowerCase().equals("abi"))) {
          ab1Files.add(activeFile);
        } else if (!"config.ini".equals(activeFile.getName())) {
          oddFiles.add(activeFile);
        }
      }
    }
    return new Pair<LinkedList<File>, LinkedList<File>>(ab1Files, oddFiles);
  }



  /**
   * Discards the current path and files.
   * 
   * @author Ben Kohr
   */
  public static void resetInputData() {
    path = null;
    files.clear();
  }
}
