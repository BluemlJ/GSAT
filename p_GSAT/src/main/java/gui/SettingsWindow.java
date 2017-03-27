package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import analysis.Gene;
import analysis.Primer;
import io.ConfigHandler;
import io.GeneHandler;
import io.PrimerHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Window to add and show genes/primers and researcher. The user can set a default path to source
 * files. The parameter and databaseSettings window can be found here.
 * 
 * @see GUIUtils
 * @category GUI.Window
 * 
 * @author jannis blueml, Kevin Otto
 *
 */
public class SettingsWindow extends Application implements javafx.fxml.Initializable {

  /**
   * Says if a parameterWindow is already open.
   */
  public static boolean addParametersOpen = false;

  /**
   * Says if there is the try to add a new researcher.
   */
  private boolean addResearcher = false;

  /**
   * Represents the selected gene in the listView.
   */
  private static Gene selectedGene;

  /**
   * Represents the selected primer in the listView.
   */
  private static Primer selectedPrimer;

  /**
   * Says if the listView shows primers (true) or genes (false).
   */
  private static boolean isPrimerOn = false;

  /**
   * Number of open add- or showGeneWindows.
   */
  private int numGeneWindows = 0;


  @FXML
  private ListView<String> geneOrPrimerList;

  // fields
  @FXML
  private TextField parameter1Field;
  @FXML
  private ChoiceBox<String> researcherDropdown;
  @FXML
  private TextField srcPathField;

  // buttons
  @FXML
  private Button parameterButton;
  @FXML
  private Button closeButton;
  @FXML
  private Button databaseButton;
  @FXML
  private Button addGeneOrPrimerButton;
  @FXML
  private Button addResearcherButton;
  @FXML
  private Button deleteGeneOrPrimerButton;
  @FXML
  private Button deleteResearcherButton;
  @FXML
  private Button showGeneOrPrimerButton;

  @FXML
  private ToggleButton geneToggle;
  @FXML
  private ToggleButton primmerToggle;


  /**
   * active scene object.
   */
  private Scene scene;



