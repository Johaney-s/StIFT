
package backend;

import backend.objects.Star;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** Extractor and parser for data from file */
public abstract class GridFileParser {

    /**
     * Reads csv file, checks validity, returns parsed data
     * @return Array of Stars objects
     * @throws IOException Could not read data file
     * @throws FileNotFoundException Could not find data file
     */
    public static Data extract(InputStream inStream) throws IOException, FileNotFoundException {
        if (inStream == null) {
            throw new FileNotFoundException("Cannot find file containing grid data.");
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        Data data = new Data();
        String row = reader.readLine();
        int recordCounter = 1;

        while (row != null && row.length() > 0 && row.charAt(0) == '#') { //skip comments in file's header
            recordCounter++;
            row = reader.readLine();
        }

        while (row != null) {
            try {
                String[] record = row.split(",|\\s"); //delimiters

                if (record.length != 6 && record.length != 7) { //validate number of attributes
                    reader.close();
                    inStream.close();
                    throw new IOException("Invalid number of attributes on line " + recordCounter);
                }

                double temperature = Double.parseDouble(record[0]);
                double luminosity = Double.parseDouble(record[1]);
                double age = Double.parseDouble(record[2]);
                double radius = Double.parseDouble(record[3]);
                double mass = Double.parseDouble(record[4]);
                double phase = Double.parseDouble(record[5]);
                Star star = new Star(temperature, luminosity, age, radius, mass, phase);
                if (record.length == 6) {
                    data.addStar(star);
                } else {
                    data.addStar(star, Integer.parseInt(record[6]));
                }
                recordCounter++;
                row = reader.readLine();
            } catch (NumberFormatException e){
                reader.close();
                inStream.close();
                throw new IOException("Invalid argument on line " + recordCounter);
            }
        }

        reader.close();
        inStream.close();
        data.addCurrentGroupToGroupedData();
        return data;
    }
}
