package gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import analysis.Pair;
import io.FileSaver;
import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

public class MainWindow extends Application implements javafx.fxml.Initializable {

  public static boolean settingsOpen = false;
  public static boolean autoGeneSearch = false;
  
  @FXML
  private javafx.scene.control.MenuItem aboutButton;
  // WARNING: Do not change variable name under all circumstances!

  @FXML
  private ProgressBar bar;

  // BUTTONS
  @FXML
  private Button destButton;
  @FXML
  private Button startButton;
  @FXML
  private Button settingsButton;
  @FXML
  private Button srcButton;

  // Textfields
  @FXML
  private TextField srcField;
  @FXML
  private TextField destField;

  @FXML
  private TextField fileNameField;

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
  private CheckBox findGeneCheckbox;

  Stage primaryStage;

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
    bar.setProgress(0);
    FileSaver.setSeparateFiles(false);
    GUIUtils.setColorOnNode(startButton, Color.BLUE);
    GUIUtils.setColorOnNode(settingsButton, Color.BLUE);
    
    Pair<Boolean, String> output;
    infoArea.setText("Welcome to GSAT! \n");
    // read Genes and show them in the choicebox


    output = GUIUtils.initializeGeneBox(geneBox);
    geneBox.setStyle("-fx-font-style: italic;");


    infoArea.appendText(output.second + "\n");

    geneBox.setOnMouseClicked(arg01 -> GUIUtils.initializeGeneBox(geneBox));

    // gives information about new gene selection
    geneBox.getSelectionModel().selectedItemProperty()
        .addListener((obeservable, value, newValue) -> {
          if (newValue != null) infoArea.appendText("New Gene selected: " + newValue + "\n");
        });

    // set button to select destination
    destButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        String output;
        output = GUIUtils.setDestination(destField, srcField.getText()).second;
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

        if (srcField.getText().equals("")) {
          infoArea.appendText("Source path is empty, aborting analysis.");
          return;
        }


        infoArea.appendText("Destination folder:  " + destField.getText() + "\n");

        if (destField.getText().equals("")) {
          infoArea.appendText("Destination path is empty, aborting analysis.");
          // bar.setProgress(0);
          return;
        } else
          FileSaver.setLocalPath(destField.getText());

        if (autoGeneSearch)
			infoArea.appendText("Selected gene:  automatic search\n");
		else
			infoArea.appendText("Selected gene:  " + geneBox.getSelectionModel().getSelectedItem() + "\n");

        if (geneBox.getSelectionModel().getSelectedIndex() == -1) {
          if (autoGeneSearch == false) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("No gene was selected!");
            alert.setContentText("Find gene automatically?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {

            } else {
              return;
            }
          }
        }

        infoArea.appendText(
            "---------------------------------------------------------------------------------------------------\n");


        javafx.concurrent.Task<Void> mainTask = new javafx.concurrent.Task<Void>() {

          @Override
          protected Void call() throws Exception {
            String output;

            String srcFieldTest = srcField.getText();
            String destfileNameText = fileNameField.getText();
            String geneBoxItem;
            if (autoGeneSearch)
              geneBoxItem = "-1";
            else
              geneBoxItem = geneBox.getSelectionModel().getSelectedItem().split(" ")[0];

            output = GUIUtils.runAnalysis(srcFieldTest, geneBoxItem, destfileNameText, bar).second;
            infoArea.appendText(output);
         
            return null;
          }

        };


        //bar.setProgress(-1);
        // mainThread.start();
        mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
          @Override
          public void handle(WorkerStateEvent t) {
            bar.setProgress(0);
          }
        });
        new Thread(mainTask).start();

      }

    });

    // select if you get only one output file
    findGeneCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      FileSaver.setSeparateFiles(newValue);
      if (newValue) {
        autoGeneSearch = true;
        geneBox.getSelectionModel().clearSelection();
        infoArea.appendText("The gene that fits best will be searched.");
      } else {
        autoGeneSearch = false;
        infoArea.appendText("No automatic gene search.");
      }
    });

    // set settings button to open settings window
    settingsButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        // only open window if no Settings Window is open
        if (!settingsOpen) {
          settingsOpen = true;
          SettingsWindow settings = new SettingsWindow();
          try {
            settings.start(new Stage());
          } catch (Exception e) {
            // TODO Auto-generated catch block
          }
        }
      }
    });

    // set settings button to open settings window
    aboutButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        // TextWindow textWin = new TextWindow();
        try {
          final FXMLLoader loader =
              new FXMLLoader(TextWindow.class.getResource("/fxml/TextWindow.fxml"));

          final Parent root = loader.load();

          TextWindow texWin = loader.<TextWindow>getController();
          String content =
              convertStreamToString(ClassLoader.getSystemResourceAsStream("manual/About.txt"));
          texWin.setText(content);
          texWin.setName("About");
     
          Scene scene = new Scene(root);
          Stage s = new Stage();
          s.setScene(scene);
          s.sizeToScene();
          s.show();

        } catch (IOException e) {
          e.printStackTrace();
          return;
        }
      }
    });

    // ...

    manualButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        try {
          final FXMLLoader loader =
              new FXMLLoader(TextWindow.class.getResource("/fxml/TextWindow.fxml"));

          final Parent root = loader.load();

          TextWindow texWin = loader.<TextWindow>getController();
          String content = convertStreamToString(
              ClassLoader.getSystemResourceAsStream("manual/WelcomeToGSAT.txt"));
          texWin.setText(content);
          texWin.setName("Help");
          Scene scene = new Scene(root);
          Stage s = new Stage();
          s.setScene(scene);
          s.sizeToScene();
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
      root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(root);
    primaryStage.setTitle("GSAT");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }

  private static String convertStreamToString(InputStream is) {
    Scanner s = new Scanner(is);
    String ret;
    StringBuilder builder = new StringBuilder();
    while (s.hasNextLine())
      builder.append(s.nextLine()).append("\n");
    ret = builder.toString();
    System.out.println(ret);
    s.close();
    return ret;
  }
  
  

}
