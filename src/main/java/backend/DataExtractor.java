
package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extractor and parser for data from file
 * 
 */
public class DataExtractor {
    
    private static Data currentData;
    
    /**
     * Reads csv file, checks validity, returns parsed data
     * @return Array of Stars objects
     * @throws IOException Could not read data file
     * @throws FileNotFoundException Could not find data file
     */
    public static Data extract(InputStream inStream) throws IOException, FileNotFoundException {        
        if (inStream == null) {
            throw new FileNotFoundException();
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        Data data = new Data();
        String row;
        int recordCounter = 0;

        while ((row = reader.readLine()) != null) {
            String[] record = row.split(",|\\s"); //delimiters

            if (record.length != 6) { //validate number of attributes
                Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE, "Invalid number of attributes on line " + recordCounter, "");
            }

            //+++++++++add further attributes checks (positive age, radius, etc.)

            double temperature = Double.parseDouble(record[0]);
            double luminosity = Double.parseDouble(record[1]);
            double age = Double.parseDouble(record[2]);
            double radius = Double.parseDouble(record[3]);
            double mass = Double.parseDouble(record[4]);
            double phase = Double.parseDouble(record[5]);
            Star star = new Star(temperature, luminosity, age, radius, mass, phase);
            data.addStar(star);
        }

        reader.close();
        data.addCurrentGroupToGroupedData();
        currentData = data;
        return data;
    }
    
    /**
     * Get currently operated data
     * @return Current data
     */
    public static Data getCurrentData() {
        return currentData;
    }
}
