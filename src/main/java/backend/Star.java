
package backend;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static backend.State.*;

/**
 * Representation of a star
 */
public class Star {
    private final Double temperature;
    private final Double luminosity;
    private final Double age;
    private final Double radius;
    private final Double mass;
    private final Double phase;
    private final Double[] deviations = {0.0, 0.0, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private double[] errors = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private final double[] uncertainties = {0, 0, 0, 0, 0, 0};
    private final String ROUNDING_FORMAT = "%.4f %s %s";
    private State sd = VALID;
    private State error = VALID;
    public static final double BIG_ERROR_RATIO = 0.2;

    public Star(Double temperature, Double luminosity, Double age, Double radius, Double mass, Double phase) {
        this.temperature = temperature;
        this.luminosity = luminosity;
        this.age = age;
        this.radius = radius;
        this.mass = mass;
        this.phase = phase;
    }

    public Star(Double[] data) {
        this.temperature = data[0];
        this.luminosity = data[1];
        this.age = data[2];
        this.radius = data[3];
        this.mass = data[4];
        this.phase = data[5];
    }

    /**
     * Set SD uncertainties to characteristics
     * [age, radius, mass, phase] deviations, saved to low and high uncertainty attributes
     * @param data Uncertainties excluding the temperature and luminosity (input) uncertainties
     */
    public void setDeviations(double[] data) {
        this.deviations[2] = data[0];
        this.deviations[3] = data[1];
        this.deviations[4] = data[2];
        this.deviations[5] = data[3];
    }

    /**
     * Sets temperature and luminosity uncertainties (input)
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     */
    public void setInputUncertainties(double temp_unc, double lum_unc) {
        this.deviations[0] = temp_unc;
        this.deviations[1] = lum_unc;
    }
    
    /**
     * Getter for all attributes as an array
     * @return Array of {temperature, luminosity, age, radius, mass, phase}
     */
    public Double[] getAllAttributes() {
        return new Double[]{getTemperature(), getLuminosity(), getAge(), getRadius(), getMass(), getPhase()};
    }

    /**
     * Prints all characteristics of current star
     */
    public void printValues() {
        System.out.printf("%.4f\t%.4f\t%s\t%s\t%s\t%s\n", temperature, luminosity,
                (age != null) ? String.format("%.4f",age) : "-",
                (radius != null) ? String.format("%.4f", radius) : "-",
                (mass != null) ? String.format("%.4f",mass) : "-",
                (phase != null) ? String.format("%.4f",phase) : "-");
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

    //Returns text representation for tableView
    public TextFlow getTemColumnText() {
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

    public TextFlow getPhaColumnText() { return getTextRepresentation(phase, 5); }

    /**
     * Returns text representation to fit in tableView
     * @param attribute Mean value
     * @param index index of attribute (used as index in lists)
     * @return String representation of value and uncertainty according to valid attributes
     */
    private TextFlow getTextRepresentation(Double attribute, int index) {
        if (attribute == null || attribute.isNaN()) { return new TextFlow(new Text("-")); }

        if ((sd != VALID || deviations[index].isNaN()) && (error != VALID || Math.abs(errors[index] / attribute) > BIG_ERROR_RATIO)) {
            Text t1 = new Text(String.format("%.4f ", attribute));
            Text t2 = new Text("SD");
            t2.setStrikethrough(true);
            Text t3 = new Text(" ");
            Text t4 = new Text("Err");
            t4.setStrikethrough(true);
            return new TextFlow(t1, t2, t3, t4);
        }

        if (error != VALID || Math.abs(errors[index] / attribute) > BIG_ERROR_RATIO) {
            Text t1 = new Text(String.format("%.4f", attribute));
            Text t2 = new Text(String.format("±%.4f ", deviations[index]));
            if (deviations[index] < 0.00005) { t2.setStyle("-fx-font-style: italic"); }
            Text t3 = new Text("Err");
            t3.setStrikethrough(true);
            return new TextFlow(t1, t2, t3);
        }


        if ((sd != VALID || deviations[index].isNaN())) {
            Text t1 = new Text(String.format("%.4f", attribute));
            Text t2 = new Text(String.format("±%.4f ", errors[index]));
            if (errors[index] < 0.00005) { t2.setStyle("-fx-font-style: italic"); }
            Text t3 = new Text("SD");
            t3.setStrikethrough(true);
            return new TextFlow(t1, t2, t3);
        }

        Text t1 = new Text(String.format("%.4f±", attribute));
        Text t2 = new Text(String.format("%.4f", uncertainties[index]));
        if (uncertainties[index] < 0.00005) { t2.setStyle("-fx-font-style: italic"); }
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
                (Math.abs(errors[2] / age) > BIG_ERROR_RATIO) ? "-" : String.format("%.4f", errors[2]),
                (sd != VALID || deviations[2].isNaN()) ? "-" : String.format("%.4f", deviations[2]));
    }

    public String getFormattedRadius() {
        return (radius == null || radius.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, radius,
                (Math.abs(errors[3] / radius) > BIG_ERROR_RATIO) ? "-" : String.format("%.4f", errors[3]),
                (sd != VALID || deviations[3].isNaN()) ? "-" : String.format("%.4f", deviations[3]));
    }

    public String getFormattedMass() {
        return (mass == null || mass.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, mass,
                (Math.abs(errors[4] / mass) > BIG_ERROR_RATIO) ? "-" : String.format("%.4f", errors[4]),
                (sd != VALID || deviations[4].isNaN()) ? "-" : String.format("%.4f", deviations[4]));
    }

    public String getFormattedPhase() {
        return (phase == null || phase.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, phase,
                (Math.abs(errors[5] / phase) > BIG_ERROR_RATIO) ? "-" : String.format("%.4f",errors[5]),
                (sd != VALID || deviations[4].isNaN()) ? "-" : String.format("%.4f", deviations[5]));
    }

    public void printAllUncertainties() {
        System.out.printf("%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n", deviations[0], deviations[1], deviations[2],
                deviations[3], deviations[4], deviations[5]);
    }

    /**
     * Set and convert errors to absolute values, sum with SD error and save to uncertainties
     * Omit SD if invalid
     * @param input_errors [age, rad, mass, phase] in percent
     */
    public void setErrors(double[] input_errors) {
        this.errors = new double[]{0, 0, (age / 100) * input_errors[0], (radius / 100) * input_errors[1],
                (mass / 100) * input_errors[2], (phase / 100) * input_errors[3]};
    }

    public double[] getErrors() {
        return this.errors;
    }

    public void setHideError() {
        error = HIDE;
    }

    public void setInvalidSD() {
        sd = INVALID;
    }

    /** Hide SD in results, but don't overwrite INVALID state */
    public void setHideSD() {
        sd = (sd != INVALID) ? HIDE : sd;
    }

    public double[] getUncertainties() {
        return this.uncertainties;
    }

    public boolean isValidSD() {
        return sd == VALID;
    }

    public boolean errorIsSet() { return errors[0] != Double.MAX_VALUE; }

    /** Call when errors are set */
    public void countUncertainty() {
        this.uncertainties[0] = deviations[0];
        this.uncertainties[1] = deviations[1];
        this.uncertainties[2] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[2], 2) + Math.pow(errors[2], 2)) : errors[2];
        this.uncertainties[3] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[3], 2) + Math.pow(errors[3], 2)) : errors[3];
        this.uncertainties[4] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[4], 2) + Math.pow(errors[4], 2)) : errors[4];
        this.uncertainties[5] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[5], 2) + Math.pow(errors[5], 2)) : errors[5];
    }
}
