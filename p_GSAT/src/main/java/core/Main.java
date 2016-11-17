package core;

import io.DatabaseConnection;
import io.SequenceReader;

/**
 * This class coordinates the overall behavior.
 *
 */
public class Main {

    
    /**
     * Start of the GSAT program.
     */
    public static void main(String[] args) {
	
    }
    
    
    
    
    /**
     * Resets the analysis pipeline to be able to start with a completely
     * new analyzing process
     */
    private static void resetPipeline() {
	SequenceReader.resetInputData();
	DatabaseConnection.flushQueue();
    }
    
    
}
