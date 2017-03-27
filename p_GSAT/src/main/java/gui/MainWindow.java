package gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.Pair;
import io.ConfigHandler;
import io.FileSaver;
import io.ProblematicComment;
import io.SequenceReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This window represent the start window and replace the functionality of the console version. It
 * can start analysis.
 * 
 * @see GUIUtils
 * @category GUI.Window
 * 
 * @author jannis blueml, Kevin Otto
 *
 */
public class MainWindow extends Application implements javafx.fxml.Initializable {

  /**
   * Boolean that controls that no more then one SettingsWindow is open at the same time.
   */
  static boolean settingsOpen = false;

  /**
   * FindRightGene yes or no.
   */
  static boolean autoGeneSearch = false;

  /**
   * Selected gene in dropdown.
   */
  static Gene dropdownGene;

  /**
   * All selected files.
   */
  private static Pair<LinkedList<File>, LinkedList<File>> files;

  /**
   * Sequenced from selected files.
   */
  private static LinkedList<AnalysedSequence> sequences = new LinkedList<>();

  // Warnings by closing without saving

  /**
   * Were there any changes on the genes, i.e. were new genes added?
   */
  public static boolean changesOnGenes = false;

  /**
   * Were there any changes on the primers, i.e. were new primers added?
   */
  public static boolean changesOnPrimers = false;

  /**
   * Were there any changes on the results, i.e. where new analysis results created?
   */
  public static boolean changesOnResults = false;

  /**
   * ProgressBar to show Progress of the Analysis.
   */
  @FXML
  private ProgressBar bar;

  // BUTTONS
  /**
   * Button to open the path where the results were saved.
   */
  @FXML
  private Button openResFile;

  /**
   * Button to open the DatabaseConnection window.
   */
  @FXML
  private Button databaseButton;

  /**
   * Button to specify the destination folder for the analysis results. (opens a Folder Chooser)
   */
  @FXML
  private Button destButton;

  /**
   * Button to start the Analysis.
   */
  @FXML
  private Button startButton;

  /**
   * Button to open the Settings Window.
   */
  @FXML
  private Button settingsButton;

  /**
   * Button to specify the Files to be Analyisd (opens a selector wich opens a File or Folder
   * Chooser).
   */
  @FXML
  private Button srcButton;

  /**
   * Button to open the About Window.
   */
  @FXML
  private Button aboutButton;

  /**
   * Button to open the User Manual.
   */
  @FXML
  private Button manualButton;

  /**
   * Button to open the showChromatogramWindow.
   */
  @FXML
  private Button chromatogramButton;

  /**
   * Button to open the Destination Dolder in explorer.
   */
  @FXML
  private Button openDest;

  /**
   * Button to open Source Folder in explorer.
   */
  @FXML
  private Button openSrc;

  // Textfields
  /**
   * TextField to enter and display selected Source folder.
   */
  @FXML
  private TextField srcField;

  /**
   * TextField to enter and display selected destination folder.
   */
  @FXML
  private TextField destField;

  /**
   * TextField to enter a alternative Filename for the output file.
   */
  @FXML
  private TextField fileNameField;

  // dropdownMenu
  /**
   * ChoiceBox to select the gene that should be used for the analysis.
   */
  @FXML
  private ChoiceBox<String> geneBox;

  // info output area
  /**
   * TextFlow to display status messages of the program during usage.
   */
  @FXML
  private TextFlow infoArea;

  // checkbox
  /**
   * Checkbox to specify if one ore multiple files should be saved.
   */
  @FXML
  private CheckBox outputCheckbox;

  /**
   * CheckBox to specify if gene should be selected automatically.
   */
  @FXML
  private CheckBox findGeneCheckbox;

  /**
   * scrollPane that makes the infoarea scrollable.
   * 
   * @see #infoArea
   */
  @FXML
  private ScrollPane textScroll;

  /**
   * stage of this Window.
   */
  Stage primaryStage;



