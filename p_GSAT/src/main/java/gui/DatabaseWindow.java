package gui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import analysis.AnalysedSequence;
import exceptions.DatabaseConnectionException;
import io.ConfigHandler;
import io.DatabaseConnection;
import io.FileRetriever;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
						DatabaseConnection.pushAllPrimer();
					} catch (DatabaseConnectionException | SQLException e) {
						// error while connecting to database
						e.printStackTrace();
					} catch(NumberFormatException | IOException e){
						// error while writing txt
						e.printStackTrace();
					}
				}
				
				// download data to primer.txt
				else if (downloadToggle.isSelected()) {
					try {
						DatabaseConnection.pullAndSavePrimer();
					} catch (DatabaseConnectionException | SQLException e) {
						// error while connecting to database
						e.printStackTrace();
					} catch(NumberFormatException | IOException e){
						// error while writing txt
						e.printStackTrace();
					}
				}
			}
			
			// update gene data
			if (geneToggle.isSelected()) {
				// upload all data from genes.txt
				if (uploadToggle.isSelected()) {
					try {
						DatabaseConnection.pushAllGenes();
					} catch (SQLException | DatabaseConnectionException e) {
						// error while connecting to database
						e.printStackTrace();
					} catch (IOException e){
						// error while reading genes from txt
						e.printStackTrace();
					}
				}
				// download genes to genes.txt
				else if (downloadToggle.isSelected()) {
					try {
						DatabaseConnection.pullAndSaveGenes();
					} catch (DatabaseConnectionException | SQLException e) {
						// error while connecting to database
						e.printStackTrace();
					} catch (IOException e){
						// error while writing txt
						e.printStackTrace();
					}
				}
			}

			// sequences
			if (resultToggle.isSelected()) {
				
				// upload data from file
				if (uploadToggle.isSelected()) {
					String path = destField.getText();
					try {
						LinkedList<AnalysedSequence> sequences = FileRetriever.convertFilesToSequences(path);
						DatabaseConnection.pushAllData(sequences);
					} catch (IOException e) {
						// error while reading file
						e.printStackTrace();
					} catch (SQLException | DatabaseConnectionException e) {
						// error while writing to database
						e.printStackTrace();
					}
				}
				
				// download data to folder
				else if (downloadToggle.isSelected()) {
					//TODO
					//java.sql.Date datePickerDate = java.sql.Date.valueOf(datePicker.getValue());
				}
			}
		}
	});

    destField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
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
   * @Override public void start(Stage stage) throws Exception { Parent root; try { root =
   * FXMLLoader.load(getClass().getResource("/fxml/DatabaseWindow.fxml")); } catch (IOException e) {
   * e.printStackTrace(); return; } Scene scene = new Scene(root); stage.setTitle("Database");
   * stage.setScene(scene); stage.sizeToScene(); stage.show(); }
   */

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
     * @Override public void handle(ActionEvent arg0) { primaryStage.close(); } });
     */
  }

}
