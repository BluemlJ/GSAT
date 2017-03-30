package core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import exceptions.ConfigNotFoundException;
import exceptions.UnknownConfigFieldException;
import gui.GUIUtils;
import gui.MainWindow;
import io.ConfigHandler;
import io.GeneHandler;
import io.PrimerHandler;
import javafx.scene.control.Alert.AlertType;

/**
 * This class coordinates the overall behavior of the program. It moderates the analyzing pipeline.
 *
 * @author Ben Kohr
 */
class Main {

  /**
   * Start of the GSAT program.
   * 
   * @param args Unused input parameters
   */
  public static void main(String[] args) {

    // check for heap space
    try {
      String currentPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()
          .getPath().replace('/', File.separator.charAt(0)).substring(1);

      // check if enough heap space is awailable
      if (args.length == 0 && Runtime.getRuntime().maxMemory() < 1024 * 1024 * 1024) {
        System.out.println("Set Heapspace");
        Process p = Runtime.getRuntime().exec("java -jar -Xmx1024M " + currentPath + " restart");
        p.waitFor();
        return;
      }
    } catch (URISyntaxException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (Runtime.getRuntime().maxMemory() < 1024 * 1024 * 1024) {
       GUIUtils.showInfo(AlertType.ERROR, "Heap space error", "Maximum heap space capacity is low, this might cause malfunction of the chromatogram window. Please run the program with the command" + System.lineSeparator() + "java -jar -Xmx1024M PATHTO-GSAT.JAR" + System.lineSeparator() + " or increase the java heap space");
    }

    System.out.println((Runtime.getRuntime().maxMemory() / (1024 * 1024)) + "MB Awailable");

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
      javafx.application.Application.launch(MainWindow.class);
    }

  }

}