  /**
   * initialize all components and set Eventhandlers.
   * 
   * @param arg0 the URL to init, more information at {@link Initializable}
   * @param arg1 a ResourceBunde, for more informations see {@link Initializable}
   * 
   * @see Initializable
   * @author jannis blueml
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

    // set default values and set some configurations
    bar.setProgress(0);
    bar.setStyle("-fx-accent: rgb(130, 177, 255);");
    FileSaver.setSeparateFiles(false);
    GUIUtils.setColorOnButton(startButton, ButtonColor.BLUE);
    GUIUtils.setColorOnButton(settingsButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(manualButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(aboutButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(databaseButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(chromatogramButton, ButtonColor.GRAY);


    infoArea.getChildren().addListener((ListChangeListener<Node>) ((change) -> {
      infoArea.layout();
      textScroll.layout();
      textScroll.setVvalue(1.0f);
    }));

    textScroll.setContent(infoArea);

    infoArea.setTranslateX(3);
    infoArea.setTranslateY(3);

    // exclude separator
    destField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
          destField.setText(oldValue);
        } else {
          destField.setText(newValue);
        }
      }
    });

    // checks if config.txt has a default path. Set path if possible
    if (!ConfigHandler.getSrcPath().isEmpty()) {
      srcField.setText(ConfigHandler.getSrcPath());
      chromatogramButton.setDisable(false);
      files = GUIUtils.getSequencesFromSourceFolder(srcField.getText());

      // read files
      if (files.first != null) {
        for (File file : files.first) {
          try {
            sequences.add(SequenceReader.convertFileIntoSequence(file));
          } catch (Throwable e) {
            infoArea.getChildren().add(GUIUtils.getRedText("Reading error with " + file.getName()));
          }
        }
      }
    }

    // read source path
    srcField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
          srcField.setText(oldValue);
        } else {
          srcField.setText(newValue);
          if (newValue.isEmpty()) {
            chromatogramButton.setDisable(true);
          } else {
            chromatogramButton.setDisable(false);

            if (new File(srcField.getText()).exists()) {

              files = GUIUtils.getSequencesFromSourceFolder(srcField.getText());
              
              if (files.first != null) {
              for (File file : files.first) {
                try {
                  sequences.add(SequenceReader.convertFileIntoSequence(file));
                } catch (Throwable e) {
                  infoArea.getChildren()
                      .add(GUIUtils.getRedText("Reading error with " + file.getName()));
                }
              }
            }
              }
            
          }
        }
      }
    });

    // exclude some separator for files and database
    fileNameField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches(".*[" + ConfigHandler.SEPARATOR_CHAR + "/\\\\].*")) {
          fileNameField.setText(oldValue);
        } else {
          fileNameField.setText(newValue);
        }
      }
    });
    geneBox.setStyle("-fx-font-style: italic;");

    // read Genes and show them in the choicebox

    Text output = GUIUtils.initializeGeneBox(geneBox);

    infoArea.getChildren().add(output);

    // init geneBox by mouseclick
    geneBox.setOnMouseClicked(arg01 -> GUIUtils.initializeGeneBox(geneBox));

    // gives information about new gene selection
    geneBox.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          if (newValue != null) {
            infoArea.getChildren().add(new Text("New gene selected: " + newValue + "\n"));
          }
        });

    // set button to select destination
    destButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        Text output;
        output = GUIUtils.setDestination(destField, srcField.getText());
        infoArea.getChildren().add(output);
      }
    });

    // add Action to open directory to destination
    openDest.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {

        if (destField.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "No path specified",
              "Please enter the path of a destination folder first.");
          return;
        }

        try {
          Desktop.getDesktop().open(new File(destField.getText()));
        } catch (Throwable e) {
          GUIUtils.showInfo(AlertType.ERROR, "Opening failed",
              "Could not open destination folder.");
          return;
        }
      }
    });

    // add Action to open directory to source
    openSrc.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {

        if (srcField.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "No path specified",
              "Please enter the path of a source file or a folder first.");
          return;
        }

        try {
          Desktop.getDesktop().open(new File(srcField.getText()));
        } catch (Throwable e) {
          GUIUtils.showInfo(AlertType.ERROR, "Opening failed",
              "Could not open source file or folder.");
        }
      }
    });

    // extend openDest with the result file given by the field
    openResFile.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        if (destField.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "No path specified",
              "Please enter the path of a destination folder first.");
          return;
        }

        File toOpen;
        if (fileNameField.getText().isEmpty()) {
          toOpen = new File(destField.getText() + File.separatorChar + "gsat_results.csv");
        } else {
          toOpen =
              new File(destField.getText() + File.separatorChar + fileNameField.getText() + ".csv");
        }

        if (!toOpen.exists()) {
          String name;
          if (fileNameField.getText().isEmpty()) {
            name = "gsat_results.csv";
          } else {
            name = fileNameField.getText() + ".csv";
          }
          GUIUtils.showInfo(AlertType.ERROR, "No analysis result",
              "No file exists with the name '" + name + "' at the given destination path.");
          return;
        }

        try {
          Desktop.getDesktop().open(toOpen);

        } catch (Throwable e) {
          GUIUtils.showInfo(AlertType.ERROR, "Opening failed", "Could not open destination file.");
        }
      }
    });



    // select if you get only one output file
    outputCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      FileSaver.setSeparateFiles(newValue);
      if (newValue) {
        infoArea.getChildren().add(new Text("One single output file will be created. \n"));
      } else {
        infoArea.getChildren()
            .add(new Text("There will be one output file for each input file. \n"));
      }
    });

    // set button to select source files
    srcButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        Text output;
        output = GUIUtils.setSourceFolder(srcField).second;
        infoArea.getChildren().add(output);
      }
    });

    // start analyzing process
    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {


        // reset Filesaver to delete possible old data
        FileSaver.reset();

        // set some text in the infoBox and make comment, also check if necessary information is
        // given
        infoArea.getChildren().add(new Text(
            "--------------------------------------------------------------------------------------"
                + "\nStarting analysis\n"
                + "---------------------------------------------------------------------------\n"));
        infoArea.getChildren()
            .add(new Text("Source folder or file:  " + srcField.getText() + "\n"));

        if (srcField.getText().equals("")) {
          infoArea.getChildren()
              .add(GUIUtils.getRedText("Source path is empty, aborting analysis.\n"));
          return;
        }

        infoArea.getChildren().add(new Text("Destination folder:  " + destField.getText() + "\n"));

        if (destField.getText().equals("")) {
          infoArea.getChildren()
              .add(GUIUtils.getRedText("Destination path is empty, aborting analysis.\n"));
          // bar.setProgress(0);
          return;
        } else {
          FileSaver.setLocalPath(destField.getText());
        }

        if (autoGeneSearch) {
          infoArea.getChildren().add(new Text("Selected gene: automatic search\n"));
        } else {
          infoArea.getChildren().add(
              new Text("Selected gene:  " + geneBox.getSelectionModel().getSelectedItem() + "\n"));
        }

        // check if solution should be a single file
        if (outputCheckbox.selectedProperty().get()) {
          FileSaver.setSeparateFiles(true);
        } else {
          FileSaver.setSeparateFiles(false);
        }

        // checks gene and if empty, give a solution with alert dialog
        if (geneBox.getSelectionModel().getSelectedIndex() == -1) {
          if (!autoGeneSearch) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("No gene was selected!");
            alert.setContentText("Find best fitting gene automatically?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
              return;
            }
          }
        }
        infoArea.getChildren().add(new Text(
            "---------------------------------------------------------------------------------\n"));
        // end of report

        // start of process in seperate task for the progressbar
        javafx.concurrent.Task<Void> mainTask = new javafx.concurrent.Task<Void>() {

          @Override
          protected Void call() throws Exception {

            // set necessary Strings like destination, source, gene, ...
            String destfileNameText = fileNameField.getText();
            String geneBoxItem;
            if (autoGeneSearch) {
              geneBoxItem = "-1";
            } else {
              geneBoxItem = geneBox.getSelectionModel().getSelectedItem().split(" ")[0];
            }
            
            //DISABLE ALL INPUTS
            callSetDisableOfManyComponents(true);     
            
            sequences = new LinkedList<AnalysedSequence>();
            AnalysedSequence sequence;

            if (files.first == null) {
              files.first = new LinkedList<File>();
            }
            
            // convert files into sequences and analyse them
            
            for (File file : files.first) {
              try {
                sequence = SequenceReader.convertFileIntoSequence(file);
              } catch (Throwable e) {
                sequence = new AnalysedSequence();
                sequence.setFileName(file.getName());
                sequence.addProblematicComment(ProblematicComment.COULD_NOT_READ_SEQUENCE);
              }
              sequences.add(sequence);
            }
            LinkedList<Text> resultingLines =
                GUIUtils.runAnalysis(sequences, geneBoxItem, destfileNameText, bar);
            Platform.runLater(new Runnable() {

              @Override
              public void run() {
                infoArea.getChildren().addAll(resultingLines);
                callSetDisableOfManyComponents(false);     
              }

            });
            return null;
          }

        };

        // if successful reset bar and add sequences to local list
        mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
          @Override
          public void handle(WorkerStateEvent t) {
            bar.setProgress(0);
            changesOnResults = true;
            sequences = new LinkedList<AnalysedSequence>();
            for (File file : files.first) {
              try {
                sequences.add(SequenceReader.convertFileIntoSequence(file));
              } catch (Throwable e) {
                infoArea.getChildren()
                    .add(GUIUtils.getRedText("Reading error with " + file.getName()));
              }
            }
          }
        });
        new Thread(mainTask).start();

      }

    });

    // select if you get only one output file
    findGeneCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      FileSaver.setSeparateFiles(newValue);
      if (newValue) {
        autoGeneSearch = true;
        geneBox.getSelectionModel().clearSelection();
        infoArea.getChildren().add(new Text("The gene that fits best will be searched.\n"));
      } else {
        autoGeneSearch = false;
        infoArea.getChildren().add(new Text("No automatic gene search.\n"));
      }
    });

    // set settings button to open settings window
    settingsButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        // only open window if no Settings Window is open
        if (!settingsOpen) {
          settingsOpen = true;
          SettingsWindow settings = new SettingsWindow();
          try {
            settings.start(new Stage());
          } catch (Exception e) {
            GUIUtils.showInfo(AlertType.ERROR, "Could not open main window",
                "The main window could not be opened. Please try again.");
          }
        }
      }
    });

    // set settings button to open settings window
    aboutButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        // TextWindow textWin = new TextWindow();
        try {
          final FXMLLoader loader =
              new FXMLLoader(TextWindow.class.getResource("/fxml/TextWindow.fxml"));

          final Parent root = loader.load();

          TextWindow texWin = loader.<TextWindow>getController();
          String content = GUIUtils
              .convertStreamToString(ClassLoader.getSystemResourceAsStream("manual/About.txt"));
          texWin.setText(content);

          Scene scene = new Scene(root);
          Stage s = new Stage();
          s.setScene(scene);
          s.sizeToScene();
          s.setTitle("GSAT - About");
          s.show();

        } catch (IOException e) {
          return;
        }
      }
    });

    manualButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        try {
          final FXMLLoader loader =
              new FXMLLoader(TextWindow.class.getResource("/fxml/TextWindow.fxml"));

          final Parent root = loader.load();

          TextWindow texWin = loader.<TextWindow>getController();
          String content = GUIUtils.convertStreamToString(
              ClassLoader.getSystemResourceAsStream("manual/WelcomeToGSAT.txt"));
          texWin.setText(content);
          Scene scene = new Scene(root);
          Stage s = new Stage();
          s.setScene(scene);
          s.sizeToScene();
          s.setTitle("GSAT - Help");
          s.show();

        } catch (IOException e) {
          return;
        }
      }
    });

    // set Database button to open Database window
    databaseButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        // only open window if no Settings Window is open
        if (!settingsOpen) {
          settingsOpen = true;
          DatabaseWindow base = new DatabaseWindow();
          try {
            base.start(new Stage());
          } catch (Exception e) {
            GUIUtils.showInfo(AlertType.ERROR, "Could not open database window",
                "The database window could not be opened. Please try again.");
          }
        }
      }
    });

    // set Database button to open Database window
    chromatogramButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        ShowChromatogramWindow chromaWindow = new ShowChromatogramWindow();
        try {
          dropdownGene =
              GUIUtils.getGeneFromDropDown(geneBox.getSelectionModel().getSelectedItem());
          chromaWindow.start(new Stage());

          chromaWindow.setSequences(sequences);
        } catch (Exception e) {
          GUIUtils.showInfo(AlertType.ERROR, "Chromatogram window error",
              "The chromatogram window could not be opened.");
        }
      }
    });

    // IT IS ESENTIAL THAT THIS IS EXECUTED LAST!
    // ALWAYS MOVE THIS TO THE END OF THE INIT BLOCK, ELSE CRAZY STUFF WILL HAPPEN
    Text introText = new Text("Welcome to GSAT!" + System.lineSeparator());
    infoArea.getChildren().add(introText);
  }


  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
    } catch (IOException e) {
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();

    // on closeRequest control if there were any changes and if, then give an alert with the option
    // to save changes in database.
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        if (changesOnGenes || changesOnPrimers || changesOnResults) {
          Alert alertWindow = new Alert(AlertType.CONFIRMATION);
          alertWindow.setTitle("Warning: changes");
          alertWindow.setHeaderText("Changes habe been made with ");
          if (changesOnGenes) {
            alertWindow.setHeaderText(alertWindow.getHeaderText() + "genes,");
          }
          if (changesOnPrimers) {
            alertWindow.setHeaderText(alertWindow.getHeaderText() + "primers,");
          }
          if (changesOnResults) {
            alertWindow.setHeaderText(alertWindow.getHeaderText() + "results,");
          }
          alertWindow.setHeaderText(
              alertWindow.getHeaderText().substring(0, alertWindow.getHeaderText().length() - 1)
                  + ".");

          alertWindow.setHeaderText(alertWindow.getHeaderText() + System.lineSeparator()
              + "Do you want to upload new data?");

          ButtonType save = new ButtonType("Open database window");
          ButtonType dontSave = new ButtonType("Close");
          ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

          alertWindow.getButtonTypes().setAll(save, dontSave, buttonTypeCancel);

          ButtonBar buttonBar = (ButtonBar) alertWindow.getDialogPane().lookup(".button-bar");
          ObservableList<Node> nodes = buttonBar.getButtons();
          GUIUtils.setColorOnButton((Button) nodes.get(0), ButtonColor.GREEN);
          GUIUtils.setColorOnButton((Button) nodes.get(1), ButtonColor.RED);
          GUIUtils.setColorOnButton((Button) nodes.get(2), ButtonColor.GRAY);

          Optional<ButtonType> result = alertWindow.showAndWait();
          if (result.get() == save) {
            DatabaseWindow databaseWindow = new DatabaseWindow();
            event.consume();
            try {
              databaseWindow.start(new Stage());
            } catch (Exception e) {
              GUIUtils.showInfo(AlertType.ERROR, "Could not open database window",
                  "The database window could not be opened. Please try again.");
            }
          } else if (result.get() == buttonTypeCancel) {
            event.consume();
          }
        }
      }
    });

  }
  
  /**
   * Disables or enables all critical fields.
   * 
   * @param status the disabled status to set
   * 
   * @author Kevin Otto
   */
  private void callSetDisableOfManyComponents(boolean status) {
    
    openResFile.setDisable(status);
    databaseButton.setDisable(status);
    destButton.setDisable(status); 
    startButton.setDisable(status);    
    settingsButton.setDisable(status);
    srcButton.setDisable(status);
    chromatogramButton.setDisable(status);
    openDest.setDisable(status);
    openSrc.setDisable(status);
    srcField.setDisable(status);
    destField.setDisable(status);
    fileNameField.setDisable(status);
    geneBox.setDisable(status);
    outputCheckbox.setDisable(status);
    findGeneCheckbox.setDisable(status);
  }

}
