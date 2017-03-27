package gui;

import java.awt.BasicStroke;
import java.awt.Color;
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
import analysis.StringAnalysis;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Class for displaying Chromatogram data with JavaFX
 * 
 * @category GUI.Window
 * @author kevin
 *
 */
public class ShowChromatogramWindow extends Application implements javafx.fxml.Initializable {

  // Preset colors for Chromatogram:
  
  /**
   * geenish color for trace A
   */
  private final Color colorA = new Color(170, 220, 80);
  
  /**
   * redish color for trace T
   */
  private final Color colorT = new Color(240, 60, 60);
  
  /**
   * Black color for trace G
   */
  private final Color colorG = Color.BLACK;
  
  /**
   * Bluish color for trace C
   */
  private final Color colorC = new Color(110, 180, 200);
  
  /**
   * color for undefined genes
   */
  private final Color colorN = Color.MAGENTA;
  
  /**
   * color for background of Chromatogram window (matches default Window Color)
   */
  private final Color background = new Color(244, 244, 244);

  private static final int IMAGE_HEIGHT = 400;

  /**
   * Thickness of trace graphs.
   */
  private int lineThickness = 5;

  /**
   * list of Sequences from selected folder.
   */
  LinkedList<AnalysedSequence> sequences;

  /**
   * the currently selected sequence.
   */
  private int activeSequence = 0;

  /**
   * the image that is Drawn to
   */
  private Image img;

  /**
   * ScrollPane for scrolling the Chromatogram
   */
  private ScrollPane scrollPane;

  /**
   * Buttons for previous File.
   */
  private Button prevs;

  /**
   * Buttons for next File.
   */
  private Button next;

  /**
   * Label to display the selected File
   */
  private Label fileName = new Label();

  /**
   * required by JavaFX but not used in this class
   * 
   * @author kevin
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

  }

  /**
   * active scene
   */
  private Scene scene;

