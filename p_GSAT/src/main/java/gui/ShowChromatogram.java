package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import analysis.AnalysedSequence;
import analysis.Gene;
import analysis.MutationAnalysis;
import analysis.QualityAnalysis;
import analysis.StringAnalysis;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ShowChromatogram extends Application implements javafx.fxml.Initializable {

  private final Color colorA = new Color(170, 220, 80);     // green
  private final Color colorT = new Color(240, 60, 60);      // red
  private final Color colorG = Color.BLACK;                 // black
  private final Color colorC = new Color(110, 180, 200);    // blue
  private final Color background = new Color(244, 244, 244);// blue

  private int lineThickness = 5;

  LinkedList<AnalysedSequence> sequences;

  private int activeSequence = 0;


  Image img;

  private ScrollPane scrollPane;

  Button prevs;
  Button next;
  Label fileName = new Label();

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

  }

  private Scene scene;

  @Override
  public void start(Stage primaryStage) throws Exception {


    scrollPane = new ScrollPane();
    // scrollPane.setMaxHeight(600);
    scrollPane.setMinHeight(420);
    scrollPane.setMaxHeight(Double.MAX_VALUE);
    scrollPane.setMaxWidth(Double.MAX_VALUE);

    next = new Button("Next file");
    prevs = new Button("Previous file");

    next.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        if (activeSequence + 1 < sequences.size()) {
          updateSequences(activeSequence + 1);
          if (activeSequence + 1 >= sequences.size()) {
            next.setDisable(true);
          }
        }
        prevs.setDisable(false);
      }
    });


    prevs.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        if (activeSequence - 1 >= 0) {
          updateSequences(activeSequence - 1);
          if (activeSequence - 1 <= 0) {
            prevs.setDisable(true);
          }
        }
        next.setDisable(false);

      }
    });

    Button export = new Button("Export");
    export.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        String filename = sequences.get(activeSequence).getFileName();
        fileChooser.setInitialFileName(filename.substring(0, filename.length() - 3) + "png");

        fileChooser.setTitle("Save as image");
        File file = fileChooser.showSaveDialog(primaryStage);


        if (file != null) {
          try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
          } catch (IOException ex) {
            System.out.println(ex.getMessage());
          }
        }
      }
    });

    Button close = new Button("Close");

    close.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();

      }
    });


    HBox buttonLeft = new HBox(prevs, next);
    buttonLeft.setAlignment(Pos.BOTTOM_LEFT);
    buttonLeft.setSpacing(10);

    HBox buttonRight = new HBox(export, close);
    buttonRight.setAlignment(Pos.BOTTOM_RIGHT);
    buttonRight.setSpacing(10);

    HBox buttonBox = new HBox(fileName, buttonLeft, buttonRight);
    buttonBox.setSpacing(100);
    buttonBox.setAlignment(Pos.BOTTOM_CENTER);
    buttonBox.setPadding(new Insets(10, 10, 10, 10));

    VBox v = new VBox(scrollPane, buttonBox);
    v.setMinWidth(800);
    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    scene = new Scene(v);


    primaryStage.setTitle("GSAT - Chromatogram view");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }


  private void updateSequences(int id) {



    activeSequence = id;

    // get necessary variables
    AnalysedSequence startSequence = sequences.get(id);
    fileName.setText(startSequence.getFileName());

    int[] channelA = startSequence.getChannelA();
    int[] channelC = startSequence.getChannelC();
    int[] channelT = startSequence.getChannelT();
    int[] channelG = startSequence.getChannelG();

    int[] baseCalls = startSequence.getBaseCalls();

    // analyse start of aminoacids
    try {
      Gene refgene = StringAnalysis.findRightGene(startSequence);
      startSequence.setReferencedGene(refgene);
      StringAnalysis.findOffset(startSequence);
    } catch (Exception e) {
      e.printStackTrace();
    }



    // determine length of chromatogram
    int last = (int) channelA.length;

    last = (int) Math.min(last, channelC.length);
    last = (int) Math.min(last, channelT.length);
    last = (int) Math.min(last, channelG.length);

    // create new viewer and set image
    ImageView viewer = new ImageView();


    try {
      scrollPane.setContent(viewer);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // variables for scaling image
    double stretchY = 0.2;
    int stretchX = 4;

    System.out.println("chromatogram length = " + last);

    // create image
    BufferedImage buffImg = new BufferedImage(last * stretchX, 400, BufferedImage.TYPE_INT_RGB);
    buffImg.createGraphics();
    Graphics2D buffGraph = (Graphics2D) buffImg.getGraphics();

    BasicStroke bigStroke =
        new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    BasicStroke smallStroke =
        new BasicStroke(lineThickness / 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    // graphics settings
    RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    renderingHints.add(
        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

    buffGraph.setRenderingHints(renderingHints);

    System.out.println("image Created");

    buffGraph.setColor(background);
    buffGraph.fillRect(0, 0, last * stretchX, 400);
    System.out.println("background set");

    // remember last location to draw line
    int lastA = 0;
    int lastT = 0;
    int lastG = 0;
    int lastC = 0;

    int basecallIndex = 0;

    for (int i = 0; i < last; i++) {
      buffGraph.setStroke(bigStroke);
      // scale traces to match image coordinate system
      int aBasecall = (int) (400 - ((channelA[i]) * stretchY));
      int tBasecall = (int) (400 - ((channelT[i]) * stretchY));
      int gBasecall = (int) (400 - ((channelG[i]) * stretchY));
      int cBasecall = (int) (400 - ((channelC[i]) * stretchY));

      // draw A
      buffGraph.setColor(colorA);
      buffGraph.drawLine((i - 1) * stretchX, lastA, i * stretchX, aBasecall);

      // draw T
      buffGraph.setColor(colorT);
      buffGraph.drawLine((i - 1) * stretchX, lastT, i * stretchX, tBasecall);

      // draw G
      buffGraph.setColor(colorG);
      buffGraph.drawLine((i - 1) * stretchX, lastG, i * stretchX, gBasecall);

      // draw C
      buffGraph.setColor(colorC);
      buffGraph.drawLine((i - 1) * stretchX, lastC, i * stretchX, cBasecall);


      if (baseCalls[basecallIndex] == i) {
        // draw line
        // buffGraph.setColor(new java.awt.Color(240, 240, 240));
        // buffGraph.drawLine(i*strechX, 0, i*strechX, 400);

        // get char of Nucleotide
        char nucleotide = Character.toUpperCase(startSequence.getSequence().charAt(basecallIndex));

        // set Nucleotide color
        switch (nucleotide) {
          case 'A':
            buffGraph.setColor(colorA);
            break;
          case 'T':
            buffGraph.setColor(colorT);
            break;
          case 'G':
            buffGraph.setColor(colorG);
            break;
          case 'C':
            buffGraph.setColor(colorC);
            break;
          default:
            buffGraph.setColor(Color.MAGENTA);
            break;
        }

        // draw nucleotide
        int fontWidth = buffGraph.getFontMetrics().stringWidth("" + nucleotide) / 2;
        buffGraph.drawString("" + nucleotide, i * stretchX - fontWidth, 15);

        // Draw aminoacid
        buffGraph.setColor(Color.BLACK);
        if ((basecallIndex - startSequence.getOffset()) % 3 == 0) {
          if (basecallIndex + 3 < startSequence.getSequence().length()) {
            buffGraph.setStroke(smallStroke);

            String aminoInNucleotides =
                (startSequence.getSequence().substring(basecallIndex, basecallIndex + 3))
                    .toUpperCase();
            String Aminoascid = StringAnalysis.codonsToAminoAcids(aminoInNucleotides);
            buffGraph.drawString(Aminoascid, baseCalls[basecallIndex + 1] * stretchX - fontWidth,
                30);
            buffGraph.drawLine(baseCalls[basecallIndex] * stretchX - fontWidth * 2, 35,
                (baseCalls[basecallIndex + 2]) * stretchX + fontWidth * 2, 35);
            buffGraph.drawLine(baseCalls[basecallIndex] * stretchX - fontWidth * 2, 40,
                baseCalls[basecallIndex] * stretchX - fontWidth * 2, 20);
            buffGraph.drawLine(baseCalls[basecallIndex + 2] * stretchX + fontWidth * 2, 40,
                baseCalls[basecallIndex + 2] * stretchX + fontWidth * 2, 20);
          }
        }

        // next
        if (basecallIndex + 1 < baseCalls.length) {
          basecallIndex++;
        }


      }

      // update last location
      lastA = aBasecall;
      lastT = tBasecall;
      lastG = gBasecall;
      lastC = cBasecall;

    }
    System.out.println(startSequence.getSequence().length() + " = seq");

    WritableImage wrtieImg = new WritableImage(last * stretchX, 400);

    SwingFXUtils.toFXImage(buffImg, wrtieImg);
    viewer.setImage(wrtieImg);
    img = wrtieImg;

    System.out.println("Finished");
  }

  public void setSequence(AnalysedSequence sequence) {
    this.sequences = new LinkedList<AnalysedSequence>();
    this.sequences.add(sequence);
    updateSequences(0);
  }

  public void setSequences(LinkedList<AnalysedSequence> sequences) {
    this.sequences = sequences;
    activeSequence = 0;
    prevs.setDisable(true);
    if (sequences.size() > 1) {
      next.setDisable(false);
    } else {
      next.setDisable(true);
    }
    updateSequences(0);
  }

}
