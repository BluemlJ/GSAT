package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import exceptions.ConfigNotFoundException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;
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
 * With this window, the user can change most of the parameter used by the anlysing process. It
 * contains also a own help text and the possibility to reset the parameter to default values. This
 * default values were found on a test set with ~200 results.
 * 
 * @category GUI.Window
 * 
 * @author Jannis Blueml,
 * @author Kevin Otto
 *
 */
public class ParameterWindow extends Application implements javafx.fxml.Initializable {

  /**
   * Textfield for the avgApproximationEnd parameter.
   * 
   * @see analysis.QualityAnalysis
   */
  @FXML
  private TextField avgApproximationEnd;

  /**
   * Textfield for the avgApproximationStart parameter.
   * 
   * @see analysis.QualityAnalysis
   */
  @FXML
  private TextField avgApproximationStart;


  /**
   * Textfield for the breakcounter parameter.
   * 
   * @see analysis.QualityAnalysis
   */
  @FXML
  private TextField breakcounter;

  /**
   * Textfield for the startcounter parameter.
   * 
   * @see analysis.QualityAnalysis
   */
  @FXML
  private TextField startcounter;

  /**
   * Textfield for the numAverageNucleotides parameter.
   * 
   * @see analysis.QualityAnalysis
   */
  @FXML
  private TextField numAverageNucleotides;

  /**
   * Saves the current values.
   */
  @FXML
  private Button saveButton;

  /**
   * Restores the default values.
   */
  @FXML
  private Button defaultButton;

  /**
   * Closes the window without saving the new entered ones.
   */
  @FXML
  private Button cancelButton;

  /**
   * Opens a help window to explain the parameters.
   */
  @FXML
  private Button helpButton;


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

    // window configuration
    GUIUtils.setColorOnButton(defaultButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(saveButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(cancelButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(helpButton, ButtonColor.GRAY);

    // read config.txt file with saved parameters
    try {
      ConfigHandler.readConfig();
    } catch (UnknownConfigFieldException | ConfigNotFoundException | IOException e) {
      GUIUtils.showInfo(AlertType.ERROR, "Configuration reading error",
          "The configuration file could not be read. Maybe it's corrupted.");
    }

    // get parameter
    avgApproximationEnd.setText(ConfigHandler.getAvgApproximationEnd() + "");
    avgApproximationStart.setText(ConfigHandler.getAvgApproximationStart() + "");
    breakcounter.setText(ConfigHandler.getBreakcounter() + "");
    startcounter.setText(ConfigHandler.getStartcounter() + "");
    numAverageNucleotides.setText(ConfigHandler.getNumAverageNucleotides() + "");

    // changeListener only allowing numbers for all fields

    avgApproximationEnd.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches("[0-9]*")) {
          avgApproximationEnd.setText(newValue);
        } else {
          avgApproximationEnd.setText(oldValue);
        }
      }
    });

    avgApproximationStart.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches("[0-9]*")) {
          avgApproximationStart.setText(newValue);
        } else {
          avgApproximationStart.setText(oldValue);
        }
      }
    });

    breakcounter.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches("[0-9]*")) {
          breakcounter.setText(newValue);
        } else {
          breakcounter.setText(oldValue);
        }
      }
    });

    startcounter.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches("[0-9]*")) {
          startcounter.setText(newValue);
        } else {
          startcounter.setText(oldValue);
        }
      }
    });

    numAverageNucleotides.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (newValue.matches("[0-9]*")) {
          numAverageNucleotides.setText(newValue);
        } else {
          numAverageNucleotides.setText(oldValue);
        }
      }
    });

    // set values to the default values, saved in the config file
    defaultButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        avgApproximationStart.setText(ConfigHandler.getDefaultValues()[0] + "");
        avgApproximationEnd.setText(ConfigHandler.getDefaultValues()[1] + "");
        breakcounter.setText(ConfigHandler.getDefaultValues()[2] + "");
        numAverageNucleotides.setText(ConfigHandler.getDefaultValues()[3] + "");
        startcounter.setText(ConfigHandler.getDefaultValues()[4] + "");

      }
    });

    // change parameter and add changes by rewriting the config file with new values
    saveButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        if (!avgApproximationEnd.getText().equals("")) {
          ConfigHandler.setAvgApproximationStart(Integer.parseInt(avgApproximationStart.getText()));
        }
        if (!avgApproximationEnd.getText().equals("")) {
          ConfigHandler.setAvgApproximationEnd(Integer.parseInt(avgApproximationEnd.getText()));
        }

        if (!breakcounter.getText().equals("")) {
          ConfigHandler.setBreakcounter(Integer.parseInt(breakcounter.getText()));
        }

        if (!startcounter.getText().equals("")) {
          ConfigHandler.setStartcounter(Integer.parseInt(startcounter.getText()));
        }

        if (!numAverageNucleotides.getText().equals("")) {
          ConfigHandler.setNumAverageNucleotides(Integer.parseInt(numAverageNucleotides.getText()));
        }

        try {
          ConfigHandler.writeConfig();
        } catch (IOException e) {
          GUIUtils.showInfo(AlertType.ERROR, "Configuration file error",
              "The configuration file could not be updated. Please try again.");
        }
      }
    });

    // close Stage
    cancelButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        SettingsWindow.addParametersOpen = false;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

      }
    });

    // open new Window with help text, saved in intern resource files
    helpButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        try {
          final FXMLLoader loader =
              new FXMLLoader(TextWindow.class.getResource("/fxml/TextWindow.fxml"));

          final Parent root = loader.load();

          TextWindow texWin = loader.<TextWindow>getController();
          String content = GUIUtils.convertStreamToString(
              ClassLoader.getSystemResourceAsStream("manual/Parameters.txt"));
          texWin.setText(content);

          Scene scene = new Scene(root);
          Stage s = new Stage();
          s.setScene(scene);
          s.sizeToScene();
          s.setTitle("GSAT - Parameter explanation");
          s.show();

        } catch (IOException e) {
          System.err.println("Parameter explanation window could not be constructed.");
          return;
        }
      }
    });
  }

  /**
   * Starts the window.
   * 
   * @param primaryStage The primary stage to be used.
   * 
   * @author Jannis Blueml
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/ParameterWindow.fxml"));
    } catch (IOException e) {
      System.err.println("Parameter window could not be constructed.");
      return;
    }

    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT - Adjust analysis parameters");
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

  /**
   * Stops the current parameter window.
   *
   * @author Kevin Otto
   */
  @Override
  public void stop() throws Exception {
    SettingsWindow.addParametersOpen = false;
    super.stop();
  }

}
