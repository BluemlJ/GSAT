package gui;

import java.awt.font.GlyphJustificationInfo;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import exceptions.DuplicateGeneException;
import io.ConfigHandler;
import io.GeneHandler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ListView.EditEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SettingsWindow extends Application implements javafx.fxml.Initializable {

  public static boolean subsettingsOpen = false;
  
  @FXML
  private ListView<String> geneList;
  // fields
  @FXML
  private TextField Parameter1Field;
  @FXML
  private ChoiceBox<String> researcherDrobdown;

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

  Scene scene;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    GUIUtils.initializeResearchers(researcherDrobdown);
    GUIUtils.initializeGeneBox(geneList);


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
        if (!subsettingsOpen) {
          subsettingsOpen = true;
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
        if (!subsettingsOpen) {
          subsettingsOpen = true;
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


    addGeneButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {


        AddGeneWindow addgene = new AddGeneWindow();
        try {
          addgene.start(new Stage());
        } catch (Exception e) {
          System.out.println("FEHLER");
        }
      }

    });



    /*
     * String genename, gene; TextInputDialog dialog = new TextInputDialog("Gene name");
     * dialog.setTitle("Add a new gene");
     * dialog.setHeaderText("Please enter the name of the new gene.");
     * dialog.setContentText("Name:");
     * 
     * // Traditional way to get the response value. Optional<String> result = dialog.showAndWait();
     * if (result.isPresent()) { genename = result.get(); if (genename == "" || genename == null)
     * return;
     * 
     * dialog = new TextInputDialog("Gene in nucleotides"); dialog.setTitle("Add a new gene");
     * dialog.setHeaderText("Please enter the gene sequence in form of nucleotides.");
     * dialog.setContentText("Sequence:");
     * 
     * // Traditional way to get the response value. result = dialog.showAndWait(); if
     * (result.isPresent()) { gene = result.get(); try { GeneHandler.addGene(genename, gene);
     * GUIUtils.initializeGeneBox(geneList); } catch (DuplicateGeneException | IOException e) {
     * System.out.println("FAIL"); // TODO Auto-generated catch block e.printStackTrace(); } } } }
     * });
     */



    addResearcherButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        TextInputDialog dialog = new TextInputDialog("your name");
        dialog.setTitle("Add a new researcher");
        dialog.setHeaderText("Please enter the name of the new researcher");
        dialog.setContentText("Name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
          if (result.get() != null && result.get() != "") ConfigHandler.addResearcher(result.get());
        }
        try {
          ConfigHandler.writeConfig();
          GUIUtils.initializeResearchers(researcherDrobdown);
          researcherDrobdown.getSelectionModel().select(result.get());
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
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

    parameterButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {

        ParameterWindow pW = new ParameterWindow();
        try {
          pW.start(new Stage());
        } catch (Exception e) {
          // TODO Auto-generated catch block
        }
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
    /*
     * returnButton.setOnAction(new EventHandler<ActionEvent>() {
     * 
     * @Override public void handle(ActionEvent arg0) { primaryStage.close(); } });
     */
  }

}
