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

  private final Color colorA = Color.GREEN;
  private final Color colorT = Color.RED;
  private final Color colorG = Color.BLACK;
  private final Color colorC = Color.BLUE;

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
    double stretchY = 0.25;
    int stretchX = 4;

    System.out.println("chromatogram length = " + last);

    // create image
    BufferedImage buffImg = new BufferedImage(last * stretchX, 400, BufferedImage.TYPE_INT_RGB);
    buffImg.createGraphics();
    Graphics2D buffGraph = (Graphics2D) buffImg.getGraphics();

    BasicStroke stroke =
        new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    // graphics settings
    buffGraph.setStroke(stroke);
    RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    renderingHints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

    buffGraph.setRenderingHints(renderingHints);

    System.out.println("image Created");

    buffGraph.setColor(java.awt.Color.WHITE);
    buffGraph.fillRect(0, 0, last * stretchX, 400);
    System.out.println("background set");

    // remember last location to draw line
    int lastA = 0;
    int lastT = 0;
    int lastG = 0;
    int lastC = 0;

    int basecallIndex = 0;

    for (int i = 0; i < last; i++) {
      // System.out.println(i + "/" + last);
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
        buffGraph.drawString("" + nucleotide, i * stretchX - fontWidth, 10);

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
    /*
     * try
     * 
     * { File outputfile = new File("resources/GeneData/chrom.png"); ImageIO.write(buffImg, "png",
     * outputfile);
     * 
     * } catch (IOException e1) {
     * 
     * e1.printStackTrace(); }
     */

    /*
     * System.out.println("getGraph"); // GraphicsContext graph =
     * chartCanvas.getGraphicsContext2D(); try { FileInputStream fin = new FileInputStream(new
     * File("resources/GeneData/chrom.png")); Image img = new Image(fin); viewer.setImage(img); }
     * catch (FileNotFoundException e) { e.printStackTrace(); }
     */

    System.out.println("Finished");
  }

  /*
   * private void updateSequences(int id) { System.out.println("seqence Update");
   * 
   * activeSequence = id;
   * 
   * AnalysedSequence startSequence = sequences.get(id);
   * 
   * int[] channelA = startSequence.getChannelA(); int[] channelC = startSequence.getChannelC();
   * int[] channelT = startSequence.getChannelT(); int[] channelG = startSequence.getChannelG();
   * 
   * int last = (int) channelA.length;
   * 
   * last = (int) Math.min(last, channelC.length); last = (int) Math.min(last, channelT.length);
   * last = (int) Math.min(last, channelG.length);
   * 
   * System.out.println("new Canvas"); ImageView viewr = new ImageView();
   * System.out.println("add canvas"); try { scrollPane.setContent(viewr); } catch (Exception e) {
   * e.printStackTrace(); }
   * 
   * double strechX = 0.25; int strechY = 4;
   * 
   * System.out.println("chromatogram length = " + last);
   * 
   * BufferedImage buffImg = new BufferedImage(last * strechY, 400, BufferedImage.TYPE_INT_RGB);
   * buffImg.createGraphics(); Graphics buffGraph = buffImg.getGraphics();
   * 
   * buffGraph.setColor(java.awt.Color.WHITE); buffGraph.fillRect(0, 0, last * strechY, 400);
   * 
   * int lastA = 0; int lastT = 0; int lastG = 0; int lastC = 0;
   * 
   * // A int lastAmax = 0; int nextAmax = 0; int lastAhight = 0; int lastACandidate = 0; int aHold
   * = 0; boolean aFalling = true;
   * 
   * // T int lastTmax = 0; int nextTmax = 0; int lastThight = 0; int lastTCandidate = 0; int tHold
   * = 0; boolean tFalling = true;
   * 
   * // G int lastGmax = 0; int nextGmax = 0; int lastGhight = 0; int lastGCandidate = 0; int gHold
   * = 0; boolean gFalling = true;
   * 
   * // C int lastCmax = 0; int nextCmax = 0; int lastChight = 0; int lastCCandidate = 0; int cHold
   * = 0; boolean cFalling = true;
   * 
   * int num = 0;
   * 
   * for (int i = 0; i < last; i++) { int a = (int) (400 - ((channelA[i]) * strechX)); int t = (int)
   * (400 - ((channelT[i]) * strechX)); int g = (int) (400 - ((channelG[i]) * strechX)); int c =
   * (int) (400 - ((channelC[i]) * strechX));
   * 
   * // A buffGraph.setColor(colorA); buffGraph.drawLine((i - 1) * strechY, lastA, i * strechY, a);
   * 
   * // T buffGraph.setColor(colorT); buffGraph.drawLine((i - 1) * strechY, lastT, i * strechY, t);
   * 
   * // G buffGraph.setColor(colorG); buffGraph.drawLine((i - 1) * strechY, lastG, i * strechY, g);
   * 
   * // C buffGraph.setColor(colorC); buffGraph.drawLine((i - 1) * strechY, lastC, i * strechY, c);
   * 
   * // A if (channelA[i] >= lastACandidate) { if (!aFalling) { aHold++; } else { aHold = 0; }
   * aFalling = false;
   * 
   * } else { if (aFalling) { aHold++; } else { aHold = 0; nextAmax = i - 1; }
   * 
   * aFalling = true; aHold++; }
   * 
   * if (aHold > 5 && lastAmax != nextAmax) { if (channelA[nextAmax] > channelT[nextAmax] &&
   * channelA[nextAmax] > channelG[nextAmax] && channelA[nextAmax] > channelC[nextAmax]) { num++;
   * buffGraph.setColor(colorA); buffGraph.drawLine((nextAmax) * strechY, 0, (nextAmax) * strechY,
   * 400); } } // *********************************************T if (channelT[i] >= lastTCandidate)
   * { if (!tFalling) { tHold++; } else { tHold = 0; } tFalling = false;
   * 
   * } else { if (tFalling) { tHold++; } else { tHold = 0; nextTmax = i - 1; }
   * 
   * tFalling = true; tHold++; }
   * 
   * if (tHold > 5 && lastTmax != nextTmax) { if (channelT[nextTmax] > channelA[nextTmax] &&
   * channelT[nextTmax] > channelG[nextTmax] && channelT[nextTmax] > channelC[nextTmax]) { num++;
   * buffGraph.setColor(colorT); buffGraph.drawLine((nextTmax) * strechY, 0, (nextTmax) * strechY,
   * 400); } } // ********************************G if (channelG[i] >= lastGCandidate) { if
   * (!gFalling) { gHold++; } else { gHold = 0; } gFalling = false;
   * 
   * } else { if (gFalling) { gHold++; } else { gHold = 0; nextGmax = i - 1; }
   * 
   * gFalling = true; gHold++; }
   * 
   * if (gHold > 5 && lastGmax != nextGmax) { if (channelG[nextGmax] > channelT[nextGmax] &&
   * channelG[nextGmax] > channelA[nextGmax] && channelG[nextGmax] > channelC[nextGmax]) { num++;
   * buffGraph.setColor(colorG); buffGraph.drawLine((nextGmax) * strechY, 0, (nextGmax) * strechY,
   * 400); } } // ***********************C if (channelC[i] >= lastCCandidate) { if (!cFalling) {
   * cHold++; } else { cHold = 0; } cFalling = false;
   * 
   * } else { if (cFalling) { cHold++; } else { cHold = 0; nextCmax = i - 1; }
   * 
   * cFalling = true; cHold++; }
   * 
   * if (cHold > 5 && lastCmax != nextCmax) { if (channelC[nextCmax] > channelT[nextCmax] &&
   * channelC[nextCmax] > channelG[nextCmax] && channelC[nextCmax] > channelA[nextCmax]) { num++;
   * buffGraph.setColor(colorC); buffGraph.drawLine((nextCmax) * strechY, 0, (nextCmax) * strechY,
   * 400); } }
   * 
   * lastACandidate = channelA[i]; lastTCandidate = channelT[i]; lastGCandidate = channelG[i];
   * lastCCandidate = channelC[i]; //
   * 
   * lastA = a; lastT = t; lastG = g; lastC = c;
   * 
   * } System.out.println(num + " = num"); System.out.println(startSequence.getSequence().length() +
   * " = seq"); try
   * 
   * { File outputfile = new File("resources/GeneData/chrom.png"); ImageIO.write(buffImg, "png",
   * outputfile);
   * 
   * } catch (IOException e1) {
   * 
   * e1.printStackTrace(); }
   * 
   * System.out.println("getGraph"); // GraphicsContext graph = chartCanvas.getGraphicsContext2D();
   * try { FileInputStream fin = new FileInputStream(new File("resources/GeneData/chrom.png"));
   * Image img = new Image(fin); viewr.setImage(img); } catch (FileNotFoundException e) {
   * e.printStackTrace(); }
   * 
   * System.out.println("Finished"); }
   */

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
