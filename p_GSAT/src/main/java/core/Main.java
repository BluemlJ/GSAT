package core;

import io.DatabaseConnection;
import io.SequenceReader;

/**
 * This class coordinates the overall behavior of the program. It moderates the analyzing
 * pipeline.
 *
 * @author Ben Kohr
 */
public class Main {

	/**
	 * Start of the GSAT program.
	 * 
	 * @param args Unused input parameters
	 */
	public static void main(String[] args) {

	}


	/**
	 * Resets the analysis pipeline to be able to start with a completely new analyzing
	 * process.
	 * 
	 * @author Ben Kohr
	 */
	private static void resetPipeline() {
		SequenceReader.resetInputData();
		DatabaseConnection.flushQueue();
		DatabaseConnection.resetIDs();
	}

}
