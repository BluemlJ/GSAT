package core;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.QualityAnalysis;
import analysis.Sequence;
import analysis.StringAnalysis;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.UndefinedTypeOfMutationException;
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

	/**
	 * Start of the GSAT program.
	 * 
	 * @param args
	 *            Unused input parameters
	 */
	public static void main(String[] args) {
	  boolean console = false;
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
		LinkedList<File> files = askForAB1Files();
		String destinationPath = askForDestinationPath();
		DatabaseConnection.setLocalPath(destinationPath);
		//TODO Aks for GEN
		
		Gene gene = null;//TODO Read GEN
		for (File file : files) {
			AnalysedSequence activeSequence = null;
			try {
				activeSequence = SequenceReader.convertFileIntoSequence(file);
			} catch (FileReadingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			QualityAnalysis.trimLowQuality(activeSequence);
			
			StringAnalysis.trimVector(activeSequence, gene);
			
			try {
				MutationAnalysis.findMutations(activeSequence);
			} catch (UndefinedTypeOfMutationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//TODO:
			//Ask for Comment and add
			//read researcher from config
			try {
				LinkedList<DatabaseEntry> entries = DatabaseEntry.convertSequenceIntoEntries(activeSequence);
				DatabaseConnection.addAllIntoQueue(entries);
				DatabaseConnection.storeAllLocally(file.getName() +"_result");
				resetPipeline();
			} catch (UndefinedTypeOfMutationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MissingPathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Program end");
	}
	
	private static String askForDestinationPath(){
		boolean invalidPath = true;
		while (invalidPath) {
			try {
				String path = ConsoleIO.readLine("Please give destination path for output File");
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
	private static LinkedList<File> askForAB1Files() {
		boolean inputInvalid = true;

		LinkedList<File> files = null;
		// Ask User for AB1 File
		while (inputInvalid) {
			try {
				String path = ConsoleIO.readLine("Please give path to AB1 files:");

				io.SequenceReader.configurePath(path);
				files = io.SequenceReader.listFiles();
				if (files.isEmpty()) {
					System.err.println("No AB1 files found in given path");
				} else {
					inputInvalid = false;
				}
			} catch (IOException e) {
				System.err.println("Invalid Input Please Try Again");
			}
		}
		return files;
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
	}

}
