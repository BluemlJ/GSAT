package gui;

import java.awt.Scrollbar;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;

import analysis.AnalysedSequence;
import analysis.Sequence;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
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

public class ShowChromatogram extends Application implements javafx.fxml.Initializable {


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


  private XYChart.Series<Number, String> seriesA = new XYChart.Series<Number, String>();

  private XYChart.Series<Number, String> seriesT = new XYChart.Series<Number, String>();

  private XYChart.Series<Number, String> seriesG = new XYChart.Series<Number, String>();

  private XYChart.Series<Number, String> seriesC = new XYChart.Series<Number, String>();



  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

  }

  @Override
  public void start(Stage stage) throws Exception {

    seriesA.setName("A");
    seriesA.setName("T");
    seriesA.setName("G");

    stage.setTitle("GSAT - Chromatogram");
    // defining the axes
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Quality");
    yAxis.setLabel("Nukleotide");

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
      public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
        xAxis.setUpperBound((int) (maxScroll * new_val.doubleValue() + numberRange));
        xAxis.setLowerBound((int) (maxScroll * new_val.doubleValue()));
      }
    });


    // ***********

    // set linechart
    lineChart.getData().add(series);
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
    activeSequence = id;


    AnalysedSequence startSequence = this.sequences.get(id);


    QualitySequence aChannel = startSequence.getChannels().getAChannel().getQualitySequence();
    QualitySequence cChannel = startSequence.getChannels().getCChannel().getQualitySequence();
    QualitySequence tChannel = startSequence.getChannels().getTChannel().getQualitySequence();
    QualitySequence gChannel = startSequence.getChannels().getGChannel().getQualitySequence();

    int last = (int) aChannel.getLength();
    last = (int) Math.min(last, cChannel.getLength());
    last = (int) Math.min(last, tChannel.getLength());
    last = (int) Math.min(last, gChannel.getLength());

    for (int i = 0; i < last; i++) {

      seriesA.getData().set(i,
          new XYChart.Data<Number, String>(aChannel.get(i).getQualityScore(), i + "F"));
      seriesC.getData().set(i,
          new XYChart.Data<Number, String>(cChannel.get(i).getQualityScore(), i + "F"));
      seriesT.getData().set(i,
          new XYChart.Data<Number, String>(tChannel.get(i).getQualityScore(), i + "F"));
      seriesG.getData().set(i,
          new XYChart.Data<Number, String>(gChannel.get(i).getQualityScore(), i + "F"));

    }



    seriesA.getData().remove(last, seriesA.getData().size());
    seriesC.getData().remove(last, seriesC.getData().size());
    seriesT.getData().remove(last, seriesT.getData().size());
    seriesG.getData().remove(last, seriesG.getData().size());


    bar.setMin(0);
    bar.setMax(100);
    bar.setValue(0);
    maxScroll = last;

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
