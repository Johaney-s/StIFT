
package GUI;

import backend.Data;
import backend.GridFileParser;
import backend.objects.Star;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 */
public class FXMLLineChartController implements Initializable {
    
    @FXML
    private LineChart lineChart;
    private Tooltip tooltip;
    private ObjectProperty<Point2D> mouseLocationInScene;
    private FXMLMainController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);

        //
        NumberAxis xAxis = (NumberAxis)lineChart.getXAxis();
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number value) {
                //minus value FIXME if you can
                return String.format("%.1f", -value.doubleValue());
            }
        });

        //-- Tooltip for lineChart
        mouseLocationInScene = new SimpleObjectProperty<>();
        tooltip = new Tooltip();
        
        lineChart.setOnMouseMoved((MouseEvent evt) -> {          
            mouseLocationInScene.set(new Point2D(evt.getSceneX(), evt.getSceneY()));
            double x = getXMouseCoordinate();
            double y = getYMouseCoordinate();
            tooltip.show(lineChart, evt.getScreenX() + 50, evt.getScreenY());
            tooltip.setText(String.format("[%.4f; %.4f]", -x, y));
        });
    }
    
    @FXML
    public void graphExited() {
        tooltip.hide();
    }
        
    @FXML
    public void graphClicked() {
        if (!lineChart.getData().isEmpty()) {
            double x = -Double.parseDouble(String.format("%.4f", getXMouseCoordinate())); //use showed values in tooltip
            double y = Double.parseDouble(String.format("%.4f", getYMouseCoordinate()));
            mainController.manageInput(x, y, 0.0, 0.0, (short)2);
        }
    }
    
    private double getXMouseCoordinate() {
        NumberAxis xAxis = (NumberAxis)lineChart.getXAxis();
        double xInXAxis = xAxis.sceneToLocal(mouseLocationInScene.get()).getX();
        return (double)xAxis.getValueForDisplay(xInXAxis);
    }
    
    private double getYMouseCoordinate() {
        NumberAxis yAxis = (NumberAxis)lineChart.getYAxis();
        double yInYAxis = yAxis.sceneToLocal(mouseLocationInScene.get()).getY();
        return (double)yAxis.getValueForDisplay(yInYAxis);
    }
    
    /**
     * Takes input data file, extracts data and shows in graph
     * @return true if successfull, false otherwise
     */
    public boolean showGraph(InputStream inStream) {
        try {
            Data newData = GridFileParser.extract(inStream);
            lineChart.getData().clear();
            addIsochronesToChart(newData.getGroupedData());
        } catch (IOException ex) {
            mainController.showAlert("Error while parsing grid data", ex.getMessage(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }
    
    public boolean showGraph(File file) {
        try {
            InputStream inStream = new FileInputStream(file);
            return showGraph(inStream);
        } catch (FileNotFoundException ex) {
            mainController.showAlert("File not found", ex.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }
    
    /**
     * Iterates over groups of stars and adds them into graph as isochrones
     */
    private void addIsochronesToChart(ArrayList<ArrayList<Star>> list) {
        int isoCounter = 0;
        int MAX_ISOCHRONES = 400;
        int SKIPPING_COUNT = list.size() / MAX_ISOCHRONES + 1;
        for (ArrayList<Star> isochrone : list) {
            isoCounter++;

            if (isoCounter % SKIPPING_COUNT != 0) {
                continue;
            }

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    XYChart.Series series = new XYChart.Series();
                    int index = 0;
                    for (Star s : isochrone) {
                        if (index % 4 != 0) { index++; continue; } //faster rendering
                        //need to invert x-axis and this solution sucks, but FIXME later
                        series.getData().add(new XYChart.Data(-s.getTemperature(), s.getLuminosity()));
                        index++;
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lineChart.getData().add(series);
                        }
                    });
                    return null;
                }
            };

            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }

        //ZAMS
        XYChart.Series series = new XYChart.Series();
        ArrayList<Star> track = GridFileParser.getCurrentData().getZAMS().getTrack();
        for (int i = 0; i < track.size(); i++) {
            //also need to invert x-axis and this solution sucks, but FIXME later
            series.getData().add(new XYChart.Data(-track.get(i).getTemperature(), track.get(i).getLuminosity()));
        }
        lineChart.getData().add(series);
    }
    
    /**
     * Set main controller
     * @param controller Main controller
     */
    public void setMainController(FXMLMainController controller) {
        this.mainController = controller;
    }
}
