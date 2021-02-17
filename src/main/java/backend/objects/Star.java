
package backend.objects;

/**
 * Representation of a star
 */
public class Star {
    protected final Double temperature;
    protected final Double luminosity;
    protected final Double age;
    protected final Double radius;
    protected final Double mass;
    protected final Double phase;

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
}
