package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

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

	@FXML
	private Text folderText;

	@FXML
	private TextFlow infoArea;

	private ToggleGroup typeGroupe;
	private ToggleGroup usageGroupe;

	private final String uploadFail = "Upload to database failed.";
	private final String downloadFail = "Download from database failed";
	private final String writeFail = "Writing local file failed.";
	private final String readFail = "Reading local file failed.";
	private final String uploadSuccess = "Upload to database complete.";
	private final String downloadSuccess = "Download from database complete.";

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// set calender to german standard
		startDate.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					try {
						return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
					} catch (DateTimeException dte) {
					}
					System.out.println("Format Error");
				}
				return "";
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					try {
						return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					} catch (DateTimeParseException dtpe) {
					}
					System.out.println("Parse Error");
				}
				return null;
			}
		});

		endDate.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					try {
						return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
					} catch (DateTimeException dte) {
					}
					System.out.println("Format Error");
				}
				return "";
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					try {
						return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					} catch (DateTimeParseException dtpe) {
					}
					System.out.println("Parse Error");
				}
				return null;
			}
		});

		try {
			ConfigHandler.readConfig();
			DatabaseConnection.setDatabaseConnection(ConfigHandler.getDbUser(), ConfigHandler.getDbPass(),
					ConfigHandler.getDbPort(), ConfigHandler.getDbUrl());
		} catch (UnknownConfigFieldException | ConfigNotFoundException | IOException e1) {
			GUIUtils.showInfo(AlertType.ERROR, "Failed", "Failure while reading config file");
			e1.printStackTrace();
		} catch (DatabaseConnectionException | SQLException e) {
			GUIUtils.showInfo(AlertType.ERROR, "Failed", "Failure while connecting to database");
			e.printStackTrace();
		}

		typeGroupe = new ToggleGroup();
		resultToggle.setToggleGroup(typeGroupe);
		geneToggle.setToggleGroup(typeGroupe);
		primerToggle.setToggleGroup(typeGroupe);
		allToggle.setToggleGroup(typeGroupe);
		
		activateOnlyPath();

		resultToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (resultToggle.equals(typeGroupe.getSelectedToggle())) {
					mouseEvent.consume();
				}
				// upload only path value is needed
				if (uploadToggle.isSelected()) {
					activateOnlyPath();
				} else if(downloadToggle.isSelected()){
					activateEverything();
				}
			}
		});

		geneToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (geneToggle.equals(typeGroupe.getSelectedToggle())) {
					mouseEvent.consume();
				}
				// no additional user input needed
				activateNothing();
			}
		});

		primerToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (primerToggle.equals(typeGroupe.getSelectedToggle())) {
					mouseEvent.consume();
				}
				// no additional user input needed
				activateNothing();
			}
		});

		allToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (allToggle.equals(typeGroupe.getSelectedToggle())) {
					mouseEvent.consume();
				}
				if (downloadToggle.isSelected()) {
					activateEverything();
				} else if(uploadToggle.isSelected()){
					activateOnlyPath();
				}
			}
		});

		usageGroupe = new ToggleGroup();
		uploadToggle.setToggleGroup(usageGroupe);
		downloadToggle.setToggleGroup(usageGroupe);

		uploadToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (uploadToggle.equals(usageGroupe.getSelectedToggle())) {
					mouseEvent.consume();
				}
				if (primerToggle.isSelected() || geneToggle.isSelected()) {
					activateNothing();
				} else if(resultToggle.isSelected() || allToggle.isSelected()){
					activateOnlyPath();
				}
			}
		});

		downloadToggle.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (downloadToggle.equals(usageGroupe.getSelectedToggle())) {
					mouseEvent.consume();
				}
				if (primerToggle.isSelected() || geneToggle.isSelected()) {
					activateNothing();
				} else if(resultToggle.isSelected() || allToggle.isSelected()){
					activateEverything();
				}
			}
		});

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				// update primer data
				if (primerToggle.isSelected()) {

					// upload data from primer.txt
					if (uploadToggle.isSelected()) {
						try {
							uploadPrimer();
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", uploadSuccess);
						} catch (DatabaseConnectionException | SQLException e) {
							// error while connecting to database

							GUIUtils.showInfo(AlertType.ERROR, "Error", uploadFail);

							e.printStackTrace();
						} catch (NumberFormatException | IOException e) {
							// error while writing txt
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", readFail);
						}
					}

					// download data to primer.txt
					else if (downloadToggle.isSelected()) {
						try {
							downloadPrimer();
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", downloadSuccess);
						} catch (DatabaseConnectionException | SQLException e) {
							// error while connecting to database
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", downloadFail);
						} catch (NumberFormatException | IOException e) {
							// error while writing txt
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", writeFail);
						}
					}
				}

				// update gene data
				else if (geneToggle.isSelected()) {
					// upload all data from genes.txt
					if (uploadToggle.isSelected()) {
						try {
							uploadGenes();
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", uploadSuccess);
						} catch (SQLException | DatabaseConnectionException e) {
							// error while connecting to database
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", uploadFail);
						} catch (IOException e) {
							// error while reading genes from txt
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", readFail);
						}
					}
					// download genes to genes.txt
					else if (downloadToggle.isSelected()) {
						try {
							downloadGenes();
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", downloadSuccess);
						} catch (DatabaseConnectionException | SQLException e) {
							// error while connecting to database
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", downloadFail);
						} catch (IOException e) {
							// error while writing txt
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", writeFail);
						}
					}
				}

				// sequences
				else if (resultToggle.isSelected()) {

					// upload data from file
					if (uploadToggle.isSelected()) {
						try {
							uploadResults();
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", uploadSuccess);
						} catch (IOException e) {
							// error while reading file
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", readFail);
						} catch (SQLException | DatabaseConnectionException e) {
							// error while writing to database
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", uploadFail);
						}
					}

					// download data to folder
					else if (downloadToggle.isSelected()) {
						try {
							downloadResults();
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", downloadSuccess);
						} catch (SQLException | DatabaseConnectionException e) {
							GUIUtils.showInfo(AlertType.ERROR, "Error", downloadFail);
							e.printStackTrace();
						} catch (MissingPathException | IOException e) {
							GUIUtils.showInfo(AlertType.ERROR, "Error", writeFail);
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
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", uploadSuccess);
						} catch (IOException e) {
							// error while reading file
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", readFail);
						} catch (SQLException | DatabaseConnectionException e) {
							// error while writing to database
							e.printStackTrace();
							GUIUtils.showInfo(AlertType.ERROR, "Error", uploadFail);
						}
					}

					// download everything
					else if (downloadToggle.isSelected()) {
						try {
							downloadGenes();
							downloadPrimer();
							downloadResults();
							GUIUtils.showInfo(AlertType.CONFIRMATION, "Success", downloadSuccess);
						} catch (SQLException | DatabaseConnectionException e) {
							GUIUtils.showInfo(AlertType.ERROR, "Error", downloadFail);
							e.printStackTrace();
						} catch (MissingPathException | IOException e) {
							GUIUtils.showInfo(AlertType.ERROR, "Error", writeFail);
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
					/*
					 * researcherField.setDisable(true);
					 * geneField.setDisable(true); startDate.setDisable(true);
					 * endDate.setDisable(true);
					 */
				}

			}
		});

		downloadToggle.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (upload) {
					upload = false;
					/*
					 * researcherField.setDisable(false);
					 * geneField.setDisable(false); startDate.setDisable(false);
					 * endDate.setDisable(false);
					 */
				}

			}
		});

		destButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Set path to the .ab1 files (folder)");
				File start = new File("user.home");
				chooser.setInitialDirectory(start);
				File selectedDirectory = null;
				try {
					selectedDirectory = chooser.showDialog(null);
				} catch (java.lang.IllegalArgumentException e) {
					chooser.setInitialDirectory(new File(System.getProperty("user.home")));
					selectedDirectory = chooser.showDialog(null);
				}
				destField.setText(selectedDirectory.getAbsolutePath());

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
		java.sql.Date datePickerStartDate;
		if (startDate.getValue() == null) {
			datePickerStartDate = null;
		} else {
			datePickerStartDate = java.sql.Date.valueOf(startDate.getValue());
		}
		java.sql.Date datePickerEndDate;
		if (endDate.getValue() == null) {
			datePickerEndDate = null;
		} else {
			datePickerEndDate = java.sql.Date.valueOf(endDate.getValue());
		}

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

	}

	private void downloadGenes() throws DatabaseConnectionException, SQLException, IOException {

		DatabaseConnection.pullAndSaveGenes();

	}

	private void uploadGenes() throws SQLException, DatabaseConnectionException, IOException {

		DatabaseConnection.pushAllGenes();

	}

	private void downloadPrimer() throws NumberFormatException, DatabaseConnectionException, SQLException, IOException {

		DatabaseConnection.pullAndSavePrimer();

	}

	private void uploadPrimer() throws NumberFormatException, DatabaseConnectionException, SQLException, IOException {

		DatabaseConnection.pushAllPrimer();

	}

	private void activateOnlyPath() {
		destButton.setDisable(false);
		destField.setDisable(false);
		researcherField.setDisable(true);
		geneField.setDisable(true);
		startDate.setDisable(true);
		endDate.setDisable(true);
	}

	private void activateEverything() {
		destButton.setDisable(false);
		destField.setDisable(false);
		researcherField.setDisable(false);
		geneField.setDisable(false);
		startDate.setDisable(false);
		endDate.setDisable(false);
	}

	private void activateNothing() {
		destButton.setDisable(true);
		destField.setDisable(true);
		researcherField.setDisable(true);
		geneField.setDisable(true);
		startDate.setDisable(true);
		endDate.setDisable(true);
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
