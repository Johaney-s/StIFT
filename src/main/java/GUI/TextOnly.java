
package GUI;

import backend.Data;
import backend.DataExtractor;
import backend.Interpolator;
import backend.Star;
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
            InputStream file = new FileInputStream("resources\\Data.txt");
            data = DataExtractor.extract(file);
            System.out.println("Total number of groups: " + data.getGroupedData().size() + "\n");
            
            /*double x = 3.66883;
            double y = -0.71457;*/
            double x = 4;
            double y = 3.5;
            /*double x = 4.9849557522123895;
            double y = 6.017082785808146; <---------BUGGED LINE POINTS
            double x = 4.387308533916849;
            double y = 5.664536741214057; <-- WEIRD PHASE RESULT
            */
            
            Star[] neighbours = data.findNearestStars(x, y);
            System.out.printf("Input:\n%f\t%f%n", x, y);
            System.out.println("Neighbours:");
            for (Star n : neighbours) { n.printValues(); }
            double[] line = Interpolator.determineEvolutionaryStatus(neighbours[0], neighbours[1], neighbours[2], neighbours[3], x, y);
            System.out.printf("Line:\n%f\t%f\n%f\t%f%n", line[0], line[1], line[2], line[3]);
            Star result = Interpolator.interpolateAllCharacteristics(neighbours[0], neighbours[1], neighbours[2], neighbours[3], line, x, y);
            System.out.println("Result:");
            result.printValues();
        } catch (IOException ex) {
            Logger.getLogger(TextOnly.class.getName()).log(Level.SEVERE, "Error reading file", ex);
        }
    }
    
    
    public static void printData(Data data) {
        System.out.println("Teff \t L \t lg Age \t radius \t mass \t phase");
        int counter = 0;
        for (ArrayList<Star> l : data.getGroupedData().values()) {
            System.out.println("Group of initial mass " + l.get(0).getMass());
            for (Star s : l) {
            System.out.println(s.getTemperature() + "\t" + s.getLuminosity() + "\t" + s.getAge() + "\t" + s.getRadius() + "\t" + s.getMass() + "\t" + s.getPhase());
            counter++;
            }
        }
        System.out.println("Total number of stars: " + counter);
    }
}
