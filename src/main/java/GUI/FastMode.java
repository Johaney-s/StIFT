package GUI;

import backend.*;

import java.io.*;

/** Fast mode performs input file -> export file processing */
public class FastMode {

    /** @param args the command line arguments */
    public static void main(String[] args) {
        Data data;
        File inputFile;
        File gridFile = null;
        File exportFile;
        System.out.println("========= StIFT fast mode =========");

        try {
            inputFile = new File(args[1]);
            gridFile = (args.length == 4) ? new File(args[2]) : null;
            exportFile = (args.length == 4) ? new File(args[3] + ".txt") : new File(args[2] + ".txt");
            if (args.length > 4) {
                throw new Exception();
            }
        } catch (Exception ex) {
            System.out.println("Invalid arguments - please provide valid input parameters.");
            System.out.println("Use command 'java -jar file_name.jar fast INPUT_FILE [GRID_FILE] EXPORT_FILE_NAME'.");
            System.out.println("Missing grid file argument instructs StIFT to use default.");
            System.out.println("===================================");
            return;
        }

        try {
            System.out.println("Input file: " + inputFile);
            InputStream is = TextOnly.class.getClassLoader().getResourceAsStream("Data.txt");
            if (gridFile != null) {
                is = new FileInputStream(gridFile);
                System.out.println("Grid file: " + gridFile);
            } else {
                System.out.println("Grid file: default (PARSEC + COLIBRI)");
            }
            data = GridFileParser.extract(is);
            System.out.println("Export file: " + exportFile);

            System.out.println("Total number of isochrones: " + data.getGroupedData().size());
            TableModel tableModel = new TableModel();
            InputFileParser.extract(inputFile, tableModel);
            tableModel.exportResults(exportFile);
            System.out.println("Computation finished, data exported to " + exportFile + ".");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("===================================");
    }
}
