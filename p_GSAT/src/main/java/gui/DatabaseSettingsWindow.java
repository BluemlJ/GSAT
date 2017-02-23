package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DatabaseSettingsWindow extends Application implements javafx.fxml.Initializable {

  @FXML
  private TextField adressField;

  @FXML
  private TextField portField;

  @FXML
  private TextField userNameField;

  @FXML
  private PasswordField passwordField;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

  }


  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/DatabaseSettingsWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT - Adjust database parameters");
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