  /**
   * initialize all components and set Eventhandlers.
   * 
   * @param location the URL to init, more information at {@link Initializable}
   * @param resources a ResourceBunde, for more informations see {@link Initializable}
   * 
   * @see Initializable
   * @author jannis blueml
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // settings for the toogleGroup (genes or primers)
    ToggleGroup selectorGroup = new ToggleGroup();
    primmerToggle.setToggleGroup(selectorGroup);
    geneToggle.setToggleGroup(selectorGroup);

    primmerToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (primmerToggle.equals(selectorGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
      }
    });

    geneToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (geneToggle.equals(selectorGroup.getSelectedToggle())) {
          mouseEvent.consume();
        }
      }
    });


    geneToggle.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        isPrimerOn = false;
        GUIUtils.initializeGeneBox(geneOrPrimerList);
        geneOrPrimerList.getSelectionModel().clearSelection();
        showGeneOrPrimerButton.setDisable(true);
      }
    });

    primmerToggle.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        isPrimerOn = true;
        GUIUtils.initializePrimerBox(geneOrPrimerList);
        geneOrPrimerList.getSelectionModel().clearSelection();
        showGeneOrPrimerButton.setDisable(true);
        System.out.println("Primer soll es sein");

      }
    });

    showGeneOrPrimerButton.setDisable(true);

    // settings for the window and initializing genes/primers and researchers
    GUIUtils.initializeResearchers(researcherDropdown);
    GUIUtils.initializeGeneBox(geneOrPrimerList);
    isPrimerOn = false;
    geneOrPrimerList.setStyle("-fx-font-style: italic;");

    // set button colors
    GUIUtils.setColorOnButton(closeButton, ButtonColor.BLUE);
    GUIUtils.setColorOnButton(databaseButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(parameterButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(deleteResearcherButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(addGeneOrPrimerButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(addResearcherButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(deleteGeneOrPrimerButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(deleteResearcherButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(showGeneOrPrimerButton, ButtonColor.BLUE);

    // checks if there is any srcPath and add changeListener to exclude separator
    srcPathField.setText(ConfigHandler.getSrcPath());
    srcPathField.textProperty().addListener(new ChangeListener<String>() {

      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
          srcPathField.setText(oldValue);
        } else {
          srcPathField.setText(newValue);
        }

        ConfigHandler.setSrcPath(newValue);
        try {
          ConfigHandler.writeConfig();
        } catch (IOException e) {
          srcPathField.setText("");
        }
      }
    });

    // observe the selescted gene/primer and set it with changes
    geneOrPrimerList.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          if (!geneOrPrimerList.getSelectionModel().isEmpty()) {
            if (isPrimerOn) {
              selectedPrimer = PrimerHandler.getPrimer(newValue);
            } else {
              selectedGene = GeneHandler.getGene(newValue.split(" ")[0]);
            }
            showGeneOrPrimerButton.setDisable(false);
          }
        });

    // opens a new window with informations about gene/primer
    showGeneOrPrimerButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        if (isPrimerOn) {
          ShowPrimerWindow primerWindow = new ShowPrimerWindow();
          try {
            primerWindow.start(new Stage());
          } catch (Exception e) {
            GUIUtils.showInfo(AlertType.ERROR, "Error during primer window opening",
                "The primer window could not be opened. Please try again.");
          }

        } else {
          ShowGeneWindow geneWindow = new ShowGeneWindow();
          try {
            geneWindow.start(new Stage());
          } catch (Exception e) {
            GUIUtils.showInfo(AlertType.ERROR, "Gene window opening error",
                "Gene window could not be opened. Please try again.");
          }
        }

      }
    });

    // observe researcher and write changes in the config file
    researcherDropdown.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          ConfigHandler.setResearcher(newValue);
          try {
            ConfigHandler.writeConfig();
          } catch (IOException e) {
            GUIUtils.showInfo(AlertType.ERROR, "Error processing configuration file",
                "There was an error during configuration file processing.");

          }
        });

    // gives you a short menu to change parameters
    parameterButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        if (!addParametersOpen) {
          addParametersOpen = true;
          ParameterWindow settings = new ParameterWindow();
          try {
            settings.start(new Stage());
          } catch (Exception e) {
            GUIUtils.showInfo(AlertType.ERROR, "Error during parameter window opening",
                "The parameter window could not be opened. Please try again.");
          }
        }
      }
    });

    // opens a window for connection settings with the database
    databaseButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        if (!addParametersOpen) {
          System.err.println(addParametersOpen);
          addParametersOpen = true;
          DatabaseSettingsWindow settings = new DatabaseSettingsWindow();
          try {
            settings.start(new Stage());
          } catch (Exception e) {
            GUIUtils.showInfo(AlertType.ERROR, "Error during settings window opening",
                "The settings window could not be opened. Please try again.");
          }
        }
        System.out.println("Database Button!");
      }
    });

    // opens addGeneWindow/addPrimerWindow in a seperate window
    SettingsWindow self = this;
    addGeneOrPrimerButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        numGeneWindows++;

        if (isPrimerOn) {
          try {
            final FXMLLoader loader =
                new FXMLLoader(TextWindow.class.getResource("/fxml/AddPrimerWindow.fxml"));

            final Parent root = loader.load();

            AddPrimerWindow primerWin = loader.<AddPrimerWindow>getController();

            primerWin.setParent(self);

            Scene scene = new Scene(root);
            Stage s = new Stage();
            s.setScene(scene);
            s.sizeToScene();
            s.setTitle("GSAT - Adding a primer");
            s.show();

          } catch (IOException e) {
            return;
          }
        } else {
          try {
            final FXMLLoader loader =
                new FXMLLoader(TextWindow.class.getResource("/fxml/AddGeneWindow.fxml"));

            final Parent root = loader.load();

            AddGeneWindow genWin = loader.<AddGeneWindow>getController();

            genWin.setParent(self);

            Scene scene = new Scene(root);
            Stage s = new Stage();
            s.setScene(scene);
            s.sizeToScene();
            s.setTitle("GSAT - Adding a gene");
            s.show();

          } catch (IOException e) {
            return;
          }
        }
      }
    });

    // actionEvent to add a new researcher with some dialog window from javafx
    addResearcherButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        TextInputDialog dialog = new TextInputDialog("your name");
        dialog.setTitle("Add a new researcher");
        dialog.setHeaderText("Please enter the name of the new researcher.");
        dialog.setContentText("The name should have a form like 'Max M'.");
        dialog.setContentText("Name:");

        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
        ObservableList<Node> nodes = buttonBar.getButtons();
        GUIUtils.setColorOnButton((Button) nodes.get(0), ButtonColor.GREEN);
        GUIUtils.setColorOnButton((Button) nodes.get(1), ButtonColor.RED);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
          if (result.get() != null && !result.get().isEmpty()) {
            ConfigHandler
                .addResearcher(result.get().replaceAll(ConfigHandler.SEPARATOR_CHAR + "", ""));
            addResearcher = true;
          }

        }
        // if there is a new researcher to add, write him in the config file
        if (addResearcher) {
          try {
            ConfigHandler.writeConfig();
            GUIUtils.initializeResearchers(researcherDropdown);
            researcherDropdown.getSelectionModel()
                .select(result.get().replaceAll(ConfigHandler.SEPARATOR_CHAR + "", ""));
            addResearcher = false;
          } catch (IOException e) {
            GUIUtils.showInfo(AlertType.ERROR, "Error while adding researcher",
                "There was an error during adding a researcher.");
          }
        }
      }
    });

    // checks if there is a selected primer/gene to delete and deletes it after validation
    deleteGeneOrPrimerButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        int geneindex = geneOrPrimerList.getSelectionModel().getSelectedIndex();
        if (geneindex != -1) {
          try {
            if (isPrimerOn) {
              PrimerHandler.deletePrimer(geneOrPrimerList.getSelectionModel().getSelectedItem());
              PrimerHandler.writePrimer();
              GUIUtils.initializePrimerBox(geneOrPrimerList);
            } else {
              GeneHandler.deleteGene(geneOrPrimerList.getSelectionModel().getSelectedItem());
              GeneHandler.writeGenes();
              GUIUtils.initializeGeneBox(geneOrPrimerList);
            }

            showGeneOrPrimerButton.setDisable(true);
          } catch (IOException e) {
            GUIUtils.showInfo(AlertType.ERROR, "Error while deleting gene",
                "There was an error during gene deletion.");
          }
        }

      }
    });

    // delete the selected researcher
    deleteResearcherButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        ConfigHandler.deleteResearcher(researcherDropdown.getSelectionModel().getSelectedItem());
        researcherDropdown.getSelectionModel().clearSelection();
        GUIUtils.initializeResearchers(researcherDropdown);
      }
    });

    closeButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        MainWindow.settingsOpen = false;
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
      }
    });
  }

  @Override
  public void stop() throws Exception {
    MainWindow.settingsOpen = false;
    System.out.println("Settings Closed");
    super.stop();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/SettingsWindow.fxml"));
    } catch (IOException e) {
      GUIUtils.showInfo(AlertType.ERROR, "Error during settings window opening",
          "The settings window could not be opened. Please try again.");
      return;
    }
    scene = new Scene(root);
    primaryStage.setTitle("GSAT - Settings");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
    // alow opening again when settingswindow was closed
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

      @Override
      public void handle(WindowEvent arg0) {
        if (numGeneWindows == 0) {
          MainWindow.settingsOpen = false;
        } else {
          arg0.consume();
        }

      }
    });
  }

  /**
   * reinitialize geneBox in MainWindow and SettingsWindow by reread genes.txt
   * 
   * @author jannis blueml
   */
  public void updateGenes() {
    GUIUtils.initializeGeneBox(geneOrPrimerList);
  }

  /**
   * reinitialize primerBox in MainWindow and SettingsWindow by reread primers.txt
   * 
   * @author jannis blueml
   */
  public void updatePrimers() {
    GUIUtils.initializePrimerBox(geneOrPrimerList);
  }

  /**
   * decrement number of open GeneWindows
   * 
   * @author Kevin Otto
   */
  public void decNumGenWindows() {
    numGeneWindows--;

  }

  // GETTER AND SETTERS

  public static Primer getSelectedPrimer() {
    return selectedPrimer;
  }

  public static Gene getSelectedGene() {
    return selectedGene;
  }

  public static void setSelectedPrimer(Primer selectedPrimer) {
    SettingsWindow.selectedPrimer = selectedPrimer;
  }



}
