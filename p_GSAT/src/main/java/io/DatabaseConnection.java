package io;

import java.util.LinkedList;

import analysis.Gene;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseErrorException;

/**
 * Class to communicate with the database. It's also responsible for storing files locally, if
 * there is e.g. no connection to the database available.
 * 
 * @author Ben Kohr
 *
 */
public class DatabaseConnection {

	/**
	 * List off all entries to be written into the database. These are typically the results of
	 * the analysis of a single file.
	 */
	private static LinkedList<DatabaseEntry> queue = new LinkedList<DatabaseEntry>();

	/**
	 * Specifies the location of the database.
	 */
	private static String connectionString;

	/**
	 * Specifies the path where local files shall be created (if necessary).
	 */
	private static String localPath;



	/**
	 * Inserts all currently stored entries into the specified database.
	 * 
	 * @see #insertIntoDatabase()
	 * 
	 * @author Ben Kohr
	 */
	public static void insertAllIntoDatabase() throws DatabaseConnectionException, DatabaseErrorException {
		while (!queue.isEmpty()) {
			insertIntoDatabase();
		}
	}


	/**
	 * Inserts a single data point into the specified database.
	 */
	private static void insertIntoDatabase() throws DatabaseConnectionException, DatabaseErrorException {

	}


	/**
	 * Inserts the data points into a local file (e.g. if there is no connection to the
	 * database available right now).
	 */
	private static void storeAllLocally() {

	}


	/**
	 * Puts a single entry in the waiting queue for being written into the database.
	 * 
	 * @param dbe New Data point to be written into the database
	 * 
	 * @author Ben Kohr
	 */
	public static void addIntoQueue(DatabaseEntry dbe) {
		queue.add(dbe);
	}


	/**
	 * Empties the current waiting queue.
	 * 
	 * @author Ben Kohr
	 */
	public static void flushQueue() {
		queue.clear();
	}


	/**
	 * Sets the path where local files shall be created if necessary.
	 * 
	 * @param path The path to create local files as a String
	 * 
	 * @author Ben Kohr
	 */
	public static void setLocalPath(String path) {
		localPath = path;
	}


	/**
	 * Retrieves all genes from the database and returns them.
	 * 
	 * @return List of genes currently stored in the database
	 */
	public static LinkedList<Gene> retrieveAllGenes() {
		return null;
	}

}
