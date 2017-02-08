package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import analysis.AnalysedSequence;
import analysis.StringAnalysis;
import exceptions.MissingPathException;
import exceptions.PathUsage;
import exceptions.UndefinedTypeOfMutationException;

/**
 * Class to store the analysis results in local files. This class produces comma separated value
 * files (CSV) which can be read with Excel.
 * 
 * @author Ben Kohr
 */
public class FileSaver {


  /**
   * This is the file name if only one file is desired. If multiple files are desired, the name of
   * the original AB1 file will be added to this name for each resulting file.
   */
  private static String destinationFileName = "gsat_results";



  /**
   * Indicating whether this is the first call of the storage method in the current storage process.
   * This field is necessary to keep up with the labeling.
   */
  private static boolean firstCall = true;


  /**
   * This value is needed to keep track of the momentarily used id of the data. One number
   * corresponds to a single sequence. Id start with one to be human-understandable.
   */
  private static long id = 1;

  /**
   * Specifies the path where local files shall be created. This specifies the folder, not the file!
   */
  private static File localPath;


  /**
   * Indicates whether one or multiple files shall be used for storage.
   */
  private static boolean separateFiles = false;


  /**
   * This method resets the class's state by resetting the ids and setting {@link #firstCall} to
   * true. This is necessary to start a completely new analyzing process.
   */
  public static void resetAll() {
    resetIDs();
    firstCall = true;
  }



  /**
   * Sets the momentarily used id to one.
   * 
   * @author Ben Kohr
   */
  public static void resetIDs() {
    id = 1;
  }



  /**
   * Sets the path where local files shall be created. The String argument is converted into a File.
   * If null is passed, the path is reset to null.
   * 
   * @param path The path to create local files (as String)
   * 
   * @author Ben Kohr
   */
  public static void setLocalPath(String pathString) {
    if (pathString != null) {
      localPath = new File(pathString);
    } else {
      localPath = null;
    }
  }


  public static void setDestFileName(String destFileName) {
    if (destFileName.isEmpty())
      destinationFileName = "gsat_results";
    else
      destinationFileName = destFileName;
  }


  public static void setSeparateFiles(boolean pSeparateFiles) {
    separateFiles = pSeparateFiles;
  }


