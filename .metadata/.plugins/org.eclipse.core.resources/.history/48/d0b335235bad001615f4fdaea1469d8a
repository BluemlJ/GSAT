package io;

import java.util.LinkedList;

import analysis.Gene;

/**
 * Class to communicate with the database.
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
     * Inserts all currently stored entries into the specified database.
     * 
     * @see #insertIntoDatabase()
     * 
     * @author Ben Kohr
     */
    public static void insertAllIntoDatabase() {
	while (!queue.isEmpty()) {
	    insertIntoDatabase();
	}
    }
    
    
    
    /**
     * Inserts a single data point into the specified database.
     */
    private static void insertIntoDatabase() {
	
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
     * Retrieves all genes from the database and returns them.
     * 
     * @return List of genes currently stored in the database
     */
    public static LinkedList<Gene> retrieveAllGenes() {
	return null;
    }
    
    
    
}
