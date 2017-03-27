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
import javafx.scene.control.Alert.AlertType;
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
  /**
   * TextField to display the name of the Gene.
   */
  @FXML
  private TextField nameField;
  
  /**
   * TextField to display the organism of this gene.
   */
  @FXML
  private TextField organismField;
  
  /**
   * TextArea to display the sequence of the gene.
   */
  @FXML
  private TextArea geneArea;
  
  /**
   * TextArea to display the comments of the gene.
   */
  @FXML
  private TextArea commentArea;

  // buttons
  /**
   * Button to close the window
   */
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

  /**
   * javaFX method to start this window
   * @param primaryStage the Stage to be used
   * @author Kevin Otto
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/ShowGeneWindow.fxml"));
    } catch (IOException e) {
      GUIUtils.showInfo(AlertType.ERROR, "Could not open gene window",
          "The gene window could not be opened. Please try again.");
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT - Gene - " + SettingsWindow.getSelectedGene().getName());
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }
}
