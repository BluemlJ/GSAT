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

public class ShowGeneWindow extends Application implements javafx.fxml.Initializable {

  // private SettingsWindow parent;

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
  private Button okButton;


  Scene scene;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO @Jannis: schau nach was du davon brauchst und schmeis den rest weg
    /*
     * geneArea.setWrapText(true); commentArea.setWrapText(true);
     * 
     * GUIUtils.setColorOnNode(confirmButton, ButtonColor.GREEN);
     * GUIUtils.setColorOnNode(cancelButton, ButtonColor.RED);
     * 
     * nameField.textProperty().addListener(new ChangeListener<String>() {
     * 
     * @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
     * String newValue) { if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
     * nameField.setText(oldValue); } else { nameField.setText(newValue); } } });
     * 
     * organismField.textProperty().addListener(new ChangeListener<String>() {
     * 
     * @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
     * String newValue) { if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
     * organismField.setText(oldValue); } else { organismField.setText(newValue); } } });
     * 
     * geneArea.textProperty().addListener(new ChangeListener<String>() {
     * 
     * @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
     * String newValue) { if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
     * geneArea.setText(oldValue); } else { geneArea.setText(newValue); } } });
     * 
     * commentArea.textProperty().addListener(new ChangeListener<String>() {
     * 
     * @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
     * String newValue) { if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
     * commentArea.setText(oldValue); } else { commentArea.setText(newValue); } } });
     * 
     * confirmButton.setOnAction(new EventHandler<ActionEvent>() {
     * 
     * @Override public void handle(ActionEvent arg0) { if (!nameField.getText().isEmpty() &&
     * !geneArea.getText().isEmpty()) { try { if (GeneHandler.addGene(nameField.getText(),
     * geneArea.getText(), organismField.getText(), commentArea.getText()) == true) {
     * 
     * Alert alert = new Alert(AlertType.INFORMATION); alert.setTitle("Adding gene");
     * alert.setHeaderText("Gene added successfully."); alert.showAndWait(); parent.updateGenes();
     * Stage stage = (Stage) cancelButton.getScene().getWindow(); stage.close(); } else { Alert
     * alert = new Alert(AlertType.INFORMATION); alert.setTitle("Adding gene failed");
     * alert.setHeaderText( "Gene added not successful because gene already exists in local file.");
     * alert.showAndWait();
     * 
     * } } catch (DuplicateGeneException | IOException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } }
     * 
     * } });
     * 
     * cancelButton.setOnAction(new EventHandler<ActionEvent>() {
     * 
     * @Override public void handle(ActionEvent arg0) { Stage stage = (Stage)
     * cancelButton.getScene().getWindow(); stage.close();
     * 
     * } });
     */
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
    primaryStage.setTitle("GSAT - gene overview");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();

    GUIUtils.setColorOnNode(okButton, ButtonColor.BLUE);
    
    /*
     * primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
     * 
     * @Override public void handle(WindowEvent arg0) { parent.decNumGenWindows(); } });
     */
  }

  /*
   * public void setParent(SettingsWindow parent) { this.parent = parent; }
   */

}
