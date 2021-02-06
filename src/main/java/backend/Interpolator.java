
package backend;

import java.util.Arrays;

/**
 * Class for linear interpolation
 */
public abstract class Interpolator {
    
    /**
     * Processes four-angled figure into line of evolutionary status
     * naming follows steps in "Parametrization of single and binary stars"
     * Adds x1_, x2_, y1_, y2_ attributes to stats
     * @param stats Computation stats of current input
     * @return successfully fitted
     * Coordinates (index):
     * ****11***[x1_, y1_]***12**
     * ***********[x, y]**********
     * ****21***[x2_, y2_]***22**
     */
    public static boolean determineEvolutionaryStatus(ComputationStats stats) {
        double alpha = stats.getStar22().getTemperature() - stats.getStar21().getTemperature(); //x22-x21
        double beta = stats.getStar12().getLuminosity() - stats.getStar11().getLuminosity(); //y12-y11
        double gamma = stats.getStar12().getTemperature() - stats.getStar11().getTemperature(); //x12-x11
        double delta = stats.getStar22().getLuminosity() - stats.getStar21().getLuminosity(); //y22-y21
        double epsilon = stats.getStar22().getTemperature() * stats.getStar21().getLuminosity()
                - stats.getStar21().getTemperature() * stats.getStar22().getLuminosity(); //x22*y21-x21*y22
        double phi = stats.getStar11().getTemperature() * stats.getStar22().getTemperature()
                - stats.getStar12().getTemperature() * stats.getStar21().getTemperature(); //x11*x22-x12*x21
        double psi = stats.getStar22().getTemperature() * stats.getStar11().getLuminosity()
                - stats.getStar21().getTemperature() * stats.getStar12().getLuminosity(); //x22*y11-x21*y12

        double A = alpha * beta - gamma * delta;
        double B = alpha * (psi - (alpha - gamma) * stats.getY() - (beta - delta) * stats.getX()) - gamma * epsilon
                - delta * phi;
        double C = alpha * (phi * stats.getY() + (epsilon - psi) * stats.getX()) - epsilon * phi;

        double[] roots = quadraticEquation(A, B, C);

        Double valid_root = verifyRoots(stats, roots);
        if (valid_root == null) { return false; }
        stats.setX2_(valid_root);
        stats.setY1_(stats.getStar12().getLuminosity() + ((stats.getStar12().getLuminosity()
                - stats.getStar11().getLuminosity()) / (stats.getStar22().getTemperature()
                - stats.getStar21().getTemperature())) * (stats.getX2_() - stats.getStar22().getTemperature()));
        stats.setX1_(stats.getStar11().getTemperature() + ((stats.getStar12().getTemperature()
                - stats.getStar11().getTemperature()) / (stats.getStar22().getTemperature()
                - stats.getStar21().getTemperature())) * (stats.getX2_() - stats.getStar21().getTemperature()));
        stats.setY2_(stats.getStar22().getLuminosity() + ((stats.getStar22().getLuminosity()
                - stats.getStar21().getLuminosity()) / (stats.getStar22().getTemperature()
                - stats.getStar21().getTemperature())) * (stats.getX2_() - stats.getStar22().getTemperature()));
        return true;
    }
    
    /**
     * Solves quadratic equation
     * @return Array of root(s) of quadratic equation
     */
    public static double[] quadraticEquation(double a, double b, double c) {
        if (Math.abs(a) < 0.0000000001) {
            return new double[]{-c/b, -c/b};
        }
        double determinant = b * b - 4 * a * c;
        double square = Math.sqrt(determinant);
        double root1 = (-b + square) / (2 * a);
        double root2 = (-b - square) / (2 * a);
        return new double[]{root1, root2};
    }

