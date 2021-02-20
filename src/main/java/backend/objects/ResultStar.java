package backend.objects;

import backend.ResultType;
import backend.State;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import static backend.State.*;

/**
 * Enhanced representation of a star
 * Used for stars in results
 */
public class ResultStar extends Star {
    private final Double[] lowerDeviation = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private final Double[] upperDeviation = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private final String ROUNDING_FORMAT = "%.4f %s %s";
    private State sd = VALID;
    private ResultType resultType = ResultType.NONE;

    public ResultStar(Double temperature, Double luminosity, Double age, Double radius, Double mass, Double phase) {
        super(temperature, luminosity, age, radius, mass, phase);
    }

    public ResultStar(Double[] data) {
        super(data);
    }

    /**
     * Set SD uncertainties to characteristics
     * [age, radius, mass, phase] deviations, saved to low and high uncertainty attributes
     * @param index index of parameter (2=age, 3=rad, 4=mass, 5=phase)
     */
    public void setDeviation(int index, double lowerDev, double upperDev) {
        this.lowerDeviation[index] = lowerDev;
        this.upperDeviation[index] = upperDev;
    }

    /**
     * Sets temperature and luminosity uncertainties (input)
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     */
    public void setInputUncertainties(double temp_unc, double lum_unc) {
        setDeviation(0, -temp_unc, temp_unc);
        setDeviation(1, -lum_unc, lum_unc);
    }

    /**
     * Getter for all attributes as an array
     * @return Array of {temperature, luminosity, age, radius, mass, phase}
     */
    public Double[] getAllAttributes() {
        return new Double[]{getTemperature(), getLuminosity(), getAge(), getRadius(), getMass(), getPhase()};
    }

    /**
     * @return the temperature
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * @return the luminosity
     */
    public Double getLuminosity() {
        return luminosity;
    }

    /**
     * @return the age
     */
    public Double getAge() {
        return age;
    }

    /**
     * @return the radius
     */
    public Double getRadius() {
        return radius;
    }

    /**
     * @return the mass
     */
    public Double getMass() {
        return mass;
    }

    /**
     * @return the phase
     */
    public Double getPhase() {
        return phase;
    }

    //Returns text representation for tableView -- DO NOT DELETE -- valueFactory is using this -- DO NOT DELETE ----!!!
    /*public TextFlow getTemColumnText() {
        return (temperature == null || temperature.isNaN()) ? new TextFlow(new Text("-"))
                : new TextFlow(new Text(String.format("%.4f±%.4f", temperature, uncertainties[0])));
    }

    public TextFlow getLumColumnText() {
        return (luminosity == null || luminosity.isNaN())? new TextFlow(new Text("-"))
                : new TextFlow(new Text(String.format("%.4f±%.4f", luminosity, uncertainties[1])));
    }

    public TextFlow getAgeColumnText() { return getTextRepresentation(age, 2); }

    public TextFlow getRadColumnText() { return getTextRepresentation(radius, 3); }

    public TextFlow getMasColumnText() { return getTextRepresentation(mass, 4); }

    public TextFlow getPhaColumnText() { return getTextRepresentation(phase, 5); }*/

    /**
     * Returns text representation to fit in tableView
     * @param attribute Mean value
     * @param index index of attribute (used as index in lists)
     * @return String representation of value and uncertainty according to valid attributes
     */
    /*private TextFlow getTextRepresentation(Double attribute, int index) {
        double SMALL_ERROR = 0.00005;
        if (attribute == null || attribute.isNaN()) { return new TextFlow(new Text("-")); }

        if ((sd != VALID || deviations[index].isNaN()) && error != VALID) {
            Text t1 = new Text(String.format("%.4f ", attribute));
            Text t2 = new Text("SD");
            t2.setStrikethrough(true);
            Text t3 = new Text(" ");
            Text t4 = new Text("Err");
            t4.setStrikethrough(true);
            return new TextFlow(t1, t2, t3, t4);
        }

        if (error != VALID) {
            Text t1 = new Text(String.format("%.4f", attribute));
            Text t2 = new Text(String.format("±%.4f ", deviations[index]));
            if (deviations[index] < SMALL_ERROR) { t2.setStyle("-fx-font-style: italic"); }
            Text t3 = new Text("Err");
            t3.setStrikethrough(true);
            return new TextFlow(t1, t2, t3);
        }


        if ((sd != VALID || deviations[index].isNaN())) {
            Text t1 = new Text(String.format("%.4f", attribute));
            Text t2 = new Text(String.format("±%.4f ", errors[index]));
            if (errors[index] < SMALL_ERROR) { t2.setStyle("-fx-font-style: italic"); }
            Text t3 = new Text("SD");
            t3.setStrikethrough(true);
            return new TextFlow(t1, t2, t3);
        }

        Text t1 = new Text(String.format("%.4f±", attribute));
        Text t2 = new Text(String.format("%.4f", uncertainties[index]));
        if (uncertainties[index] < SMALL_ERROR) { t2.setStyle("-fx-font-style: italic"); }
        return new TextFlow(t1, t2);
    }

    //Returns string representation of rounded result (for export purpose)
    public String getFormattedTemperature() {
        return (String.format("%.4f %.4f", temperature, deviations[0]));
    }

    public String getFormattedLuminosity() {
        return (String.format("%.4f %.4f", luminosity, deviations[1]));
    }

    public String getFormattedAge() {
        return (age == null || age.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, age,
                String.format("%.4f", errors[2]),
                (sd != VALID || deviations[2].isNaN()) ? "-" : String.format("%.4f", deviations[2]));
    }

    public String getFormattedRadius() {
        return (radius == null || radius.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, radius,
                String.format("%.4f", errors[3]),
                (sd != VALID || deviations[3].isNaN()) ? "-" : String.format("%.4f", deviations[3]));
    }

    public String getFormattedMass() {
        return (mass == null || mass.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, mass,
                String.format("%.4f", errors[4]),
                (sd != VALID || deviations[4].isNaN()) ? "-" : String.format("%.4f", deviations[4]));
    }

    public String getFormattedPhase() {
        return (phase == null || phase.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, phase,
                String.format("%.4f",errors[5]),
                (sd != VALID || deviations[4].isNaN()) ? "-" : String.format("%.4f", deviations[5]));
    }*/

    public void printAllDeviations() {
        System.out.printf("%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n+%.4f\t+%.4f\t+%.4f\t+%.4f\t+%.4f\t+%.4f\n",
                lowerDeviation[0], lowerDeviation[1], lowerDeviation[2],
                lowerDeviation[3], lowerDeviation[4], lowerDeviation[5],
                upperDeviation[0], upperDeviation[1], upperDeviation[2],
                upperDeviation[3], upperDeviation[4], upperDeviation[5]);
    }

    /** Hide SD in results, but don't overwrite INVALID state */
    public void setHideSD() {
        sd = (sd != INVALID) ? HIDE : sd;
    }

    /*public boolean isValidSD() {
        return sd == VALID;
    } DELETE IF NOT NEEDED*/

    /** Set result type if current is NONE */
    public void setResultType(ResultType newType) {
        this.resultType = (this.resultType == ResultType.NONE) ? newType : this.resultType;
    }

    public ResultType getResultType() {
        return this.resultType;
    }

}
