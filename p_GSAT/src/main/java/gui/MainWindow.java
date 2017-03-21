package gui;

import java.awt.Desktop;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

import analysis.AnalysedSequence;
import analysis.Pair;
import exceptions.ConfigNotFoundException;
import exceptions.FileReadingException;
import exceptions.MissingPathException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;
import io.FileSaver;
import io.GeneHandler;
import io.PrimerHandler;
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

public class MainWindow extends Application implements javafx.fxml.Initializable {

  public static boolean settingsOpen = false;
  public static boolean autoGeneSearch = false;

  private static Pair<LinkedList<File>, LinkedList<File>> files;
  private static LinkedList<AnalysedSequence> sequences = new LinkedList<>();

  // Warnings by closing without saving
  public static boolean changesOnGenes = false;
  public static boolean changesOnPrimers = false;
  public static boolean changesOnResults = false;

  // WARNING: Do not change variable name under all circumstances!

  @FXML
  private ProgressBar bar;

  // BUTTONS
  @FXML
  private Button openResFile;
  @FXML
  private Button databaseButton;
  @FXML
  private Button destButton;
  @FXML
  private Button startButton;
  @FXML
  private Button settingsButton;
  @FXML
  private Button srcButton;
  @FXML
  private Button aboutButton;
  @FXML
  private Button manualButton;
  @FXML
  private Button chromatogramButton;

  // Textfields
  @FXML
  private TextField srcField;
  @FXML
  private TextField destField;

  @FXML
  private TextField fileNameField;

  // dropdownMenu
  @FXML
  private ChoiceBox<String> geneBox;

  // info output area
  @FXML
  private TextFlow infoArea;

  // checkbox
  @FXML
  private CheckBox outputCheckbox;

  @FXML
  private CheckBox findGeneCheckbox;

  @FXML
  private ScrollPane textScroll;

  @FXML
  private Button openDest;

  @FXML
  private Button openSrc;

  Stage primaryStage;


