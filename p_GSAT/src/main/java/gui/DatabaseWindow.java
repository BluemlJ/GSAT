package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

import analysis.AnalysedSequence;
import exceptions.ConfigNotFoundException;
import exceptions.DatabaseConnectionException;
import exceptions.MissingPathException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;
import io.DatabaseConnection;
import io.FileRetriever;
import io.FileSaver;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

/**
 * Main interface for database connection. Used to push and pull data from database.
 * 
 * @author Lovis Heindrich
 *
 */
public class DatabaseWindow extends Application implements javafx.fxml.Initializable {

  /**
   * Scene object.
   */
  private Scene scene;

  /**
   * Boolean that specifies if upload or download is requested.
   */
  private boolean upload = true;

  /**
   * Toggle button to upload data.
   */
  @FXML
  private ToggleButton uploadToggle;

  /**
   * Toggle button to download data.
   */
  @FXML
  private ToggleButton downloadToggle;

  /**
   * Toggle button to upload/download results. This includes sequences and the referenced genes and
   * researchers.
   */
  @FXML
  private ToggleButton resultToggle;

  /**
   * Toggle button to upload/download genes.
   */
  @FXML
  private ToggleButton geneToggle;

  /**
   * Toggle button to upload/download primer.
   */
  @FXML
  private ToggleButton primerToggle;

  /**
   * Toggle button to upload/download all data, this includes all sequences, genes and primers.
   */
  @FXML
  private ToggleButton allToggle;

  /**
   * Text field where a path can be entered.
   */
  @FXML
  private javafx.scene.control.TextField destField;

  /**
   * Button to set the destField with a file chooser.
   */
  @FXML
  private Button destButton;

  /**
   * Button to open the database settings.
   */
  @FXML
  private Button settingsButton;

  /**
   * Button to start upload/download process.
   */
  @FXML
  private Button startButton;

  /**
   * Textfield to enter a researcher name to specify download requests.
   */
  @FXML
  private TextField researcherField;

  /**
   * Textfield to enter a gene name to specify download requests.
   */
  @FXML
  private TextField geneField;

  /**
   * Datepicker to select a start date. This is used to only download data which was created after
   * this date.
   */
  @FXML
  private DatePicker startDate;

  /**
   * Datepicker to select a end date. This is used to only download data which was created before
   * this date.
   */
  @FXML
  private DatePicker endDate;

  /**
   * Toggle group containing results, genes, primers and all toggle buttons.
   */
  private ToggleGroup typeGroup;

  /**
   * Toggle group containing upload and download toggles.
   */
  private ToggleGroup usageGroup;

  /**
   * String containing a message which will be shown when the upload failed.
   */
  private static final String UPLOAD_FAIL = "Upload to database failed.";

  /**
   * String containing a message which will be shown when the download failed.
   */
  private static final String DOWNLOAD_FAIL = "Download from database failed";

  /**
   * String containing a message which will be shown when a local file could not be written.
   */
  private static final String WRITE_FAIL = "Writing local file failed.";

  /**
   * String containing a message which will be shown when a local file could not be read.
   */
  private static final String READ_FAIL = "Reading local file failed.";

  /**
   * String containing a message which will be shown when the upload succeeded.
   */
  private static final String UPLOAD_SUCCESS = "Upload to database complete.";

  /**
   * String containing a message which will be shown when the download succeeded.
   */
  private static final String DOWNLOAD_SUCCESS = "Download from database complete.";

