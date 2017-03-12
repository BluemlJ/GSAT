package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import exceptions.DuplicateGeneException;
import io.ConfigHandler;
import io.GeneHandler;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AddGeneWindow extends Application implements javafx.fxml.Initializable {

	private SettingsWindow parent;

	// fields
	@FXML
	private TextField nameField;

	@FXML
	private TextField organismField;

	@FXML
	private javafx.scene.control.TextArea geneArea;

	@FXML
	private javafx.scene.control.TextArea commentArea;

	@FXML
	private Button confirmButton;

	@FXML
	private Button cancelButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		geneArea.setWrapText(true);
		commentArea.setWrapText(true);

		GUIUtils.setColorOnButton(confirmButton, ButtonColor.GREEN);
		GUIUtils.setColorOnButton(cancelButton, ButtonColor.RED);

		nameField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
					nameField.setText(oldValue);
				} else {
					nameField.setText(newValue);
				}
			}
		});

		organismField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.contains(ConfigHandler.SEPARATOR_CHAR + "")) {
					organismField.setText(oldValue);
				} else {
					organismField.setText(newValue);
				}
			}
		});

		geneArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.toUpperCase().matches(".*[^ATCG].*")) {
					geneArea.setText(oldValue);
				} else {
					geneArea.setText(newValue);
				}
			}
		});

		commentArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
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
				if (!nameField.getText().isEmpty() && !geneArea.getText().isEmpty()) {
					try {
						if (organismField.getText().equals("")) {
							organismField.setText("none");
						}
						if (GeneHandler.addGene(nameField.getText(), geneArea.getText(), organismField.getText(),
								commentArea.getText())) {

						    GUIUtils.showInfo(AlertType.INFORMATION, "Adding a gene", "Gene added successfully.");
							MainWindow.changesOnGenes = true;
							parent.updateGenes();
							Stage stage = (Stage) cancelButton.getScene().getWindow();
							stage.close();
						} else {		
						  GUIUtils.showInfo(AlertType.ERROR, "Faild to add a gene", "Gene added not successful because gene already exists in local file.");
						}

					} catch (DuplicateGeneException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
			root = FXMLLoader.load(getClass().getResource("/fxml/AddGeneWindow.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(root);
		primaryStage.setTitle("GSAT - Adding a gene");
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
