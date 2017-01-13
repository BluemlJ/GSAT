package exceptions;

import analysis.AnalysedSequence;
import analysis.Gene;

public class DissimilarGeneException extends Exception {


  /**
   * The sequence for which the gene was found (but with low fitting quality)
   */
  public AnalysedSequence toAnalyse;

  /**
   * The gene that fits the sequence best
   */
  public Gene bestGene;

  /**
   * The similarity rate of the sequence and the gene
   */
  public double similarity;


  /**
   * Constructor sets the internal fields.
   * 
   * @param toAnalyze Sequence with a low-fitting-quality gene found
   * @param bestGene The gene for this sequence with a low similariy
   * @param similarity The similarity rate
   */
  public DissimilarGeneException(AnalysedSequence toAnalyse, Gene bestGene, double similarity) {
    super(
        "Best found gene for given sequence (" + bestGene.getName() + ") has a similarity of only "
            + similarity + " for the given sequence (file: " + toAnalyse.getFileName() + ").");

    this.toAnalyse = toAnalyse;
    this.bestGene = bestGene;
    this.similarity = similarity;
  }


}