  /**
   * Initializes all window components and sets button actions.
   * 
   * @param arg0 the URL to init, more information at {@link Initializable}
   * @param arg1 a ResourceBunde, for more informations see {@link Initializable}
   * @author Lovis Heindrich
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    // set calender to german standard
    startDate.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate date) {
        if (date != null) {
          try {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
          } catch (DateTimeException dte) {
            System.out.println("Format Error");
          }
        }
        return "";
      }

      @Override
      public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
          try {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
          } catch (DateTimeParseException dtpe) {
            System.out.println("Parse Error");
          }
        }
        return null;
      }
    });

    endDate.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate date) {
        if (date != null) {
          try {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
          } catch (DateTimeException dte) {
            System.out.println("Format Error");
          }
        }
        return "";
      }

      @Override
      public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
          try {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
          } catch (DateTimeParseException dtpe) {
            System.out.println("Parse Error");
          }
        }
        return null;
      }
    });

    try {
      ConfigHandler.readConfig();
      DatabaseConnection.setDatabaseConnection(ConfigHandler.getDbUser(), ConfigHandler.getDbPass(),
          ConfigHandler.getDbPort(), ConfigHandler.getDbUrl());
    } catch (UnknownConfigFieldException | ConfigNotFoundException | IOException e1) {
      GUIUtils.showInfo(AlertType.ERROR, "Failed", "Failure while reading config file");
    } catch (DatabaseConnectionException | SQLException e) {
      GUIUtils.showInfo(AlertType.ERROR, "Failed", "Failure while connecting to database");
    }

    typeGroup = new ToggleGroup();
    resultToggle.setToggleGroup(typeGroup);
    geneToggle.setToggleGroup(typeGroup);
    primerToggle.setToggleGroup(typeGroup);
    allToggle.setToggleGroup(typeGroup);

    activateOnlyPath();

    resultToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (resultToggle.equals(typeGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
        // upload only path value is needed
        if (uploadToggle.isSelected()) {
          activateOnlyPath();
        } else if (downloadToggle.isSelected()) {
          activateEverything();
        }
      }
    });

    geneToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (geneToggle.equals(typeGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
        // no additional user input needed
        activateNothing();
      }
    });

    primerToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (primerToggle.equals(typeGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
        // no additional user input needed
        activateNothing();
      }
    });

    allToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (allToggle.equals(typeGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
        if (downloadToggle.isSelected()) {
          activateEverything();
        } else if (uploadToggle.isSelected()) {
          activateOnlyPath();
        }
      }
    });

    usageGroup = new ToggleGroup();
    uploadToggle.setToggleGroup(usageGroup);
    downloadToggle.setToggleGroup(usageGroup);

    uploadToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (uploadToggle.equals(usageGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
        if (primerToggle.isSelected() || geneToggle.isSelected()) {
          activateNothing();
        } else if (resultToggle.isSelected() || allToggle.isSelected()) {
          activateOnlyPath();
        }
      }
    });

    downloadToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (downloadToggle.equals(usageGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
        if (primerToggle.isSelected() || geneToggle.isSelected()) {
          activateNothing();
        } else if (resultToggle.isSelected() || allToggle.isSelected()) {
          activateEverything();
        }
      }
    });

    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {

        // update primer data
        if (primerToggle.isSelected()) {

          // upload data from primer.txt
          if (uploadToggle.isSelected()) {
            try {
              DatabaseConnection.pushAllPrimer();
              MainWindow.changesOnPrimers = false;
              GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", UPLOAD_SUCCESS);
            } catch (DatabaseConnectionException | SQLException e) {
              // error while connecting to database
              GUIUtils.showInfo(AlertType.ERROR, "Error", UPLOAD_FAIL);
            } catch (NumberFormatException | IOException e) {
              // error while writing txt
              GUIUtils.showInfo(AlertType.ERROR, "Error", READ_FAIL);
            }

            // download data to primer.txt
          } else if (downloadToggle.isSelected()) {
            try {
              DatabaseConnection.pullAndSavePrimer();
              GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", DOWNLOAD_SUCCESS);
            } catch (DatabaseConnectionException | SQLException e) {
              // error while connecting to database
              GUIUtils.showInfo(AlertType.ERROR, "Error", DOWNLOAD_FAIL);
            } catch (NumberFormatException | IOException e) {
              // error while writing txt
              GUIUtils.showInfo(AlertType.ERROR, "Error", WRITE_FAIL);
            }
          }

          // update gene data
        } else if (geneToggle.isSelected()) {
          // upload all data from genes.txt
          if (uploadToggle.isSelected()) {
            try {
              DatabaseConnection.pushAllGenes();
              MainWindow.changesOnGenes = false;
              GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", UPLOAD_SUCCESS);
            } catch (SQLException | DatabaseConnectionException e) {
              // error while connecting to database
              GUIUtils.showInfo(AlertType.ERROR, "Error", UPLOAD_FAIL);
            } catch (IOException e) {
              // error while reading genes from txt
              GUIUtils.showInfo(AlertType.ERROR, "Error", READ_FAIL);
            }

            // download genes to genes.txt
          } else if (downloadToggle.isSelected()) {
            try {
              DatabaseConnection.pullAndSaveGenes();
              GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", DOWNLOAD_SUCCESS);
            } catch (DatabaseConnectionException | SQLException e) {
              // error while connecting to database
              GUIUtils.showInfo(AlertType.ERROR, "Error", DOWNLOAD_FAIL);
            } catch (IOException e) {
              // error while writing txt
              GUIUtils.showInfo(AlertType.ERROR, "Error", WRITE_FAIL);
            }
          }

          // sequences
        } else if (resultToggle.isSelected()) {

          // upload data from file
          if (uploadToggle.isSelected()) {

            if (destField.getText().isEmpty()) {
              GUIUtils.showInfo(AlertType.ERROR, "Empty path",
                  "Please enter a path to the CSV files to be uploaded.");
              return;
            }

            if (!new File(destField.getText()).exists()
                || !new File(destField.getText()).isDirectory()) {
              GUIUtils.showInfo(AlertType.ERROR, "Invalid path",
                  "The entered path does not describe a folder. " + ""
                      + "Please enter a folder to the CSV files to be uploaded.");
              return;
            }

            try {
              uploadResults();
            } catch (IOException e) {
              // error while reading file
              GUIUtils.showInfo(AlertType.ERROR, "Error", READ_FAIL);
            } catch (SQLException | DatabaseConnectionException e) {
              // error while writing to database
              GUIUtils.showInfo(AlertType.ERROR, "Error", UPLOAD_FAIL);
            }

            // download data to folder
          } else if (downloadToggle.isSelected()) {

            if (destField.getText().isEmpty()) {
              GUIUtils.showInfo(AlertType.ERROR, "Empty path",
                  "Please enter a folder path to specify where the data should be placed.");
              return;
            }

            if (!new File(destField.getText()).exists()
                || !new File(destField.getText()).isDirectory()) {
              GUIUtils.showInfo(AlertType.ERROR, "Invalid path",
                  "The entered path does not describe a folder. "
                      + "Please enter a folder indicating where to place the retrieved data.");
              return;
            }

            try {
              downloadResults();
            } catch (SQLException | DatabaseConnectionException e) {
              GUIUtils.showInfo(AlertType.ERROR, "Error", DOWNLOAD_FAIL);
            } catch (MissingPathException | IOException e) {
              GUIUtils.showInfo(AlertType.ERROR, "Error", WRITE_FAIL);
            }
          }

        } else if (allToggle.isSelected()) {

          // upload everything
          if (uploadToggle.isSelected()) {

            if (destField.getText().isEmpty()) {
              GUIUtils.showInfo(AlertType.ERROR, "Empty path",
                  "Please enter a path to the CSV files to be uploaded.");
              return;
            }

            if (!new File(destField.getText()).exists()
                || !new File(destField.getText()).isDirectory()) {
              GUIUtils.showInfo(AlertType.ERROR, "Invalid path",
                  "The entered path does not describe a folder. " + ""
                      + "Please enter a folder to the CSV files to be uploaded.");
              return;
            }

            try {
              DatabaseConnection.pushAllGenes();
              DatabaseConnection.pushAllPrimer();
              uploadResults();
              MainWindow.changesOnGenes = false;
              MainWindow.changesOnPrimers = false;
            } catch (IOException e) {
              // error while reading file
              GUIUtils.showInfo(AlertType.ERROR, "Error", READ_FAIL);
            } catch (SQLException | DatabaseConnectionException e) {
              // error while writing to database
              GUIUtils.showInfo(AlertType.ERROR, "Error", UPLOAD_FAIL);
            }

            // download everything
          } else if (downloadToggle.isSelected()) {

            if (destField.getText().isEmpty()) {
              GUIUtils.showInfo(AlertType.ERROR, "Empty path",
                  "Please enter a folder path to specify where the data should be placed.");
              return;
            }

            if (!new File(destField.getText()).exists()
                || !new File(destField.getText()).isDirectory()) {
              GUIUtils.showInfo(AlertType.ERROR, "Invalid path",
                  "The entered path does not describe a folder. "
                      + "Please enter a folder indicating where to place the retrieved data.");
              return;
            }

            try {
              DatabaseConnection.pullAndSaveGenes();
              DatabaseConnection.pullAndSavePrimer();
              downloadResults();
              GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", DOWNLOAD_SUCCESS);
            } catch (SQLException | DatabaseConnectionException e) {
              GUIUtils.showInfo(AlertType.ERROR, "Error", DOWNLOAD_FAIL);
            } catch (MissingPathException | IOException e) {
              GUIUtils.showInfo(AlertType.ERROR, "Error", WRITE_FAIL);
            }
          }
        }
      }

    });

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

    uploadToggle.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        if (!upload) {
          upload = true;
          /*
           * researcherField.setDisable(true); geneField.setDisable(true);
           * startDate.setDisable(true); endDate.setDisable(true);
           */
        }

      }
    });

    downloadToggle.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        if (upload) {
          upload = false;
          /*
           * researcherField.setDisable(false); geneField.setDisable(false);
           * startDate.setDisable(false); endDate.setDisable(false);
           */
        }

      }
    });

    destButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Set path to the .ab1 files (folder)");
        File start = new File("user.home");
        chooser.setInitialDirectory(start);
        File selectedDirectory = null;
        try {
          selectedDirectory = chooser.showDialog(null);
        } catch (java.lang.IllegalArgumentException e) {
          chooser.setInitialDirectory(new File(System.getProperty("user.home")));
          selectedDirectory = chooser.showDialog(null);
        }

        if (selectedDirectory != null) {
          destField.setText(selectedDirectory.getAbsolutePath());
        }

      }
    });

    settingsButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        DatabaseSettingsWindow settings = new DatabaseSettingsWindow();
        try {
          settings.start(new Stage());
        } catch (Exception e) {
          GUIUtils.showInfo(AlertType.ERROR, "Database settings could not be opened",
              "The databse settings window could not be opened. Please try again.");
        }
      }
    });
  }

  /**
   * Closes the window.
   * 
   * @author Lovis Heindrich
   */
  @Override
  public void stop() throws Exception {
    MainWindow.settingsOpen = false;
    System.out.println("Database Closed");
    super.stop();
  }

  /**
   * Downloads results specified by the optional parameters date, gene and researcher. Saves them as
   * a csv file to the in destField specified path.
   * 
   * @throws SQLException Error while executing sql commands.
   * @throws DatabaseConnectionException Error while connecting to database.
   * @throws MissingPathException Path for saving the file is missing.
   * @throws IOException Error while writing file.
   * @author Lovis Heindrich
   */
  private void downloadResults()
      throws SQLException, DatabaseConnectionException, MissingPathException, IOException {
    java.sql.Date datePickerStartDate;
    if (startDate.getValue() == null) {
      datePickerStartDate = null;
    } else {
      datePickerStartDate = java.sql.Date.valueOf(startDate.getValue());
    }
    java.sql.Date datePickerEndDate;
    if (endDate.getValue() == null) {
      datePickerEndDate = null;
    } else {
      datePickerEndDate = java.sql.Date.valueOf(endDate.getValue());
    }

    String gene = geneField.getText();
    String researcher = researcherField.getText();
    String path = destField.getText();

    FileSaver.setLocalPath(path);
    FileSaver.setSeparateFiles(false);
    FileSaver.reset();
    File file = new File(path + File.separatorChar + "database_files.csv");
    if (file.exists()) {
      int i = 0;
      String newName;
      do {
        i++;
        newName = path + File.separatorChar + "database_files (" + i + ").csv";
      } while (new File(newName).exists());

      FileSaver.setDestFileName("database_files (" + i + ")");
    } else {
      FileSaver.setDestFileName("database_files");
    }

    ArrayList<AnalysedSequence> resList = DatabaseConnection
        .pullCustomSequences(datePickerStartDate, datePickerEndDate, researcher, gene);

    if (resList.isEmpty()) {
      GUIUtils.showInfo(AlertType.INFORMATION, "No results in database",
          "There are no results in the database. So no local files are created.");
      return;
    } else {
      for (AnalysedSequence res : resList) {
        FileSaver.storeResultsLocally(res.getFileName(), res);
      }
      GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", DOWNLOAD_SUCCESS);
    }

  }

  /**
   * Uploads all sequences in the in destField specified folder. Also uploads necessary genes and
   * researchers.
   * 
   * @throws IOException Error while reading local files.
   * @throws SQLException Error while executing sql commands.
   * @throws DatabaseConnectionException Error while connecting to database.
   * @author Lovis Heindrich
   */
  private void uploadResults() throws IOException, SQLException, DatabaseConnectionException {
    String path = destField.getText();
    LinkedList<AnalysedSequence> sequences = FileRetriever.convertFilesToSequences(path);

    if (sequences.isEmpty()) {
      GUIUtils.showInfo(AlertType.INFORMATION, "No usable results found",
          "There were no usable results. There either are not "
        + "result CSV files at the given path or all results in these files are unusable.");
    } else {
      DatabaseConnection.pushAllData(sequences);
      GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", UPLOAD_SUCCESS);
    }
  }

  /**
   * Disables all parameter fields except the path field and the path button.
   * 
   * @author Lovis Heindrich
   */
  private void activateOnlyPath() {
    destButton.setDisable(false);
    destField.setDisable(false);
    researcherField.setDisable(true);
    geneField.setDisable(true);
    startDate.setDisable(true);
    endDate.setDisable(true);
  }

  /**
   * Enables all parameter fields.
   * 
   * @author Lovis Heindrich
   */
  private void activateEverything() {
    destButton.setDisable(false);
    destField.setDisable(false);
    researcherField.setDisable(false);
    geneField.setDisable(false);
    startDate.setDisable(false);
    endDate.setDisable(false);
  }

  /**
   * Disables all parameter fields.
   * 
   * @author Lovis Heindrich
   */
  private void activateNothing() {
    destButton.setDisable(true);
    destField.setDisable(true);
    researcherField.setDisable(true);
    geneField.setDisable(true);
    startDate.setDisable(true);
    endDate.setDisable(true);
  }

  /**
   * Auto generated java fx start method.
   * 
   * @author Kevin Otto
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/DatabaseWindow.fxml"));
    } catch (IOException e) {
      return;
    }
    scene = new Scene(root);
    primaryStage.setTitle("GSAT - Database");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
    // alow opening again when settingswindow was closed
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

      @Override
      public void handle(WindowEvent arg0) {
        MainWindow.settingsOpen = false;
      }
    });

  }
}
