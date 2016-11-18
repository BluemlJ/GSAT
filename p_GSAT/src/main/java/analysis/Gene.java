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
	 * Constructor setting attributes.
	 * 
	 * @param sequence The nucleotide sequence as a String.
	 * @param name The name of the gene
	 * 
	 * @author Ben Kohr
	 */
	public Gene(String sequence, String name) {
		super(sequence);
		this.name = name;
	}

}
