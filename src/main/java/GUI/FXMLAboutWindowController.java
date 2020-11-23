package GUI;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import org.apache.commons.io.IOUtils;

import java.io.*;
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
        InputStream is = this.getClass().getResourceAsStream("/Readme.txt");

        try {
            File tempFile = File.createTempFile("readme", ".txt");
            tempFile.deleteOnExit();
            OutputStream out = new FileOutputStream(tempFile.getAbsolutePath());
            IOUtils.copy(is, out);
            hostServices.showDocument(tempFile.toURI().toString());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Unable to open Readme file.");
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
