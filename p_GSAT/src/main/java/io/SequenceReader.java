package io;

import java.util.ArrayList;

import analysis.AnalyzedSequence;

/**
 * 
 * This class reads files of the AB1 format and extracts the information into a sequence.
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
    private static ArrayList<String> files = new ArrayList<String>();
    
    
    
    /**
     * Sets the path to the folder or the file to be used. In case of a folder, also gathers
     * the file names into the ArrayList to be able to check that all files are analyzed,
     * and only once.
     * 
     */
    public static void configurePath(String path) {
	
    }
    
    
    
    /**
     * Parses one AB1 file (the only one or the next one in the list) into a sequence.
     * If possible, deletes the first entry of the Array List.
     * Note: There's no method to read in several files, because it might be more clever
     * to process file by file completely.
     * 
     */
    public static AnalyzedSequence convertFileIntoSequence() {
	return null;
    }
    
    
    /**
     * Discards the current path and files.
     */
    public static void resetInputData() {
	path = null;
	files.clear();
    }
    
    
    /**
     * Indicates whether there is a current path set.
     */
    public static boolean isPathSet() {
	return (path != null);
    }
    
    
    
}
