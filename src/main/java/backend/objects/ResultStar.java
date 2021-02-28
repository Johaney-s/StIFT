package backend.objects;

import backend.ResultType;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Enhanced representation of a star
 * Used for stars in results
 */
public class ResultStar extends Star {
    private final Double[] lowerDeviation = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
    private final Double[] upperDeviation = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private final String ROUNDING_FORMAT = "%." + VALUES_PRECISION + "f %s %s";
    private ResultType resultType = ResultType.NONE;
    public static int VALUES_PRECISION = 4; //eventually switch from static if rounding is input-dependent
    private int UNCERTAINTY_PRECISION = 3;

    public ResultStar(Double temperature, Double luminosity, Double age, Double radius, Double mass, Double phase) {
        super(temperature, luminosity, age, radius, mass, phase);
    }

    public ResultStar(Double[] data) {
        super(data);
    }

    /**
     * Set SD uncertainties to characteristics
     * [age, radius, mass, phase] uncertainty, saved to low and high uncertainty attributes
     * @param index index of parameter (2=age, 3=rad, 4=mass, 5=phase)
     */
    public void setUncertainty(int index, double lowerDev, double upperDev) {
        this.lowerDeviation[index] = lowerDev;
        this.upperDeviation[index] = upperDev;
    }

    /**
     * Returns (upper, lower) uncertainties of attribute given by index
     * (0 = teff, 1=lum, 2=age, 3=rad, 4=mass, 5=phase)
     * @param index Index of attribute
     * @return array [upper, lower] uncertainty
     */
    public Double[] getUncertainty(int index) {
        return new Double[]{lowerDeviation[index], upperDeviation[index]};
    }

    /**
     * Sets temperature and luminosity uncertainties (input)
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     */
    public void setInputUncertainties(double temp_unc, double lum_unc) {
        setUncertainty(0, -temp_unc, temp_unc);
        setUncertainty(1, -lum_unc, lum_unc);
    }

    //Returns text representation for tableView -- DO NOT DELETE -- valueFactory is using this -- DO NOT DELETE ----!!!
    public HBox getTemColumnText() {
        HBox hBox = (temperature == null || temperature.isNaN()) ? new HBox(new Text("-"))
                : new HBox(new Text(String.format("%." + VALUES_PRECISION + "f±%." + UNCERTAINTY_PRECISION + "f",
                temperature, upperDeviation[0])));
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    public HBox getLumColumnText() {
        HBox hBox = (luminosity == null || luminosity.isNaN())? new HBox(new Text("-"))
                : new HBox(new Text(String.format("%." + VALUES_PRECISION + "f±%." + UNCERTAINTY_PRECISION + "f",
                luminosity, upperDeviation[1])));
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    public HBox getAgeColumnText() { return getTextRepresentation(age, 2); }

    public HBox getRadColumnText() { return getTextRepresentation(radius, 3); }

    public HBox getMasColumnText() { return getTextRepresentation(mass, 4); }

    public HBox getPhaColumnText() { return getTextRepresentation(phase, 5); }

    /**
     * Returns text representation to fit in tableView
     * @param attribute Mean value
     * @param index index of attribute (used as index in lists)
     * @return String representation of value and uncertainty according to valid attributes
     */
    private HBox getTextRepresentation(Double attribute, int index) {
        if (attribute == null || attribute.isNaN()) {
            return new HBox(new Text("-"));
        }

        HBox container = new HBox();
        Text normal = new Text(formatValue(attribute, VALUES_PRECISION));
        Text errors = new Text("+" + formatValue(upperDeviation[index], UNCERTAINTY_PRECISION)
                + "\n" + " -" + formatValue(Math.abs(lowerDeviation[index]), UNCERTAINTY_PRECISION));
        errors.setStyle("-fx-font-size: 0.8em");
        container.getChildren().addAll(normal, errors);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }

    //Returns string representation of rounded result (for export purpose)
    public String getFormattedTemperature() {
        return (String.format("%." + VALUES_PRECISION + "f %." + UNCERTAINTY_PRECISION + "f", temperature, upperDeviation[0]));
    }

    public String getFormattedLuminosity() {
        return (String.format("%." + VALUES_PRECISION + "f %." + UNCERTAINTY_PRECISION + "f", luminosity, upperDeviation[1]));
    }

    public String getFormattedAge() {
        return (age == null || age.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, age,
                formatValue(lowerDeviation[2], UNCERTAINTY_PRECISION), formatValue(upperDeviation[2], UNCERTAINTY_PRECISION));
    }

    public String getFormattedRadius() {
        return (radius == null || radius.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, radius,
                formatValue(lowerDeviation[3], UNCERTAINTY_PRECISION), formatValue(upperDeviation[3], UNCERTAINTY_PRECISION));
    }

    public String getFormattedMass() {
        return (mass == null || mass.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, mass,
                formatValue(lowerDeviation[4], UNCERTAINTY_PRECISION), formatValue(upperDeviation[4], UNCERTAINTY_PRECISION));
    }

    public String getFormattedPhase() {
        return (phase == null || phase.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, phase,
                formatValue(lowerDeviation[5], UNCERTAINTY_PRECISION), formatValue(upperDeviation[5], UNCERTAINTY_PRECISION));
    }

    public void printAllDeviations() {
        System.out.printf("-%s\t-%s\t-%s\t-%s\t-%s\t-%s\n+%s\t+%s\t+%s\t+%s\t+%s\t+%s\n",
                formatValue(Math.abs(lowerDeviation[0]), UNCERTAINTY_PRECISION), formatValue(Math.abs(lowerDeviation[1]), UNCERTAINTY_PRECISION),
                formatValue(Math.abs(lowerDeviation[2]), UNCERTAINTY_PRECISION), formatValue(Math.abs(lowerDeviation[3]), UNCERTAINTY_PRECISION),
                formatValue(Math.abs(lowerDeviation[4]), UNCERTAINTY_PRECISION), formatValue(Math.abs(lowerDeviation[5]), UNCERTAINTY_PRECISION),
                formatValue(upperDeviation[0], UNCERTAINTY_PRECISION), formatValue(upperDeviation[1], UNCERTAINTY_PRECISION),
                formatValue(upperDeviation[2], UNCERTAINTY_PRECISION), formatValue(upperDeviation[3], UNCERTAINTY_PRECISION),
                formatValue(upperDeviation[4], UNCERTAINTY_PRECISION), formatValue(upperDeviation[5], UNCERTAINTY_PRECISION));
    }

    /** Set result type if current is NONE */
    public void setResultType(ResultType newType) {
        this.resultType = (this.resultType == ResultType.NONE) ? newType : this.resultType;
    }

    public ResultType getResultType() {
        return this.resultType;
    }

    /** Representative form of values */
    private String formatValue(Double value, int precision) {
        if (value.isNaN() || value == Double.MAX_VALUE || value == -Double.MAX_VALUE) {
            return "N/A";
        } else {
            return String.format("%." + precision + "f", value);
        }
    }

    /** Set new rounding to be printed for uncertainty*/
    public void changeUncertaintyPrecision(int i) {
        this.UNCERTAINTY_PRECISION = i;
    }
}
