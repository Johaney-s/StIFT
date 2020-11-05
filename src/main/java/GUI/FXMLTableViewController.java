
package GUI;

import backend.InputFileExtractor;
import backend.Star;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import org.controlsfx.control.RangeSlider;

/**
 * FXML Controller class
 */
public class FXMLTableViewController implements Initializable {
    
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
        
        slider.setOnMouseReleased(event -> {
            handleFilterChange();
        });
        
        vBox.getChildren().add(label);
        vBox.getChildren().add(slider);
        vBox.getChildren().add(removeButton);
    }
    
    private void handleFilterChange() {
        if (slider.getLowValue() == slider.getMin() && slider.getHighValue() == slider.getMax()) {
                tableModel.removeFilter();
        } else {
            tableModel.setFilter(slider.getLowValue(), slider.getHighValue());
        }
        updateHiddenRowsCounter();
    }

    private void updateHiddenRowsCounter() {
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
    private void updateSliderBounds() {
        Double lowerBound = 200.0;
        Double upperBound = 0.0;
        
        for (Star s : tableModel.getAllResults()) {
            if (s.getPhase() != null && s.getPhase() > upperBound) { upperBound = s.getPhase(); }
            if (s.getPhase() != null && s.getPhase() < lowerBound) { lowerBound = s.getPhase(); }
        }
        
        slider.setMin(lowerBound);
        slider.setMax(upperBound);
        if (!tableModel.isFiltered()) {
            slider.setLowValue(lowerBound);
            slider.setHighValue(upperBound);
        }
    }

    private void setValueFactories() {
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        lumCol.setCellValueFactory(new PropertyValueFactory<>("luminosity"));
        massCol.setCellValueFactory(new PropertyValueFactory<>("mass"));
        radCol.setCellValueFactory(new PropertyValueFactory<>("radius"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        phaseCol.setCellValueFactory(new PropertyValueFactory<>("phase"));
    }
    
    private void showFilterIcon() {
        Image phaseIcon = new Image("filter.png", 15, 15, true, true);
        hiddenRowsCounter = new Label();
        hiddenRowsCounter.setStyle("-fx-text-fill: red;");
        hiddenRowsCounter.setMaxSize(25.0, 17.0);
        hiddenRowsCounter.setMinSize(15.0, 15.0);
        hiddenRowsCounter.setBackground(new Background(new BackgroundImage(
                phaseIcon,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, 
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
    
    public TableView getTableView() {
        return tableView;
    }
    
    /**
     * Adds new single result to table model and changes filtering slider's values
     * @param result New result
     */
    public void handleNewResult(Star result) {
        tableModel.addResult(result);
        updateSliderBounds();
        updateHiddenRowsCounter();
    }
    
    /**
     * Adds whole collection of results obtained from input data file,
     * changes filtering slider's value
     * @param file Inpud data file
     * @throws IOException File not found / could not be read
     */
    public void setResults(File file) throws IOException {
        tableModel.setResults(InputFileExtractor.extract(file));
        updateSliderBounds();
        updateHiddenRowsCounter();
    }
}
