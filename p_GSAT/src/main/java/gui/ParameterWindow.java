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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ParameterWindow extends Application implements javafx.fxml.Initializable {


  @FXML
  private TextField avgApproximationEnd;

  @FXML
  private TextField avgApproximationStart;

  @FXML
  private TextField breakcounter;

  @FXML
  private TextField startcounter;

  @FXML
  private TextField numAverageNucleotides;

  @FXML
  private Button saveButton;

  @FXML
  private Button cancelButton;



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
  }


}
