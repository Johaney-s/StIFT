
package GUI;

import backend.*;

import java.io.InputStream;

/**
 * Alternative class for printing steps to console
 */
public class TextOnly {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Data data;
        double x;
        double y;
        double x_unc = 0.0;
        double y_unc = 0.0;
        System.out.println("========= StIFT text mode =========");

        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            if (args.length == 5) {
                x_unc = Double.parseDouble(args[3]);
                y_unc = Double.parseDouble(args[4]);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid arguments - please provide valid input parameters.");
            System.out.println("Use command 'java -jar file_name.jar text TEMP LUM TEMPunc LUMunc'.\nMissing uncertainties "
                    + "will be treated as 0.0 values.");
            System.out.println("===================================");
            return;
        }

        try {
            InputStream is = TextOnly.class.getClassLoader().getResourceAsStream("Data.txt");
            data = GridFileParser.extract(is);

            System.out.println("Total number of isochrones: " + data.getGroupedData().size() + "\n");
            ComputationStats stats = data.estimate_stats(x, y, x_unc, y_unc);

            System.out.printf("Input:\n%.4f\t%.4f uncertainties: %.4f\t%.4f\n", x, y, x_unc, y_unc);

            System.out.println("Neighbours:");
            stats.getStar11().printValues();
            stats.getStar12().printValues();
            stats.getStar21().printValues();
            stats.getStar22().printValues();

            System.out.println("Evolutionary line:");
            stats.getResult1_().printValues();
            stats.getResult2_().printValues();

            System.out.println("Mean value:");
            stats.getResult().printValues();

            System.out.println("Sigma region:");
            for (Star star : stats.getSigmaRegion()) { star.printValues(); }

            //System.out.println("Sum of squared differences:\n[age, radius, mass, phase]");
            //System.out.printf("%.4f\t%.4f\t%4f\t%.4f\n", stats.getDeviations()[0], stats.getDeviations()[1],
            //        stats.getDeviations()[2], stats.getDeviations()[3]);

            System.out.println("Standard deviation:");
            stats.getResult().printAllUncertainties();

            //System.out.println(stats.toString()); ALPHA - PSI, A, B, C

            Interpolator.determineUncertainties(stats);

        } catch (NullPointerException ex) {
            System.out.println("No more computable data found.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("===================================");
    }
}

/* Sgt. Pepper's lonely values club band
            double x = 3.66883;
            double y = -0.71457;
            double x = 4;
            double y = 3.5;
            double x = 4.4;
            double y = 5.5;
            double x = 4.9849557522123895;
            double y = 6.017082785808146; <---------BUGGED LINE POINTS
            double x = 4.387308533916849;
            double y = 5.664536741214057; <-- WEIRD PHASE RESULT*/
