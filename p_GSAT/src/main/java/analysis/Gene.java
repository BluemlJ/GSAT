package analysis;

/**
 * This class models a gene, which can be compared with obtained DNA sequences. Genes can be seen as
 * original templates for mutated sequences.
 * 
 * @author Ben Kohr
 */
public class Gene extends Sequence {

  /**
   * The globally unique id of this gene. This id is stored in the database.
   */
  private int id;


  /**
   * Name of the gene (e.g. FSA).
   */
  private String name;

  private String organism;

  private String comment;


  /**
   * Constructor setting all given attributes (by calling the super constructor).
   * 
   * @param sequence The nucleotide sequence as a String.
   * @param id the unique identification number
   * @param name The name of the gene
   * @param researcher the name of the researcher who added this gene
   * 
   * @author Ben Kohr
   */
  public Gene(String sequence, int id, String name, String researcher) {
    super(sequence, researcher);
    this.name = name;
    this.id = id;
  }

  /**
   * Constructor setting all given attributes (by calling the super constructor).
   * 
   * @param sequence The nucleotide sequence as a String.
   * @param id the unique identification number
   * @param name The name of the gene
   * @param organism Name of the organism (more information)
   * @param comment a comment about this specific gene sequence
   * @param researcher the name of the researcher who added this gene
   * 
   * @author Jannis Blueml
   */
  public Gene(String sequence, int id, String name, String researcher, String organism,
      String comment) {
    super(sequence, researcher);
    this.name = name;
    this.id = id;
    this.organism = organism;
    this.comment = comment;
  }



  // GETTERs and SETTERs:

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }


  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOrganism() {
    return organism;
  }

  public String getComment() {
    return comment;
  }

  public void setOrganism(String organism) {
    this.organism = organism;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

}
