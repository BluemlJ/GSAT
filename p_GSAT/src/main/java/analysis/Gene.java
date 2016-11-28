package analysis;

/**
 * This class models a reference gene. Genes can be compared with obtained DNA sequences.
 * 
 * @author Ben Kohr
 */
public class Gene extends Sequence {

  /**
   * Name of the gene.
   */
  private String name;

  
  /**
   * The globally unique id of this gene.
   */
  private int id;
  


  /**
   * Constructor setting attributes.
   * 
   * @param sequence The nucleotide sequence as a String.
   * @param name The name of the gene
   * 
   * @author Ben Kohr
   */
  public Gene(String sequence, int id, String name, String addingDate, String researcher) {
    super(sequence, addingDate, researcher);
    this.name = name;
    this.id = id;
  }



  public String getName() {
    return name;
  }



  public void setName(String name) {
    this.name = name;
  }



  public int getId() {
    return id;
  }



  public void setId(int id) {
    this.id = id;
  }

  
  
}
