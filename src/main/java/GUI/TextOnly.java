
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
            System.out.println("Use command 'java -jar file_name.jar text TEMP LUM TEMPunc LUMunc'.");
            System.out.println("Missing uncertainties will be treated as 0.0 values.");
            System.out.println("===================================");
            return;
        }

        try {
            InputStream is = TextOnly.class.getClassLoader().getResourceAsStream("Data.txt");
            data = GridFileParser.extract(is);

            System.out.println("Total number of isochrones: " + data.getGroupedData().size());
            ComputationStats stats = data.estimate_stats(x, y, x_unc, y_unc, true, true);

            System.out.printf("Input:%n%.4f\t%.4f uncertainties: %.4f\t%.4f%n", x, y, x_unc, y_unc);

            System.out.println("Teff[lg] Lum[lg] Age[dex] Rad Mass Phase");
            if (stats.getStar11() == null || stats.getStar22() == null) {
                if (stats.getResult() != null) {
                    System.out.println("Star match");
                }
            } else {
                System.out.println("Neighbours:");
                stats.getStar11().printValues();
                stats.getStar12().printValues();
                stats.getStar21().printValues();
                stats.getStar22().printValues();
            }

            if (stats.getResult1_() != null) {
                System.out.println("Evolutionary line:");
                stats.getResult1_().printValues();
                stats.getResult2_().printValues();
            }

            System.out.println("Mean value: <----------------------");
            stats.getResult().printValues();

            System.out.println("Sigma region:");
            if (stats.getResult().isValidSD()) {
                for (Star star : stats.getSigmaRegion()) { star.printValues(); }
                System.out.println("Standard deviation:");
                stats.getResult().printAllUncertainties();
            } else {
                System.out.println("No stars in sigma region <- no deviation results");
            }

            //System.out.println(stats);
            Star mean_result = stats.getResult();

            //Show all error values
            System.out.printf("Interpolation error:%n------\t------\t%.4f\t%.4f\t%.4f\t%.4f%n", mean_result.getErrors()[2],
                    mean_result.getErrors()[3], mean_result.getErrors()[4], mean_result.getErrors()[5]);

            System.out.printf("Uncertainties: <-------------------"
                            + "%n%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f%n", mean_result.getUncertainties()[0],
                    mean_result.getUncertainties()[1], mean_result.getUncertainties()[2], mean_result.getUncertainties()[3],
                    mean_result.getUncertainties()[4], mean_result.getUncertainties()[5]);
        } catch (NullPointerException ex) {
            System.out.println("No more computable data found.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("===================================");
    }
}
