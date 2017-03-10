package gui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

import analysis.AnalysedSequence;
import exceptions.ConfigNotFoundException;
import exceptions.DatabaseConnectionException;
import exceptions.MissingPathException;
import exceptions.UnknownConfigFieldException;
import io.ConfigHandler;
import io.DatabaseConnection;
import io.FileRetriever;
import io.FileSaver;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DatabaseWindow extends Application implements javafx.fxml.Initializable {

	private Scene scene;

	private boolean upload = true;

	@FXML
	private ToggleButton uploadToggle;
	@FXML
	private ToggleButton downloadToggle;

	@FXML
	private ToggleButton resultToggle;
	@FXML
	private ToggleButton geneToggle;
	@FXML
	private ToggleButton primerToggle;
	@FXML
	private ToggleButton allToggle;

	@FXML
	private javafx.scene.control.TextField destField;

	@FXML
	private Button destButton;
	@FXML
	private Button settingsButton;
	@FXML
	private Button startButton;

	@FXML
	private TextField researcherField;
	@FXML
	private TextField geneField;

	@FXML
	private DatePicker startDate;
	@FXML
	private DatePicker endDate;

	private ToggleGroup typeGroupe;
	private ToggleGroup usageGroupe;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		try {
			ConfigHandler.readConfig();
			DatabaseConnection.setDatabaseConnection(ConfigHandler.getDbUser(), ConfigHandler.getDbPass(),
					ConfigHandler.getDbPort(), ConfigHandler.getDbUrl());
		} catch (UnknownConfigFieldException | ConfigNotFoundException | IOException e1) {
			showAlertWindow("Failed", "Failure while reading config file");
			e1.printStackTrace();
		} catch (DatabaseConnectionException | SQLException e) {
			showAlertWindow("Failed", "Failure while connecting to database");
			e.printStackTrace();
		}

		typeGroupe = new ToggleGroup();
		resultToggle.setToggleGroup(typeGroupe);
		geneToggle.setToggleGroup(typeGroupe);
		primerToggle.setToggleGroup(typeGroupe);
		allToggle.setToggleGroup(typeGroupe);

		usageGroupe = new ToggleGroup();
		uploadToggle.setToggleGroup(usageGroupe);
		downloadToggle.setToggleGroup(usageGroupe);

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				// update primer data
				if (primerToggle.isSelected()) {

					// upload data from primer.txt
					if (uploadToggle.isSelected()) {
						try {
							uploadPrimer();
						} catch (DatabaseConnectionException | SQLException e) {
							// error while connecting to database

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Failed");
							alert.setHeaderText("Connection to database failed");
							alert.showAndWait();

							e.printStackTrace();
						} catch (NumberFormatException | IOException e) {
							// error while writing txt
							e.printStackTrace();

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Failed");
							alert.setHeaderText("Failure while reading local file");
							alert.showAndWait();
						}
					}

					// download data to primer.txt
					else if (downloadToggle.isSelected()) {
						try {
							downloadPrimer();
						} catch (DatabaseConnectionException | SQLException e) {
							// error while connecting to database
							e.printStackTrace();
							showAlertWindow("Failed", "Database connection error");
						} catch (NumberFormatException | IOException e) {
							// error while writing txt
							e.printStackTrace();
							showAlertWindow("Failed", "Writing local file failed");
						}
					}
				}

				// update gene data
				else if (geneToggle.isSelected()) {
					// upload all data from genes.txt
					if (uploadToggle.isSelected()) {
						try {
							uploadGenes();
						} catch (SQLException | DatabaseConnectionException e) {
							// error while connecting to database
							e.printStackTrace();
							showAlertWindow("Failed", "Error while connecting to database");
						} catch (IOException e) {
							// error while reading genes from txt
							e.printStackTrace();
							showAlertWindow("Failed", "Error while reading local genes");
						}
					}
					// download genes to genes.txt
					else if (downloadToggle.isSelected()) {
						try {
							downloadGenes();
						} catch (DatabaseConnectionException | SQLException e) {
							// error while connecting to database
							e.printStackTrace();
							showAlertWindow("Failed", "Connection to database failed");
						} catch (IOException e) {
							// error while writing txt
							e.printStackTrace();
							showAlertWindow("Failed", "Error while writign local gene file");
						}
					}
				}

				// sequences
				else if (resultToggle.isSelected()) {

					// upload data from file
					if (uploadToggle.isSelected()) {
						try {
							uploadResults();
						} catch (IOException e) {
							// error while reading file
							e.printStackTrace();
							showAlertWindow("Failed", "Error while reading local file");
						} catch (SQLException | DatabaseConnectionException e) {
							// error while writing to database
							e.printStackTrace();
							showAlertWindow("Failed", "Error while connecting to database");
						}
					}

					// download data to folder
					else if (downloadToggle.isSelected()) {
						try {
							downloadResults();
						} catch (SQLException | DatabaseConnectionException e) {
							showAlertWindow("Failed", "Failure while downloading sequences");
							e.printStackTrace();
						} catch (MissingPathException | IOException e) {
							showAlertWindow("Failed", "Failure while writing local file");
							e.printStackTrace();
						}
					}
				}

				else if (allToggle.isSelected()) {

					// upload everything
					if (uploadToggle.isSelected()) {
						try {
							uploadGenes();
							uploadPrimer();
							uploadResults();
						} catch (IOException e) {
							// error while reading file
							e.printStackTrace();
							showAlertWindow("Failed", "Error while reading local file");
						} catch (SQLException | DatabaseConnectionException e) {
							// error while writing to database
							e.printStackTrace();
							showAlertWindow("Failed", "Error while connecting to database");
						}
					}

					// download everything
					else if (downloadToggle.isSelected()) {
						try {
							downloadGenes();
							downloadPrimer();
							downloadResults();
						} catch (SQLException | DatabaseConnectionException e) {
							showAlertWindow("Failed", "Failure while downloading from database");
							e.printStackTrace();
						} catch (MissingPathException | IOException e) {
							showAlertWindow("Failed", "Failure while writing local file");
							e.printStackTrace();
						}
					}
				}
			}

		});

		destField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches(ConfigHandler.SEPARATOR_CHAR + "")) {
					destField.setText(oldValue);
				} else {
					destField.setText(newValue);
				}
			}
		});

		uploadToggle.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (!upload) {
					upload = true;

					researcherField.setDisable(true);
					geneField.setDisable(true);
					startDate.setDisable(true);
					endDate.setDisable(true);
				}

			}
		});

		downloadToggle.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (upload) {
					upload = false;

					researcherField.setDisable(false);
					geneField.setDisable(false);
					startDate.setDisable(false);
					endDate.setDisable(false);
				}

			}
		});

		destButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		settingsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void stop() throws Exception {
		MainWindow.settingsOpen = false;
		System.out.println("Database Closed");
		super.stop();
	}

	/*
	 * @Override public void start(Stage stage) throws Exception { Parent root;
	 * try { root =
	 * FXMLLoader.load(getClass().getResource("/fxml/DatabaseWindow.fxml")); }
	 * catch (IOException e) { e.printStackTrace(); return; } Scene scene = new
	 * Scene(root); stage.setTitle("Database"); stage.setScene(scene);
	 * stage.sizeToScene(); stage.show(); }
	 */

	private void downloadResults() throws SQLException, DatabaseConnectionException, MissingPathException, IOException {
		java.sql.Date datePickerStartDate = java.sql.Date.valueOf(startDate.getValue());
		java.sql.Date datePickerEndDate = java.sql.Date.valueOf(endDate.getValue());
		String gene = geneField.getText();
		String researcher = researcherField.getText();
		String path = destField.getText();
		ArrayList<AnalysedSequence> resList = DatabaseConnection.pullCustomSequences(datePickerStartDate,
				datePickerEndDate, researcher, gene);
		
		FileSaver.setLocalPath(path);
		FileSaver.setSeparateFiles(false);
		FileSaver.setDestFileName("database_files");
		for (AnalysedSequence res : resList) {
			FileSaver.storeResultsLocally(res.getFileName(), res);
		}

	}

	private void uploadResults() throws IOException, SQLException, DatabaseConnectionException {
		String path = destField.getText();

		LinkedList<AnalysedSequence> sequences = FileRetriever.convertFilesToSequences(path);
		DatabaseConnection.pushAllData(sequences);
		showAlertWindow("Success", "Results upload was successful");

	}

	private void downloadGenes() throws DatabaseConnectionException, SQLException, IOException {

		DatabaseConnection.pullAndSaveGenes();
		showAlertWindow("Success", "Gene download was successful");

	}

	private void uploadGenes() throws SQLException, DatabaseConnectionException, IOException {

		DatabaseConnection.pushAllGenes();
		showAlertWindow("Success", "Gene upload was successful");

	}

	private void downloadPrimer() throws NumberFormatException, DatabaseConnectionException, SQLException, IOException {

		DatabaseConnection.pullAndSavePrimer();
		showAlertWindow("Success", "Downloading primer data was successful");

	}

	private void uploadPrimer() throws NumberFormatException, DatabaseConnectionException, SQLException, IOException {

		DatabaseConnection.pushAllPrimer();
		showAlertWindow("Success", "Primer upload was successful");

	}

	private void showAlertWindow(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/fxml/DatabaseWindow.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		scene = new Scene(root);
		primaryStage.setTitle("GSAT - Database");
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
		// alow opening again when settingswindow was closed
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				MainWindow.settingsOpen = false;
			}
		});
		/*
		 * returnButton.setOnAction(new EventHandler<ActionEvent>() {
		 * 
		 * @Override public void handle(ActionEvent arg0) {
		 * primaryStage.close(); } });
		 */
	}

}
