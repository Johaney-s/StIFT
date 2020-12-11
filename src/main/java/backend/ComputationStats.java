package backend;

public class ComputationStats {
    private double x2_;
    private double y1_;
    private double x1_;
    private double y2_;
    private final double x;
    private final double y;
    private Star star11;
    private Star star12;
    private Star star21;
    private Star star22;
    private Star result1_;
    private Star result2_;
    private Star result;
    private Star[] sigma_region = null; //estimations for stars in sigma region of input
    private double[] deviations = null;

    public ComputationStats(double x, double y) {
        this.x = x;
        this.y = y;
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

    public double getX2_() {
        return x2_;
    }

    public double getY1_() {
        return y1_;
    }

    public double getX1_() {
        return x1_;
    }

    public double getY2_() {
        return y2_;
    }

    public Star getResult() {
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

    public void setResult(Star result) {
        this.result = result;
    }

    public Star[] getSigmaRegion() {
        return sigma_region;
    }

    public void setSigmaRegion(Star[] uncertainty_estimations) {
        this.sigma_region = uncertainty_estimations;
    }

    public double[] getDeviations() {
        return deviations;
    }

    public void setDeviations(double[] deviations) {
        this.deviations = deviations;
    }
}