  /**
   * Mainwindow to initialize all components and set Eventhandlers.
   * 
   * @see Initializable
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
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

    if (!ConfigHandler.getSrcPath().isEmpty()) {
      srcField.setText(ConfigHandler.getSrcPath());
      chromatogramButton.setDisable(false);
      files = GUIUtils.getSequencesFromSourceFolder(srcField.getText());

      for (File file : files.first) {
        try {
          sequences.add(SequenceReader.convertFileIntoSequence(file));
        } catch (FileReadingException | IOException | MissingPathException e) {
          e.printStackTrace();
        }
      }
    }
    
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
  
              for (File file : files.first) {
                try {
                  sequences.add(SequenceReader.convertFileIntoSequence(file));
                } catch (FileReadingException | IOException | MissingPathException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        }
      }
    });

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

    infoArea.getChildren().add(new Text("Welcome to GSAT! \n"));
    // read Genes and show them in the choicebox

    Text output = GUIUtils.initializeGeneBox(geneBox);
    geneBox.setStyle("-fx-font-style: italic;");

    infoArea.getChildren().add(output);

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


    openDest.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        
        if (destField.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "No path specified", "Please enter the path of a destination folder first.");
          return;
        }
        
        try {
          Desktop.getDesktop().open(new File(destField.getText()));
        } catch (IllegalArgumentException | IOException e) {
            GUIUtils.showInfo(AlertType.ERROR, "Opening failed", "Could not open destination folder.");
        }
      }
    });


    openSrc.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        
        if (srcField.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "No path specified", "Please enter the path of a source file or a folder first.");
          return;
        }
        
        try {
          Desktop.getDesktop().open(new File(srcField.getText()));
        } catch (IllegalArgumentException | IOException e) {
          GUIUtils.showInfo(AlertType.ERROR, "Opening failed", "Could not open source file or folder.");
        }
      }
    });

    
    openResFile.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        
        if (destField.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "No path specified", "Please enter the path of a destination folder first.");
          return;
        }
          
          File toOpen;
          
          if (fileNameField.getText().isEmpty()) {
            toOpen = new File(destField.getText() + File.separatorChar + "gsat_results.csv");
          } else {
            toOpen = new File(destField.getText() + File.separatorChar + fileNameField.getText() + ".csv");
          }
          
          if (!toOpen.exists()) {
            GUIUtils.showInfo(AlertType.ERROR, "No analysis result", "No file exists with the name '" + (fileNameField.getText().isEmpty() ? "gsat_results.csv" : fileNameField.getText() + ".csv")  + "' at the given destination path.");
            return;
          }
          
        try {
          Desktop.getDesktop().open(toOpen);
          
        } catch (IllegalArgumentException | IOException e) {
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
        infoArea.getChildren().add(new Text(
            "--------------------------------------------------------------------------------------"
                + "\nStarting analysis\n"
                + "---------------------------------------------------------------------------\n"));

        FileSaver.reset();
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
          infoArea.getChildren().add(new Text("Selected gene:  automatic search\n"));
        } else {
          infoArea.getChildren().add(
              new Text("Selected gene:  " + geneBox.getSelectionModel().getSelectedItem() + "\n"));
        }

        if (outputCheckbox.selectedProperty().get()) {
          FileSaver.setSeparateFiles(true);
        } else {
          FileSaver.setSeparateFiles(false);
        }

        if (geneBox.getSelectionModel().getSelectedIndex() == -1) {
          if (!autoGeneSearch) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("No gene was selected!");
            alert.setContentText("Find gene automatically?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
              return;
            }
          }
        }
        infoArea.getChildren().add(new Text(
            "---------------------------------------------------------------------------------\n"));

        javafx.concurrent.Task<Void> mainTask = new javafx.concurrent.Task<Void>() {

          @Override
          protected Void call() throws Exception {

            String destfileNameText = fileNameField.getText();
            String geneBoxItem;
            if (autoGeneSearch) {
              geneBoxItem = "-1";
            } else {
              geneBoxItem = geneBox.getSelectionModel().getSelectedItem().split(" ")[0];
            }
            startButton.setDisable(true);
            LinkedList<Text> resultingLines =
                GUIUtils.runAnalysis(sequences, geneBoxItem, destfileNameText, bar);
            Platform.runLater(new Runnable() {

              @Override
              public void run() {
                infoArea.getChildren().addAll(resultingLines);
                startButton.setDisable(false);
              }

            });
            return null;
          }

        };

        // bar.setProgress(-1);
        // mainThread.start();
        mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
          @Override
          public void handle(WorkerStateEvent t) {
            bar.setProgress(0);
            changesOnResults = true;
            sequences = new LinkedList<AnalysedSequence>();
            for (File file : files.first) {
              try {
                sequences.add(SequenceReader.convertFileIntoSequence(file));
              } catch (FileReadingException | IOException | MissingPathException e) {
                e.printStackTrace();
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
            // TODO Auto-generated catch block
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
          e.printStackTrace();
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
          e.printStackTrace();
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
            // TODO Auto-generated catch block
          }
        }
      }
    });

    // set Database button to open Database window
    chromatogramButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        ShowChromatogram chromaWindow = new ShowChromatogram();
        try {
          chromaWindow.start(new Stage());

          chromaWindow.setSequences(sequences);
        } catch (Exception e) {
          GUIUtils.showInfo(AlertType.ERROR, "Chromatogram window error", "The chromatogram window could not be opened.");
        }
      }
    });

  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();

    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        if (changesOnGenes || changesOnPrimers || changesOnResults) {
          Alert alertWindow = new Alert(AlertType.CONFIRMATION);
          alertWindow.setTitle("Warning: Changes not uploaded to database");
          alertWindow.setHeaderText("There are changes with ");
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
              alertWindow.getHeaderText().substring(0, alertWindow.getHeaderText().length() - 1));

          ButtonType save = new ButtonType("Upload changes");
          ButtonType dontSave = new ButtonType("Close without upload");
          ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

          alertWindow.getButtonTypes().setAll(save, dontSave, buttonTypeCancel);

          ButtonBar buttonBar = (ButtonBar) alertWindow.getDialogPane().lookup(".button-bar");
          ObservableList<Node> nodes = buttonBar.getButtons();
          GUIUtils.setColorOnButton((Button) nodes.get(0), ButtonColor.GREEN);
          GUIUtils.setColorOnButton((Button) nodes.get(1), ButtonColor.RED);
          GUIUtils.setColorOnButton((Button) nodes.get(2), ButtonColor.GRAY);

          Optional<ButtonType> result = alertWindow.showAndWait();
          if (result.get() == save) {
            DatabaseWindow base = new DatabaseWindow();
            event.consume();
            try {
              base.start(new Stage());
            } catch (Exception e) {
              // TODO Auto-generated catch block
            }
          } else if (result.get() == dontSave) {
            System.out.println("dont save");
            // TODO
          } else {
            event.consume();
          }
        }
      }
    });

  }

}
