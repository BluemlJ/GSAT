package gui;

import io.ConfigHandler;
import io.GeneHandler;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import analysis.Gene;
import io.ConfigHandler;
import io.GeneHandler;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class SettingsWindow extends Application implements javafx.fxml.Initializable {


  public static boolean addParametersOpen = false;
  private boolean addResearcher = false;
  public static Gene selectedGene;

  @FXML
  private ListView<String> geneList;
  // fields
  @FXML
  private TextField parameter1Field;
  @FXML
  private ChoiceBox<String> researcherDrobdown;

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
  private Button addGeneButton;
  @FXML
  private Button addResearcherButton;
  @FXML
  private Button deleteGeneButton;
  @FXML
  private Button deleteResearcherButton;

  private Scene scene;

  private int numGeneWindows = 0;

  public static Gene getSelectedGene() {
    return selectedGene;
  }

  public static void setSelectedGene(Gene selectedGene) {
    SettingsWindow.selectedGene = selectedGene;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    GUIUtils.initializeResearchers(researcherDrobdown);
    GUIUtils.initializeGeneBox(geneList);
    geneList.setStyle("-fx-font-style: italic;");

    GUIUtils.setColorOnButton(closeButton, ButtonColor.BLUE);
    GUIUtils.setColorOnButton(databaseButton, ButtonColor.BLUE);
    GUIUtils.setColorOnButton(parameterButton, ButtonColor.BLUE);
    GUIUtils.setColorOnButton(deleteResearcherButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(addGeneButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(addResearcherButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(deleteGeneButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(deleteResearcherButton, ButtonColor.RED);

    srcPathField.setText(ConfigHandler.getSrcPath());

    srcPathField.textProperty().addListener(new ChangeListener<String>() {

      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
          srcPathField.setText(oldValue);
        } else {
          srcPathField.setText(newValue);
        }

        ConfigHandler.setSrcPath(newValue);
        try {
          ConfigHandler.writeConfig();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });

    geneList.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          if (!geneList.getSelectionModel().isEmpty()) {
            selectedGene = GeneHandler.getGene(newValue.split(" ")[0]);

          }
        });

    /* TODO implementieren, wenn button vorhanden
     * showGeneButton.setOnAction(new EventHandler<ActionEvent>() {
     * 
     * @Override public void handle(ActionEvent arg0) { ShowGeneWindow geneWindow = new
     * ShowGeneWindow(); try { geneWindow.start(new Stage()); } catch (Exception e) { // TODO
     * Auto-generated catch block e.printStackTrace(); } } });
     */

    researcherDrobdown.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          ConfigHandler.setResearcher(newValue);
          try {
            ConfigHandler.writeConfig();
          } catch (IOException e) {
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        System.out.println("Database Button!");
      }
    });

    SettingsWindow self = this;
    addGeneButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {

        numGeneWindows++;

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

    });

    addResearcherButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        TextInputDialog dialog = new TextInputDialog("your name");
        dialog.setTitle("Add a new researcher");
        dialog.setHeaderText("Please enter the name of the new researcher.");
        dialog.setContentText("The name should have a form like Max M.");
        dialog.setContentText("Name:");

        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
        ObservableList<Node> nodes = buttonBar.getButtons();
        GUIUtils.setColorOnButton((Button)nodes.get(0), ButtonColor.GREEN);
        GUIUtils.setColorOnButton((Button)nodes.get(1), ButtonColor.RED);

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
            GUIUtils.initializeResearchers(researcherDrobdown);
            researcherDrobdown.getSelectionModel()
                .select(result.get().replaceAll(ConfigHandler.SEPARATOR_CHAR + "", ""));
            addResearcher = false;
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    });

    deleteGeneButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        int geneindex = geneList.getSelectionModel().getSelectedIndex();
        if (geneindex != -1) {
          try {
            GeneHandler.deleteGene(geneList.getSelectionModel().getSelectedItem());
            GeneHandler.writeGenes();
            GUIUtils.initializeGeneBox(geneList);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

      }
    });

    deleteResearcherButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        ConfigHandler.deleteResearcher(researcherDrobdown.getSelectionModel().getSelectedItem());
        researcherDrobdown.getSelectionModel().clearSelection();
        GUIUtils.initializeResearchers(researcherDrobdown);
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
    /*
     * returnButton.setOnAction(new EventHandler<ActionEvent>() {
     * 
     * @Override public void handle(ActionEvent arg0) { primaryStage.close(); } });
     */
  }

  public void updateGenes() {
    geneList.getSelectionModel().clearSelection();
    GUIUtils.initializeGeneBox(geneList);
  }

  public void decNumGenWindows() {
    numGeneWindows--;

  }

}
