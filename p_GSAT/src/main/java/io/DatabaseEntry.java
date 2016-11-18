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
	 * The unique id of the analysed file;
	 */
	private int id;

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
	 * Constructor that sets up all attributes.
	 * 
	 * @param id The unique id of the sequence
	 * @param gene The sequence's reference gene
	 * @param primer Given meta information about the sequence
	 * @param mutatin One mutation, represented as a String
	 * @param silent Whether the mutation is silent or not
	 * 
	 * @author Ben Kohr
	 */
	public DatabaseEntry(int id, String gene, String primer, String mutation, boolean silent) {
		this.id = id;
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
	 */
	public static LinkedList<DatabaseEntry> convertSequenceIntoDBEs(AnalyzedSequence seq) {
		return null;
	}

}
