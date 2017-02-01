package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import exceptions.DuplicateGeneException;
import io.GeneHandler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddGeneWindow extends Application implements javafx.fxml.Initializable {


  // fields
  @FXML
  private TextField nameField;

  @FXML
  private TextField organismField;

  @FXML
  private javafx.scene.control.TextArea geneArea;

  @FXML
  private javafx.scene.control.TextArea commentArea;

  @FXML
  private Button confirmButton;

  @FXML
  private Button cancelButton;

  Scene scene;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    confirmButton.setOnAction(new EventHandler<ActionEvent>() {


      @Override
      public void handle(ActionEvent arg0) {
        if (nameField.getText() != "" && geneArea.getText() != "") {
          try {
            GeneHandler.addGene(nameField.getText(), geneArea.getText(), organismField.getText(),
                commentArea.getText());
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Adding gene");
            alert.setHeaderText("Gene added successful" + organismField.getText());
            alert.showAndWait();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
          } catch (DuplicateGeneException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

      }
    });

    cancelButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

      }
    });
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
