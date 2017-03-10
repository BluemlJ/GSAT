package core;

import gui.MainWindow;

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
		boolean consoleMode = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().equals("c")) {
				consoleMode = true;
				break;
			}
		}

		if (consoleMode) {
			ConsoleVersion.startConsoleVersion();
		} else {
			javafx.application.Application.launch(MainWindow.class);
		}

	}

}
