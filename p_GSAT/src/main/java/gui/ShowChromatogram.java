package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import analysis.AnalysedSequence;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ShowChromatogram extends Application implements javafx.fxml.Initializable {

	java.awt.Color colorA = new java.awt.Color(0, 255, 0);
	java.awt.Color colorT = new java.awt.Color(255, 0, 0);
	java.awt.Color colorG = new java.awt.Color(0, 0, 0);
	java.awt.Color colorC = new java.awt.Color(0, 0, 255);
	LinkedList<AnalysedSequence> sequences;

	private int numberRange = 200;

	private int activeSequence = 0;

	// initial scroll possitino
	private int initialPos = 0;

	private int maxScroll = 10;
	private int minScroll = 5;

	javafx.scene.image.Image img;

	private ScrollPane scrollPane;

	private Button next = new Button();

	private Button prev = new Button();

	private ScrollBar bar = new ScrollBar();

	private XYChart.Series<Number, Number> seriesA = new XYChart.Series<Number, Number>();

	private XYChart.Series<Number, Number> seriesT = new XYChart.Series<Number, Number>();

	private XYChart.Series<Number, Number> seriesG = new XYChart.Series<Number, Number>();

	private XYChart.Series<Number, Number> seriesC = new XYChart.Series<Number, Number>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	private Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception {

		scrollPane = new ScrollPane();
		scrollPane.setMaxHeight(600);
		scrollPane.setMinHeight(600);

		VBox v = new VBox(scrollPane);

		scene = new Scene(v, 800, 600);

		primaryStage.setTitle("GSAT - Settings");
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void updateSequences(int id) {
		System.out.println("seqence Update");

		activeSequence = id;

		AnalysedSequence startSequence = sequences.get(id);

		int[] channelA = startSequence.getChannelA();
		int[] channelC = startSequence.getChannelC();
		int[] channelT = startSequence.getChannelT();
		int[] channelG = startSequence.getChannelG();

		int last = (int) channelA.length;

		last = (int) Math.min(last, channelC.length);
		last = (int) Math.min(last, channelT.length);
		last = (int) Math.min(last, channelG.length);

		System.out.println("new Canvas");
		ImageView viewr = new ImageView();
		System.out.println("add canvas");
		try {
			scrollPane.setContent(viewr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		double strechX = 0.25;
		int strechY = 4;

		System.out.println("chromatogram length = " + last);

		BufferedImage buffImg = new BufferedImage(last * strechY, 400, BufferedImage.TYPE_INT_RGB);
		buffImg.createGraphics();
		Graphics buffGraph = buffImg.getGraphics();

		buffGraph.setColor(java.awt.Color.WHITE);
		buffGraph.fillRect(0, 0, last * strechY, 400);

		int lastA = 0;
		int lastT = 0;
		int lastG = 0;
		int lastC = 0;

		// A
		int lastAmax = 0;
		int nextAmax = 0;
		int lastAhight = 0;
		int lastACandidate = 0;
		int aHold = 0;
		boolean aFalling = true;

		// T
		int lastTmax = 0;
		int nextTmax = 0;
		int lastThight = 0;
		int lastTCandidate = 0;
		int tHold = 0;
		boolean tFalling = true;

		// G
		int lastGmax = 0;
		int nextGmax = 0;
		int lastGhight = 0;
		int lastGCandidate = 0;
		int gHold = 0;
		boolean gFalling = true;

		// C
		int lastCmax = 0;
		int nextCmax = 0;
		int lastChight = 0;
		int lastCCandidate = 0;
		int cHold = 0;
		boolean cFalling = true;

		for (int i = 0; i < last; i++) {
			int a = (int) (400 - ((channelA[i]) * strechX));
			int t = (int) (400 - ((channelT[i]) * strechX));
			int g = (int) (400 - ((channelG[i]) * strechX));
			int c = (int) (400 - ((channelC[i]) * strechX));

			// A
			buffGraph.setColor(colorA);
			buffGraph.drawLine((i - 1) * strechY, lastA, i * strechY, a);

			// T
			buffGraph.setColor(colorT);
			buffGraph.drawLine((i - 1) * strechY, lastT, i * strechY, t);

			// G
			buffGraph.setColor(colorG);
			buffGraph.drawLine((i - 1) * strechY, lastG, i * strechY, g);

			// C
			buffGraph.setColor(colorC);
			buffGraph.drawLine((i - 1) * strechY, lastC, i * strechY, c);

			// A
			if (channelA[i] >= lastACandidate) {
				if (!aFalling) {
					aHold++;
				} else {
					aHold = 0;
				}
				aFalling = false;

			} else {
				if (aFalling) {
					aHold++;
				} else {
					aHold = 0;
					nextAmax = i - 1;
				}

				aFalling = true;
				aHold++;
			}

			if (aHold > 5 && lastAmax != nextAmax) {
				if (channelA[nextAmax] > channelT[nextAmax] && channelA[nextAmax] > channelG[nextAmax]
						&& channelA[nextAmax] > channelC[nextAmax]) {

					buffGraph.setColor(colorA);
					buffGraph.drawLine((nextAmax) * strechY, 0, (nextAmax) * strechY, 400);
				}
			}
			// *********************************************T
			if (channelT[i] >= lastTCandidate) {
				if (!tFalling) {
					tHold++;
				} else {
					tHold = 0;
				}
				tFalling = false;

			} else {
				if (tFalling) {
					tHold++;
				} else {
					tHold = 0;
					nextTmax = i - 1;
				}

				tFalling = true;
				tHold++;
			}

			if (tHold > 5 && lastTmax != nextTmax) {
				if (channelT[nextTmax] > channelA[nextTmax] && channelT[nextTmax] > channelG[nextTmax]
						&& channelT[nextTmax] > channelC[nextTmax]) {

					buffGraph.setColor(colorT);
					buffGraph.drawLine((nextTmax) * strechY, 0, (nextTmax) * strechY, 400);
				}
			}
			// ********************************G
			if (channelG[i] >= lastGCandidate) {
				if (!gFalling) {
					gHold++;
				} else {
					gHold = 0;
				}
				gFalling = false;

			} else {
				if (gFalling) {
					gHold++;
				} else {
					gHold = 0;
					nextGmax = i - 1;
				}

				gFalling = true;
				gHold++;
			}

			if (gHold > 5 && lastGmax != nextGmax) {
				if (channelG[nextGmax] > channelT[nextGmax] && channelG[nextGmax] > channelA[nextGmax]
						&& channelG[nextGmax] > channelC[nextGmax]) {

					buffGraph.setColor(colorG);
					buffGraph.drawLine((nextGmax) * strechY, 0, (nextGmax) * strechY, 400);
				}
			}
			// ***********************C
			if (channelC[i] >= lastCCandidate) {
				if (!cFalling) {
					cHold++;
				} else {
					cHold = 0;
				}
				cFalling = false;

			} else {
				if (cFalling) {
					cHold++;
				} else {
					cHold = 0;
					nextCmax = i - 1;
				}

				cFalling = true;
				cHold++;
			}

			if (cHold > 5 && lastCmax != nextCmax) {
				if (channelC[nextCmax] > channelT[nextCmax] && channelC[nextCmax] > channelG[nextCmax]
						&& channelC[nextCmax] > channelA[nextCmax]) {

					buffGraph.setColor(colorC);
					buffGraph.drawLine((nextCmax) * strechY, 0, (nextCmax) * strechY, 400);
				}
			}

			lastACandidate = channelA[i];
			lastTCandidate = channelT[i];
			lastGCandidate = channelG[i];
			lastCCandidate = channelC[i];
			//

			lastA = a;
			lastT = t;
			lastG = g;
			lastC = c;

		}

		try

		{
			File outputfile = new File("resources/GeneData/chrom.png");
			ImageIO.write(buffImg, "png", outputfile);

		} catch (IOException e1) {

			e1.printStackTrace();
		}

		System.out.println("getGraph");
		// GraphicsContext graph = chartCanvas.getGraphicsContext2D();
		try {
			FileInputStream fin = new FileInputStream(new File("resources/GeneData/chrom.png"));
			Image img = new Image(fin);
			viewr.setImage(img);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("Finished");
	}

	public void setSequence(AnalysedSequence sequence) {
		this.sequences = new LinkedList<AnalysedSequence>();
		this.sequences.add(sequence);
		updateSequences(0);
	}

	public void setSequence(LinkedList<AnalysedSequence> sequences) {
		this.sequences = sequences;
		if (sequences.size() > 1) {
			next.setDisable(false);
		}
		updateSequences(0);
	}

}
