package gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import analysis.Pair;
import io.FileSaver;
import javafx.application.Application;
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

    public static boolean settingsOpen = false;

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
	Pair<Boolean, String> output;
	infoArea.setText("Welcome to GSAT! \n");
	// read Genes and show them in the choicebox
	output = GUIUtils.initializeGeneBox(geneBox);
	infoArea.appendText(output.second + "\n");

	geneBox.setOnMouseClicked(arg01 -> GUIUtils.initializeGeneBox(geneBox));

	// gives information about new gene selection
	geneBox.getSelectionModel().selectedItemProperty().addListener((obeservable, value, newValue) -> {
	    infoArea.appendText("New Gene selected: " + newValue + "\n");
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
		bar.setProgress(-1);
		infoArea.appendText(
			"---------------------------------------------------------------------------------------------------"
				+ "\nStarting analysis\n"
				+ "---------------------------------------------------------------------------------------------------\n");

		infoArea.appendText("Source folder or file:  " + srcField.getText() + "\n");

		if (srcField.getText().equals("")) {
		    infoArea.appendText("Source path is empty, aborting analysis.");
		    bar.setProgress(0);
		    return;
		}

		infoArea.appendText("Destination folder:  " + destField.getText() + "\n");

		if (destField.getText().equals("")) {
		    infoArea.appendText("Destination path is empty, aborting analysis.");
		    bar.setProgress(0);
		    return;
		} else
		    FileSaver.setLocalPath(destField.getText());

		infoArea.appendText("Selected gene:  " + geneBox.getSelectionModel().getSelectedItem() + "\n");

		if (geneBox.getSelectionModel().getSelectedIndex() == -1) {
		    infoArea.appendText("No gene was selected, aborting analysis.");
		    bar.setProgress(0);
		    return;
		}

		infoArea.appendText(
			"---------------------------------------------------------------------------------------------------\n");
		String output;

		output = GUIUtils.runAnalysis(srcField.getText(),
			geneBox.getSelectionModel().getSelectedIndex()).second;
		infoArea.appendText(output);
		bar.setProgress(0);

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
		    final FXMLLoader loader = new FXMLLoader(TextWindow.class.getResource("/fxml/TextWindow.fxml"));

		    final Parent root = loader.load();

		    TextWindow texWin = loader.<TextWindow> getController();
		    String content = convertStreamToString(
				    ClassLoader.getSystemResourceAsStream("manual/About.txt"));
		    texWin.setText(content);

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
		    final FXMLLoader loader = new FXMLLoader(TextWindow.class.getResource("/fxml/TextWindow.fxml"));

		    final Parent root = loader.load();

		    TextWindow texWin = loader.<TextWindow> getController();
		    String content = convertStreamToString(
			    ClassLoader.getSystemResourceAsStream("manual/WelcomeToGSAT.txt"));
		    texWin.setText(content);

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
	primaryStage.setScene(scene);
	primaryStage.sizeToScene();
	primaryStage.show();
    }

    private static String convertStreamToString(InputStream is) {
	Scanner s = new Scanner(is);
	String ret = "";
	while (s.hasNextLine())
	    ret = ret + s.nextLine()+ "\n" ;
	System.out.println(ret);
	s.close();
	return ret;
    }

}
