
package GUI;

import backend.*;
import backend.objects.ResultStar;

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
        short rounding = 2;
        System.out.println("========= StIFT text mode =========");

        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            if (args.length >= 5) {
                x_unc = Double.parseDouble(args[3]);
                y_unc = Double.parseDouble(args[4]);
                String[] splittedUnc = args[3].split("\\.");
                rounding = (splittedUnc.length > 1 && splittedUnc[1].length() > 1) ? (short)splittedUnc[1].length() : rounding;
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
            Data.setCurrentData(data);

            System.out.println("Total number of isochrones: " + data.getGroupedData().size());
            System.out.printf("Input:%n%.4f\t%.4f uncertainties: %.4f\t%.4f%n", x, y, x_unc, y_unc);
            ComputationStats stats = data.estimateStats(x, y, x_unc, y_unc, rounding);
            System.out.println("Estimation method: " + stats.getResult().getResultType());
            System.out.println("lgTeff[K] lgL[Lsun] lgAge[yrs] Rad[Rsun] Mass[Msun] Phase");

            if (stats.getStar11() != null || stats.getStar12() != null || stats.getStar21() != null || stats.getStar22() != null) {
                System.out.println("Neighbours:");
                if (stats.getStar11() != null)
                    stats.getStar11().printValues();
                if (stats.getStar12() != null)
                    stats.getStar12().printValues();
                if (stats.getStar21() != null)
                    stats.getStar21().printValues();
                if (stats.getStar22() != null)
                    stats.getStar22().printValues();
            }

            if (stats.getResult1_() != null) {
                System.out.println("Evolutionary line:");
                stats.getResult1_().printValues();
                stats.getResult2_().printValues();
            }

            ResultStar mean = stats.getResult();
            System.out.println("Mean value: <----------------------");
            mean.printValues();

            System.out.println("Uncertainties:");
            mean.printAllDeviations();
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
