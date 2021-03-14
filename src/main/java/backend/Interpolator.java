
package backend;

import backend.objects.ResultStar;
import backend.objects.Star;

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
        stats.setX1_(stats.getX());
        stats.setX2_(stats.getX());
        stats.setY1_(stats.getStar12().getLuminosity() + ((stats.getStar12().getLuminosity()
                - stats.getStar11().getLuminosity()) / (stats.getStar12().getTemperature()
                - stats.getStar11().getTemperature())) * (stats.getX1_() - stats.getStar12().getTemperature()));
        stats.setY2_(stats.getStar22().getLuminosity() + ((stats.getStar22().getLuminosity()
                - stats.getStar21().getLuminosity()) / (stats.getStar22().getTemperature()
                - stats.getStar21().getTemperature())) * (stats.getX2_() - stats.getStar22().getTemperature()));
        return true;
    }
    
    /**
     * Repeatedly fits attributes into equations and estimates characteristics
     * for given [x, y] coordinates, following "Parametrization of single and
     * binary stars" (altered 11, altered 13, altered 14). Adds results to stats.
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
            double result1_ = att11[index] + ((att12[index] - att11[index]) / (att12[0] - att11[0])) * (stats.getX1_() - att11[0]); //(13)
            result1Estimation[index] = result1_;
            double result2_ = att21[index] + ((att22[index] - att21[index]) / (att22[0] - att21[0])) * (stats.getX2_() - att21[0]); //(14)
            result2Estimation[index] = result2_;
            double result = result1_ + ((result2_ - result1_) /
                    (stats.getY2_() - stats.getY1_())) * (stats.getY() - stats.getY1_()); //(11) [X -> Y]
            finalEstimation[index] = result;
        }

        finalEstimation[0] = stats.getX(); //to correspond with given input
        finalEstimation[1] = stats.getY();

        stats.setResult1_(new Star(result1Estimation));
        stats.setResult2_(new Star(result2Estimation));
        stats.setResult(new ResultStar(finalEstimation));
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
