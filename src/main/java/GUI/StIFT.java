
package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class
 */
public class StIFT extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setMinWidth(680);
        stage.setMinHeight(610);
        stage.setScene(scene);
        stage.setTitle("StIFT");
        stage.getProperties().put("hostServices", this.getHostServices());
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon.png")));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
