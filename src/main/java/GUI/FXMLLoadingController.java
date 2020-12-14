package GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLLoadingController implements Initializable {

    @FXML
    private AnchorPane loadingPane;

    private Stage stage;
    private Stage owner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Scene scene = new Scene(loadingPane);
        stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
    }

    public void setOwner(Stage owner) {
        this.owner = owner;
        stage.initOwner(owner);
    }

    public void startLoading() {
        Stage ownerStage = (Stage) owner.getScene().getWindow();
        stage.setX(ownerStage.getX() + ownerStage.getWidth() / 2);
        stage.setY(ownerStage.getY() + ownerStage.getHeight() / 2);
        stage.show();
    }

    public void stopLoading() {
        stage.close();
    }
}
