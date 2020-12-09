
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
    private Double tem_uncertainity_low = 0.0;
    private Double lum_uncertainity_low = 0.0;
    private Double age_uncertainity_low = 0.0;
    private Double rad_uncertainity_low = 0.0;
    private Double mas_uncertainity_low = 0.0;
    private Double pha_uncertainity_low = 0.0;
    private Double tem_uncertainity_high = 0.0;
    private Double lum_uncertainity_high = 0.0;
    private Double age_uncertainity_high = 0.0;
    private Double rad_uncertainity_high = 0.0;
    private Double mas_uncertainity_high = 0.0;
    private Double pha_uncertainity_high = 0.0;
    private final String LATEX_FORMAT = "%.4f_{-%.4f}^{+%.4f}";
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
        this.age_uncertainity_low = data[4];
        this.age_uncertainity_high = data[5];
        this.rad_uncertainity_low = data[6];
        this.rad_uncertainity_high = data[7];
        this.mas_uncertainity_low = data[8];
        this.mas_uncertainity_high = data[9];
        this.pha_uncertainity_low = data[10];
        this.pha_uncertainity_high= data[11];
    }

    /**
     * Sets temperature and luminosity uncertainties (input)
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     */
    public void setInputUncertainties(double temp_unc, double lum_unc) {
        this.tem_uncertainity_low = temp_unc;
        this.tem_uncertainity_high = temp_unc;
        this.lum_uncertainity_low = lum_unc;
        this.lum_uncertainity_high = lum_unc;
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
    public ImageView getTeXTemperature() {
        String latex = (temperature == null || temperature.isNaN()) ? "-"
                : String.format(LATEX_FORMAT, temperature, Math.abs(tem_uncertainity_low), tem_uncertainity_high);

        return ResultFormatter.latexToImage(latex);
    }

    public ImageView getTeXLuminosity() {
        String latex = (luminosity == null || luminosity.isNaN()) ? "-"
                : String.format(LATEX_FORMAT, luminosity, Math.abs(lum_uncertainity_low), lum_uncertainity_high);

        return ResultFormatter.latexToImage(latex);
    }

    public ImageView getTeXAge() {
        String latex = (age == null || age.isNaN()) ? "-"
                : String.format(LATEX_FORMAT, age, Math.abs(age_uncertainity_low), age_uncertainity_high);

        return ResultFormatter.latexToImage(latex);
    }

    public ImageView getTeXRadius() {
        String latex = (radius == null || radius.isNaN()) ? "-"
                : String.format(LATEX_FORMAT, radius, Math.abs(rad_uncertainity_low), rad_uncertainity_high);

        return ResultFormatter.latexToImage(latex);
    }

    public ImageView getTeXMass() {
        String latex = (mass == null || mass.isNaN()) ? "-"
                : String.format(LATEX_FORMAT, mass, Math.abs(mas_uncertainity_low), mas_uncertainity_high);

        return ResultFormatter.latexToImage(latex);
    }

    public ImageView getTeXPhase() {
        String latex = (phase == null || phase.isNaN()) ? "-"
                : String.format(LATEX_FORMAT, phase, Math.abs(pha_uncertainity_low), pha_uncertainity_high);

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
}
