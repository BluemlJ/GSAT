package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
import io.Config;
import io.ConsoleIO;
import io.DatabaseConnection;
import io.DatabaseEntry;
import io.SequenceReader;

/**
 * This class coordinates the overall behavior of the program. It moderates the
 * analyzing pipeline.
 *
 * @author Ben Kohr
 */
public class Main {

	static String readingPath = null;
	
	/**
	 * Start of the GSAT program.
	 * 
	 * @param args
	 *            Unused input parameters
	 */
	public static void main(String[] args) {
	  boolean console = false;
	  //DEBUG: TODO remove
	  console = true;
	  //DEBUG END
	  for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().equals("c")) {
				console = true; 
				break;
			}
		}
	  
	  if (console) 
	    startConsoleVersion();
	  
	}

	private static void startConsoleVersion() {
		ConsoleIO.clearConsole();
		//ask User for Filepath
		Object[] okayAndOddFiles = askForAB1Files();
		
		String configReport = getConfig();
		
		LinkedList<File> files = (LinkedList<File>) okayAndOddFiles[0];
		
		String destinationPath = askForDestinationPath("Please give destination path for output File");
		
		reportOnInput(destinationPath, (LinkedList<File>) okayAndOddFiles[0], 
				(LinkedList<File>) okayAndOddFiles[1], configReport);
		
		DatabaseConnection.setLocalPath(destinationPath);
		//TODO Aks for GEN
		String genPath = askForDestinationPath("Please give path to gene");
		
		//TODO Read GENE
		Gene gene = null;
		for (File file : files) {
			AnalysedSequence activeSequence = null;
			
			//Read Sequence From File
			readSequenceFromFile(file);
			
			//cut out low Quality parts of sequence
			QualityAnalysis.trimLowQuality(activeSequence);
			
			//cut out Vector
			StringAnalysis.trimVector(activeSequence, gene);
			
			try {
				MutationAnalysis.findMutations(activeSequence);
			} catch (UndefinedTypeOfMutationException e) {
				System.err.println("Unknown mutation type found.");
				System.err.println("Mutation:");
				System.err.println(e.mutationString);
				System.out.println();
			}
			
			//Ask for Comment
			try {
				activeSequence.setComments(ConsoleIO.readLine("Please type comment text for File " + file.getName()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//TODO:
			//read researcher from config
			
			try {
				LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(activeSequence);
				DatabaseConnection.addAllIntoQueue(entries);
				DatabaseConnection.storeAllLocally(file.getName() +"_result");
				resetPipeline();
			} catch (UndefinedTypeOfMutationException e) {
				System.err.println("Unknowen Mutation Type Found.");
				System.err.println("Mutation:");
				System.err.println(e.mutationString);
				System.out.println();
			} catch (MissingPathException e) {
				DatabaseConnection.setLocalPath(askForDestinationPath("Please give destination path for output File"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Program end");
	}
	
	/**
	 * Creates, prints and stores a report of the reading of the files.
	 * 
	 * @param destination The destination path (where to store the results)
	 * @param validFiles The list of valid AB1/ABI files
	 * @param badFiles The list of invalid files
	 * @param configReport A message on the level of success from reading the configuration file
	 * 
	 * @author Ben Kohr
	 * 
	 */
	public static void reportOnInput(String destination, LinkedList<File> validFiles, LinkedList<File> badFiles, String configReport) {
		StringBuilder builder = new StringBuilder();
		
		if (validFiles.isEmpty()) {
			builder.append("No valid AB1/ABI found.");
		} else {
			builder.append("The following files have been detected as valid AB1/ABI files:");
			builder.append(System.lineSeparator());
			
			// List all valid files
			for (File goodFile : validFiles) {
				builder.append(">>> ");
				builder.append(goodFile.getName());
				builder.append(System.lineSeparator());
			}
		
			builder.append(System.lineSeparator());
		
			// Number of files
			builder.append("Number of valid files: " + validFiles.size());
		}
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		
		if (badFiles.isEmpty()) {
			builder.append("No invalid files detected.");
		} else {
			builder.append("The following files were invalid:");
			builder.append(System.lineSeparator());
			
			// List all valid files
			for (File badFile : badFiles) {
				builder.append(">>> ");
				builder.append(badFile.getName());
				builder.append(System.lineSeparator());
			}
			builder.append(System.lineSeparator());
			
			// Number of files
			builder.append("Number of invalid files: " + badFiles.size());
		}
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		
		// configuration file
		builder.append("Config file: " + configReport);
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		
		String message = builder.toString();
		System.out.println();
		System.out.print(message);
		try {
			FileWriter writer = new FileWriter(destination + "/report.txt");
			writer.write(message);
			writer.close();
		} catch (IOException e) {
			System.out.println("Report could not be saved as a file.");
		}
	}
	
	
	/**
	 * Reads in the config file.
	 * 
	 * @return Message indicating the level of success for reading the configuration.
	 * 
	 * @author Ben Kohr
	 */
	private static String getConfig() {
		
		Config.setPath(readingPath);
		String report = "found";
		try {
			Config.readConfig();
		} catch (ConfigReadException e2) {
			report = "An error occured while reading the configuration file.";
		} catch (ConfigNotFoundException e2) {
			report = "No configuration file was found at the given path.";
		} catch (IOException e2) {
			System.out.println("Error during reading occurred.");
		}
		return report;
	}
	
	
	
	/**
	 * Reads the Sequence of the given File and prints Errors if necessary
	 * @param file
	 * @return
	 * @author Kevin
	 */
	private static AnalysedSequence readSequenceFromFile(File file){
		try {
			return SequenceReader.convertFileIntoSequence(file);
		} catch (FileReadingException e) {
			System.err.println("Could not Read File " + e.filename + ", File might be Damaged");
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String askForDestinationPath(String message){
		boolean invalidPath = true;
		while (invalidPath) {
			try {
				String path = ConsoleIO.readLine(message);
				invalidPath = false;

				return path;
			} catch (IOException e) {
				invalidPath = true;
				e.printStackTrace();
			}	
		}
		return null;
	}

	/**
	 * Asks the User for the path to the AB1 files and returns a list of the found files
	 * Also checks corectness of path and asks again if neccesary
	 * @return
	 */
	private static Object[] askForAB1Files() {
		boolean inputInvalid = true;

		LinkedList<File> files = null;
		LinkedList<File> oddFiles = null;
		// Ask User for AB1 File
		while (inputInvalid) {
			try {
				String path = ConsoleIO.readLine("Please give path to AB1 files:");

				io.SequenceReader.configurePath(path);
				readingPath = path;
				Object[] fileLists = io.SequenceReader.listFiles();
				files = (LinkedList<File>) fileLists[0];
				oddFiles = (LinkedList<File>) fileLists[1];
				
				if (files.isEmpty()) {
					System.err.println("No AB1 files found in given path");
				} else {
					inputInvalid = false;
				}
			} catch (IOException e) {
				System.err.println("Invalid Input Please Try Again");
			}
		}
		return new Object[] {files, oddFiles};
	}

	/**
	 * Resets the analysis pipeline to be able to start with a completely new
	 * analyzing process.
	 * 
	 * @author Ben Kohr
	 */
	private static void resetPipeline() {
		DatabaseConnection.flushQueue();
		DatabaseConnection.resetIDs();
		readingPath = null;
	}

}
