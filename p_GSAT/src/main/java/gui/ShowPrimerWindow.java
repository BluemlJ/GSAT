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
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Window to show extended primer informations
 * 
 * @see GUIUtils
 * @category GUI.Window
 * 
 * @author jannis blueml, Kevin Otto
 *
 */
public class ShowPrimerWindow extends Application implements javafx.fxml.Initializable {

  // fields
  @FXML
  private TextField nameField;
  @FXML
  private TextField idField;
  @FXML
  private TextField meltingTempField;
  @FXML
  private TextArea sequenceArea;
  @FXML
  private TextArea commentArea;

  // button
  @FXML
  private Button okButton;

  /**
   * initialize all components and set Eventhandlers.
   * 
   * @param location the URL to init, more information at {@link Initializable}
   * @param resources a ResourceBunde, for more informations see {@link Initializable}
   * 
   * @see Initializable
   * @author jannis blueml
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    sequenceArea.setWrapText(true);
    GUIUtils.setColorOnButton(okButton, ButtonColor.BLUE);

    Primer selectedPrimer = SettingsWindow.getSelectedPrimer();
    nameField.setText(selectedPrimer.getName());
    sequenceArea.setText(selectedPrimer.getSequence());
    int meltingPoint = selectedPrimer.getMeltingPoint();
    if (meltingPoint != -1) {
      meltingTempField.setText(selectedPrimer.getMeltingPoint() + " \u00b0C");
    }
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
      GUIUtils.showInfo(AlertType.ERROR, "Could not open primer window",
          "The primer window could not be opened. Please try again.");

      return;
    }
    Scene scene = new Scene(root);

    primaryStage.setTitle("GSAT - Primer - " + SettingsWindow.getSelectedPrimer().getName());

    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }
}
