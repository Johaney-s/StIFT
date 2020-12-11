
package GUI;

import backend.ResultFormatter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Main class
 */
public class StIFT extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        
        stage.setMinWidth(700);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setTitle("StIFT");
        stage.getProperties().put("hostServices", this.getHostServices());
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon.png")));
        ResultFormatter.latexToImage("PRERENDER TEX"); //first rendered result is slower, prerender now
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));
        launch(args);
    }
    
}
