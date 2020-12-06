
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
        if (data.length > 6) {
            this.tem_uncertainity_low = data[6];
            this.tem_uncertainity_high = data[6];
            this.lum_uncertainity_high = data[7];
            this.lum_uncertainity_low = data[7];
        }
    }

    /**
     * Set uncertainties to characteristics
     * Order copies order of attributes in Star class, first negative, then positive uncertainty
     * @param data
     */
    public void setUncertainties(double[] data) {
        this.tem_uncertainity_low = data[0];
        this.tem_uncertainity_high = data[1];
        this.lum_uncertainity_low = data[2];
        this.lum_uncertainity_high = data[3];
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
        System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\n", getFormattedTemperature(), getFormattedLuminosity(), getFormattedAge(),
                getFormattedRadius(), getFormattedMass(), getFormattedPhase());
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

    /* Return string representation of attribute rounded to 4 decimal places or '-' if no such exists*/
    public String getFormattedTemperature() {
        return (temperature == null || temperature.isNaN()) ? "-" : String.format("%.4f", temperature);
    }

    public String getFormattedLuminosity() {
        return (luminosity == null || luminosity.isNaN()) ? "-" : String.format("%.4f", luminosity);
    }

    public String getFormattedAge() {
        return (age == null || age.isNaN()) ? "-" : String.format("%.4f", age);
    }

    public String getFormattedRadius() {
        return (radius == null || radius.isNaN()) ? "-" : String.format("%.4f", radius);
    }

    public String getFormattedMass() {
        return (mass == null || mass.isNaN()) ? "-" : String.format("%.4f", mass);
    }

    public String getFormattedPhase() {
        return (phase == null || phase.isNaN()) ? "-" : String.format("%.4f", phase);
    }
    
}
