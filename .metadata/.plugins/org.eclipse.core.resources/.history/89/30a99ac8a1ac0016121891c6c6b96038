package io;

/**
 * Models an "abstract" entry to be written into the database. The database itself may have
 * different tables and this abstract entry could actually be split with its parts 
 * being distributed among those tables. Recombination possible with joins.
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
     * Quality measure
     */
    private double quality;
    
    
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
     */
    public DatabaseEntry(int id, String gene, String primer, double quality, 
	    		 String mutation, boolean silent) {
	this.id = id;
	this.gene = gene;
	this.primer = primer;
	this.quality = quality;
	this.mutation = mutation;
	this.silent = silent;
	
    }
    
    
    
    
}
