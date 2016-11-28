package io;

import java.util.LinkedList;

import analysis.AnalyzedSequence;

/**
 * Models an "abstract" entry to be written into the database. The database itself may have
 * different tables and this abstract entry can actually be split with its parts being distributed
 * among those tables (while making sure that it is possible to combine the data again).
 * 
 * @author Ben Kohr
 *
 */
public class DatabaseEntry {

  /**
   * The id for this entry. It is given by the DatabaseConnection class.
   * Until the id is given, -1 is the placeholder id.
   */
  private int id = -1;

  /**
   * The name of the file this entry was retrieved from.
   */
  private String fileName;
  
  /**
   * The id for the referenced gene.
   */
  private int geneID;
  
  /**
   * The sequence of nucleotides.
   */
  private String sequence;
  
  /**
   * The date at which this sequence was added.
   */
  private String addingDate;

  /**
   * The researcher who added this sequence.
   */
  private String researcher;

  /**
   * The used primer.
   */
  private String comments;

  /**
   * A mutation string; might be empty.
   */
  private String mutation;

  /**
   * Indicates if the mutation is silent or not.
   */
  private boolean silent;
  
  /**
   * Indicates whether the results of this analysis have been checked by a researcher.
   */
  private boolean manuallyChecked;

  
  /**
   * The vector to be stored with the sequence.
   */
  private String vector;
  
  
  /**
   * The promotor to be stored with the sequence.
   */
  private String promotor;
  


  /**
   * Constructor that sets up all attributes except the id (which is given by the DatabaseConnection
   * class).
   * 
   * @author Ben Kohr
   */
  public DatabaseEntry(String fileName, int geneID, String sequence, 
                                       String addingDate, String researcher, String comments, 
                                       String vector, String promotor, boolean manuallyChecked,
                                       String mutation, boolean silent) {

    this.fileName = fileName;
    this.geneID = geneID;
    this.sequence = sequence;
    this.addingDate = addingDate;
    this.researcher = researcher;
    this.comments = comments;
    this.vector = vector;
    this.promotor = promotor;
    this.manuallyChecked = manuallyChecked;
    this.mutation = mutation;
    this.silent = silent;
    
  }


  /**
   * This method converts a given sequence into a list of database entries to be stored and returns
   * them.
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
    int geneID = seq.getReferencedGene().getId();
    String sequence = seq.getSequence();
    String addingDate = seq.getAddingDate();
    String researcher = seq.getResearcher();
    String comments = seq.getComments();
    String vector = seq.getVector();
    String promotor = seq.getPromotor();
    boolean manuallyChecked = seq.isManuallyChecked();
    

    // Initialize lists for (silent) mutations
    LinkedList<String> mutations = seq.getMutations();
    LinkedList<String> silentMutations = seq.getSilentMutations();

    // Collect normal mutations
    for (String mutation : mutations) {
      DatabaseEntry dbe = 
          new DatabaseEntry(fileName, geneID, sequence, addingDate, researcher, comments, vector, promotor, manuallyChecked, mutation, false);
      entries.add(dbe);
    }

    // Collect silent mutations
    for (String silentMutation : silentMutations) {
      DatabaseEntry dbe = 
          new DatabaseEntry(fileName, geneID, sequence, addingDate, researcher, comments, vector, promotor, manuallyChecked, silentMutation, true);
      entries.add(dbe);
    }

    // return the results
    return entries;

  }



  /**
   * Sets the id of this entry. Typically used be DatabaseConnection to update the ids correctly.
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
   * Returns this entry's gene id.
   * 
   * @return gene id
   * 
   * @author Ben Kohr
   */
  public int getGeneID() {
    return geneID;
  }


  /**
   * Returns this entry's nucleotide sequence
   * 
   * @return the nucleotide sequence
   * 
   * @author Ben Kohr
   */
  public String getSequence() {
    return sequence;
  }


  /**
   * Returns this entry's adding date
   * 
   * @return adding date
   * 
   * @author Ben Kohr
   */
  public String getAddingDate() {
    return addingDate;
  }


  /**
   * Returns this entry's reasercher
   * 
   * @return this entry's researcher
   * 
   * @author Ben Kohr
   */
  public String getResearcher() {
    return researcher;
  }

  /**
   * Returns this entry's stored comments
   * 
   * @return comments
   * 
   * @author Ben Kohr
   */
  public String getComments() {
    return comments;
  }


  /**
   * Returns a boolean indicating whether this entry's mutation is silent or not
   * 
   * @return Is this mutation silent?
   * 
   * @author Ben Kohr
   */
  public boolean isSilent() {
    return silent;
  }


  /**
   * Returns a boolean indicating whether this result is manually checked
   * 
   * @return Is this mutation silent?
   * 
   * @author Ben Kohr
   */
  public boolean isManuallyChecked() {
    return manuallyChecked;
  }


  /**
   * Returns this entry's vector
   * 
   * @return the vector
   * 
   * @author Ben Kohr
   */
  public String getVector() {
    return vector;
  }


  /**
   * Returns this entry's promotor
   * 
   * @return the promotor
   * 
   * @author Ben Kohr
   */
  public String getPromotor() {
    return promotor;
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
   * Returns the single mutation of this entry.
   * 
   * @return mutation
   * 
   * @author Ben Kohr
   */
  public String getMutation() {
    return mutation;
  }


}
