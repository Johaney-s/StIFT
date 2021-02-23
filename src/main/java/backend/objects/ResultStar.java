package backend.objects;

import backend.ResultType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Enhanced representation of a star
 * Used for stars in results
 */
public class ResultStar extends Star {
    private final Double[] lowerDeviation = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private final Double[] upperDeviation = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private final String ROUNDING_FORMAT = "%.4f %s %s";
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

    //Returns text representation for tableView -- DO NOT DELETE -- valueFactory is using this -- DO NOT DELETE ----!!!
    public TextFlow getTemColumnText() {
        return (temperature == null || temperature.isNaN()) ? new TextFlow(new Text("-"))
                : new TextFlow(new Text(String.format("%.4f±%.4f", temperature, upperDeviation[0])));
    }

    public TextFlow getLumColumnText() {
        return (luminosity == null || luminosity.isNaN())? new TextFlow(new Text("-"))
                : new TextFlow(new Text(String.format("%.4f±%.4f", luminosity, upperDeviation[1])));
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

        /*double SMALL_ERROR = 0.00005;
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
        return new TextFlow(t1, t2);*/

        /*Text t1 = new Text(String.format("%.4f +%s %s",
                attribute,
                (upperDeviation[index] == Double.MAX_VALUE || upperDeviation[index].isNaN()) ? "N/A" : String.format("%.2f", upperDeviation[index]),
                (lowerDeviation[index] == Double.MAX_VALUE || lowerDeviation[index].isNaN()) ? "-N/A" : String.format("%.2f", lowerDeviation[index])));*/
        TextFlow container = new TextFlow();
        Text normal = new Text(String.format("%.4f", attribute));
        Text sup = new Text(String.format("%s", (upperDeviation[index] == Double.MAX_VALUE
                        || upperDeviation[index].isNaN()) ? "N/A" : String.format("+%.2f", upperDeviation[index])));
        Text sub = new Text(String.format("%s", (lowerDeviation[index] == Double.MAX_VALUE
                || lowerDeviation[index].isNaN()) ? "-N/A" : String.format("%.2f", lowerDeviation[index])));
        sup.setTranslateY(normal.getFont().getSize() * -0.3);
        sub.setTranslateY(normal.getFont().getSize() * 0.3);
        container.getChildren().addAll(normal, sup, sub);
        return new TextFlow(container);
    }

    //Returns string representation of rounded result (for export purpose)
    public String getFormattedTemperature() {
        return (String.format("%.4f %.4f", temperature, upperDeviation[0]));
    }

    public String getFormattedLuminosity() {
        return (String.format("%.4f %.4f", luminosity, upperDeviation[1]));
    }

    public String getFormattedAge() {
        return (age == null || age.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, age,
                (lowerDeviation[2].isNaN() || lowerDeviation[2] == Double.MAX_VALUE) ? "-" : String.format("%.4f",lowerDeviation[2]),
                (upperDeviation[2].isNaN() || upperDeviation[2] == Double.MAX_VALUE) ? "-" : String.format("%.4f",upperDeviation[2]));
    }

    public String getFormattedRadius() {
        return (radius == null || radius.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, radius,
                (lowerDeviation[3].isNaN() || lowerDeviation[3] == Double.MAX_VALUE) ? "-" : String.format("%.4f",lowerDeviation[3]),
                (upperDeviation[3].isNaN() || upperDeviation[3] == Double.MAX_VALUE) ? "-" : String.format("%.4f",upperDeviation[3]));
    }

    public String getFormattedMass() {
        return (mass == null || mass.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, mass,
                (lowerDeviation[4].isNaN() || lowerDeviation[4] == Double.MAX_VALUE) ? "-" : String.format("%.4f",lowerDeviation[4]),
                (upperDeviation[4].isNaN() || upperDeviation[4] == Double.MAX_VALUE) ? "-" : String.format("%.4f",upperDeviation[4]));
    }

    public String getFormattedPhase() {
        return (phase == null || phase.isNaN()) ? "- - -" : String.format(ROUNDING_FORMAT, phase,
                (lowerDeviation[5].isNaN() || lowerDeviation[5] == Double.MAX_VALUE) ? "-" : String.format("%.4f",lowerDeviation[5]),
                (upperDeviation[5].isNaN() || upperDeviation[5] == Double.MAX_VALUE) ? "-" : String.format("%.4f",upperDeviation[5]));
    }

    public void printAllDeviations() {
        System.out.printf("%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n+%.4f\t+%.4f\t+%.4f\t+%.4f\t+%.4f\t+%.4f\n",
                lowerDeviation[0], lowerDeviation[1], lowerDeviation[2],
                lowerDeviation[3], lowerDeviation[4], lowerDeviation[5],
                upperDeviation[0], upperDeviation[1], upperDeviation[2],
                upperDeviation[3], upperDeviation[4], upperDeviation[5]);
    }

    /** Set result type if current is NONE */
    public void setResultType(ResultType newType) {
        this.resultType = (this.resultType == ResultType.NONE) ? newType : this.resultType;
    }

    public ResultType getResultType() {
        return this.resultType;
    }
}
