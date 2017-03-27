package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import exceptions.DuplicateGeneException;
import io.ConfigHandler;
import io.GeneHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This window is necessary to add new genes to the local gene pool. It contains four fields for
 * informations (name,gene,organism and comment).
 * 
 * @category GUI.Window
 * 
 * @author jannis blueml, Kevin Otto
 *
 */
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

  // buttons
  @FXML
  private Button confirmButton;
  @FXML
  private Button cancelButton;

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

    // set start text for geneArea
    geneArea.setWrapText(true);
    geneArea.setPromptText("sequence, starting with 5' and " + System.lineSeparator()
        + System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
        + System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
        + System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
        + "                                                                                       "
        + "                                                                       ending with 3'");

    commentArea.setWrapText(true);

    // set button colors
    GUIUtils.setColorOnButton(confirmButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(cancelButton, ButtonColor.RED);

    // add ChangeListener to nameField and exclude separator and spaces
    nameField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "") || newValue.contains(" ")) {
          nameField.setText(oldValue);
        } else {
          nameField.setText(newValue);
        }
      }
    });

    // add ChangeListener to organismField, exclude separator
    organismField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
          organismField.setText(oldValue);
        } else {
          organismField.setText(newValue);
        }
      }
    });

    // add ChangeListener to geneArea, only allows spaces and A,T,C and G.
    geneArea.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        newValue = newValue.replaceAll("\\s", "");
        if (newValue.toUpperCase().matches(".*[^ATCG].*")) {
          geneArea.setText(oldValue);
        } else {
          geneArea.setText(newValue);
        }
      }
    });

    // add ChangeListener to commentArea, exclude separator
    commentArea.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
          commentArea.setText(oldValue);
        } else {
          commentArea.setText(newValue);
        }
      }
    });

    // add ActonEvent to confirmButton, that checked needed Fields and add gene to the gene pool.
    confirmButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {

        // check necessary
        if (nameField.getText().isEmpty() || geneArea.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "Required fields are not filled",
              "Please enter a gene name and its sequence.");
          return;
        }

        // check if gene already exists in the local gene pool. If not, then add gene and close
        // window.
        try {
          if (organismField.getText().equals("")) {
            organismField.setText("none");
          }
          if (GeneHandler.addGene(nameField.getText(), geneArea.getText(), organismField.getText(),
              commentArea.getText())) {

            GUIUtils.showInfo(AlertType.INFORMATION, "Adding a gene", "Gene added successfully.");
            MainWindow.changesOnGenes = true;
            parent.updateGenes();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
          } else {
            GUIUtils.showInfo(AlertType.ERROR, "Faild to add a gene",
                "Gene added not successful because gene already exists in local file.");
          }

        } catch (DuplicateGeneException | IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }


      }
    });

    // add ActionEvent to cancelButton, that close the window/stage.
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
    // try to add parent
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/AddGeneWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    // set some informations for the window
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
