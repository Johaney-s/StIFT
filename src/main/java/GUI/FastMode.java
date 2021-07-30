package GUI;

import backend.*;

import java.io.*;
import java.util.stream.Collectors;

/** Fast mode performs input file -> export file processing */
public class FastMode {

    /** @param args the command line arguments */
    public static void main(String[] args) {
        Data data;
        File inputFile;
        File gridFile;
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

            // try to set zams for custom grid
            Short phaseZAMS = null;
            if (gridFile != null) {
                System.out.println("Select ZAMS phase or type [N] to skip: " + data.getCurrentPhases().stream().sorted().collect(Collectors.toList()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    Short inputZamsPhase;
                    String zams = reader.readLine();
                    if (zams.equalsIgnoreCase("N"))
                        break;
                    try {
                        inputZamsPhase = Short.parseShort(zams);
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect phase. Skip [N] or choose one of the phases included in your data file: "
                                + data.getCurrentPhases().stream().sorted().collect(Collectors.toList()));
                        continue;
                    }

                    if (!data.getCurrentPhases().contains(inputZamsPhase)) {
                        System.out.println("Incorrect phase. Skip [N] or choose one of the phases included in your data file: "
                                + data.getCurrentPhases().stream().sorted().collect(Collectors.toList()));
                    } else {
                        phaseZAMS = inputZamsPhase;
                        break;
                    }
                }
            }

            Settings settings = new Settings();
            if (gridFile == null) {
                settings.setDefaultSettings();
            } else {
                settings = new Settings(data.getCurrentPhases(), phaseZAMS, false);
            }
            data.applySettings(settings);
            Data.setCurrentData(data);

            System.out.println("===================================");
            System.out.println("Total number of isochrones: " + data.getGroupedData().size());
            if (settings.getPhaseZams()!=null)
                System.out.println("ZAMS phase: " + settings.getPhaseZams().toString());
            TableModel tableModel = new TableModel();
            InputFileParser.extract(inputFile, tableModel);
            tableModel.exportResults(exportFile);
            System.out.println("Computation finished, data exported to " + exportFile + ".");
        } catch (IOException ex) {
            System.out.println("ERROR MESSAGE: " + ex.getMessage());
        }

        System.out.println("===================================");
    }
}
