package backend;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ResultFormatter {

    /**
     * Converts formula to image
     * @param latex Input formula
     * @return ImageView with transferred formulain a TeX style
     */
    public static ImageView latexToImage(String latex){
        String fml  ="\\begin{array}{l}" + latex + "\\end{array}";
        TeXFormula formula = new TeXFormula(fml);
        BufferedImage image = (BufferedImage) formula.createBufferedImage(fml, 0, 16, Color.BLACK,null);

        WritableImage writableImage = SwingFXUtils.toFXImage(image, null);
        return new ImageView(writableImage);
    }
}
