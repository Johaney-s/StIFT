
package GUI;

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
        if (lowerBound != this.lowerBound || upperBound != this.upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            isSet = true;
        }
        
        if (lowerBound == null) {
            isSet = false;
        }
    }
}
