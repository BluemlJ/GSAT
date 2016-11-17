package io;

import java.util.LinkedList;

import analysis.Gene;

/**
 * Class to communicate with the database
 *
 */
public class DatabaseConnection {

    /**
     * List off all entries to be written into the database.
     */
    private static LinkedList<DatabaseEntry> queue = new LinkedList<DatabaseEntry>();
    
    
    
    /**
     * Specifies the location of the database.
     */
    private static String connectionString;
    
    
    
    /**
     * Inserts all currently stored Entries into the specified database.
     * Makes use of insertIntoDatabase 
     */
    public static void insertAllIntoDatabase() {
	
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
     * @param dbe
     */
    public static void addIntoQueue(DatabaseEntry dbe) {
	queue.add(dbe);
    }
    
    
    
    /**
     * Empties the current waiting queue.
     */
    public static void flushQueue() {
	queue.clear();
    }
    
    
    /**
     * Retrieves all genes from the database and returns them.
     */
    public static LinkedList<Gene> retrieveAllGenes() {
	return null;
    }
    
    
    
}
