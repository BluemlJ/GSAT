package core;

import java.io.IOException;
import java.util.Locale;

import exceptions.ConfigNotFoundException;
import exceptions.UnknownConfigFieldException;
import gui.GUIUtils;
import gui.MainWindow;
import io.ConfigHandler;
import io.GeneHandler;
import io.PrimerHandler;
import javafx.application.Application;
import javafx.scene.control.Alert.AlertType;

/**
 * This class coordinates the overall behavior of the program. It moderates the analyzing pipeline.
 *
 * @author Kevin Otto
 */
class Main {

  /**
   * Start of the GSAT program.
   * 
   * @param args Unused input parameters
   * 
   * @see ConsoleVersion#startConsoleVersion()
   * 
   * @author Kevin Otto
   */
  public static void main(String[] args) {

    Locale.setDefault(Locale.ENGLISH);

    try {
      ConfigHandler.readConfig();
      PrimerHandler.readPrimer();
      GeneHandler.readGenes();
    } catch (IOException | UnknownConfigFieldException | ConfigNotFoundException e) {
      GUIUtils.showInfo(AlertType.ERROR, "File damaged",
          "One of the configuration files seems to be damaged. Please remove them an try again.");
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
      Application.launch(MainWindow.class);
    }

  }

}