  /**
   * Inserts the stored data entries into one or multiple local file(s). The name of the AB1 file is
   * given as a parameter. It can be used to create the CSV file name.
   * 
   * @param ab1Filename the name of the AB1 file the stored entries were obtained from. If only one
   *        file is desired, then the name will not be used.
   * 
   * @throws MissingPathException If the path to store the data is not specified
   * @throws IOException If the writing process fails (due to the used FileWriter)
   * 
   * @author Ben Kohr
   * @throws UndefinedTypeOfMutationException 
   */
  public static void storeResultsLocally(String ab1Filename, AnalysedSequence sequence)
      throws MissingPathException, IOException, UndefinedTypeOfMutationException {

    // Without a path, writing is not possible.
    if (localPath == null) {
      throw new MissingPathException(PathUsage.WRITING);
    }

    // The writer to create a file / files.
    FileWriter writer;

    // One or multiple files?
    if (separateFiles) {
      String finalName = localPath.getAbsolutePath() + File.separatorChar + destinationFileName
          + "_" + ab1Filename + ".csv";
      writer = getNewWriterForSeparateFiles(finalName, false);
    } else {
      String finalName =
          localPath.getAbsolutePath() + File.separatorChar + destinationFileName + ".csv";
      writer = getNewWriterForOneFile(finalName);
    }

    String toWrite = constructLineToWrite(sequence);
    writer.write(toWrite);


    writer.close();

    // if several files are desired, then the id field has to be set to zero. Also, ids have to be
    // incremented.
    updateIDs();
  }


  
  private static String constructLineToWrite(AnalysedSequence sequence) throws UndefinedTypeOfMutationException {
    
    StringBuilder builder = new StringBuilder();
    
    // id
    builder.append(id).append("; ");
    
    // file name
    String fileName = sequence.getFileName();
    builder.append(fileName).append("; ");
    
    // gene
    String geneName = sequence.getReferencedGene().getName();
    builder.append(geneName).append("; ");
    
    // gene organism
    String organism = sequence.getReferencedGene().getOrganism();
    builder.append(organism).append("; ");
    
    // mutations (with nucleotide codons)
    LinkedList<String> mutations = sequence.getMutations();
    int numberOfMutations = sequence.getMutations().size();
    for (int i = 0; i < numberOfMutations; i++) {
      
      String mutation = mutations.get(i);
      builder.append(mutation);
      if (i < numberOfMutations - 1) {
        builder.append(", ");
      } else {
        builder.append("; ");
      }
    }
    
    // comments
    // As ';' is the seperator charachter, each inital semicolon is replaced
    String comments = sequence.getComments().replace(';', ',');
    builder.append(comments).append("; ");
    
    // researcher
    String researcher = sequence.getResearcher();
    builder.append(researcher).append("; ");
    
    // date
    String addingDate = sequence.getAddingDate();
    builder.append(addingDate).append("; ");
    
    // average quality
    double avgQuality = sequence.getAvgQuality();
    int avgQualityInPercent = (int) (Math.pow(10, (-avgQuality)/10.0) * 100);
    builder.append(avgQualityInPercent).append("; ");
    
    // trim percentage
    int trimPercentage = (int) (sequence.getTrimPercentage() * 100);
    builder.append(trimPercentage).append("; ");
    
    // nucleotides
    String nucleotides = sequence.getSequence();
    builder.append(nucleotides).append("; ");
    
    // left vector
    String leftVector = sequence.getLeftVector();
    builder.append(leftVector).append("; ");
    
    // right vector
    String rightVector = sequence.getRightVector();
    builder.append(rightVector).append("; ");
    
    // primer
    String primer = sequence.getPrimer();
    if (primer == null)
      builder.append(" ; ");
    else
      builder.append(primer).append("; ");
    
    // his tag
    // The his tag position starts with 1 in the stored result.
    int hisTagPosition = sequence.getHisTagPosition();
    if (hisTagPosition == -1)
      builder.append("none; ");
    else
      builder.append((hisTagPosition + 1) + "; ");
    
    
    // mutations without nucleotide codons
    for (int i = 0; i < numberOfMutations; i++) {
        String mutation = mutations.get(i);
        String reducedMutation;
        if(mutation.equals("reading frame error"))
        	reducedMutation = mutation;
        else
        	reducedMutation = (mutation.trim()).split(" ")[0];
        builder.append(reducedMutation);
        if (i < numberOfMutations - 1) {
          builder.append(", ");
        } else {
          builder.append("; ");
        }
     }
   
    
    // manually checked
    boolean manuallyChecked = sequence.isManuallyChecked();
    builder.append(manuallyChecked);
    
    
    builder.append(System.lineSeparator());

    String toWrite = builder.toString();
    
    return toWrite;
  }
  
 
  
  
  

  /**
   * This method returns and initially uses a new writer, if only one file is desired.
   *
   * @see #getNewWriterForSeparateFiles(String, boolean)
   * 
   * @return the writer object, returned to continue writing
   * 
   * @throws IOException If the creation or the usage of the FileWriter object fails
   */
  private static FileWriter getNewWriterForOneFile(String finalName) throws IOException {
    FileWriter writer;
    if (firstCall) {
      writer = getNewWriterForSeparateFiles(finalName, false);
      firstCall = false;
    } else {
      writer = getNewWriterForSeparateFiles(finalName, true);
    }
    return writer;
  }


  /**
   * This method creates a new writer for the current writing situation (one or several files?). If
   * necessary, also adds the first line. It is directly called when separate files are needed. It
   * is called via {@link #getNewWriterForOneFile()} if one file is desired.
   * 
   * @param ab1Filename The name of the new file
   * @param append Shall content be added to the file (or is it a new file)?
   * 
   * @return the writer object, returned to continue writing
   * 
   * @throws IOException If the creation or the usage of the FileWriter object fails
   */
  private static FileWriter getNewWriterForSeparateFiles(String finalName, boolean append)
      throws IOException {

    File newFile = new File(finalName);

    if (!append) {
      newFile.createNewFile();
    }

    FileWriter writer = new FileWriter(newFile, append);

    if (!append) {
      writer.write(
        "id; file name; gene; gene organism; mutations; comments; researcher; date; average quality (percent); percentage of quality trim; nucleotide sequence; left vector; right vector; primer; HIS tag; manually checked"
              + System.lineSeparator());
    }

    return writer;
  }



  /**
   * Sets the momentarily used id to one, if separate files are desired (each file has it's own
   * number range, starting with zero).
   * 
   * @see #resetIDs()
   * 
   * @author Ben Kohr
   */
  private static void updateIDs() {
    if (separateFiles) {
      resetIDs();
    } else {
      id++;
    }
  }


}
