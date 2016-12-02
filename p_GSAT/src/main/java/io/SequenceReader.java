package io;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.biojava.bio.program.abi.ABITrace;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;

import analysis.AnalyzedSequence;
import exceptions.FileReadingException;

/**
 * 
 * This class reads files of the AB1 format and extracts the information into a
 * sequence.
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
	 * Constructed list of file names in a given folder (in order to not analyze
	 * a file twice).
	 */
	private static LinkedList<String> files = new LinkedList<String>();

	/**
	 * Sets the path to the folder or the file to be used. In case of a folder,
	 * also gathers the file names into the list to be able to check that all
	 * files are analyzed, and only once.
	 * 
	 * @param path
	 *            The given path to the data
	 * 
	 */
	public static void configurePath(String path) {
		SequenceReader.path = path;
	}

	/**
	 * Parses one AB1 file (the only one or the next one in the list) into a
	 * sequence. If possible, deletes the first entry of the list. Note: There's
	 * no method to read in several files at once, because the files is analyzed
	 * one by one.
	 * 
	 * @throws IOException
	 * @throws IllegalSymbolException
	 * 
	 */
	public static AnalyzedSequence convertFileIntoSequence()
			throws FileReadingException, IOException, IllegalSymbolException {
		// "D:/Dokumente/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1"
		File referencedFile = new File(path);
		ABITrace parsedTrace = new ABITrace(referencedFile);
		SymbolList parsedSymbols = parsedTrace.getSequence();
		// TODO Add Primer
		AnalyzedSequence parsedSequence = new AnalyzedSequence(parsedSymbols.seqString(), "date", "researcher",
				referencedFile.getName(), "comment", parsedTrace);
		return parsedSequence;
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
