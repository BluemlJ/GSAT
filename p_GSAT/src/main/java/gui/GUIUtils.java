package gui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.Pair;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;
import exceptions.CorruptedSequenceException;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import io.Config;
import io.ConsoleIO;
import io.FileSaver;
import io.GeneReader;
import io.SequenceReader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class GUIUtils {

    public static Pair<Boolean, String> runAnalysis(LinkedList<File> sequences) {
	boolean success = false;
	String report = "Failure within the analysing process, unknown reason.";

	// TODO DROPDOWN EINBINDEN
	Gene gene = GeneReader.getGeneAt(0);
	for (File file : sequences) {
	    AnalysedSequence toAnalyse = readSequenceFromFile(file).first;
	    toAnalyse.setReferencedGene(gene);

	    try {
		StringAnalysis.checkComplementAndReverse(toAnalyse);
	    } catch (CorruptedSequenceException e) {
		report = "Its not possible to get the complementary Sequence, analysing stops";
		return new Pair<Boolean, String>(success, report);
	    }
	    // cut out vector
	    StringAnalysis.trimVector(toAnalyse);

	    // cut out low Quality parts of sequence
	    QualityAnalysis.trimLowQuality(toAnalyse);

	    // mutation analysis

	    // add entry to database
	    try {
		FileSaver.storeResultsLocally(file.getName().replaceFirst("[.][^.]+$", "") + "_result", toAnalyse);
	    } catch (MissingPathException e2) {
		report = "Missing Path to Destination, aborting analysing.";
		FileSaver.setLocalPath("");
		return new Pair<Boolean, String>(success, report);
	    } catch (IOException e2) {
		report = "IOExeption in storing data, aborting analysing.";
		return new Pair<Boolean, String>(success, report);
	    }

	}
	report = "Analysing was successfull";
	success = true;
	return new Pair<Boolean, String>(success, report);
    }

    private static Pair<Gene, Pair<Boolean, String>> getGeneFromDropDown(int dropdownID) {
	return new Pair<Gene, Pair<Boolean, String>>(GeneReader.getGeneAt(dropdownID),
		new Pair<Boolean, String>(true, "Reading Gene was successfull"));
	
    }

    
    
    
    
    private static Pair<Pair<LinkedList<File>, LinkedList<File>>, Pair<Boolean, String>> getSequencesFromSourceFolder(
	    TextField source) {
	String report = "Reading Sequences unsuccessfull with unknown error";
	boolean success = false;

	LinkedList<File> files = null;
	LinkedList<File> oddFiles = null;

	io.SequenceReader.configurePath(source.getText());
	Pair<LinkedList<File>, LinkedList<File>> fileLists = io.SequenceReader.listFiles();
	files = fileLists.first;
	oddFiles = fileLists.second;

	if (files.isEmpty()) {
	    report = "No AB1 files were found at the given path.";
	    return new Pair<Pair<LinkedList<File>, LinkedList<File>>, Pair<Boolean, String>>(null,
		    new Pair<Boolean, String>(success, report));
	} else {
	    report = "Reading sequences successfull";
	    success = true;
	}

	Pair<LinkedList<File>, LinkedList<File>> one = new Pair<LinkedList<File>, LinkedList<File>>(files, oddFiles);
	Pair<Boolean, String> two = new Pair<Boolean, String>(success, report);

	return new Pair<Pair<LinkedList<File>, LinkedList<File>>, Pair<Boolean, String>>(one, two);
    }

    private static Pair<Boolean, String> setDestination(TextField destination) {
	boolean success = false;
	String report = "Reading destinationpath was unsuccessfull.";
	String path = null;
	try {
	    path = ConsoleIO.readLine(destination.getText());
	    success = true;
	    report = "Reading destinationpath successfull.";
	    FileSaver.setLocalPath(path);
	} catch (IOException e) {
	    report = "Reading destinationpath unsuccessfull, IOExeption";
	}
	// set destination path for database entries

	return new Pair<Boolean, String>(success, report);
    }

    private static Pair<Boolean, String> runConfiguration(TextField configpath) {
	Config.setPath(configpath.getText());
	boolean success = false;
	String report = "Reading configfile unsuccessfull with unknown error";
	try {
	    Config.readConfig();
	    success = true;
	    report = "Reading configfile successfull";
	} catch (ConfigReadException e) {
	    report = "An error occured while reading the configuration file.";
	} catch (ConfigNotFoundException e) {
	    report = "No configuration file was found at the given path.";
	} catch (IOException e) {
	    System.out.println("Error during reading occurred.");
	}
	return new Pair<Boolean, String>(success, report);
    }

    /**
     * Reads the Sequence of the given File and prints Errors if necessary
     * 
     * @param file
     * @return
     * @author Jannis
     */
    private static Pair<AnalysedSequence, Pair<Boolean, String>> readSequenceFromFile(File file) {
	String report = "Failure with" + file.getAbsolutePath() + "\n This file might be corrupted.";
	boolean success = false;
	Pair<Boolean, String> ret = new Pair<Boolean, String>(success, report);
	try {
	    success = true;
	    report = "";
	    ret = new Pair<Boolean, String>(success, report);
	    return new Pair<AnalysedSequence, Pair<Boolean, String>>(SequenceReader.convertFileIntoSequence(file), ret);
	} catch (FileReadingException e) {
	    System.out.println("Could not read from file.");
	} catch (IOException e) {
	    System.out.println("Error during reading occured.");
	}
	return new Pair<AnalysedSequence, Pair<Boolean, String>>(null, ret);
    }
}
