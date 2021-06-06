
package GUI;

import backend.Data;
import backend.GridFileParser;
import backend.Settings;
import backend.objects.Star;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;

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
     * @param isDefault true if stream refers to default file (use predefined settings)
     * @return true if successful, false otherwise
     */
    public boolean showGraph(InputStream inStream, boolean isDefault) {
        try {
            Data newData = GridFileParser.extract(inStream);

            //manage settings
            if (!isDefault) {
                Settings newSettings = manageSettings(newData);
                if (newSettings == null) {
                    return false;
                }

                newSettings.setSettings(newSettings);
                if (!newSettings.getPhases().containsAll(newData.getCurrentPhases())) {
                    //filter out missing phases
                }
                if (newSettings.getPhaseZams() != null) {
                    //set and create zams line
                }
            }

            Data.setCurrentData(newData);
            lineChart.getData().clear();
            addIsochronesToChart(newData.getGroupedData());
        } catch (IOException ex) {
            mainController.showAlert("Error while parsing grid data", ex.getMessage(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }
    
    public boolean showGraph(File file, boolean isDefault) {
        try {
            InputStream inStream = new FileInputStream(file);
            return showGraph(inStream, isDefault);
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
        ArrayList<Star> track = Data.getCurrentData().getZAMS().getTrack();
        for (int i = 0; i < track.size(); i++) {
            //also need to invert x-axis and this solution sucks, but FIXME later
            series.getData().add(new XYChart.Data(-track.get(i).getTemperature(), track.get(i).getLuminosity()));
        }
        lineChart.getData().add(series);
    }


    /**
     * Evoke dialog to specify custom settings
     */
    private Settings manageSettings(Data loadedData) {
        Dialog<Settings> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Please specify the settings for the file you are uploading.");

        Label label1 = new Label("ZAMS phase is: ");
        Label label2 = new Label("Include phases: ");

        //ZAMS
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().add("-");
        choiceBox.setValue("-");
        choiceBox.getItems().addAll(loadedData.getCurrentPhases());

        //Phases
        List<CheckBox> phaseBoxes = new ArrayList<>();
        List<Short> selectedPhases = new ArrayList<>(Data.getCurrentData().getCurrentPhases());
        GridPane allowedPhasesPane = new GridPane();
        selectedPhases.sort(Comparator.naturalOrder());
        if (Data.getCurrentData().getCurrentPhases().size() > 12) {
            allowedPhasesPane.add(new Label("Too many"), 0, 0);
            allowedPhasesPane.add(new Label("phases!"), 0, 1);
        } else {
            for (int i = 0; i < selectedPhases.size(); i++) {
                CheckBox checkBox = new CheckBox(selectedPhases.get(i).toString());
                phaseBoxes.add(checkBox);
                allowedPhasesPane.add(checkBox, i % 3, i / 3);
                checkBox.setSelected(true);
            }
        }

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(choiceBox, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(allowedPhasesPane, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Upload file", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(new Callback<ButtonType, Settings>() {
            @Override
            public Settings call(ButtonType b) {
                if (b==buttonTypeOk) {
                    for (CheckBox checkBox : phaseBoxes) {
                        if (checkBox.isSelected()) { selectedPhases.add(Short.parseShort(checkBox.getText())); }
                    }
                    Short zams = null;
                    if (choiceBox.getValue() != "-") {
                        zams = Short.parseShort(choiceBox.getValue().toString());
                    }
                    return new Settings(selectedPhases, zams, false);
                }
                return null;
            }
        });

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/settings.png").toString()));

        dialog.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node
                -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE)); //fix for broken linux dialogues
        Optional<Settings> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    /**
     * Set main controller
     * @param controller Main controller
     */
    public void setMainController(FXMLMainController controller) {
        this.mainController = controller;
    }
}
