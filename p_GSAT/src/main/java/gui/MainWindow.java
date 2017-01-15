package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.sun.glass.ui.MenuItem;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class MainWindow extends Application implements javafx.fxml.Initializable {

  //WARNING: Do not change variable name under all circumstances!
  @FXML
  private ProgressBar bar;
  //BUTTONS
  @FXML
  private Button startButton;
  @FXML
  private Button srcButton;
  @FXML
  private Button destButton;
  @FXML
  private Button settingsButton;
  
  //Menu Items
  @FXML 
  private javafx.scene.control.MenuItem manualButton;
  @FXML
  private javafx.scene.control.MenuItem aboutButton;
  
  //Textfields
  @FXML
  private TextField srcField;
  @FXML
  private TextField destField;
  
  public static void main(String[] args) {
    launch(args);
  }

  public static void OpenWindow() {}

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    
    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        System.out.println("Start Button Pressed!");
        bar.setProgress(bar.getProgress()+0.1);
      }
    });
    
    manualButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        System.out.println("Manual!");       
      }
    });
    //...
    
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
