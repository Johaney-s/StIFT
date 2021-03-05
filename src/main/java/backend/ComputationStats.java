package backend;

import backend.objects.ResultStar;
import backend.objects.Star;

import java.util.HashSet;

public class ComputationStats {
    private Double x2_;
    private Double y1_;
    private Double x1_;
    private Double y2_;
    private final double x;
    private final double y;
    private final double x_unc;
    private final double y_unc;
    private Star star11;
    private Star star12;
    private Star star21;
    private Star star22;
    private Star result1_;
    private Star result2_;
    private ResultStar result;
    private ResultType resultType = ResultType.NONE;
    private HashSet<Short> ignoredPhases;

    public ComputationStats(double x, double y, double x_unc, double y_unc) {
        this.x = x;
        this.y = y;
        this.x_unc = x_unc;
        this.y_unc = y_unc;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setStar11(Star star11) {
        this.star11 = star11;
    }

    public void setStar12(Star star12) {
        this.star12 = star12;
    }

    public void setStar21(Star star21) {
        this.star21 = star21;
    }

    public void setStar22(Star star22) {
        this.star22 = star22;
    }

    public Star getStar11() {
        return star11;
    }

    public Star getStar12() {
        return star12;
    }

    public Star getStar21() {
        return star21;
    }

    public Star getStar22() {
        return star22;
    }

    public void setX2_(double x2_) {
        this.x2_ = x2_;
    }

    public void setY1_(double y1_) {
        this.y1_ = y1_;
    }

    public void setX1_(double x1_) {
        this.x1_ = x1_;
    }

    public void setY2_(double y2_) {
        this.y2_ = y2_;
    }

    public Double getX2_() {
        return x2_;
    }

    public Double getY1_() {
        return y1_;
    }

    public Double getX1_() {
        return x1_;
    }

    public Double getY2_() {
        return y2_;
    }

    public ResultStar getResult() {
        return result;
    }

    public Star getResult1_() {
        return result1_;
    }

    public Star getResult2_() {
        return result2_;
    }

    public void setResult1_(Star result1_) {
        this.result1_ = result1_;
    }

    public void setResult2_(Star result2_) {
        this.result2_ = result2_;
    }

    public void setResult(ResultStar result) {
        this.result = result;
    }

    public Star[] getNeighbours() {
        return new Star[]{star11, star12, star21, star22};
    }

    public double getY_unc() {
        return y_unc;
    }

    public double getX_unc() {
        return x_unc;
    }

    public void setEvolutionaryLine(Star star1, Star star2) {
        x1_ = star1.getTemperature();
        y1_ = star1.getLuminosity();
        x2_ = star2.getTemperature();
        y2_ = star2.getLuminosity();
        result1_ = star1;
        result2_ = star2;
    }

    /** Changes result type if current is NONE */
    public void changeResultType(ResultType newType) {
        this.resultType = (this.resultType == ResultType.NONE) ? newType : this.resultType;
    }

    public ResultType getResultType() {
        return this.resultType;
    }

    public void setIgnoredPhases(HashSet<Short> ignoredPhases) {
        this.ignoredPhases = ignoredPhases;
    }

    public HashSet<Short> getIgnoredPhases() {
        return ignoredPhases;
    }
}
