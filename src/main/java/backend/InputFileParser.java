package backend;

import GUI.FXMLLoadingController;
import GUI.FXMLTableController;
import GUI.TableModel;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** Code adjusted from example available at:
 * https://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm */
public class InputFileParser {

    /**
     * Parse file with input data
     * @param file File with data
     * @param tableModel table storage
     * @param loadingController controller with loading spinner
     * @param fxmlTableController parent controller
     * @throws IOException File not found or couldn't be opened
     */
    public Void extract(File file, TableModel tableModel, FXMLLoadingController loadingController, FXMLTableController fxmlTableController) throws IOException {
        InputService service = new InputService();
        service.setFile(file);

        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                tableModel.setResults(service.getResults());
                loadingController.stopLoading();
                fxmlTableController.updateSliderBounds();
                fxmlTableController.updateHiddenRowsCounter();
            }
        });

        service.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                loadingController.startLoading();
            }
        });

        loadingController.startLoading();
        service.start();
        return null;
    }

    private static class InputService extends Service<Void> {
        private File file;
        private final ArrayList<Star> newResults = new ArrayList<>();

        public void setFile(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public ArrayList<Star> getResults() {
            return newResults;
        }

        @Override
        protected Task<Void> createTask() {
            File file = getFile();

            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ArrayList<Star> newResults = getResults();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String row;

                    while ((row = reader.readLine()) != null) {
                        String[] record = row.split(",|\\s"); //delimiters

                        if (record.length != 2 && record.length != 4) {
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
                    }

                    reader.close();
                    return null;
                }
            };
        }
    }
}

