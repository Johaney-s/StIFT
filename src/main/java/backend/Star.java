
package backend;

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
    private Double tem_dev = 0.0;
    private Double lum_dev = 0.0;
    private Double age_dev = Double.MAX_VALUE; //prevent hidden errors
    private Double rad_dev = Double.MAX_VALUE;
    private Double mas_dev = Double.MAX_VALUE;
    private Double pha_dev = Double.MAX_VALUE;
    private double[] errors = {0, 0, 0, 0};
    private double[] uncertainties = {0, 0, 0, 0, 0, 0};
    private final String ROUNDING_FORMAT = "%.4f";
    private boolean validSD = true;
    private boolean validError = true;

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
        this.age_dev = data[0];
        this.rad_dev = data[1];
        this.mas_dev = data[2];
        this.pha_dev = data[3];
    }

    /**
     * Sets temperature and luminosity uncertainties (input)
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     */
    public void setInputUncertainties(double temp_unc, double lum_unc) {
        this.tem_dev = temp_unc;
        this.lum_dev = lum_unc;
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

    public String getAgeColumnText() { return getTextRepresentation(age, uncertainties[2]); }

    public String getRadColumnText() { return getTextRepresentation(radius, uncertainties[3]); }

    public String getMasColumnText() { return getTextRepresentation(mass, uncertainties[4]); }

    public String getPhaColumnText() { return getTextRepresentation(phase, uncertainties[5]); }

    /**
     * Returns string representation to fit in tableView
     * @param attribute Mean value
     * @param uncertainty Root of summed error and deviation for value
     * @return String representation of value and uncertainty
     */
    private String getTextRepresentation(Double attribute, Double uncertainty) {
        String latex;
        if (attribute == null || attribute.isNaN()) {
            latex = "-";
        } else {
            latex = String.format("%.4f±%.4f", attribute, uncertainty);
            if (!validSD) {
                latex += " \\SD";
            }
        }
        return latex;
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
        System.out.printf("%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n", tem_dev, lum_dev, age_dev,
                rad_dev, mas_dev, pha_dev);
    }

    /**
     * Set and convert errors to absolute values, sum with SD error and save to uncertainties
     * Ommit SD if invalid
     * @param input_errors [age, rad, mass, phase] in percent
     */
    public void setErrors(double[] input_errors) {
        this.errors = new double[]{(age / 100) * input_errors[0], (radius / 100) * input_errors[1],
                (mass / 100) * input_errors[2], (phase / 100) * input_errors[3]};
        this.uncertainties[0] = tem_dev;
        this.uncertainties[1] = lum_dev;
        this.uncertainties[2] = (validSD) ? Math.sqrt(Math.pow(age_dev, 2) + Math.pow(errors[0], 2)) : errors[0];
        this.uncertainties[3] = (validSD) ? Math.sqrt(Math.pow(rad_dev, 2) + Math.pow(errors[1], 2)) : errors[1];
        this.uncertainties[4] = (validSD) ? Math.sqrt(Math.pow(mas_dev, 2) + Math.pow(errors[2], 2)) : errors[2];
        this.uncertainties[5] = (validSD) ? Math.sqrt(Math.pow(pha_dev, 2) + Math.pow(errors[3], 2)) : errors[3];
    }

    public double[] getErrors() {
        return this.errors;
    }

    public void setInvalidError() {
        validError = false;
    }

    public void setInvalidSD() {
        validSD = false;
    }

    public double[] getUncertainties() {
        return this.uncertainties;
    }
}
