package gui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import exceptions.DatabaseConnectionException;
import io.ConfigHandler;
import io.DatabaseConnection;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DatabaseSettingsWindow extends Application implements javafx.fxml.Initializable {

  @FXML
  private Button connectButton;
  @FXML
  private Button closeButton;

  @FXML
  private TextField adressField;

  @FXML
  private TextField portField;

  @FXML
  private TextField userNameField;

  @FXML
  private PasswordField passwordField;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    GUIUtils.setColorOnButton(connectButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(closeButton, ButtonColor.RED);

    adressField.setText(ConfigHandler.getDbUrl());
    portField.setText(ConfigHandler.getDbPort() + "");
    userNameField.setText(ConfigHandler.getDbUser());
    passwordField.setText(ConfigHandler.getDbPass());

    userNameField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
          userNameField.setText(oldValue);
        } else {
          userNameField.setText(newValue);
        }
      }
    });

    portField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (!newValue.matches("[0-9]*")) {
          portField.setText(oldValue);
        } else {
          portField.setText(newValue);
        }
      }
    });

    adressField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
          adressField.setText(oldValue);
        } else {
          adressField.setText(newValue);
        }
      }
    });

    connectButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {

        // save new values in config
        if (!adressField.getText().isEmpty()) {
          ConfigHandler.setDbUrl(adressField.getText());
        }
        if (!portField.getText().isEmpty()) {
          ConfigHandler.setDbPort(Integer.parseInt(portField.getText()));
        }
        if (!userNameField.getText().isEmpty()) {
          ConfigHandler.setDbUser(userNameField.getText());
        }
        if (!passwordField.getText().isEmpty()) {
          ConfigHandler.setDbPass(passwordField.getText());
        }

        try {
          ConfigHandler.writeConfig();
          System.out.println("config write successful");
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        // try to connect
        if (!adressField.getText().isEmpty() && !portField.getText().isEmpty()
            && !userNameField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
          System.out.println("trying to connect");
          String adress = adressField.getText();
          int port = Integer.parseInt(portField.getText());
          String username = userNameField.getText();
          String password = passwordField.getText();

          try {
            // Try to connect
            DatabaseConnection.setDatabaseConnection(username, password, port, adress);
            // show success alert window
            GUIUtils.showInfo(AlertType.INFORMATION, "Database connection",
                "Connection to database succeeded");
          } catch (DatabaseConnectionException | SQLException e) {
            GUIUtils.showInfo(AlertType.ERROR, "Database connection",
                "Connection to database failed");
          }
        } else {
          // values are missing
          GUIUtils.showInfo(AlertType.ERROR, "Database connection",
              "Please enter values for all parameters.");
        }
      }
    });

    closeButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        SettingsWindow.addParametersOpen = false;
      }
    });
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/DatabaseSettingsWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT - Adjust database parameters");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();

    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

      @Override
      public void handle(WindowEvent arg0) {
        SettingsWindow.addParametersOpen = false;

      }
    });
  }

}
