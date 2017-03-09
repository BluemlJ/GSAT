package gui;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import analysis.AnalysedSequence;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShowChromatogramOLD extends Application implements javafx.fxml.Initializable {


  LinkedList<AnalysedSequence> sequences;

  private int numberRange = 200;

  private int activeSequence = 0;

  // initial scroll possitino
  private int initialPos = 0;

  private int maxScroll = 10;
  private int minScroll = 5;

  LineChart<Number, Number> linechart;

  @FXML
  private ScrollPane scrollPane;

  @FXML
  private LineChart<Number, Number> lineChart;


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

  @Override
  public void start(Stage stage) throws Exception {

    seriesA.setName("A");
    seriesT.setName("T");
    seriesG.setName("G");
    seriesC.setName("C");

    stage.setTitle("GSAT - Chromatogram");
    // defining the axes
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Quality");
    yAxis.setLabel("Nukleotide");

    yAxis.setAutoRanging(true);

    xAxis.setAutoRanging(false);
    xAxis.setUpperBound(5);
    xAxis.setLowerBound(numberRange + 5);

    // creating the chart
    lineChart = new LineChart<Number, Number>(xAxis, yAxis);

    lineChart.setTitle("FILENAME");
    // defining a series
    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
    series.setName("G");
    // populating the series with data
    series.getData().add(new XYChart.Data<Number, Number>(1, 23));
    series.getData().add(new XYChart.Data<Number, Number>(2, 14));
    series.getData().add(new XYChart.Data<Number, Number>(3, 15));
    series.getData().add(new XYChart.Data<Number, Number>(4, 24));
    series.getData().add(new XYChart.Data<Number, Number>(5, 34));
    series.getData().add(new XYChart.Data<Number, Number>(6, 36));
    series.getData().add(new XYChart.Data<Number, Number>(7, 22));
    series.getData().add(new XYChart.Data<Number, Number>(8, 45));
    series.getData().add(new XYChart.Data<Number, Number>(9, 43));
    series.getData().add(new XYChart.Data<Number, Number>(10, 17));
    series.getData().add(new XYChart.Data<Number, Number>(11, 29));
    series.getData().add(new XYChart.Data<Number, Number>(12, 25));



    // define scroll bar
    bar.setMin(0);
    bar.setMax(100);
    bar.setValue(0);
    bar.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
        int high = (int) (maxScroll * newVal.doubleValue() + numberRange);
        int low = (int) (maxScroll * newVal.doubleValue());
        xAxis.setUpperBound(high);
        xAxis.setLowerBound(low);
        
        //updateGraph(high, low);
        
      }

      private void updateGraph(int high, int low) {
        AnalysedSequence startSequence = sequences.get(activeSequence);
        
        int[] channelA = startSequence.getChannelA();
        int[] channelC = startSequence.getChannelC();
        int[] channelT = startSequence.getChannelT();
        int[] channelG = startSequence.getChannelG();


        int last = (int) channelA.length;

        last = (int) Math.min(last, channelC.length);
        last = (int) Math.min(last, channelT.length);
        last = (int) Math.min(last, channelG.length);

        System.out.println("chromatogram length = " + last);

        int max = 1;

        for (int i = low; i < high; i++) {
          if (seriesA.getData().size() > i) {
            seriesA.getData().set(i, new XYChart.Data<Number, Number>(i, channelA[i]));
          } else {
            seriesA.getData().add(i, new XYChart.Data<Number, Number>(i, channelA[i]));
          }

          if (seriesC.getData().size() > i) {
            seriesC.getData().set(i, new XYChart.Data<Number, Number>(i, channelC[i]));
          } else {
            seriesC.getData().add(i, new XYChart.Data<Number, Number>(i, channelC[i]));
          }

          if (seriesT.getData().size() > i) {
            seriesT.getData().set(i, new XYChart.Data<Number, Number>(i, channelT[i]));
          } else {
            seriesT.getData().add(i, new XYChart.Data<Number, Number>(i, channelT[i]));
          }

          if (seriesG.getData().size() > i) {
            seriesG.getData().set(i, new XYChart.Data<Number, Number>(i, channelG[i]));
          } else {
            seriesG.getData().add(i, new XYChart.Data<Number, Number>(i, channelG[i]));

          }

          max = Math.max(max, channelA[i]);
          max = Math.max(max, channelC[i]);
          max = Math.max(max, channelT[i]);
          max = Math.max(max, channelG[i]);
        }
        
      }
    });


    // ***********

    // set linechart
    // lineChart.getData().add(series);
    lineChart.getData().add(seriesA);
    lineChart.getData().add(seriesC);
    lineChart.getData().add(seriesT);
    lineChart.getData().add(seriesG);


    lineChart.setPrefSize(8000, 600);

    // SET Colors
    lineChart.setStyle("CHART_COLOR_1: #00ff00;");


    // button box begin
    HBox buttonBox = new HBox(10);

    // next sequence button:

    next.setText("Next");


    prev.setText("Previous");


    next.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {

        if (activeSequence + 1 == sequences.size()) {
          next.setDisable(true);
        }
        activeSequence = Math.min(activeSequence + 1, sequences.size());
        updateSequences(activeSequence);
        if (activeSequence - 1 >= 0) {
          prev.setDisable(false);
        }
      }
    });
    //
    prev.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {

        if (activeSequence - 1 == 0) {
          prev.setDisable(true);
        }

        activeSequence = Math.max(activeSequence - 1, 0);
        updateSequences(activeSequence);

        if (activeSequence + 1 < sequences.size()) {
          next.setDisable(false);
        }
      }
    });
    next.setDisable(true);
    prev.setDisable(true);

    Button cancel = new Button();
    cancel.setText("Cancel");

    final Pane spacer = new Pane();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    spacer.setMinSize(10, 1);

    buttonBox.getChildren().addAll(spacer, next, prev, cancel);



    // define Hbox to order window content
    VBox box = new VBox();
    final Pane spacer2 = new Pane();
    spacer2.setMinSize(10, 10);
    final Pane spacer3 = new Pane();
    spacer3.setMinSize(10, 10);
    box.getChildren().addAll(lineChart, bar, spacer2, buttonBox, spacer3);

    // set scene
    Scene scene = new Scene(box, 800, 600);


    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  private void updateSequences(int id) {
    System.out.println("seqence Updated");

    activeSequence = id;


    AnalysedSequence startSequence = sequences.get(id);


    // TODO channelA ist nun nur noch als int[] zu haben....

    // QualitySequence channelA =
    // startSequence.getChannels().getAChannel().getQualitySequence();
    // QualitySequence channelC =
    // startSequence.getChannels().getCChannel().getQualitySequence();
    // QualitySequence channelT =
    // startSequence.getChannels().getTChannel().getQualitySequence();
    // QualitySequence channelG =
    // startSequence.getChannels().getGChannel().getQualitySequence();

    int[] channelA = startSequence.getChannelA();
    int[] channelC = startSequence.getChannelC();
    int[] channelT = startSequence.getChannelT();
    int[] channelG = startSequence.getChannelG();


    int last = (int) channelA.length;

    last = (int) Math.min(last, channelC.length);
    last = (int) Math.min(last, channelT.length);
    last = (int) Math.min(last, channelG.length);

    System.out.println("chromatogram length = " + last);

    int max = 1;

    for (int i = 0; i < last; i++) {
      if (seriesA.getData().size() > i) {
        seriesA.getData().set(i, new XYChart.Data<Number, Number>(i, channelA[i]));
      } else {
        seriesA.getData().add(i, new XYChart.Data<Number, Number>(i, channelA[i]));
      }

      if (seriesC.getData().size() > i) {
        seriesC.getData().set(i, new XYChart.Data<Number, Number>(i, channelC[i]));
      } else {
        seriesC.getData().add(i, new XYChart.Data<Number, Number>(i, channelC[i]));
      }

      if (seriesT.getData().size() > i) {
        seriesT.getData().set(i, new XYChart.Data<Number, Number>(i, channelT[i]));
      } else {
        seriesT.getData().add(i, new XYChart.Data<Number, Number>(i, channelT[i]));
      }

      if (seriesG.getData().size() > i) {
        seriesG.getData().set(i, new XYChart.Data<Number, Number>(i, channelG[i]));
      } else {
        seriesG.getData().add(i, new XYChart.Data<Number, Number>(i, channelG[i]));

      }

      max = Math.max(max, channelA[i]);
      max = Math.max(max, channelC[i]);
      max = Math.max(max, channelT[i]);
      max = Math.max(max, channelG[i]);
    }



    seriesA.getData().remove(last, seriesA.getData().size());
    seriesC.getData().remove(last, seriesC.getData().size());
    seriesT.getData().remove(last, seriesT.getData().size());
    seriesG.getData().remove(last, seriesG.getData().size());


    bar.setMin(0);
    bar.setMax(100);
    bar.setValue(0);


    maxScroll = max;
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
