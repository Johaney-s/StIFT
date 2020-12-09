
package GUI;

import backend.Data;
import backend.DataExtractor;
import backend.Star;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
        //-- Tooltip for lineChart
        mouseLocationInScene = new SimpleObjectProperty<>();
        tooltip = new Tooltip();
        
        lineChart.setOnMouseMoved((MouseEvent evt) -> {          
            mouseLocationInScene.set(new Point2D(evt.getSceneX(), evt.getSceneY()));
            double x = getXMouseCoordinate();
            double y = getYMouseCoordinate();
            tooltip.show(lineChart, evt.getScreenX() + 50, evt.getScreenY());
            tooltip.setText(String.format("[%.4f; %.4f]", x, y));
        });
    }
    
    @FXML
    public void graphExited() {
        tooltip.hide();
    }
        
    @FXML
    public void graphClicked() {
        if (!lineChart.getData().isEmpty()) {
            double x = getXMouseCoordinate();
            double y = getYMouseCoordinate();
            mainController.manageInput(x, y, 0.0, 0.0);
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
     * @param inStream
     */
    public void showGraph(InputStream inStream) {
        try {
            Data newData = DataExtractor.extract(inStream);
            lineChart.getData().clear();
            addIsochronesToChart(newData.getGroupedData().entrySet().iterator());
        } catch (IOException ex) {
            mainController.showAlert("Unable to read / close file",
                    "If existing, previous data instance remains valid.", Alert.AlertType.ERROR);
        }

        lineChart.setLegendVisible(false);
        lineChart.applyCss();
        lineChart.getStylesheets().add(StIFT.class.getResource("Styles.css").toExternalForm());
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
    }
    
    public void showGraph(File file) throws FileNotFoundException {
        InputStream inStream = new FileInputStream(file);
        showGraph(inStream);
    }
    
    /**
     * Iterates over groups of stars and adds them into graph as isochrones
     * @param iter Iterator of collection of groups of stars
     */
    private void addIsochronesToChart(Iterator<Map.Entry<Double, ArrayList<Star>>> iter) {
        while (iter.hasNext()) {
            Map.Entry<Double, ArrayList<Star>> isochrone = iter.next();
            XYChart.Series series = new XYChart.Series();
            int index = 0;
            for (Star s : isochrone.getValue()) {
                if (index % 4 != 0) { index++; continue; } //faster rendering
                series.getData().add(new XYChart.Data(s.getTemperature(), s.getLuminosity()));
                index++;
            }
            
            lineChart.getData().add(series);
        }
    }
    
    /**
     * Set main controller
     * @param controller Main controller
     */
    public void setMainController(FXMLMainController controller) {
        this.mainController = controller;
    }
}
