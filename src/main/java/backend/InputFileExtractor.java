
package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for extracting input data from data file
 */
public class InputFileExtractor {
    
    public static ArrayList<Star> extract(File file) throws IOException {
        ArrayList<Star> newResults = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String row;

        while ((row = reader.readLine()) != null) {
            String[] record = row.split(",|\\s"); //delimiters

            if (record.length != 2 && record.length != 4) { throw new IOException("Invalid number of parameters"); }

            double temperature = Double.parseDouble(record[0]);
            double luminosity = Double.parseDouble(record[1]);
            double temp_unc = 0.0;
            double lum_unc = 0.0;

            if (record.length == 4) {
                temp_unc = Double.parseDouble(record[2]);
                lum_unc = Double.parseDouble(record[3]);
            }

            newResults.add(DataExtractor.getCurrentData().estimate(temperature, luminosity, temp_unc, lum_unc));
        }

        reader.close();
        return newResults;
    }
}
