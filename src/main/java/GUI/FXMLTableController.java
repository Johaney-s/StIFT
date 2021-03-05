
package GUI;

import backend.InputFileParser;
import backend.objects.ResultStar;
import backend.objects.Star;
import backend.TableModel;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;

/**
 * FXML Controller class
 */
public class FXMLTableController implements Initializable {
    
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn tempCol;
    @FXML
    private TableColumn lumCol;
    @FXML
    private TableColumn ageCol;
    @FXML
    private TableColumn radCol;
    @FXML
    private TableColumn massCol;
    @FXML
    private TableColumn phaseCol;
    
    private Label hiddenRowsCounter;
    private final TableModel tableModel = new TableModel();
    private final Tooltip tooltip = new Tooltip();
    private final VBox vBox = new VBox();
    private final Label lowValue = new Label();
    private final Label highValue = new Label();
    private final RangeSlider slider = new RangeSlider();
    private final Button removeButton = new Button("REMOVE FILTER");
    private final CheckBox checkbox = new CheckBox("Hide empty");
    private FXMLLoadingController loadingController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showFilterIcon();
        setValueFactories();
        constructTooltipGraphic();
        tableView.setPlaceholder(new Label("No results."));
        tableView.setItems(tableModel.getResults());
        tooltip.setGraphic(vBox);
        vBox.getStyleClass().add("filterBox");
        
        removeButton.setOnMouseClicked(event -> {
            slider.setLowValue(slider.getMin());
            slider.setHighValue(slider.getMax());
            handleFilterChange();
        });

    }

    private void constructTooltipGraphic() {
        ImageView cancelIcon = new ImageView(new Image(getClass().getClassLoader().getResource("cancel.png").toString(),
                11, 11, false, false));
        Label label = new Label("Phase filter:");
        label.setStyle("-fx-font-size:11px");
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.3);
        slider.setMinorTickCount(3);
        slider.setOnMouseReleased(event -> {
            handleFilterChange();
        });

        checkbox.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                    tableModel.setHideEmptyRows(!tableModel.getHideEmptyRows());
                    updateHiddenRowsCounter();
                });

        cancelIcon.setOnMouseClicked(mouseEvent -> tooltip.hide());
        cancelIcon.setOnMousePressed(mouseEvent -> tooltip.hide());


        vBox.setMaxWidth(100);
        BorderPane firstRow = new BorderPane();
        firstRow.setRight(cancelIcon);
        firstRow.setLeft(label);
        vBox.getChildren().add(firstRow);
        BorderPane sliderValues = new BorderPane();
        sliderValues.leftProperty().setValue(lowValue);
        sliderValues.rightProperty().setValue(highValue);
        vBox.getChildren().add(sliderValues);
        vBox.getChildren().add(slider);
        vBox.getChildren().add(removeButton);
        VBox.setMargin(removeButton, new Insets(0, 0, 5, 0));
        vBox.getChildren().add(checkbox);

        slider.lowValueProperty().addListener(
                (observable, oldValue, newValue) -> lowValue.setText(String.format("%.4f", newValue.doubleValue()))
        );

        slider.highValueProperty().addListener(
                (observable, oldValue, newValue) -> highValue.setText(String.format("%.4f", newValue.doubleValue()))
        );
    }
    
    private void handleFilterChange() {
        if (slider.getLowValue() == slider.getMin() && slider.getHighValue() == slider.getMax()) {
                tableModel.removeFilter();
        } else {
            tableModel.setFilter(slider.getLowValue(), slider.getHighValue());
        }
        updateHiddenRowsCounter();
    }

    public void updateHiddenRowsCounter() {
        int hiddenRows = tableModel.getHiddenCount();
        if (hiddenRows == 0) {
            Platform.runLater(() -> hiddenRowsCounter.setText(""));
        } else {
            Platform.runLater(() -> hiddenRowsCounter.setText(Integer.toString(hiddenRows)));
        }
    }
    
    /**
     * Sets bounds to slider accordingly to current results
     */
    public void updateSliderBounds() {
        Double lowerBound = 200.0;
        Double upperBound = 0.0;
        
        for (Star s : tableModel.getAllResults()) {
            if (s.getPhase() != null  && !s.getPhase().isNaN() && s.getPhase() > upperBound) { upperBound = s.getPhase(); }
            if (s.getPhase() != null && !s.getPhase().isNaN() && s.getPhase() < lowerBound) { lowerBound = s.getPhase(); }
        }
        
        slider.setMin(lowerBound);
        slider.setMax(upperBound);
        if (!tableModel.isFiltered()) {
            slider.setLowValue(lowerBound);
            slider.setHighValue(upperBound);
        }
    }

    private void setValueFactories() {
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temColumnText"));
        lumCol.setCellValueFactory(new PropertyValueFactory<>("lumColumnText"));
        massCol.setCellValueFactory(new PropertyValueFactory<>("masColumnText"));
        radCol.setCellValueFactory(new PropertyValueFactory<>("radColumnText"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("ageColumnText"));
        phaseCol.setCellValueFactory(new PropertyValueFactory<>("phaColumnText"));
    }
    
    private void showFilterIcon() {
        Image phaseIcon = new Image("filter.png", 17, 17, true, true);
        hiddenRowsCounter = new Label();
        hiddenRowsCounter.getStyleClass().add("hiddenRowsCounter");
        hiddenRowsCounter.setBackground(new Background(new BackgroundImage(
                phaseIcon,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
        HBox wrappingBox = new HBox(hiddenRowsCounter);
        hiddenRowsCounter.setMinWidth(wrappingBox.getPrefWidth());
        Label phaseColHeader = new Label("Phase", wrappingBox);
        phaseCol.setGraphic(phaseColHeader);
        Tooltip.install(wrappingBox, tooltip);
        
        //prevents tooltip from showing by hovering over icon
        hiddenRowsCounter.setOnMouseMoved(Event::consume);
        
        wrappingBox.setOnMouseClicked(event -> {
            if (tooltip.isShowing()) {
                tooltip.hide();
            } else {
                tooltip.show(tableView, event.getScreenX() - 60, event.getScreenY() - 125);
            }
        });
    }

    public TableModel getTableModel() {
        return tableModel;
    }
    
    /**
     * Adds new single result to table model and changes filtering slider's values
     * @param result New result
     */
    public void handleNewResult(ResultStar result) {
        tableModel.addResult(result);
        updateFilter();
    }
    
    /**
     * Adds whole collection of results obtained from input data file,
     * updates filters
     * @param file Input data file
     */
    public void setResults(File file) {
        tableView.getScene().getRoot().setDisable(true);
        InputFileParser.extract(file, tableView.getScene().getRoot(), tableModel, loadingController, this);
    }

    /**
     * Updates slider bounds and hidden rows counter
     */
    public void updateFilter() {
        updateSliderBounds();
        updateHiddenRowsCounter();
    }

    /**
     * Resets results in table model, resets filter and updates filter tooltip
     */
    public void reset() {
        tableModel.reset();
        updateFilter();
        checkbox.setSelected(false);
    }

    public void setLoadingController(FXMLLoadingController loadingController) {
        this.loadingController = loadingController;
    }

    /**
     * Shows alert when parsing input data file fails
     */
    public void inputFailed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Upload input data file");
        alert.setContentText("Error occurred, please refer to readme file (Help > About) to check formatting.");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/error.png").toString()));
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node
                -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE)); //fix for broken linux dialogues
        alert.showAndWait();
    }
}
