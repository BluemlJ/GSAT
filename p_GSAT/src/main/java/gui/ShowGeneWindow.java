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
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Window to show extended gene informations
 * 
 * @see GUIUtils
 * @category GUI.Window
 * 
 * @author jannis blueml, Kevin Otto
 *
 */
public class ShowGeneWindow extends Application implements javafx.fxml.Initializable {

  // private SettingsWindow parent;

  // fields
  @FXML
  private TextField nameField;
  @FXML
  private TextField organismField;
  @FXML
  private TextArea geneArea;
  @FXML
  private TextArea commentArea;

  // buttons
  @FXML
  private Button okButton;

  /**
   * initialize all components and set Eventhandlers.
   * 
   * @param location  the URL to init, more information at {@link Initializable}
   * @param resources  a ResourceBunde, for more informations see {@link Initializable}
   * 
   * @see Initializable
   * @author jannis blueml
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    geneArea.setWrapText(true);
    commentArea.setWrapText(true);
    GUIUtils.setColorOnButton(okButton, ButtonColor.BLUE);

    Gene selectedGene = SettingsWindow.getSelectedGene();
    String tmp;
    nameField.setText(selectedGene.getName());

    tmp = selectedGene.getOrganism();
    if (tmp != null) {
      organismField.setText(selectedGene.getOrganism());
    }

    geneArea.setText(selectedGene.getSequence());

    tmp = selectedGene.getComment();
    if (tmp != null) {
      commentArea.setText(tmp);
    }

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
