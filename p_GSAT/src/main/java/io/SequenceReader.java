package io;

import java.util.LinkedList;

import analysis.AnalyzedSequence;
import exceptions.FileReadingException;

/**
 * 
 * This class reads files of the AB1 format and extracts the information into a sequence.
 * 
 * @author Ben Kohr
 *
 */
public class SequenceReader {

	/**
	 * Path the the source of information (folder/file).
	 */
	private static String path;

	/**
	 * Indicates whether the given path specifies a folder or not (i.e. a file);
	 */
	private static boolean pathLeadsToFolder;

	/**
	 * Constructed list of file names in a given folder (in order to not analyze a file twice).
	 */
	private static LinkedList<String> files = new LinkedList<String>();



	/**
	 * Sets the path to the folder or the file to be used. In case of a folder, also gathers
	 * the file names into the list to be able to check that all files are analyzed, and only
	 * once.
	 * 
	 * @param path The given path to the data
	 * 
	 */
	public static void configurePath(String path) {

	}


	/**
	 * Parses one AB1 file (the only one or the next one in the list) into a sequence. If
	 * possible, deletes the first entry of the list. Note: There's no method to read in
	 * several files at once, because the files is analyzed one by one.
	 * 
	 */
	public static AnalyzedSequence convertFileIntoSequence() throws FileReadingException {
		return null;
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


	/**
	 * Indicates whether there is a path set at the moment.
	 * 
	 * @return Whether a path is set or not
	 * 
	 * @author Ben Kohr
	 */
	public static boolean isPathSet() {
		return (path != null);
	}

}