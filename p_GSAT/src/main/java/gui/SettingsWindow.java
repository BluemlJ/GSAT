package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsWindow  extends Application implements javafx.fxml.Initializable{

  //buttons
  @FXML
  private Button returnButton;  
  @FXML
  private Button restoreButton;
  @FXML
  private Button addResearcherButton;
  @FXML
  private Button deleteResearcherButton;
  @FXML
  private Button addGeneButton;
  @FXML
  private Button deleteGeneButton;
  @FXML
  private Button editGeneButton;
  
  //fields
  @FXML
  private TextField Parameter1Field;
  
  @FXML
  private ChoiceBox<String> researcherDrobdown;
  
  @FXML
  private ListView<String> geneList;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }

}
