package io;

import java.util.LinkedList;

import analysis.AnalyzedSequence;

/**
 * Models an "abstract" entry to be written into the database. The database itself may have
 * different tables and this abstract entry can actually be split with its parts being
 * distributed among those tables (while making sure that it is possible to combine the data
 * again).
 * 
 * @author Ben Kohr
 *
 */
public class DatabaseEntry {

	/**
	 * The id for this entry. 
	 * It is given by the DatabaseConnection class.
	 */
	private int id = -1;

	
	/**
	 * The name of the file this entry was retrieved from.
	 */
	private String fileName;
	
	
	/**
	 * The referenced gene of analysis.
	 */
	private String gene;

	/**
	 * The used primer.
	 */
	private String primer;

	/**
	 * A mutation string; might be empty.
	 */
	private String mutation;

	/**
	 * Indicates if the mutation is silent or not.
	 */
	private boolean silent;



	/**
	 * Constructor that sets up all attributes except the id (which is given
	 * by the DatabaseConnection class).
	 * 
	 * @param gene The sequence's reference gene
	 * @param primer Given meta information about the sequence
	 * @param mutatin One mutation, represented as a String
	 * @param silent Whether the mutation is silent or not
	 * 
	 * @author Ben Kohr
	 */
	public DatabaseEntry(String fileName, String gene, String primer, String mutation, boolean silent) {
		this.fileName = fileName;
		this.gene = gene;
		this.primer = primer;
		this.mutation = mutation;
		this.silent = silent;

	}


	/**
	 * This method converts a given sequence into a list of database entries to be stored and
	 * returns them.
	 * 
	 * @param seq The sequence to be converted to database entries
	 * @return List of database entries ready to be stored
	 * 
	 * @author Ben Kohr
	 */
	public static LinkedList<DatabaseEntry> convertSequenceIntoEntries(AnalyzedSequence seq) {

		LinkedList<DatabaseEntry> entries = new LinkedList<DatabaseEntry>();
		
		// Get information valid for all entries
		String fileName = seq.getFileName();
		String primer = seq.getPrimer();
		String geneName = seq.getReferencedGene().getName();
		
		// Initialize lists for (silent) mutations
		LinkedList<String> mutations = seq.getMutations();
		LinkedList<String> silentMutations = seq.getSilentMutations();
	
		// Collect normal mutations
		for(String mutation : mutations) {
			DatabaseEntry dbe = new DatabaseEntry(fileName, geneName, primer, mutation, false);
			entries.add(dbe);
		}
		
		// Collect silent mutations
		for(String silentMutation : silentMutations) {
			DatabaseEntry dbe = new DatabaseEntry(fileName, geneName, primer, silentMutation, true);
			entries.add(dbe);
		}
		
		return entries;
		
	}

	
	
	
	/**
	 * Sets the id of this entry.
	 * Typically used be DatabaseConnection to update the ids
	 * correctly.
	 * 
	 * @param id The current id
	 * 
	 * @author Ben Kohr
	 */
	public void setID(int id) {
		this.id = id;
	}
	
	
	
	/**
	 * Returns the id of this entry.
	 * 
	 * @return id
	 * 
	 * @author Ben Kohr
	 */
	public int getID() {
		return id;
	}
	
	
	
	
	/**
	 * Returns the file name this database entry was obtained from.
	 * 
	 * @return the file name
	 * 
	 * @author Ben Kohr
	 */
	public String getFileName() {
		return fileName;
	}
	
	
	
	/**
	 * Returns the referenced gene of this entry.
	 * 
	 * @return gene
	 * 
	 * @author Ben Kohr
	 */
	public String getGene() {
		return gene;
	}
	
	
	/**
	 * Returns the primer of this entry.
	 * 
	 * @return primer
	 * 
	 * @author Ben Kohr
	 */
	public String getPrimer() {
		return primer;
	}
	
	
	/**
	 * Returns the single mutation of this entry.
	 * 
	 * @return mutation
	 * 
	 * @author Ben Kohr
	 */
	public String getMutation() {
		return mutation;
	}
	
	
	/**
	 * Returns the boolean indicated whether the mutation is
	 * silent or not.
	 * 
	 * @return boolean indicating a silent mutation
	 * 
	 * @author Ben Kohr
	 */
	public boolean getSilentBoolean() {
		return silent;
	}
	
}
