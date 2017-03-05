package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import analysis.Gene;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ShowPrimerWindow extends Application implements javafx.fxml.Initializable {

  // private SettingsWindow parent;

  // fields
  @FXML
  private TextField nameField;

  @FXML
  private TextField idField;

  @FXML
  private TextField meltingTempField;
  
  @FXML
  private javafx.scene.control.TextArea geneArea;

  @FXML
  private Button okButton;



  @Override
  public void initialize(URL location, ResourceBundle resources) {
    geneArea.setWrapText(true);
    GUIUtils.setColorOnButton(okButton, ButtonColor.BLUE);


    Gene selectedGene = SettingsWindow.getSelectedGene();
    String tmp;
    nameField.setText(selectedGene.getName());



    geneArea.setText(selectedGene.getSequence());



    okButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        Stage stage = (Stage) okButton.getScene().getWindow();
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
      root = FXMLLoader.load(getClass().getResource("/fxml/ShowGeneWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT - Gene - " + SettingsWindow.getSelectedGene().getName());
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }
}
