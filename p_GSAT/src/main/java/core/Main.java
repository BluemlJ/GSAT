package core;

import gui.MainWindow;
import gui.SettingsWindow;
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
    for (int i = 0; i < args.length; i++) {
      if (args[i].toLowerCase().equals("c")) {
        consoleMode = true;
        break;
      }
    }

    if (consoleMode)
      ConsoleVersion.startConsoleVersion();
    else
      javafx.application.Application.launch(MainWindow.class);

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
