
package backend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Model class for storing estimated results
 */
public class TableModel {
    private final ObservableList<Star> resultList = FXCollections.observableArrayList();
    private final FilteredList<Star> filteredList = new FilteredList<>(resultList);
    private final Filter filter = new Filter(null, null);
    private boolean saved = true;
    private boolean hideEmptyRows = false;
    
    public TableModel() {
        updatePredicate();
    }
    
    /**
     * Adds result to the beginning of the resultList
     * @param newResult Result to be added to the result list
     */
    public void addResult(Star newResult) {
        resultList.add(0, newResult); //how to optimize this (observable linked list needed)
        saved = false;
    }

    /**
     * Exports resultList to file
     * @param file Output file
     * @throws IOException Cannot create file
     */
    public void exportResults(File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("Teff[lgK] Teff_SD L[lgLsun] L_SD Age[dexYrs] Age_err Age_SD R[Rsun] R_err R_SD M[Msun] M_err M_SD Phase Phase_err Phase_SD" + System.getProperty("line.separator"));
        for (Star result : filteredList) {
            fileWriter.write(String.format("%s %s %s %s %s %s" + System.getProperty("line.separator"), result.getFormattedTemperature(), result.getFormattedLuminosity(),
                    result.getFormattedAge(), result.getFormattedRadius(), result.getFormattedMass(), result.getFormattedPhase()));
        }
        fileWriter.close();
        saved = true;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public ObservableList<Star> getResults() {
        return filteredList;
    }
    
    public ObservableList<Star> getAllResults() {
        return resultList;
    }
    
    public void setResults(ArrayList<Star> newResults) {
        resultList.clear();
        resultList.addAll(newResults);
        removeFilter();
        hideEmptyRows = false;
        saved = false;
    }
    
    public boolean isFiltered() {
        return filter.isSet();
    }
    
    public void removeFilter() {
        filter.setBounds(null, null);
        updatePredicate();
    }
    
    public void setFilter(Double lowerBound, Double upperBound) {
        filter.setBounds(lowerBound, upperBound);
        updatePredicate();
    }
    
    private void updatePredicate() {
        filteredList.setPredicate(x -> {
            if (x.getPhase() == null || x.getPhase().isNaN()) { return !hideEmptyRows; }
            if (!filter.isSet()) { return true; }
            return (x.getPhase() >= filter.getLowerBound() &&
                    x.getPhase() <= filter.getUpperBound());
        });
    }

    public void setHideEmptyRows(boolean boo) {
        hideEmptyRows = boo;
        updatePredicate();
    }

    public boolean getHideEmptyRows() {
        return hideEmptyRows;
    }

    /**
     * Clears all results and resets filters, sets flag as saved
     */
    public void reset() {
        resultList.clear();
        saved = true;
        filter.setBounds(null, null);
        hideEmptyRows = false;
    }

    /**
     * @return Number of hidden rows due to filtering
     */
    public int getHiddenCount() {
        return resultList.size() - filteredList.size();
    }
}
