package GUI;

import backend.Data;
import backend.objects.ResultStar;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javafx.animation.FadeTransition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;

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
    private VBox vBox;
    @FXML
    private FXMLLineChartController lineChartController;
    @FXML
    private FXMLTableController tableViewController;
    @FXML
    private FXMLLoadingController loadingController;
    @FXML
    private CheckBox includeDeviationBox;
    @FXML
    private GridPane phasePane;
    @FXML
    private ProgressBar estimationsBar;

    ArrayList<CheckBox> allCheckBoxes = new ArrayList<>();
    private final FadeTransition fadeIn = new FadeTransition(Duration.millis(1000));
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
    }); //terminate threads when exiting application

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        lineChartController.setMainController(this);
        InputStream inStream = getClass().getResourceAsStream("/Data.txt");
        lineChartController.showGraph(inStream, true);
        fillPhaseBoxes();
        tableViewController.getTableModel().reset();
        tableViewController.setLoadingController(loadingController);
        
        //-- Information label fade in effect
        fadeIn.setNode(informationLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
    }

    //-- MENU BAR -- File / Edit / Options
    @FXML
    public void exportDataItemAction() {
        if (executor.getQueue().size() > 0) {
            showAlert("Running tasks", "Some computations are still running, please wait.", AlertType.INFORMATION);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export data");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(vBox.getScene().getWindow());

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
        if (executor.getQueue().size() > 0) {
            showAlert("Running tasks", "Some computations are still running, please wait.", AlertType.INFORMATION);
            return;
        }

        if(!tableViewController.getTableModel().isSaved() && !unsavedChangesAlert()) {
            return;
        }
        
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload new grid");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showOpenDialog(vBox.getScene().getWindow());
        if (file != null) {
            if (lineChartController.showGraph(file, false)) {
                tableViewController.reset();
                fillPhaseBoxes();
                showAlert("Upload new grid", "New grid uploaded successfully.", AlertType.INFORMATION);
            }
        }
    }
    
    @FXML
    public void uploadInputDataFileAction() {
        if (executor.getQueue().size() > 0) {
            showAlert("Running tasks", "Some computations are still running, please wait.", AlertType.INFORMATION);
            return;
        }

        if(!tableViewController.getTableModel().isSaved() && !unsavedChangesAlert()) {
            return;
        }
        
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload input data file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showOpenDialog(vBox.getScene().getWindow());
        if (file != null) {
            tableViewController.setResults(file);
        }
    }
    
    @FXML
    public void resetGridItemAction() {
        if (executor.getQueue().size() > 0) {
            showAlert("Running tasks", "Some computations are still running, please wait.", AlertType.INFORMATION);
            return;
        }

        if(!tableViewController.getTableModel().isSaved() && !unsavedChangesAlert()) {
            return;
        }
        
        Alert alert = showAlert("Reset grid to default", "Do you want to reset grid to default grid?",
                    AlertType.CONFIRMATION);
        if (alert.getResult() != null && alert.getResult().equals(ButtonType.OK)) {
            InputStream inStream = getClass().getResourceAsStream("/Data.txt");
            lineChartController.showGraph(inStream, true);
            tableViewController.reset();
            fillPhaseBoxes();
            showAlert("Reset grid", "Grid successfully reset to default.", AlertType.INFORMATION);
        }
    }
    
    @FXML
    public void aboutItemAction() {
        FXMLLoader aboutFxmlLoader = new FXMLLoader(getClass().getResource("FXMLAboutWindow.fxml"));
        try {
            Parent loaderRoot = aboutFxmlLoader.load();
            FXMLAboutWindowController aboutWindowController = aboutFxmlLoader.getController();
            HostServices hs = (HostServices)vBox.getScene().getWindow().getProperties().get("hostServices");
            aboutWindowController.addHostServices(hs);
            Scene scene = new Scene(loaderRoot);
            final Stage dialog = new Stage();
            dialog.setMinHeight(300);
            dialog.setMinWidth(290);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(loaderRoot.getScene().getWindow());
            dialog.getIcons().add(new Image(this.getClass().getResourceAsStream("/manual.png")));
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showAlert("Open about section", "Could not open about section.", AlertType.ERROR);
        }
    }

    @FXML
    public void resetResultsAction() {
        if (executor.getQueue().size() > 0) {
            showAlert("Running tasks", "Some computations are still running, please wait.", AlertType.INFORMATION);
            return;
        }
        Alert alert = showAlert("Reset results table", "Do you want to reset the results table?",
                AlertType.CONFIRMATION);
        if (alert.getResult() != null && alert.getResult().equals(ButtonType.OK)) {
            tableViewController.reset();
        }
    }
    
    //-- INPUT GROUP -- Go button, phase checkboxes
    @FXML
    public void goButtonAction() {
        informationLabel.setVisible(false);
        temperatureField.getStyleClass().removeAll("invalid");
        tempUncertaintyField.getStyleClass().removeAll("invalid");
        luminosityField.getStyleClass().removeAll("invalid");
        lumUncertaintyField.getStyleClass().removeAll("invalid");
        
        Double inputTemVal = checkInput(temperatureField);
        Double inputLumVal = checkInput(luminosityField);
        Double inputTemUnc= checkInput(tempUncertaintyField);
        Double inputLumUnc = checkInput(lumUncertaintyField);

        if (inputTemVal != null && inputTemUnc != null && inputLumVal != null && inputLumUnc != null) {
            String[] splittedUnc = tempUncertaintyField.getText().split("\\.");
            short rounding = (splittedUnc.length > 1 && splittedUnc[1].length() > 1) ? (short)splittedUnc[1].length() : 2;
            manageInput(inputTemVal, inputLumVal, inputTemUnc, inputLumUnc, rounding);
            temperatureField.clear();
            tempUncertaintyField.setText("0.0");
            luminosityField.clear();
            lumUncertaintyField.setText("0.0");
        }
    }

    /**
     * Fill grid pane with checkboxes for phases
     */
    private void fillPhaseBoxes() {
        phasePane.getChildren().clear();
        allCheckBoxes.clear();
        List<Short> phasesValues = new ArrayList<>(Data.getCurrentData().getCurrentPhases());
        phasesValues.sort(Comparator.naturalOrder());
        if (Data.getCurrentData().getCurrentPhases().size() > 12) {
            phasePane.add(new Label("Not"), 0, 0);
            phasePane.add(new Label("available"), 0, 1);
        } else {
            for (int i = 0; i < phasesValues.size(); i++) {
                CheckBox checkBox = new CheckBox(phasesValues.get(i).toString());
                allCheckBoxes.add(checkBox);
                phasePane.add(checkBox, i % 3, i / 3);
                checkBox.setSelected(true);
            }
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
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        switch(type) {
            case ERROR: stage.getIcons().add(new Image(this.getClass().getResource("/error.png").toString()));
                        break;
            case CONFIRMATION: stage.getIcons().add(new Image(this.getClass().getResource("/question.png").toString()));
                        break;
            case INFORMATION: stage.getIcons().add(new Image(this.getClass().getResource("/ok.png").toString()));
                        break;
        }

        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node
                -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE)); //fix for broken linux dialogues
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
    public void manageInput(double x, double y, double temp_unc, double lum_unc, short rounding) {
        boolean includeDeviation = includeDeviationBox.isSelected();
        HashSet<Short> ignoredPhases = new HashSet<>();
        estimationsBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        for (CheckBox checkBox : allCheckBoxes) {
            if (!checkBox.isSelected()) { ignoredPhases.add(Short.parseShort(checkBox.getText())); }
        }

        Runnable runnable = () -> {
            ResultStar result = Data.getCurrentData().estimate(x, y, temp_unc, lum_unc, includeDeviation, rounding, ignoredPhases);
            tableViewController.handleNewResult(result);
            if (executor.getQueue().size() > 0) {
                Platform.runLater(() -> {
                    estimationsBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                });
            } else {
                Platform.runLater(() -> {
                    estimationsBar.setProgress(0);
                });
            }
        };
        executor.execute(runnable);
    }
    
    /**
     * Call when unsaved changes detected
     * @return true if previous action shall continue, false otherwise (cancelled / saving)
     */
    private boolean unsavedChangesAlert() {
        Alert alert = new Alert(AlertType.NONE,  "Some results might not be saved, would you like to export data?",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/question.png").toString()));
        alert.setHeaderText("Unsaved results");
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node
                -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE)); //fix for broken linux dialogues
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
