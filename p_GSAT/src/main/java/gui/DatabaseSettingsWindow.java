package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DatabaseSettingsWindow extends Application implements javafx.fxml.Initializable {

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/ParameterWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT - Adjust analysis parameters");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
    
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

      @Override
      public void handle(WindowEvent arg0) {
        SettingsWindow.addParametersOpen = false;

      }
    });
  }

}
