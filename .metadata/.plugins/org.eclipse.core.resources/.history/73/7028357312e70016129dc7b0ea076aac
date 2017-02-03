package gui;

import java.awt.TextArea;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AddGeneWindow extends Application implements javafx.fxml.Initializable {

  public static boolean subsettingsOpen = false;

  // fields
  @FXML
  private TextField nameField;

  @FXML
  private TextField organismField;
  
  @FXML
  private TextArea geneArea;
  
  @FXML
  private TextArea commentArea;
  
  Scene scene;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  @Override
  public void stop() throws Exception {
   super.stop();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/AddGeneWindow.fxml"));
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
