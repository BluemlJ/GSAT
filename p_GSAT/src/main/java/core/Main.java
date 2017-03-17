package core;

import java.io.IOException;
import java.util.Locale;

import exceptions.ConfigNotFoundException;
import exceptions.UnknownConfigFieldException;
import gui.MainWindow;
import io.ConfigHandler;
import io.GeneHandler;
import io.PrimerHandler;

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

    Locale.setDefault(Locale.ENGLISH);

    try {
      ConfigHandler.readConfig();
      PrimerHandler.readPrimer();
      GeneHandler.readGenes();
    } catch (IOException | UnknownConfigFieldException | ConfigNotFoundException e) {
      e.printStackTrace();
    }
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
