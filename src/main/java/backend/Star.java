
package backend;

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
    private Double[] deviations = {0.0, 0.0, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private double[] errors = {0, 0, 0, 0, 0, 0};
    private double[] uncertainties = {0, 0, 0, 0, 0, 0};
    private final String ROUNDING_FORMAT = "%.4f";
    private State sd = VALID;
    private State error = VALID;

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
    public double[] getAllAttributes() {
        return new double[]{getTemperature(), getLuminosity(), getAge(), getRadius(), getMass(), getPhase()};
    }

    /**
     * Prints all characteristics of current star
     */
    public void printValues() {
        System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\n", getFormattedTemperature(), getFormattedLuminosity(),
                getFormattedAge(), getFormattedRadius(), getFormattedMass(), getFormattedPhase());
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
    public String getTemColumnText() {
        return (temperature == null || temperature.isNaN()) ? "-" : String.format("%.4f±%.4f", temperature, uncertainties[0]);
    }

    public String getLumColumnText() {
        return (luminosity == null || luminosity.isNaN()) ? "-" : String.format("%.4f±%.4f", luminosity, uncertainties[1]);
    }

    public String getAgeColumnText() { return getTextRepresentation(age, 2); }

    public String getRadColumnText() { return getTextRepresentation(radius, 3); }

    public String getMasColumnText() { return getTextRepresentation(mass, 4); }

    public String getPhaColumnText() { return getTextRepresentation(phase, 5); }

    /**
     * Returns string representation to fit in tableView
     * @param attribute Mean value
     * @param index index of attribute (used as index in lists)
     * @return String representation of value and uncertainty according to valid attributes
     */
    private String getTextRepresentation(Double attribute, int index) {
        if (attribute == null || attribute.isNaN()) { return "-"; }
        if (sd != VALID && error != VALID) { return String.format("%.4f \\SD\\Err", attribute); }
        if (sd != VALID) { return String.format("%.4f±%.4f \\SD", attribute, errors[index]); }
        if (error != VALID) { return String.format("%.4f±%.4f \\Err", attribute, deviations[index]); }
        return String.format("%.4f±%.4f", attribute, uncertainties[index]);
    }

    //Returns string representation of rounded result
    public String getFormattedTemperature() {
        return (temperature == null || temperature.isNaN()) ? "-" : String.format(ROUNDING_FORMAT, temperature);
    }

    public String getFormattedLuminosity() {
        return (luminosity == null || luminosity.isNaN()) ? "-" : String.format(ROUNDING_FORMAT, luminosity);
    }

    public String getFormattedAge() {
        return (age == null || age.isNaN()) ? "-" : String.format(ROUNDING_FORMAT, age);
    }

    public String getFormattedRadius() {
        return (radius == null || radius.isNaN()) ? "-" : String.format(ROUNDING_FORMAT, radius);
    }

    public String getFormattedMass() {
        return (mass == null || mass.isNaN()) ? "-" : String.format(ROUNDING_FORMAT, mass);
    }

    public String getFormattedPhase() {
        return (phase == null || phase.isNaN()) ? "-" : String.format(ROUNDING_FORMAT, phase);
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
        this.uncertainties[0] = deviations[0];
        this.uncertainties[1] = deviations[1];
        this.uncertainties[2] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[2], 2) + Math.pow(errors[2], 2)) : errors[2];
        this.uncertainties[3] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[3], 2) + Math.pow(errors[3], 2)) : errors[3];
        this.uncertainties[4] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[4], 2) + Math.pow(errors[4], 2)) : errors[4];
        this.uncertainties[5] = (sd != INVALID) ? Math.sqrt(Math.pow(deviations[5], 2) + Math.pow(errors[5], 2)) : errors[5];
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

    /**
     * Hide SD in results, but don't overwrite INVALID state
     */
    public void setHideSD() {
        sd = (sd != INVALID) ? HIDE : sd;
    }

    public double[] getUncertainties() {
        return this.uncertainties;
    }
}
