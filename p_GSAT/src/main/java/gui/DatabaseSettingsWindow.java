package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.ConfigHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
	        if (! newValue.matches("[0-9]+")) {
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
	  
	  
	  closeButton.setOnAction(new EventHandler<ActionEvent>() {

	      @Override
	      public void handle(ActionEvent arg0) {
	        Stage stage = (Stage) closeButton.getScene().getWindow();
	        stage.close();

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
