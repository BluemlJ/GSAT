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
import javafx.stage.WindowEvent;

public class AddGeneWindow extends Application implements javafx.fxml.Initializable {

  private SettingsWindow parent;
  
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

	geneArea.setWrapText(true);
	commentArea.setWrapText(true);
	
	GUIUtils.setColorOnNode(confirmButton, Color.GREEN);
    GUIUtils.setColorOnNode(cancelButton, Color.RED);
	
    confirmButton.setOnAction(new EventHandler<ActionEvent>() {
    	

      @Override
      public void handle(ActionEvent arg0) {
        if (!nameField.getText().isEmpty() && !geneArea.getText().isEmpty()) {
          try {
            if(GeneHandler.addGene(nameField.getText(), geneArea.getText(), organismField.getText(),
                commentArea.getText()) == true){

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Adding gene");
            alert.setHeaderText("Gene added successfully.");
            alert.showAndWait();
            parent.updateGenes();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
            }
            else {
            	 Alert alert = new Alert(AlertType.INFORMATION);
                 alert.setTitle("Adding gene failed");
                 alert.setHeaderText("Gene added not successful because gene already exists in local file.");
                 alert.showAndWait();
                 
            }
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
    primaryStage.setTitle("GSAT - Adding a gene");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
    
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

      @Override
      public void handle(WindowEvent arg0) {
        parent.decNumGenWindows();

      }
    });
  }

  public void setParent(SettingsWindow parent) {
      this.parent = parent;
  }


}
