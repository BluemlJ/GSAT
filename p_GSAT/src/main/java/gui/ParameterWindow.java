package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;
import io.ConfigHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
	private Button cancelButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		GUIUtils.setColorOnNode(saveButton, Color.GREEN);
		GUIUtils.setColorOnNode(cancelButton, Color.RED);

		try {
			ConfigHandler.readConfig();
		} catch (ConfigReadException | ConfigNotFoundException | IOException e1) {
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
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("[0-9]*")) {
					avgApproximationEnd.setText(newValue);
				} else {
					avgApproximationEnd.setText(oldValue);
				}
			}
		});

		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
			System.out.println("hallo");
				if (!avgApproximationStart.getText().equals("")) {
					ConfigHandler.setAvgApproximationStart(Integer.parseInt(avgApproximationStart.getText()));
				}

				if (!avgApproximationEnd.getText().equals("")) {
					ConfigHandler.setAvgApproximationStart(Integer.parseInt(avgApproximationEnd.getText()));
				}

				if (!breakcounter.getText().equals("")) {
					ConfigHandler.setAvgApproximationStart(Integer.parseInt(breakcounter.getText()));
				}

				if (!startcounter.getText().equals("")) {
					ConfigHandler.setAvgApproximationStart(Integer.parseInt(startcounter.getText()));
				}

				if (!numAverageNucleotides.getText().equals("")) {
					ConfigHandler.setAvgApproximationStart(Integer.parseInt(numAverageNucleotides.getText()));
				}

				try {
					ConfigHandler.writeConfig();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Stage stage = (Stage) cancelButton.getScene().getWindow();
				stage.close();
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
				SettingsWindow.subsettingsOpen = false;
			}
		});
	}

	@Override
	public void stop() throws Exception {
		SettingsWindow.subsettingsOpen = false;
		super.stop();
	}
}
