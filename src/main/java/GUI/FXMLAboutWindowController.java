package GUI;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLAboutWindowController implements Initializable {

    @FXML
    private Hyperlink gitLink;
    private HostServices hostServices = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void openReadme() {
        URL url = this.getClass().getResource("/Readme.txt");
        try {
            hostServices.showDocument(url.toURI().toString()); //TO BE FIXED YET
        } catch (URISyntaxException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Unable to open Readme.txt file.");
            alert.showAndWait();
        }
    }

    public void addHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void openGit() {
        hostServices.showDocument(gitLink.getText());
    }
}
