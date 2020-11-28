
package backend;

/**
 * Class for linear interpolation
 */
public class Interpolator {
    
    /**
     * Processes four-angled figure into line of evolutionary status
     * naming follows steps in "Parametrization of single and binary stars"
     * Adds x1_, x2_, y1_, y2_ attributes to stats
     * @param stats Computation stats of current input
     * Coordinates (index):
     * ****11***[x1_, y1_]***12**
     * ***********[x, y]**********
     * ****21***[x2_, y2_]***22**
     */
    public static void determineEvolutionaryStatus(ComputationStats stats) {
        stats.setAlpha(stats.getStar22().getTemperature() - stats.getStar21().getTemperature()); //x22-x21
        stats.setBeta(stats.getStar12().getLuminosity() - stats.getStar11().getLuminosity()); //y12-y11
        stats.setGamma(stats.getStar12().getTemperature() - stats.getStar11().getTemperature()); //x12-x11
        stats.setDelta(stats.getStar22().getLuminosity() - stats.getStar21().getLuminosity()); //y22-y21
        stats.setEpsilon(stats.getStar22().getTemperature() * stats.getStar21().getLuminosity()
                - stats.getStar21().getTemperature() * stats.getStar22().getLuminosity()); //x22*y21-x21*y22
        stats.setPhi(stats.getStar11().getTemperature() * stats.getStar22().getTemperature()
                - stats.getStar12().getTemperature() * stats.getStar21().getTemperature()); //x11*x22-x12*x21
        stats.setPsi(stats.getStar22().getTemperature() * stats.getStar11().getLuminosity()
                - stats.getStar21().getTemperature() * stats.getStar12().getLuminosity()); //x22*y11-x21*y12

        stats.setA(stats.getAlpha() * stats.getBeta() - stats.getGamma() * stats.getDelta());
        stats.setB(stats.getAlpha() * (stats.getPsi() - (stats.getAlpha() - stats.getGamma()) * stats.getY()
                - (stats.getBeta() - stats.getDelta()) * stats.getX()) - stats.getGamma()
                * stats.getEpsilon() - stats.getDelta() * stats.getPhi());
        stats.setC(stats.getAlpha() * (stats.getPhi() * stats.getY() + (stats.getEpsilon()
                - stats.getPsi()) * stats.getX()) - stats.getEpsilon() * stats.getPhi());
        
        stats.setX2_(quadraticEquation(stats.getA(), stats.getB(), stats.getC())[0]);
        stats.setY1_(stats.getStar12().getLuminosity() + ((stats.getStar12().getLuminosity()
                - stats.getStar11().getLuminosity()) / (stats.getStar22().getTemperature()
                - stats.getStar21().getTemperature())) * (stats.getX2_() - stats.getStar22().getTemperature()));
        stats.setX1_(stats.getStar11().getTemperature() + ((stats.getStar12().getTemperature()
                - stats.getStar11().getTemperature()) / (stats.getStar22().getTemperature()
                - stats.getStar21().getTemperature())) * (stats.getX2_() - stats.getStar21().getTemperature()));
        stats.setY2_(stats.getStar22().getLuminosity() + ((stats.getStar22().getLuminosity()
                - stats.getStar21().getLuminosity()) / (stats.getStar22().getTemperature()
                - stats.getStar21().getTemperature())) * (stats.getX2_() - stats.getStar22().getTemperature()));
    }
    
    /**
     * Solves quadratic equation
     * Ax^2 + Bx + C = 0
     * @param a
     * @param b
     * @param c
     * @return Array of root(s) of quadratic equation
     */
    public static double[] quadraticEquation(double a, double b, double c) {
        double determinant = b * b - 4 * a * c;
        double square = Math.sqrt(determinant);
        
        if (determinant > 0) {
            double root1 = (-b + square) / (2 * a);
            double root2 = (-b - square) / (2 * a);
            return new double[]{root1, root2};
        } else {
            double root = (-b + square) / (2 * a);
            return new double[]{root};
        }
    }
    
    /**
     * Repeatedly fits attributes into equations and estimates characteristics
     * for given [x, y] coordinates, following "Parametrization of single and
     * binary stars" (11, 13, 14). Adds results to stats.
     * @param stats Computation stats representing current computation for [x,y] input
     */
    public static void interpolateAllCharacteristics(ComputationStats stats) {
        double[] att11 = stats.getStar11().getAllAttributes();
        double[] att12 = stats.getStar12().getAllAttributes();
        double[] att21 = stats.getStar21().getAllAttributes();
        double[] att22 = stats.getStar22().getAllAttributes();
        Double[] finalEstimation = new Double[att11.length];
        Double[] result1Estimation = new Double[att11.length];
        Double[] result2Estimation = new Double[att11.length];
        for (int index = 0; index < stats.getStar11().getAllAttributes().length; index++) {
            Double numerator = stats.getX2_() - att21[0]; //(x2_ - x21)
            Double denominator = att22[0] - att21[0]; //(x22 - x21)            
            Double result1_ = att11[index] + ((att12[index] - att11[index]) / denominator) * numerator; //(13)
            result1Estimation[index] = result1_;
            Double result2_ = att21[index] + ((att22[index] - att21[index]) / denominator) * numerator; //(14)
            result2Estimation[index] = result2_;
            Double result = result1_ + ((result2_ - result1_) /
                    (stats.getY2_() - stats.getY1_())) * (stats.getY() - stats.getY1_()); //(11) [X -> Y]
            finalEstimation[index] = result;
        }

        finalEstimation[0] = stats.getX(); //to correspond with given input
        finalEstimation[1] = stats.getY();

        stats.setResult1_(new Star(result1Estimation));
        stats.setResult2_(new Star(result2Estimation));
        stats.setResult(new Star(finalEstimation));
    }
}
