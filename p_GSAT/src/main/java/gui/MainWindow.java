package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class MainWindow extends Application implements javafx.fxml.Initializable {



  public static void main(String[] args) {
    launch(args);
  }

  public static void OpenWindow() {

  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

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
