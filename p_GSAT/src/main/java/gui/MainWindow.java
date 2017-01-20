package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import analysis.Pair;
import io.FileSaver;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainWindow extends Application implements javafx.fxml.Initializable {
  
  @FXML
  private javafx.scene.control.MenuItem aboutButton;
  // WARNING: Do not change variable name under all circumstances!
  @FXML
  private ProgressBar bar;
  @FXML
  private Button destButton;
  @FXML
  private TextField destField;
  // dropdownMenu
  @FXML
  private ChoiceBox<String> geneBox;

  // info output area
  @FXML
  private TextArea infoArea;
  // Menu Items
  @FXML
  private javafx.scene.control.MenuItem manualButton;

  // checkbox
  @FXML
  private CheckBox outputCheckbox;
  @FXML
  private Button settingsButton;

  @FXML
  private Button srcButton;

  // Textfields
  @FXML
  private TextField srcField;

  // BUTTONS
  @FXML
  private Button startButton;

  public static void main(String[] args) {
    launch(args);
  }


  /**
   * Mainwindow to initialize all components and set Eventhandlers.
   * 
   * @see Initializable
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    Pair<Boolean, String> output;
    infoArea.setText("Welcome to GSAT! \n");
    // read Genes and show them in the choicebox
    output = GUIUtils.initializeGeneBox(geneBox);
    infoArea.appendText(output.second + "\n");

    // gives information about new gene selection
    geneBox.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          infoArea.appendText("New Gene selected: " + newValue + "\n");
        });

   

    // set button to select destination
    destButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        String output;
        output = GUIUtils.setDestination(destField).second;
        infoArea.appendText(output + "\n");
      }
    });

    // select if you get only one output file
    outputCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      FileSaver.setSeparateFiles(newValue);
      if (newValue)
        infoArea.appendText("One single output file will be created. \n");
      else
        infoArea.appendText("There will be one output file for each input file. \n");
    });

    // set button to select source files
    srcButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        String output;
        output = GUIUtils.setSourceFolder(srcField).second;
        infoArea.appendText(output + "\n");

      }
    });

    // start analyzing process
    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        infoArea.appendText(
            "---------------------------------------------------------------------------------------------------"
                + "\nStarting analysis\n"
                + "---------------------------------------------------------------------------------------------------\n");

        infoArea.appendText("Source folder or file:  " + srcField.getText() + "\n");
        infoArea.appendText("Destination folder:  " + destField.getText() + "\n");
        infoArea
            .appendText("Selected gene:  " + geneBox.getSelectionModel().getSelectedItem() + "\n");
        infoArea.appendText(
            "---------------------------------------------------------------------------------------------------\n");
        String output;

        output = GUIUtils.runAnalysis(srcField.getText(),
            geneBox.getSelectionModel().getSelectedIndex()).second;
        infoArea.appendText(output);


      }
    });

    // gives you a short manu
    manualButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        System.out.println("Manual!");
      }
    });
    // ...

  }
  
  

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }



}
