package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
  private Button returnButton;
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
  @FXML
  private Button editGeneButton;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
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
        System.out.println("addGene Button!");
      }
    });


    addResearcherButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        System.out.println("addResearcher Button!");
      }
    });

    deleteGeneButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        System.out.println("deleteGene Button!");
      }
    });

    deleteResearcherButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        System.out.println("deleteResearcher Button!");
      }
    });

    editGeneButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        System.out.println("editGene Button!");
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
    Scene scene = new Scene(root);
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
    returnButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        primaryStage.close();
      }
    });

  }

}
