package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import analysis.Primer;
import io.ConfigHandler;
import io.PrimerHandler;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This window is necessary to add new primers to the local primer pool. It contains four fields for
 * information (name,id,meltingPoint and comment).
 * 
 * @category GUI.Window
 * 
 * @author jannis blueml, 
 * @author Kevin Otto
 *
 */
public class AddPrimerWindow extends Application implements javafx.fxml.Initializable {

  /**
   * the SettingsWindow that this Window was opened from.
   */
  private SettingsWindow parent;

  /**
   * TextField to input the name of the primer.
   */
  @FXML
  private TextField nameField;

  /**
   * TextField to input the id of the primer.
   */
  @FXML
  private TextField idField;

  /**
   * TextField to inptut the melting temperature of the primer.
   */
  @FXML
  private TextField meltingTempField;

  /**
   * TextArea to input the sequence of the primer.
   */
  @FXML
  private javafx.scene.control.TextArea sequenceArea;

  /**
   * TextArea to add comments to the primer.
   */
  @FXML
  private TextArea commentArea;

  // buttons
  /**
   * Button to cancle adding the primer.
   */
  @FXML
  private Button cancelButton;

  /**
   * Button to confirm adding the primer.
   */
  @FXML
  private Button confirmButton;

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


    // set some configurations for the window (button color and text settings)
    sequenceArea.setWrapText(true);
    GUIUtils.setColorOnButton(confirmButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(cancelButton, ButtonColor.RED);

    // add ChangeListener to nameFiled, exclude separator and spaces
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

    // add ChangeListener to idField, exclude separator
    idField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
          idField.setText(oldValue);
        } else {
          idField.setText(newValue);
        }
      }
    });

    // add ChangeListener to meltingTempField, only allows numbers
    meltingTempField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches(".*[^1234567890].*")) {
          meltingTempField.setText(oldValue);
        } else {
          meltingTempField.setText(newValue);
        }
      }
    });

    // add ChangeListener, only allows A,T,C,G and spaces
    sequenceArea.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        newValue = newValue.replaceAll("\\s", "");
        if (newValue.toUpperCase().matches(".*[^ATCG].*")) {
          sequenceArea.setText(oldValue);
        } else {
          sequenceArea.setText(newValue);
        }
      }
    });

    // ChangeListener to exclude sparator
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

    // checks all informations and add the primer to the local pool. Then close the window
    confirmButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {

        // check if no fields are empty (not meltingPoint)
        if (nameField.getText().isEmpty() || sequenceArea.getText().isEmpty()
            || idField.getText().isEmpty()) {
          GUIUtils.showInfo(AlertType.ERROR, "Empty field(s)",
              "Name, sequence and ID are required.");
          return;
        }

        // set meltingPoint to the default value
        String meltingPoint;
        if (meltingTempField.getText().isEmpty()) {
          meltingPoint = "-1";
        } else {
          meltingPoint = meltingTempField.getText();
        }

        // add primer to local pool and close stage
        if (PrimerHandler.addPrimer(new Primer(sequenceArea.getText(),
            ConfigHandler.getResearcher(), Integer.parseInt(meltingPoint), idField.getText(),
            nameField.getText(), commentArea.getText()))) {

          GUIUtils.showInfo(AlertType.INFORMATION, "Adding primer", "Primer added successfully.");
          MainWindow.changesOnPrimers = true;
          parent.updatePrimers();
          Stage stage = (Stage) cancelButton.getScene().getWindow();
          stage.close();
        } else {
          GUIUtils.showInfo(AlertType.ERROR, "Adding primer failed",
              "Primer not added because it already exists in local file.");
        }


      }
    });

    // close stage
    cancelButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

      }
    });
  }

  /**
   * Stops this window by calling {@link Application#stop()}.
   * 
   * @author Kevin Otto
   */
  @Override
  public void stop() throws Exception {
    super.stop();
  }

  /**
   * javaFX method to start this window.
   * 
   * @param primaryStage the Stage to be used
   * @author Kevin Otto
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/AddPrimerWindow.fxml"));
    } catch (IOException e) {
      System.err.println("Primer adding window could not be constructed.");
      return;
    }
    Scene scene = new Scene(root);

    primaryStage.setTitle("GSAT - Adding a primer");
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


  // GETTERs and SETTERs:

  public void setParent(SettingsWindow parent) {
    this.parent = parent;
  }
}
