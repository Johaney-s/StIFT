
package GUI;

import backend.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class TextOnly {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Data data;
        try {
            InputStream is = TextOnly.class.getClassLoader().getResourceAsStream("Data.txt");
            data = DataExtractor.extract(is);
            System.out.println("Total number of groups: " + data.getGroupedData().size() + "\n");
            
            /*double x = 3.66883;
            double y = -0.71457;
            double x = 4;
            double y = 3.5;
            double x = 4.9849557522123895;
            double y = 6.017082785808146; <---------BUGGED LINE POINTS
            double x = 4.387308533916849;
            double y = 5.664536741214057; <-- WEIRD PHASE RESULT
            */
            double x = 3.6;
            double y = 4.5;
            
            ComputationStats stats = new ComputationStats(x,y);
            data.findNearestStars(stats);
            System.out.printf("Input:\n%f\t%f%n", x, y);

            System.out.println("Neighbours:");
            stats.getStar11().printValues();
            stats.getStar12().printValues();
            stats.getStar21().printValues();
            stats.getStar22().printValues();

            Interpolator.determineEvolutionaryStatus(stats);
            Interpolator.interpolateAllCharacteristics(stats);
            System.out.println("Line:");
            stats.getResult1_().printValues();
            stats.getResult2_().printValues();
            System.out.println("Result:");
            stats.getResult().printValues();

            //System.out.println("Derivation:");
            //Interpolator.determineUncertainties(stats);
        } catch (IOException ex) {
            Logger.getLogger(TextOnly.class.getName()).log(Level.SEVERE, "Error reading file", ex);
        }
    }

}
