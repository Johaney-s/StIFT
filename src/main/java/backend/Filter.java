
package backend;

/**
 * Class for filtering function
 */
public class Filter {
    private Double lowerBound;
    private Double upperBound;
    boolean isSet = false;
    
    public Filter(Double lowerBound, Double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    public boolean isSet() {
        return isSet;
    }
    
    public Double getLowerBound() {
        return lowerBound;
    }
    
    public Double getUpperBound() {
        return upperBound;
    }
    
    public void setBounds(Double lowerBound, Double upperBound) {
        if (lowerBound == null) {
            isSet = false;
            return;
        }

        if (!lowerBound.equals(this.lowerBound) || !upperBound.equals(this.upperBound)) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            isSet = true;
        }
    }
}