  /**
   * JavaFX start method Setup all necessary variables and opens the Chromatogram
   * 
   * @param primaryStage the stage to use for this window
   * @author kevin
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    // create and configure Scrollpane;
    scrollPane = new ScrollPane();
    scrollPane.setMinHeight(420);
    scrollPane.setMaxHeight(Double.MAX_VALUE);
    scrollPane.setMaxWidth(Double.MAX_VALUE);

    // create Buttons for next and previous file
    next = new Button("Next file");
    prevs = new Button("Previous file");

    // set EventHandler for next file button
    next.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        next.setDisable(true);
        // check if next file exists
        if (activeSequence + 1 < sequences.size()) {
          // update Sequence
          updateSequences(activeSequence + 1);
          if (activeSequence + 1 >= sequences.size()) {
            // Disable button if no next file exists
            next.setDisable(true);
          } else {
            next.setDisable(false);
          }
        }
        // enable previous button
        prevs.setDisable(false);
      }
    });

    // set EventHandler for previous file button
    prevs.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        prevs.setDisable(true);
        // check if next file exists
        if (activeSequence - 1 >= 0) {
          // update Sequence
          updateSequences(activeSequence - 1);
          if (activeSequence - 1 <= 0) {
            // Disable button if no next file exists
            prevs.setDisable(true);
          } else {
            prevs.setDisable(false);
          }
        }
        // enable next Button
        next.setDisable(false);

      }
    });

    // create export Button
    Button export = new Button("Export");

    // set EventHandler for previous file button
    export.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        // Open File Chooser window
        FileChooser fileChooser = new FileChooser();

        // save path in String
        String filename = sequences.get(activeSequence).getFileName();

        // remove possible File endings and set File ending to png
        fileChooser.setInitialFileName(filename.substring(0, filename.length() - 3) + "png");

        fileChooser.setTitle("Save as image");
        File file = fileChooser.showSaveDialog(primaryStage);

        // if File was set, save file in given path
        if (file != null) {
          try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
          } catch (IOException ex) {
            System.out.println(ex.getMessage());
          }
        }
      }
    });

    // create close button
    Button close = new Button("Close");

    // set close event for close button
    close.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();

      }
    });

    // create box on the left for next and previous button
    HBox buttonLeft = new HBox(prevs, next);
    buttonLeft.setAlignment(Pos.BOTTOM_LEFT);
    buttonLeft.setSpacing(10);

    // create box on the right for export and close button
    HBox buttonRight = new HBox(export, close);
    buttonRight.setAlignment(Pos.BOTTOM_RIGHT);
    buttonRight.setSpacing(10);

    // fill boxes and filename label in bigger box
    HBox buttonBox = new HBox(fileName, buttonLeft, buttonRight);
    buttonBox.setSpacing(100);
    buttonBox.setAlignment(Pos.BOTTOM_CENTER);
    buttonBox.setPadding(new Insets(10, 10, 10, 10));

    VBox v = new VBox(scrollPane, buttonBox);
    v.setMinWidth(800);
    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    scene = new Scene(v);

    // Standard window startup
    primaryStage.setTitle("GSAT - Chromatogram view");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }


  /**
   * Sets the active Sequence to id and updates the image Redraws the image and Displays it
   * 
   * @param id The id of the currently selected Sequence
   * @author kevin
   */
  private void updateSequences(int id) {

    activeSequence = id;

    // get necessary variables
    AnalysedSequence startSequence = sequences.get(id);
    fileName.setText(startSequence.getFileName());

    // get basecalls from sequence (spikes in trace)
    int[] baseCalls = startSequence.getBaseCalls();

    // get trace channels from sequence
    int[] channelA = startSequence.getChannelA();
    int[] channelC = startSequence.getChannelC();
    int[] channelT = startSequence.getChannelT();
    int[] channelG = startSequence.getChannelG();

    // determine reference Gene and calculate offset (needed for aminoacid determination)
    try {
      if (startSequence.getReferencedGene() == null) {
        if (MainWindow.dropdownGene != null) {
          startSequence.setReferencedGene(MainWindow.dropdownGene);
        } else {
          Gene refgene = StringAnalysis.findRightGene(startSequence);
          if (refgene != null) {
            startSequence.setReferencedGene(refgene);
          } else {
            startSequence.setReferencedGene(new Gene(startSequence.getSequence(), 0, "", ""));
          }
        }
      }

      StringAnalysis.findOffset(startSequence);
    } catch (Exception e) {
      GUIUtils.showInfo(AlertType.ERROR, "Chromatogramm error",
          "Error occurred during chromatogram construction occurred.");
    }



    // determine length of Chromatogram

    int last = Math.min(channelA.length,
        Math.min(channelC.length, Math.min(channelT.length, channelG.length)));


    // create new viewer and set image
    ImageView viewer = new ImageView();

    // add viewer to scrollPane to make it scrollable
    try {
      scrollPane.setContent(viewer);
    } catch (Exception e) {
      GUIUtils.showInfo(AlertType.ERROR, "Scroll error", "An error during scrolling occurred.");

    }


    // get maximal graph hight:
    int maxHeight = 0;
    for (int i = 0; i < last; i++) {
      maxHeight = Math.max(maxHeight,
          Math.max(channelA[i], Math.max(channelC[i], Math.max(channelG[i], channelT[i]))));
    }

    // variables for scaling image
    double stretchY = 0.2;
    int stretchX = 4;

    // rescale if necesary:
    if (maxHeight * stretchY > IMAGE_HEIGHT - 40) {
      stretchY = (IMAGE_HEIGHT - 40) / (double) maxHeight;
      System.out.println(stretchY);
      System.out.println(maxHeight);
    }

    // create image
    BufferedImage buffImg =
        new BufferedImage(last * stretchX, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    buffImg.createGraphics();
    Graphics2D buffGraph = (Graphics2D) buffImg.getGraphics();

    // create stroke for trace lines
    BasicStroke bigStroke =
        new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    // create half size stroke for misc stuff
    BasicStroke smallStroke =
        new BasicStroke(lineThickness / 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    // graphics settings
    RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    renderingHints.add(
        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

    buffGraph.setRenderingHints(renderingHints);

    // draw single collor background
    buffGraph.setColor(background);
    buffGraph.fillRect(0, 0, last * stretchX, 400);

    // remember last location to draw line from
    int lastA = 0;
    int lastT = 0;
    int lastG = 0;
    int lastC = 0;

    // count basecall index
    int basecallIndex = 0;

    // drawing loop
    for (int i = 0; i < last; i++) {

      // Draw trace lines:
      buffGraph.setStroke(bigStroke);
      // scale traces to match image coordinate system
      int basecallA = (int) (400 - ((channelA[i]) * stretchY));
      int basecallT = (int) (400 - ((channelT[i]) * stretchY));
      int basecallG = (int) (400 - ((channelG[i]) * stretchY));
      int basecallC = (int) (400 - ((channelC[i]) * stretchY));

      // draw A
      buffGraph.setColor(colorA);
      buffGraph.drawLine((i - 1) * stretchX, lastA, i * stretchX, basecallA);

      // draw T
      buffGraph.setColor(colorT);
      buffGraph.drawLine((i - 1) * stretchX, lastT, i * stretchX, basecallT);

      // draw G
      buffGraph.setColor(colorG);
      buffGraph.drawLine((i - 1) * stretchX, lastG, i * stretchX, basecallG);

      // draw C
      buffGraph.setColor(colorC);
      buffGraph.drawLine((i - 1) * stretchX, lastC, i * stretchX, basecallC);


      // if active point contains a basecall:
      if (baseCalls[basecallIndex] == i) {

        // get char of Nucleotide
        char nucleotide = Character.toUpperCase(startSequence.getSequence().charAt(basecallIndex));

        // set Nucleotide color according to nucleotide at basecall
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
            buffGraph.setColor(colorN);
            break;
        }

        // draw nucleotide
        int fontWidth = buffGraph.getFontMetrics().stringWidth("" + nucleotide) / 2;
        buffGraph.drawString("" + nucleotide, i * stretchX - fontWidth, 15);

        // Draw aminoacid
        buffGraph.setColor(Color.BLACK);

        // check if aminoacid is complete and at correct position
        if ((basecallIndex - startSequence.getOffset()) % 3 == 0) {
          if (basecallIndex + 3 < startSequence.getSequence().length()) {
            buffGraph.setStroke(smallStroke);
            try {

              // get nucleotides of aminoacid
              String aminoInNucleotides =
                  (startSequence.getSequence().substring(basecallIndex, basecallIndex + 3))
                      .toUpperCase();

              // convert nucleotides in aminoacid
              String aminoacid = StringAnalysis.codonsToAminoAcids(aminoInNucleotides, false);

              // draw aminoacid string
              buffGraph.drawString(aminoacid,
                  baseCalls[basecallIndex + 1] * stretchX - fontWidth * (aminoacid.length()), 30);

              // draw horizontal line under nucleotides
              buffGraph.drawLine(baseCalls[basecallIndex] * stretchX - fontWidth * 2, 35,
                  (baseCalls[basecallIndex + 2]) * stretchX + fontWidth * 2, 35);

              // draw left and right end line
              buffGraph.drawLine(baseCalls[basecallIndex] * stretchX - fontWidth * 2, 40,
                  baseCalls[basecallIndex] * stretchX - fontWidth * 2, 20);
              buffGraph.drawLine(baseCalls[basecallIndex + 2] * stretchX + fontWidth * 2, 40,
                  baseCalls[basecallIndex + 2] * stretchX + fontWidth * 2, 20);
            } catch (Exception e) {
              GUIUtils.showInfo(AlertType.ERROR, "Could not display chromatogram",
                  "The chromatogram could not be created. Please try again.");

            }
          }
        }

        // next
        if (basecallIndex + 1 < baseCalls.length) {
          basecallIndex++;
        }


      }

      // update last location
      lastA = basecallA;
      lastT = basecallT;
      lastG = basecallG;
      lastC = basecallC;

    }

    // convert buffered image to JavaFX WritableImage
    WritableImage wrtieImg = new WritableImage(last * stretchX, 400);

    SwingFXUtils.toFXImage(buffImg, wrtieImg);
    viewer.setImage(wrtieImg);
    img = wrtieImg;
  }

  /**
   * Sets sequence and updates chromatograms accordingly.
   * 
   * @param sequence The AnalysedSequence object to be showed as a chromatogram
   * 
   * @author Kevin
   */
  public void setSequence(AnalysedSequence sequence) {
    // create new list
    this.sequences = new LinkedList<AnalysedSequence>();

    // add sequence to list
    this.sequences.add(sequence);

    // set active sequence to 0 and udate
    updateSequences(0);
  }

  /**
   * sets list of Sequences and updates Chromatogram accordingly Automatically sets first sequence
   * of the list.
   * 
   * @author Kevin
   * @param sequences the list of sequences that should be displayed in the chromatogram
   */
  public void setSequences(LinkedList<AnalysedSequence> sequences) {
    // set sequence
    this.sequences = sequences;
    activeSequence = 0;
    prevs.setDisable(true);
    if (sequences.size() > 1) {
      next.setDisable(false);
    } else {
      next.setDisable(true);
    }

    // set active sequence to 0 and udate
    updateSequences(0);
  }

}
