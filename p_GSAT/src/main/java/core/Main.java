package core;

import java.io.IOException;

import io.ConsoleIO;
import io.DatabaseConnection;
import io.SequenceReader;

/**
 * This class coordinates the overall behavior of the program. It moderates the
 * analyzing pipeline.
 *
 * @author Ben Kohr
 */
public class Main {

	/**
	 * Start of the GSAT program.
	 * 
	 * @param args
	 *            Unused input parameters
	 */
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("c")) {
				startConsoleVersion();
			}
		}
	}

	private static void startConsoleVersion() {
		ConsoleIO.clearConsole();

		boolean inputInvalid = true;
		
		while (inputInvalid) {
			try {
				String path = ConsoleIO.readLine("Bitte Ordner Für AB1 Dateien Angeben:");
				io.SequenceReader.configurePath(path);
			} catch (IOException e) {
				System.err.println("Invalid Input Please Try Again");
			}
			
		}
	}

	/**
	 * Resets the analysis pipeline to be able to start with a completely new
	 * analyzing process.
	 * 
	 * @author Ben Kohr
	 */
	private static void resetPipeline() {
		DatabaseConnection.flushQueue();
		DatabaseConnection.resetIDs();
	}

}
