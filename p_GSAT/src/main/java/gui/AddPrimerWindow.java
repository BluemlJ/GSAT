package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import analysis.Primer;
import exceptions.DuplicateGeneException;
import io.ConfigHandler;
import io.GeneHandler;
import io.PrimerHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AddPrimerWindow extends Application implements javafx.fxml.Initializable {

  private SettingsWindow parent;

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
  private Button cancelButton;

  @FXML
  private Button confirmButton;

  @FXML
  private TextArea commentArea;


  @Override
  public void initialize(URL location, ResourceBundle resources) {


    sequenceArea.setWrapText(true);
    GUIUtils.setColorOnButton(confirmButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(cancelButton, ButtonColor.RED);

    nameField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
          nameField.setText(oldValue);
        } else {
          nameField.setText(newValue);
        }
      }
    });

    idField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.contains(ConfigHandler.SEPARATOR_CHAR+"")) {
          idField.setText(oldValue);
        } else {
          idField.setText(newValue);
        }
      }
    });

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

    sequenceArea.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.toUpperCase().matches(".*[^ATCG].*")) {
          sequenceArea.setText(oldValue);
        } else {
          sequenceArea.setText(newValue);
        }
      }
    });

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

    confirmButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        if (!nameField.getText().isEmpty() && !sequenceArea.getText().isEmpty()
            && !idField.getText().isEmpty() && !meltingTempField.getText().isEmpty() ) {
          if (PrimerHandler.addPrimer(new Primer(sequenceArea.getText(), ConfigHandler.getResearcher(),
              Integer.parseInt(meltingTempField.getText()), idField.getText(), nameField.getText(),
              commentArea.getText()))) {

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Adding primer");
            alert.setHeaderText("Primer added successfully.");
            alert.showAndWait();
            MainWindow.changesOnPrimers = true;
            parent.updatePrimers();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
          } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Adding primer failed");
            alert.setHeaderText(
                "Primer added not successful because gene already exists in local file.");
            alert.showAndWait();

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
      root = FXMLLoader.load(getClass().getResource("/fxml/AddPrimerWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
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

  public void setParent(SettingsWindow parent) {
    this.parent = parent;
  }
}