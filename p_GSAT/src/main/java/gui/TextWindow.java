package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * a modular Window that shows a single TextArea with the given text. Used for manual and help
 * windows
 * 
 * @author Kevin Otto
 */
public class TextWindow extends Application implements javafx.fxml.Initializable {

  /**
   * The active scene.
   */
  Scene scene;

  /**
   * This field is used to display text.
   */
  @FXML
  private TextArea textArea;

  /**
   * The text to display.
   */
  private String text = "";

  /**
   * Set textArea configuration.
   * 
   * @param arg0 the URL to init, more information at {@link Initializable}
   * @param arg1 a ResourceBunde, for more informations see {@link Initializable}
   * 
   * @author Kevin Otto
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    textArea.setWrapText(true);
  }

  /**
   * Displays the text window.
   * 
   * @param primaryStage The primary stage
   * 
   * @author Kevin Otto
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
    } catch (IOException e) {
      GUIUtils.showInfo(AlertType.ERROR, "Could not main gene window",
          "The main window could not be opened. Please try again.");

      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }

  
  /**
   * This method saves the given text internally and passes it to the textArea.
   * @param text
   */
  public void setText(String text) {
    textArea.setText(text);
    this.text = text;
  }

  
  
  // GETTERS and SETTERs

  public String getText() {
    return text;
  }

}
