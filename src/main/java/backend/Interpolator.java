
package backend;

import java.util.Arrays;

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
                - (stats.getBeta() - stats.getDelta()) * stats.getX()) - stats.getGamma() * stats.getEpsilon() - stats.getDelta()
                * stats.getPhi());
        stats.setC(stats.getAlpha() * (stats.getPhi() * stats.getY() + (stats.getEpsilon() - stats.getPsi())
                * stats.getX()) - stats.getEpsilon() * stats.getPhi());

        double[] roots = quadraticEquation(stats.getA(), stats.getB(), stats.getC());

        stats.setX2_(verifyRoots(stats, roots));
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

        double root1 = (-b + square) / (2 * a);
        double root2 = (-b - square) / (2 * a);
        return new double[]{root1, root2};
    }

    /**
     * Returns root within the interval
     * @param stats Computation stats
     * @param roots Two roots from quadratic equation
     * @return root to be used
     */
    public static double verifyRoots(ComputationStats stats, double[] roots) {
        double[] neighbours = {stats.getStar11().getTemperature(), stats.getStar12().getTemperature(),
            stats.getStar21().getTemperature(), stats.getStar22().getTemperature()};

        Arrays.sort(neighbours);
        return (roots[0] <= neighbours[3] && roots[0] >= neighbours[0]) ? roots[0] : roots[1];
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

    public static void determineUncertainties(ComputationStats stats) {
        double LOG_E = 0.4342944819;
        double pt1 = (stats.getAlpha() * (stats.getBeta() - stats.getDelta())) / (2 * stats.getA());
        double der_denominator = Math.sqrt(stats.getB() * stats.getB() - 4 * stats.getA() * stats.getC());
        double pt2 = (stats.getB() + 2 * stats.getA() * ((stats.getEpsilon() - stats.getPsi())
                / (stats.getBeta() - stats.getDelta()))) / der_denominator;
        double dx2_Idxminus = pt1 * (1 - pt2); // (21)
        double dx2_Idxplus = pt1 * (1 + pt2);

        double pt3 = (stats.getAlpha() * (stats.getAlpha() - stats.getGamma())) / (2 * stats.getA());
        double pt4 = (stats.getB() + 2 * stats.getA() * (stats.getPhi() / (stats.getAlpha() - stats.getGamma())))
                / der_denominator;
        double dx2_Idyminus = pt3 * (1 - pt4); // (22)
        double dx2_Idyplus = pt3 * (1 + pt4);

        double gamal_1 = (stats.getGamma() / stats.getAlpha()) - 1;
        double phial = stats.getPhi() / stats.getAlpha();
        double repetative = stats.getX2_() * gamal_1 + phial;
        double fml3 = stats.getX() * gamal_1 + phial;

        /*System.out.println("(Gamma/alpha - 1) = " + gamal_1 + "\nPhi/alpha = " + phial + "\nX2 * (gamma/alpha - 1) + phi/alpha ="
                + repetative + "\nX * (gamma/alpha - 1) + phi/alpha = " + fml3 + "\n");*/
        for (int index = 0; index < stats.getResult().getAllAttributes().length; index++) {
            double D = (stats.getStar22().getAllAttributes()[index] - stats.getStar21().getAllAttributes()[index]
                    - stats.getStar12().getAllAttributes()[index] + stats.getStar11().getAllAttributes()[index])
                    * (stats.getX2_() - stats.getX());
            double fml1 = ((stats.getStar22().getAllAttributes()[index] - stats.getStar21().getAllAttributes()[index])
                    * repetative + D) / (stats.getAlpha() * repetative);
            double fml2 = (stats.getResult2_().getAllAttributes()[index] - stats.getResult1_().getAllAttributes()[index])
                    /(repetative * repetative);
            double derx1 = fml1 * dx2_Idxminus + fml2 * (fml3 * dx2_Idxminus - repetative);
            double derx2 = fml1 * dx2_Idxminus + fml2 * (fml3 * dx2_Idxplus - repetative);
            double derx3 = fml1 * dx2_Idxplus  + fml2 * (fml3 * dx2_Idxplus - repetative);
            double derx4 = fml1 * dx2_Idxplus  + fml2 * (fml3 * dx2_Idxminus - repetative);
            //System.out.printf("Attribute %d : Dx: %f\t%f\t%f\t%f\t", index, derx1, derx2, derx3, derx4);

            double dery1 = fml1 * dx2_Idyminus + fml2 * fml3 * dx2_Idyminus;
            double dery2 = fml1 * dx2_Idyminus+ fml2 * fml3 * dx2_Idyplus;
            double dery3 = fml1 * dx2_Idyplus + fml2 * fml3 * dx2_Idyplus;
            double dery4 = fml1 * dx2_Idyplus + fml2 * fml3 * dx2_Idyminus;
            //System.out.printf("Dy: %f\t%f\t%f\t%f\n", dery1, dery2, dery3, dery4);

            double est1 = (stats.getResult().getAllAttributes()[index] / LOG_E) * derx1;
            double est2 = (stats.getResult().getAllAttributes()[index] / LOG_E) * derx2;
            double est3 = (stats.getResult().getAllAttributes()[index] / LOG_E) * derx3;
            double est4 = (stats.getResult().getAllAttributes()[index] / LOG_E) * derx4;
            //System.out.printf("dm/dx: %f\n", est3);
        }
    }

}
