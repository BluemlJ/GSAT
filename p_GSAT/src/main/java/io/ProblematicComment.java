package io;

/**
 * This enumeration contains items indicating anomalies that may happen during analysis. These items
 * are translated into user messages if needed.
 * 
 * @author Ben Kohr
 */
public enum ProblematicComment {
  // sequence is to short
  SEQUENCE_TO_SHORT,

  // sequence and gene could not be aligned
  NO_MATCH_FOUND,

  // ninety percent or more of the sequence is of bad quality
  NINETY_PERCENT_QUALITY_TRIM,

  // sequence could not be read
  COULD_NOT_READ_SEQUENCE,

  // an error during analysis happened
  ERROR_DURING_ANALYSIS_OCCURRED
}
