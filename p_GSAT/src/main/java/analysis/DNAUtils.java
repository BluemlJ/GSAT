package analysis;

import java.util.LinkedList;

/**
 * This class contains the logic of analyzing DNA sequences. Thus, it is the
 * main part of the analyzing pipeline.
 * 
 */
public class DNAUtils {

	/**
	 * This method finds the point in given sequence from which on the sequence
	 * is not reliable anymore. The nucleotide at the point itself is the last
	 * "reliable" nucleotide.
	 * 
	 * @param sequence
	 *            The sequence for which the end of reliability should be
	 *            determined.
	 * 
	 * @return the index of the sequence String from which on it is considered
	 *         unreliable
	 */
	public int findEndOfTrustworthyness(AnalyzedSequence sequence) {
		return 0;
	}

	/**
	 * Measures the quality of a sequence or of one of its parts which is used
	 * to find the end of trustwothyness.
	 * 
	 * @return The quality measure for the given sequence (may also be a part of
	 *         a reference sequence)
	 */
	private static double measureQuality(AnalyzedSequence seq) {
		return 0.0;
	}

	/**
	 * Compares a sequence to a gene to find mutations. Returns the list of
	 * mutations, denoted as described by the department of organic chemistry.
	 * 
	 * @param toAnalyze
	 *            The sequence to be analyzed (which may have mutations)
	 * @param reference
	 *            The referenced gene (used to compare the sequence against it)
	 * 
	 * @return A List of Mutations, represented as Strings in the given format
	 * 
	 * @author Jannis Blüml
	 */
	public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze, Gene reference) {
		// list to return
		LinkedList<String> mutations = new LinkedList<>();
		// the sequence to analyze
		String mutatedSequence = toAnalyze.getSequence();
		// the gene sequence
		String originalSequence = reference.getSequence();
		// list of differences in form like C|12
		LinkedList<String> differences = reportDifferences(toAnalyze, reference);
		// the shiftdifference between mutated and original because of
		// injections/deletions
		int shift = 0;

		if (originalSequence.length() % 3 != 0 || mutatedSequence.length() % 3 != 0) {
			differences.set(0, "E|2");
		}

		for (String difference : differences) {
			// type of mutation (C,I,D,E)
			String typeOfMutations = difference.split("|")[0];
			// position relative to mutatedSequence
			int position = Integer.parseInt(difference.split("|")[1]);
			// position of mutation relative to the amino acid
			int positionInTriple = position % 3;
			// Strings for the amino acid in nukleotideTriple-form
			String mutatedAminoAcid = "", originalAminoAcid = "";

			switch (typeOfMutations) {
			// C = Change, normal mutation of one nukleotide
			case "C":
				// get amino acid nukleotide triple
				switch (positionInTriple) {
				case 0:
					mutatedAminoAcid += mutatedSequence.substring(position, position + 2);
					originalAminoAcid += originalSequence.substring(position + shift, position + 2 + shift);
					break;
				case 1:
					mutatedAminoAcid += mutatedSequence.substring(position - 1, position + 1);
					originalAminoAcid += originalSequence.substring(position - 1 + shift, position + 1 + shift);
					break;
				case 2:
					mutatedAminoAcid += mutatedSequence.substring(position - 2, position);
					originalAminoAcid += originalSequence.substring(position - 2 + shift, position + shift);
					break;
				default:
					// TODO hier eine execption werfen
					break;
				}
				String newAminoAcid = getAminoAcidName(mutatedAminoAcid);
				String oldAminoAcid = getAminoAcidName(originalAminoAcid);

				if (newAminoAcid.equals(oldAminoAcid)) {
					// SILENT MUTATION
				} else {
					mutations.add(reference.getName() + "   " + oldAminoAcid + position + newAminoAcid);
				}
				break;
			// I = injection, inject of an new amino acid (nukleotide triple
			// form)
			case "I":
				shift -= 3;
				mutatedAminoAcid += mutatedSequence.substring(position, position + 2);
				mutations.add(reference.getName() + "  +1" + getAminoAcidName(mutatedAminoAcid) + position);
				break;
			// D = deletion, deletion of an amino acid
			case "D":
				originalAminoAcid += originalSequence.substring(position, position + 2);
				shift += 3;
				mutations.add(reference.getName() + "  -1" + getAminoAcidName(originalAminoAcid) + position);
				break;
			// E = error, if an injection or a deletion has more or less then 3
			// nukleotides or the sequence at all are not "% 3 = 0"
			case "E":
				mutations.clear();
				mutations.add(" - reading frame");
				return mutations;
			default:
				// TODO exeption werfen
				break;
			}

		}

		return mutations;
	}

	/**
	 * This method gets the nukleotideTriple and returns the Name of the amino
	 * acid
	 * 
	 * @param AminoAcidTriple
	 *            a nukleotideTriple to test
	 * @return the name of the amino acid in short form
	 * @author Jannis Blüml
	 */
	private static String getAminoAcidName(String AminoAcidTriple) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Compares a sequence to its own referenced gene.
	 * 
	 * @param toAnalyse
	 *            The sequence to be analyzed
	 * 
	 * @return A List of Mutations, represented as Strings in the given format
	 * 
	 * @see #findMutations(AnalyzedSequence, Gene)
	 * 
	 * @author Ben Kohr
	 */
	public static LinkedList<String> findMutations(AnalyzedSequence toAnalyze) {

		LinkedList<String> mutations = findMutations(toAnalyze, toAnalyze.getReferencedGene());
		return mutations;
	}

	/**
	 * Finds the gene that fits best to a given sequence by comparing it to all
	 * given genes. Known genes can be found in the database.
	 * 
	 * @param toAnalyze
	 *            Sequence to be analyzed (i.e. for which the fitting gene is to
	 *            find)
	 * 
	 * @return The found gene.
	 */
	public static Gene findRightGene(AnalyzedSequence toAnalyze) {
		return null;
	}

	/**
	 * Compares to sequences and returns the differences as a list (represented
	 * by the positions). The order of the input sequences is irrelevant.
	 * 
	 * @param sOne
	 *            The first sequence
	 * @param sTwo
	 *            The second sequence.
	 * 
	 * @return A list of differences (represented as String)
	 */
	private static LinkedList<String> reportDifferences(Sequence sOne, Sequence sTwo) {
		return null;

	}

	/**
	 * Compares to sequences and returns their similarity without finding the
	 * exact differences. The order of the input sequences is irrelevant.
	 * 
	 * @param first
	 *            The first sequence
	 * @param second
	 *            The second sequence
	 * 
	 * @return Similarity measure
	 */
	private static double compare(Sequence first, Sequence second) {
		return 0.0;
	}

}
