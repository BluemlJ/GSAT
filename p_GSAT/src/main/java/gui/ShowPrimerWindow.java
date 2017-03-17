package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import analysis.Primer;
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

  // fields
  @FXML
  private TextField nameField;

  @FXML
  private TextField idField;

  @FXML
  private TextField meltingTempField;

  @FXML
  private javafx.scene.control.TextArea sequenceArea;

  @FXML
  private javafx.scene.control.TextArea commentArea;

  @FXML
  private Button okButton;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    sequenceArea.setWrapText(true);
    GUIUtils.setColorOnButton(okButton, ButtonColor.BLUE);

    Primer selectedPrimer = SettingsWindow.getSelectedPrimer();
    nameField.setText(selectedPrimer.getName());
    sequenceArea.setText(selectedPrimer.getSequence());
    meltingTempField.setText(selectedPrimer.getMeltingPoint() + "");
    idField.setText(selectedPrimer.getId());
    commentArea.setText(selectedPrimer.getComment());

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
      root = FXMLLoader.load(getClass().getResource("/fxml/ShowPrimerWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);

    primaryStage.setTitle("GSAT - Primer - " + SettingsWindow.getSelectedPrimer().getName());

    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }
}
