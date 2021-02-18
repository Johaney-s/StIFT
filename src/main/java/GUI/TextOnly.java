
package GUI;

import backend.*;
import backend.objects.ResultStar;
import backend.objects.Star;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            if (args.length >= 5) {
                x_unc = Double.parseDouble(args[3]);
                y_unc = Double.parseDouble(args[4]);
            }
            if (args.length > 6) {
                throw new Exception();
            }
        } catch (Exception ex) {
            System.out.println("Invalid arguments - please provide valid input parameters.");
            System.out.println("Use command 'java -jar file_name.jar text TEMP LUM [TEMPunc LUMunc] [GRID_FILE]'.");
            System.out.println("Missing pair of uncertainties will be treated as 0.0 values.");
            System.out.println("Missing grid file argument instructs StIFT to use default.");
            System.out.println("===================================");
            return;
        }

        try {
            InputStream is = TextOnly.class.getClassLoader().getResourceAsStream("Data.txt");
            if (args.length == 6 || args.length == 4) {
                is = new FileInputStream(args[args.length - 1]);
                System.out.println("Grid file: " + args[args.length - 1]);
            } else {
                System.out.println("Grid file: default (PARSEC + COLIBRI)");
            }
            data = GridFileParser.extract(is);

            System.out.println("Total number of isochrones: " + data.getGroupedData().size());
            System.out.printf("Input:%n%.4f\t%.4f uncertainties: %.4f\t%.4f%n", x, y, x_unc, y_unc);
            ComputationStats stats = data.estimateStats(x, y, x_unc, y_unc);
            System.out.println("Teff[lg] Lum[lg] Age[dex] Rad Mass Phase");

            System.out.println("Neighbours:");
            if (stats.getStar11() != null) {
                stats.getStar11().printValues();
                stats.getStar12().printValues();
            }

            if (stats.getStar22() != null) {
                stats.getStar21().printValues();
                stats.getStar22().printValues();
            }

            if (stats.getResult() != null && stats.getResult().getAge() != null && stats.getStar11() == null && stats.getStar21() == null) {
                System.out.println("Star match");
            }

            if (stats.getResult1_() != null) {
                System.out.println("Evolutionary line:");
                stats.getResult1_().printValues();
                stats.getResult2_().printValues();
            }

            System.out.println("Mean value: <----------------------");
            stats.getResult().printValues();

            ResultStar mean = stats.getResult();
            if (mean.getAge() != null) {
                System.out.println("Sigma region:");
                if (stats.getSigmaRegion().size() > 0) {
                    for (Star star : stats.getSigmaRegion()) {
                        star.printValues();
                    }
                    System.out.println("Sigma region deviation:");
                    stats.getResult().printAllDeviations();
                } else {
                    System.out.println("No stars in sigma region.");
                }

                //Show all error values
                if (mean.errorIsSet()) {
                    System.out.printf("Neighbours deviation:%n------\t------\t%.4f\t%.4f\t%.4f\t%.4f%n", mean.getErrors()[2],
                            mean.getErrors()[3], mean.getErrors()[4], mean.getErrors()[5]);
                } else {
                    System.out.println("Neighbours deviation: N/A");
                }

                if (mean.isValidSD()) {
                    System.out.printf("Uncertainties: <-------------------%n%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f%n",
                            mean.getUncertainties()[0], mean.getUncertainties()[1],
                            mean.getUncertainties()[2], mean.getUncertainties()[3],
                            mean.getUncertainties()[4], mean.getUncertainties()[5]);
                }

                Statistics.computeUncertainty(stats);
            }
        } catch (NullPointerException ex) {
            System.out.println("No more computable data found.");
        } catch (FileNotFoundException ex) {
            System.out.println("Grid file not found.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("===================================");
    }
}
