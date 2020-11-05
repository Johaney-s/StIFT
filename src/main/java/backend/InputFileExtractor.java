
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
        int recordCounter = 0;

        while ((row = reader.readLine()) != null) {
            String[] record = row.split(",|\\s"); //delimiters

            if (record.length != 2) { //validate number of attributes --> throw errors
                throw new IOException("Invalid number of parameters");
            }

            double temperature = Double.parseDouble(record[0]);
            double luminosity = Double.parseDouble(record[1]);
            newResults.add(DataExtractor.getCurrentData().estimate(temperature, luminosity));
            }
        reader.close();
        return newResults;
    }
}