    /**
     * Returns root within the lower neighbours or corrects precision errors smaller than 0.0001
     * @param stats Computation stats
     * @param roots Two roots from quadratic equation
     * @return root to be used or null
     */
    public static Double verifyRoots(ComputationStats stats, double[] roots) {
        double[] neighbours = {stats.getStar21().getTemperature(), stats.getStar22().getTemperature()};
        Arrays.sort(roots);
        Arrays.sort(neighbours);

        if (neighbours[0] - roots[0] < 0.0001 && neighbours[0] - roots[0] > 0) { return neighbours[0]; } //correct precisions
        if (neighbours[0] - roots[1] < 0.0001 && neighbours[0] - roots[1] > 0) { return neighbours[0]; } //could also happen
        if (roots[1] - neighbours[1] < 0.0001 && roots[1] - neighbours[1] > 0) { return neighbours[1]; }
        if (roots[0] - neighbours[1] < 0.0001 && roots[0] - neighbours[1] > 0) { return neighbours[1]; }
        if (roots[0] <= neighbours[1] && roots[0] >= neighbours[0]) { return roots[0]; } //first root sufficient
        if (roots[1] <= neighbours[1] && roots[1] >= neighbours[0]) { return roots[1]; } //second root sufficient
        return null;
    }
    
    /**
     * Repeatedly fits attributes into equations and estimates characteristics
     * for given [x, y] coordinates, following "Parametrization of single and
     * binary stars" (11, 13, 14). Adds results to stats.
     * @param stats Computation stats representing current computation for [x,y] input
     */
    public static void interpolateAllCharacteristics(ComputationStats stats) {
        Double[] att11 = stats.getStar11().getAllAttributes();
        Double[] att12 = stats.getStar12().getAllAttributes();
        Double[] att21 = stats.getStar21().getAllAttributes();
        Double[] att22 = stats.getStar22().getAllAttributes();
        Double[] finalEstimation = new Double[att11.length];
        Double[] result1Estimation = new Double[att11.length];
        Double[] result2Estimation = new Double[att11.length];
        for (int index = 0; index < stats.getStar11().getAllAttributes().length; index++) {
            double numerator = stats.getX2_() - att21[0]; //(x2_ - x21)
            double denominator = att22[0] - att21[0]; //(x22 - x21)
            double result1_ = att11[index] + ((att12[index] - att11[index]) / denominator) * numerator; //(13)
            result1Estimation[index] = result1_;
            double result2_ = att21[index] + ((att22[index] - att21[index]) / denominator) * numerator; //(14)
            result2Estimation[index] = result2_;
            double result = result1_ + ((result2_ - result1_) /
                    (stats.getY2_() - stats.getY1_())) * (stats.getY() - stats.getY1_()); //(11) [X -> Y]
            finalEstimation[index] = result;
        }

        finalEstimation[0] = stats.getX(); //to correspond with given input
        finalEstimation[1] = stats.getY();

        stats.setResult1_(new Star(result1Estimation));
        stats.setResult2_(new Star(result2Estimation));
        stats.setResult(new Star(finalEstimation));
    }

    /** y = y0 + ((x - x0) * (y1 - y0)) / (x1 - x0) */
    public static double interpolate(double x, double x0, double x1, double y0, double y1) {
        return y0 + ((x - x0) * (y1 - y0)) / (x1 - x0);
    }

    /**
     * Perform single linear interpolation on two stars and x, y coordinates
     * @return result star
     */
    public static Star interpolateStars(Star star1, Star star2, double x, double y) {
        Double[] att0 = star1.getAllAttributes();
        Double[] att1 = star2.getAllAttributes();
        Double[] finalEstimation = new Double[att1.length];
        for (int index = 2; index < star1.getAllAttributes().length; index++) {
            finalEstimation[index] = att0[index] + ((x - star1.getTemperature()) * (att1[index] - att0[index]))
                    / (star2.getTemperature() - star1.getTemperature());
        }

        finalEstimation[0] = x; //to correspond with given input
        finalEstimation[1] = y;
        return new Star(finalEstimation);
    }

}
