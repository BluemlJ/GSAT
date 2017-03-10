package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TextWindow extends Application implements javafx.fxml.Initializable {

	Scene scene;

	@FXML
	private TextArea testArea;
	private String text = "";

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		testArea.setWrapText(true);
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		testArea.setText(text);
		this.text = text;
	}

}
