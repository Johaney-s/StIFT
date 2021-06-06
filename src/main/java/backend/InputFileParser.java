package backend;

import GUI.FXMLLoadingController;
import GUI.FXMLTableController;
import backend.objects.ResultStar;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** Code adjusted from example available at:
 * https://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm */
public abstract class InputFileParser {

    /**
     * Parse file with input data
     * @param file File with data
     * @param tableModel table storage
     * @param loadingController controller with loading spinner
     * @param fxmlTableController parent controller
     */
    public static Void extract(File file, Parent root, TableModel tableModel, FXMLLoadingController loadingController, FXMLTableController fxmlTableController) {
        InputService service = new InputService();
        service.setFile(file);

        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                tableModel.setResults(service.getResults());
                loadingController.hideLoadingPane();
                fxmlTableController.updateSliderBounds();
                fxmlTableController.updateHiddenRowsCounter();
                root.getScene().getRoot().setDisable(false); //refactor
            }
        });

        service.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                loadingController.hideLoadingPane();
                root.getScene().getRoot().setDisable(false); //refactor
                fxmlTableController.inputFailed();
            }
        });

        loadingController.showLoadingPane();
        service.start();
        return null;
    }

    private static class InputService extends Service<Void> {
        private File file;
        private final ArrayList<ResultStar> newResults = new ArrayList<>();

        public void setFile(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public ArrayList<ResultStar> getResults() {
            return newResults;
        }

        @Override
        protected Task<Void> createTask() {
            File file = getFile();

            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ArrayList<ResultStar> newResults = getResults();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String row = reader.readLine();

                    while (row != null && row.length() > 0 && row.charAt(0) == '#') { //skip header in file
                        row = reader.readLine();
                    }

                    int counter = 0;
                    while (row  != null) {
                        counter++;
                        String[] record = row.split(",|\\s"); //delimiters

                        if (record.length != 2 && record.length != 4) {
                            reader.close();
                            throw new IOException("Invalid number of parameters on line " + counter);
                        }

                        double temperature = Double.parseDouble(record[0]);
                        double luminosity = Double.parseDouble(record[1]);
                        double temp_unc = 0.0;
                        double lum_unc = 0.0;
                        short rounding = 2;

                        if (record.length == 4) {
                            temp_unc = Double.parseDouble(record[2]);
                            lum_unc = Double.parseDouble(record[3]);
                            String[] splittedUnc = record[2].split("\\.");
                            rounding = (splittedUnc.length > 1 && splittedUnc[1].length() > 1) ? (short)splittedUnc[1].length() : rounding;
                        }

                        newResults.add(Data.getCurrentData().estimate(temperature, luminosity, temp_unc, lum_unc, rounding));
                        row = reader.readLine();
                    }

                    reader.close();
                    return null;
                }
            };
        }
    }

    /**
     * Parse input file (so far fast mode usage)
     * @param file Checked input file
     * @param tableModel storage of results
     */
    public static void extract(File file, TableModel tableModel) throws IOException, NumberFormatException {
        ArrayList<ResultStar> newResults = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String row = reader.readLine();

        while (row != null && row.length() > 0 && row.charAt(0) == '#') { //skip header in file
            row = reader.readLine();
        }

        int counter = 0;
        while (row  != null) {
            counter++;
            String[] record = row.split(",|\\s"); //delimiters

            if (record.length != 2 && record.length != 4) {
                reader.close();
                throw new IOException("Invalid number of parameters on line " + counter);
            }

            try {
                double temperature = Double.parseDouble(record[0]);
                double luminosity = Double.parseDouble(record[1]);
                double temp_unc = 0.0;
                double lum_unc = 0.0;
                short rounding = 2;

                if (record.length == 4) {
                    temp_unc = Double.parseDouble(record[2]);
                    lum_unc = Double.parseDouble(record[3]);
                    String[] splittedUnc = record[2].split("\\.");
                    rounding = (splittedUnc.length > 1 && splittedUnc[1].length() > 1) ? (short)splittedUnc[1].length() : rounding;
                }

                newResults.add(Data.getCurrentData().estimate(temperature, luminosity, temp_unc, lum_unc, rounding));
                System.out.println("Processed " + counter + ". row.");
                row = reader.readLine();
            } catch (NumberFormatException ex) {
                reader.close();
                throw new IOException("Invalid parameter on line " + counter + ".");
            }
        }

        reader.close();
        tableModel.setResults(newResults);
    }
}

