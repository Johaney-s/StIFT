
package GUI;

import backend.InputFileParser;
import backend.Star;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private final RangeSlider slider = new RangeSlider();
    private final Button removeButton = new Button("Remove filter");
    private final CheckBox checkbox = new CheckBox("Hide empty rows");
    private final InputFileParser nip = new InputFileParser();

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
        Label label = new Label("Phase filter:");
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
        
        vBox.getChildren().add(label);
        vBox.getChildren().add(slider);
        vBox.getChildren().add(removeButton);
        vBox.getChildren().add(checkbox);
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
            hiddenRowsCounter.setText("");
        } else {
            hiddenRowsCounter.setText(Integer.toString(hiddenRows));
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
        tempCol.setCellValueFactory(new PropertyValueFactory<>("TeXTemperature"));
        lumCol.setCellValueFactory(new PropertyValueFactory<>("TeXLuminosity"));
        massCol.setCellValueFactory(new PropertyValueFactory<>("TeXMass"));
        radCol.setCellValueFactory(new PropertyValueFactory<>("TeXRadius"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("TeXAge"));
        phaseCol.setCellValueFactory(new PropertyValueFactory<>("TeXPhase"));
    }
    
    private void showFilterIcon() {
        Image phaseIcon = new Image("filter.png", 15, 15, true, true);
        hiddenRowsCounter = new Label();
        hiddenRowsCounter.getStyleClass().add("hiddenRowsCounter");
        hiddenRowsCounter.setBackground(new Background(new BackgroundImage(
                phaseIcon,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
        HBox wrappingBox = new HBox(hiddenRowsCounter);
        Label phaseColHeader = new Label("Phase", wrappingBox);
        phaseCol.setGraphic(phaseColHeader);
        
        //prevents tooltip from showing by hovering over icon
        hiddenRowsCounter.setOnMouseMoved(event ->
            event.consume());
        
        wrappingBox.setOnMouseClicked(event -> {
            if (tooltip.isShowing()) {
                tooltip.hide();
            } else {
                tooltip.show(tableView, event.getScreenX() - 80, event.getScreenY() - 120);
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
    public void handleNewResult(Star result) {
        tableModel.addResult(result);
        updateFilter();
    }
    
    /**
     * Adds whole collection of results obtained from input data file,
     * updates filters
     * @param file Input data file
     * @throws IOException File not found / could not be read
     */
    public void setResults(File file) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLLoading.fxml"));
        fxmlLoader.load();
        FXMLLoadingController loadingController = fxmlLoader.getController();
        loadingController.setOwner((Stage) tableView.getScene().getWindow());

        nip.extract(file, tableModel, loadingController, this);
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
}
