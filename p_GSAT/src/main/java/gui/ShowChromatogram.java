package gui;

import java.awt.Scrollbar;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import analysis.AnalysedSequence;
import analysis.Sequence;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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


  private int numberRange = 200;

  // initial scroll possitino
  private int initialPos = 0;

  private int maxScroll = 10;
  private int minScroll = 5;

  LineChart<Number, Number> linechart;
  
  @FXML
  private ScrollPane scrollPane;



  @FXML
  private LineChart<Number, Number> lineChart;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    
  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("GSAT - Chromatogram");
    // defining the axes
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Quality");
    yAxis.setLabel("Nukleotide");
    
    xAxis.setAutoRanging(false);
    xAxis.setUpperBound(5);
    xAxis.setLowerBound(numberRange+5);

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
    ScrollBar bar = new ScrollBar();
    bar.setMin(0);
    bar.setMax(100);
    bar.setValue(50);
    bar.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
          Number old_val, Number new_val) {
               xAxis.setUpperBound((int) (maxScroll*new_val.doubleValue()+numberRange));
               xAxis.setLowerBound((int) (maxScroll*new_val.doubleValue()));
      }
  });
    
    
    //***********

    // set linechart
    lineChart.getData().add(series);
    lineChart.setPrefSize(8000, 600);

    // SET Colors
    lineChart.setStyle("CHART_COLOR_1: #00ff00;");
    
    HBox buttonBox = new HBox(10);
    Button next = new Button();
    next.setText("Next");
    
    Button prev = new Button();
    prev.setText("Previous");
    
    Button cancel = new Button();
    cancel.setText("Cancel");
    
    final Pane spacer = new Pane();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    spacer.setMinSize(10, 1);
    
    buttonBox.getChildren().addAll(spacer, next, prev,cancel);
    
    

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
  
  public void setSequence(AnalysedSequence sequence){
    //TODO impement
  }
  
  public void setSequence(LinkedList<AnalysedSequence> sequences){
    //TODO impement    
  }

}
