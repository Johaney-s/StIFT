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

                    while (row  != null) {
                        String[] record = row.split(",|\\s"); //delimiters

                        if (record.length != 2 && record.length != 4) {
                            reader.close();
                            throw new IOException("Invalid number of parameters");
                        }

                        double temperature = Double.parseDouble(record[0]);
                        double luminosity = Double.parseDouble(record[1]);
                        double temp_unc = 0.0;
                        double lum_unc = 0.0;

                        if (record.length == 4) {
                            temp_unc = Double.parseDouble(record[2]);
                            lum_unc = Double.parseDouble(record[3]);
                        }

                        newResults.add(GridFileParser.getCurrentData().estimate(temperature, luminosity, temp_unc, lum_unc));
                        row = reader.readLine();
                    }

                    reader.close();
                    return null;
                }
            };
        }
    }
}

