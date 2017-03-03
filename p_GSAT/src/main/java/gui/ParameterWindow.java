package gui;

import exceptions.ConfigNotFoundException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ParameterWindow extends Application implements javafx.fxml.Initializable {

  @FXML
  private TextField avgApproximationEnd;

  @FXML
  private TextField avgApproximationStart;

  @FXML
  private TextField breakcounter;

  @FXML
  private TextField startcounter;

  @FXML
  private TextField numAverageNucleotides;

  @FXML
  private Button saveButton;

  @FXML
  private Button defaultButton;

  @FXML
  private Button cancelButton;

  @FXML
  private Button helpButton;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {



    GUIUtils.setColorOnButton(defaultButton, ButtonColor.GRAY);
    GUIUtils.setColorOnButton(saveButton, ButtonColor.GREEN);
    GUIUtils.setColorOnButton(cancelButton, ButtonColor.RED);
    GUIUtils.setColorOnButton(helpButton, ButtonColor.GRAY);

    try {
      ConfigHandler.readConfig();
    } catch (UnknownConfigFieldException | ConfigNotFoundException | IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    avgApproximationEnd.setText(ConfigHandler.getAvgApproximationEnd() + "");
    avgApproximationStart.setText(ConfigHandler.getAvgApproximationStart() + "");
    breakcounter.setText(ConfigHandler.getBreakcounter() + "");
    startcounter.setText(ConfigHandler.getStartcounter() + "");
    numAverageNucleotides.setText(ConfigHandler.getNumAverageNucleotides() + "");

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
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        SettingsWindow.addParametersOpen = false;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
      }
    });


    cancelButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        SettingsWindow.addParametersOpen = false;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

      }
    });

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
          e.printStackTrace();
          return;
        }
      }
    });
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("/fxml/ParameterWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
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

  @Override
  public void stop() throws Exception {
    SettingsWindow.addParametersOpen = false;
    super.stop();
  }
}
