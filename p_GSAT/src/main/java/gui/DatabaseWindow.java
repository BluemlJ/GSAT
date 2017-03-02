package gui;

import java.awt.TextField;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class DatabaseWindow extends Application implements javafx.fxml.Initializable {

  
  private boolean upload = true;
  
  @FXML
  private ToggleButton uploadToggle;
  @FXML
  private ToggleButton downloadToggle;
  
  @FXML
  private TextField destField;
  
  @FXML 
  private Button destButton;
  @FXML 
  private Button settingsButton;
  
  
  @FXML
  private ChoiceBox<String> researcherBox;
  @FXML
  private ChoiceBox<String> geneBox;
  
  @FXML
  private DatePicker startDate;
  @FXML
  private DatePicker endDate;
  
  
  
  
  
  
  
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    uploadToggle.setOnAction(new EventHandler<ActionEvent>() {
      
      @Override
      public void handle(ActionEvent arg0) {
        if (!upload) {
          uploadToggle.setDisable(true);
          upload = true;
          
          researcherBox.setDisable(true);
          geneBox.setDisable(true);
          startDate.setDisable(true);
          endDate.setDisable(true);
        }
        
      }
    });
    
    downloadToggle.setOnAction(new EventHandler<ActionEvent>() {
      
      @Override
      public void handle(ActionEvent arg0) {
        if (upload) {
          downloadToggle.setDisable(true);
          upload = false;
          
          researcherBox.setDisable(false);
          geneBox.setDisable(false);
          startDate.setDisable(false);
          endDate.setDisable(false);
        }
        
      }
    });
    
    destButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        // TODO Auto-generated method stub
        
      }});
    
    settingsButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        // TODO Auto-generated method stub
        
      }});
  }

  @Override
  public void start(Stage stage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/DatabaseWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    stage.setTitle("GSAT");
    stage.setScene(scene);
    stage.sizeToScene();
    stage.show();
  }

}
