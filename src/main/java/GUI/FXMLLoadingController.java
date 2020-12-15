package GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLLoadingController implements Initializable {

    @FXML
    private AnchorPane loadingPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void showLoadingPane() {
        loadingPane.setVisible(true);
    }

    public void hideLoadingPane() {
        loadingPane.setVisible(false);
    }
}
