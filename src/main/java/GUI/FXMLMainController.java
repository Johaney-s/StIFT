package GUI;

import backend.GridFileParser;
import backend.Star;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main controller class
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
    private TextField tempUncertaintyField;
    @FXML
    private TextField lumUncertaintyField;
    @FXML
    private Label informationLabel;
    @FXML
    private Button goButton;
    @FXML
    private VBox root;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private MenuItem uploadInputDataFile;
    @FXML
    private FXMLLineChartController lineChartController;
    @FXML
    private FXMLTableController tableViewController;
    
    private final FadeTransition fadeIn = new FadeTransition(
        Duration.millis(1000)
    );
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //optional scroll bar for extending tableview
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        lineChartController.setMainController(this);
        disableItemsWhileNoGridAvailable(true);
        InputStream inStream = getClass().getResourceAsStream("/Data.txt");
        lineChartController.showGraph(inStream);
        tableViewController.getTableModel().reset();
        disableItemsWhileNoGridAvailable(false);
        
        //-- Information label fade in effect
        fadeIn.setNode(informationLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
        
        root.setVgrow(scrollPane, Priority.ALWAYS); //FXML command does not work
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
                showAlert("Export data", "Data exported successfully.", AlertType.INFORMATION);
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
                tableViewController.reset();
                disableItemsWhileNoGridAvailable(false);
                showAlert("Upload new grid", "New grid uploaded successfully.", AlertType.INFORMATION);
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
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Upload input data file error", ex.getMessage(), AlertType.ERROR);
            }
        }
    }
    
    @FXML
    public void resetGridItemAction() {
        if(!tableViewController.getTableModel().isSaved() && !unsavedChangesAlert()) {
            return;
        }
        
        Alert alert = showAlert("Reset grid to default", "Do you want to reset grid to default grid?",
                    AlertType.CONFIRMATION);
        if (alert.getResult() != null && alert.getResult().equals(ButtonType.OK)) {
            InputStream inStream = getClass().getResourceAsStream("/Data.txt");
            lineChartController.showGraph(inStream);
            tableViewController.reset();
        }
    }
    
    @FXML
    public void aboutItemAction() {
        FXMLLoader aboutFxmlLoader = new FXMLLoader(getClass().getResource("FXMLAboutWindow.fxml"));
        try {
            Parent loaderRoot = aboutFxmlLoader.load();
            FXMLAboutWindowController aboutWindowController = aboutFxmlLoader.getController();
            HostServices hs = (HostServices)scrollPane.getScene().getWindow().getProperties().get("hostServices");
            aboutWindowController.addHostServices(hs);
            Scene scene = new Scene(loaderRoot);
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(loaderRoot.getScene().getWindow());
            dialog.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon.png")));
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showAlert("Open about section", "Could not open about section.", AlertType.ERROR);
        }
    }
    
    //-- INPUT GROUP -- Go button
    @FXML
    public void goButtonAction() {
        informationLabel.setVisible(false);
        temperatureField.getStyleClass().removeAll("invalid");
        tempUncertaintyField.getStyleClass().removeAll("invalid");
        luminosityField.getStyleClass().removeAll("invalid");
        lumUncertaintyField.getStyleClass().removeAll("invalid");
        
        Double inputTemperatureValue = checkInput(temperatureField);
        Double inputLuminosityValue = checkInput(luminosityField);
        Double inputTempUncertainty = checkInput(tempUncertaintyField);
        Double inputLumUncertainty = checkInput(lumUncertaintyField);

        if (inputTemperatureValue != null && inputTempUncertainty != null &&
                inputLuminosityValue != null && inputLumUncertainty != null) {
            manageInput(inputTemperatureValue, inputLuminosityValue, inputTempUncertainty, inputLumUncertainty);
            temperatureField.clear();
            tempUncertaintyField.setText("0.0");
            luminosityField.clear();
            lumUncertaintyField.setText("0.0");
        }
    }

    
    //-- other functions --
    
    /**
     * Checks input, if valid returns value, if not highlights problematic field
     * @param field Text field which value needs validation
     * @return Value or null, if input is not valid
     */
    private Double checkInput(TextField field) {
        try{
            return Double.parseDouble(field.getText());
        } catch (NumberFormatException ex) {
            field.getStyleClass().add("invalid");
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
     * @param temp_unc temperature uncertainty
     * @param lum_unc temperature uncertainty
     */
    public void manageInput(double x, double y, double temp_unc, double lum_unc) {
        Star result = GridFileParser.getCurrentData().estimate(x, y, temp_unc, lum_unc);
        tableViewController.handleNewResult(result);
    }
    
    /**
     * Public method for disabling menu items that should be disabled when
     * no graph is currently shown
     * @param boo True for disabling, false for undisabling
     */
    public void disableItemsWhileNoGridAvailable(boolean boo) {
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
