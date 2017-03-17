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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SettingsWindow extends Application implements javafx.fxml.Initializable {


  public static boolean addParametersOpen = false;
  private boolean addResearcher = false;
  private static Gene selectedGene;
  private static Primer selectedPrimer;
  private static boolean isPrimerOn = false;

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


  private Scene scene;

  private int numGeneWindows = 0;

  public static Gene getSelectedGene() {
    return selectedGene;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
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

    GUIUtils.initializeResearchers(researcherDropdown);
    GUIUtils.initializeGeneBox(geneOrPrimerList);
    isPrimerOn = false;
    geneOrPrimerList.setStyle("-fx-font-style: italic;");

    GUIUtils.setColorOnButton(closeButton, ButtonColor.BLUE);
    GUIUtils.setColorOnButton(databaseButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(parameterButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(deleteResearcherButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(addGeneOrPrimerButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(addResearcherButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(deleteGeneOrPrimerButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(deleteResearcherButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(showGeneOrPrimerButton, ButtonColor.BLUE);

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
          e.printStackTrace();
        }
      }
    });

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

    showGeneOrPrimerButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        if (isPrimerOn) {
          ShowPrimerWindow primerWindow = new ShowPrimerWindow();
          try {
            primerWindow.start(new Stage());
          } catch (Exception e) {
            e.printStackTrace();
          }

        } else {
          ShowGeneWindow geneWindow = new ShowGeneWindow();
          try {
            geneWindow.start(new Stage());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      }
    });

    researcherDropdown.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          ConfigHandler.setResearcher(newValue);
          try {
            ConfigHandler.writeConfig();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });

    // gives you a short menu
    parameterButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        if (!addParametersOpen) {
          addParametersOpen = true;
          ParameterWindow settings = new ParameterWindow();
          try {
            settings.start(new Stage());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });

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
            e.printStackTrace();
          }
        }
        System.out.println("Database Button!");
      }
    });

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
            e.printStackTrace();
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
            e.printStackTrace();
            return;
          }
        }
      }
    });

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
        if (addResearcher) {
          try {
            ConfigHandler.writeConfig();
            GUIUtils.initializeResearchers(researcherDropdown);
            researcherDropdown.getSelectionModel()
                .select(result.get().replaceAll(ConfigHandler.SEPARATOR_CHAR + "", ""));
            addResearcher = false;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    });

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
            e.printStackTrace();
          }
        }

      }
    });

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
      e.printStackTrace();
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

  public void updateGenes() {
    GUIUtils.initializeGeneBox(geneOrPrimerList);
  }

  public void updatePrimers() {
    GUIUtils.initializePrimerBox(geneOrPrimerList);
  }

  /**
   * @return the selectedPrimer
   */
  public static Primer getSelectedPrimer() {
    return selectedPrimer;
  }

  /**
   * @param selectedPrimer the selectedPrimer to set
   */
  public static void setSelectedPrimer(Primer selectedPrimer) {
    SettingsWindow.selectedPrimer = selectedPrimer;
  }

  public void decNumGenWindows() {
    numGeneWindows--;

  }

}
