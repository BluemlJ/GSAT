package analysis;

/**
 * This class models a gene, which can be compared with obtained DNA sequences. Genes can be seen as
 * original templates for mutated sequences.
 * 
 * @author Ben Kohr
 */
public class Gene extends Sequence {

  /**
   * Name of the gene (e.g. FSA).
   */
  private String name;


  /**
   * The globally unique id of this gene. This id is stored in the database.
   */
  private int id;



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


}
