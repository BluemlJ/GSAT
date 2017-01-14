package core;

import io.FileSaver;

/**
 * This class coordinates the overall behavior of the program. It moderates the analyzing pipeline.
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
    boolean consoleMode = false;
    // DEBUG: TODO remove
    consoleMode = true;
    // DEBUG END
    for (int i = 0; i < args.length; i++) {
      if (args[i].toLowerCase().equals("c")) {
        consoleMode = true;
        break;
      }
    }

    if (consoleMode) ConsoleVersion.startConsoleVersion();

  }

  /**
   * Resets the analysis pipeline to be able to start with a completely new analyzing process.
   * 
   * @author Ben Kohr
   */
  static void resetPipeline() {
    FileSaver.resetAll();
  }

}
