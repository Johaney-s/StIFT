
package backend;

import javafx.scene.image.ImageView;

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
    private Double tem_uncertainty = 0.0;
    private Double lum_uncertainty = 0.0;
    private Double age_uncertainty;
    private Double rad_uncertainty;
    private Double mas_uncertainty;
    private Double pha_uncertainty;
    private final String LATEX_FORMAT = "%.4f_\\pm%.4f";
    private final String ROUNDING_FORMAT = "%.4f";

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
     * Set uncertainties to characteristics
     * Order copies order of attributes in Star class, first negative, then positive uncertainty
     * @param data Uncertainties excluding the temperature and luminosity (input) uncertainties
     */
    public void setUncertainties(double[] data) {
        this.age_uncertainty = data[0];
        this.rad_uncertainty = data[1];
        this.mas_uncertainty = data[2];
        this.pha_uncertainty = data[3];
    }

    /**
     * Sets temperature and luminosity uncertainties (input)
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     */
    public void setInputUncertainties(double temp_unc, double lum_unc) {
        this.tem_uncertainty = temp_unc;
        this.lum_uncertainty = lum_unc;
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

    //Returns TeX representation of value and uncertainties
    public ImageView getTeXTemperature() { return getImageView(temperature, tem_uncertainty); }

    public ImageView getTeXLuminosity() { return getImageView(luminosity, lum_uncertainty); }

    public ImageView getTeXAge() { return getImageView(age, age_uncertainty); }

    public ImageView getTeXRadius() { return getImageView(radius, rad_uncertainty); }

    public ImageView getTeXMass() { return getImageView(mass, mas_uncertainty); }

    public ImageView getTeXPhase() { return getImageView(phase, pha_uncertainty); }

    /** Generates imageView of result */
    private ImageView getImageView(Double attribute, Double uncertainty) {
        String latex;
        if (attribute == null || attribute.isNaN()) { latex = "-"; }
        else if (uncertainty == null) { latex = String.format(ROUNDING_FORMAT, attribute); }
        else { latex = String.format(LATEX_FORMAT, attribute, Math.abs(uncertainty)); }

        return ResultFormatter.latexToImage(latex);
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
        System.out.printf("%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n", tem_uncertainty, lum_uncertainty, age_uncertainty,
                rad_uncertainty, mas_uncertainty, pha_uncertainty);
    }
}
