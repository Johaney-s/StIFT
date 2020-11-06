package GUI;

import backend.DataExtractor;
import backend.Star;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 *
 * @author Admin
 */
public class FXMLMainController implements Initializable {
    
    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField temperatureField;
    @FXML
    private TextField luminosityField;
    @FXML
    private Label informationLabel;
    @FXML
    private Button goButton;
    @FXML
    private VBox root;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private MenuItem setAsDefaultItem;
    @FXML
    private MenuItem uploadInputDataFile;
    @FXML
    private FXMLLineChartController lineChartController;
    @FXML
    private FXMLTableViewController tableViewController;
    
    private final FadeTransition fadeIn = new FadeTransition(
        Duration.millis(1000)
    );
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lineChartController.setMainController(this);
        disableItemsWhileNoGridAvailable(true);
        InputStream inStream = getClass().getResourceAsStream("/Data.txt");
        lineChartController.showGraph(inStream);
        disableItemsWhileNoGridAvailable(false);
        
        //-- Information label fade in effect
        fadeIn.setNode(informationLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
        
        root.setVgrow(scrollPane, Priority.ALWAYS); //FXML does not work
    }

    //-- MENU BAR -- File / Edit / Options
    @FXML
    public void exportDataItemAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export data");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());

        if (file != null) {
            try {
                tableViewController.getTableModel().exportResults(file);
                showAlert("Export data", "Data exported successfuly.", AlertType.INFORMATION);
            } catch (IOException ex) {
                showAlert("Export failed", "Could not export data.", AlertType.ERROR);
            }
        }
    }
    
    @FXML
    public void uploadNewGridItemAction() {
        if(!tableViewController.getTableModel().isSaved() && !unsavedChangesAlert()) {
            return;
        }
        
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload new grid");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            try {
                lineChartController.showGraph(file);
                disableItemsWhileNoGridAvailable(false);
                showAlert("Upload new grid", "New grid uploaded successfuly.", AlertType.INFORMATION);
            } catch (FileNotFoundException ex) {
                showAlert("Data file not found", "Could not find data file, previous data remain valid.", AlertType.ERROR);
            }
        }
    }
    
    @FXML
    public void uploadInputDataFileAction() {
        if(!tableViewController.getTableModel().isSaved() && !unsavedChangesAlert()) {
            return;
        }
        
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload input data file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            try {
                tableViewController.setResults(file);
            } catch (FileNotFoundException ex) {
                showAlert("Data file not found", "Could not find data file, previous data remain valid.", AlertType.ERROR);
            } catch (IOException ex) {
                showAlert("Data file not found", ex.getMessage(), AlertType.ERROR);
            }
        }
    }
    
    @FXML
    public void resetGridItemAction() {
        if(!tableViewController.getTableModel().isSaved() && !unsavedChangesAlert()) {
            return;
        } else {
            Alert alert = showAlert("Reset grid to default", "Do you want to reset grid to default grid?",
                    AlertType.CONFIRMATION);
            if (alert.getResult() != null && alert.getResult().equals(ButtonType.OK)) {
                InputStream inStream = getClass().getResourceAsStream("/Data.txt");
                lineChartController.showGraph(inStream);
            }
        }
    }
    
    @FXML
    public void aboutItemAction() {
        System.out.println("About");
    }
    
    @FXML
    public void setAsDefaultGridItemAction() {
        Alert alert = showAlert("Set grid as default", "Do you really want to set current grid as default?\nPath: ADD PATH TO FILE", AlertType.CONFIRMATION);
        if(alert.getResult() != null && alert.getResult().equals(ButtonType.OK)) {
            System.out.println("Preference change to current path.");
        }
    }
    
    //-- INPUT GROUP -- Go button
    @FXML
    public void goButtonAction() {
        informationLabel.setVisible(false);
        temperatureField.setStyle(null);
        luminosityField.setStyle(null);
        
        Double inputTemperatureValue = checkInput(temperatureField);
        Double inputLuminosityValue = checkInput(luminosityField);

        if (inputTemperatureValue != null && inputLuminosityValue != null) {
            manageInput(inputTemperatureValue, inputLuminosityValue);
            luminosityField.clear();
            temperatureField.clear();
        }
    }

    
    //-- other functions --
    
    /**
     * Checks input, if valid returns value, if not highlights problematic field
     * @param field
     * @return Value or null, if input is not valid
     */
    private Double checkInput(TextField field) {
        try{
            return Double.parseDouble(field.getText());
        } catch (NumberFormatException ex) {
            field.setStyle("-fx-border-color: red");
            informationLabel.setVisible(true);
            fadeIn.playFromStart();
            return null;
        }
    }

    /**
     * Creates alert dialogue
     * @param header Header of the alert
     * @param message Content of the alert
     * @param type Alert type
     * @return Alert
     */
    public Alert showAlert(String header, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
        return alert;
    }
    
    /**
     * Processes input and handles result to tableview
     * @param x X input coordinate
     * @param y Y input coordinate
     */
    public void manageInput(double x, double y) {
        Star result = DataExtractor.getCurrentData().estimate(x, y);
        tableViewController.handleNewResult(result);
    }
    
    /**
     * Public method for disabling menu items that should be disabled when
     * no graph is currently shown
     * @param boo True for disabling, false for undisabling
     */
    public void disableItemsWhileNoGridAvailable(boolean boo) {
        setAsDefaultItem.setDisable(boo);
        uploadInputDataFile.setDisable(boo);
        goButton.setDisable(boo);
    }
    
    /**
     * Call when unsaved changes detected
     * @return true if previous action shall continue, false otherwise (cancelled / saving)
     */
    private boolean unsavedChangesAlert() {
        Alert alert = new Alert(AlertType.NONE,  "Some results might not be saved, would you like to export data?",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setHeaderText("Unsaved results");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            exportDataItemAction();
            return false;
        } else if (alert.getResult() == ButtonType.CANCEL) {
            return false;
        }
        return true;
    }
}
