package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

  private static BufferedReader error;
  private static BufferedReader op;
  private static int exitVal;


  public static void jarrunner(String jarFilePath, String args) throws Throwable {
    // Create run arguments for the

    final List<String> actualArgs = new ArrayList<String>();
    actualArgs.add(0, "java");
    actualArgs.add(1, "-jar");
    actualArgs.add(2, jarFilePath);
    actualArgs.add(args);
    try {
      final Runtime re = Runtime.getRuntime();
      // final Process command = re.exec(cmdString, args.toArray(new String[0]));
      final Process command = re.exec(actualArgs.toArray(new String[0]));
      error = new BufferedReader(new InputStreamReader(command.getErrorStream()));
      op = new BufferedReader(new InputStreamReader(command.getInputStream()));
      // Wait for the application to Finish
      command.waitFor();
      /*exitVal = command.exitValue();
      if (exitVal != 0) {
        throw new IOException("Failed to execure jar, " + getExecutionLog());
      }*/

    } catch (Throwable e) {
      throw e;
    }
  }

  public static String getJarContainingFolder(Class aclass) throws Exception {
    CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

    File jarFile;

    if (codeSource.getLocation() != null) {
      jarFile = new File(codeSource.getLocation().toURI());
    } else {
      String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
      String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
      jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
      jarFile = new File(jarFilePath);
    }
    return jarFile.getParentFile().getAbsolutePath();
  }

/*  public static String getExecutionLog() {
    String errorString = "";
    String lineString;
    try {
      while ((lineString = error.readLine()) != null) {
        errorString = errorString + "\n" + lineString;
      }
    } catch (final IOException e) {}
    String output = "";
    try {
      while ((lineString = op.readLine()) != null) {
        output = output + "\n" + lineString;
      }
    } catch (final IOException e) {}
    try {
      error.close();
      op.close();
    } catch (final IOException e) {}
    return "exitVal: " + exitVal + ", error: " + error + ", output: " + output;
  }*/


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

    System.out.println(Runtime.getRuntime().maxMemory() );
    
    if (args.length == 0 && Runtime.getRuntime().maxMemory()/  (1024 * 1024 * 1024) <  1) {
      System.out.println("try realocating heap space");
      try {
        String path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
        System.out.println("starting with more heap");
        jarrunner(path, "-Xmx1024M");
        System.exit(0);
      } catch (Throwable e1) {        
        e1.printStackTrace();
        System.err.println("fallbackMode");
      }
    }else if(args.length > 0) {
      System.out.println(args[0]);
    }

    // **********************
    System.err.println("end of catchclause");
    if (Runtime.getRuntime().maxMemory() < 1024 * 1024 * 1024 *10) {
      GUIUtils.showInfo(AlertType.ERROR, "Heap space error",
          "Maximum heap space capacity is low, this might cause malfunction of the chromatogram window. Please run the program with the command"
              + System.lineSeparator() + "java -jar -Xmx1024M PATHTO-GSAT.JAR"
              + System.lineSeparator() + " or increase the java heap space");
    }
    System.err.println("begin of normal program");


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
