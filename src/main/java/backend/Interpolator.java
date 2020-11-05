
package backend;

/**
 * Class for linear interpolation
 */
public class Interpolator {
    
    /**
     * Processes four-angled figure into line of evolutionary status
     * naming follows steps in "Parametrization of single and binary stars"
     * @param star11 Upper left in figure
     * @param star12 Upper right in figure
     * @param star22 Lower right in figure
     * @param star21 Lower left in figure
     * @param x X coordinate of a point
     * @param y Y coordinate of a point
     * Coordinates (index):
     * ****11***[x1_, y1_]***12**
     * ***********[x, y]**********
     * ****21***[x2_, y2_]***22**
     * @return (x1_, y1_, x2_, y2_) = line of evolutionary status
     */
    public static double[] determineEvolutionaryStatus(Star star11, Star star12, Star star22, Star star21, double x, double y) {
        double alpha = star22.getTemperature() - star21.getTemperature(); //x22-x21
        double beta = star12.getLuminosity() - star11.getLuminosity(); //y12-y11
        double gamma = star12.getTemperature() - star11.getTemperature(); //x12-x11
        double delta = star22.getLuminosity() - star21.getLuminosity(); //y22-y21
        double epsilon = star22.getTemperature() * star21.getLuminosity() - star21.getTemperature() * star22.getLuminosity(); //x22*y21-x21*y22
        double phi = star11.getTemperature() * star22.getTemperature() - star12.getTemperature() * star21.getTemperature(); //x11*x22-x12*x21
        double psi = star22.getTemperature() * star11.getLuminosity() - star21.getTemperature() * star12.getLuminosity(); //x22*y11-x21*y12
        double A = alpha * beta - gamma * delta;
        double B = alpha * (psi - (alpha - gamma) * y - (beta - delta) * x) - gamma * epsilon - delta * phi;
        double C = alpha * (phi * y + (epsilon - psi) * x) - epsilon * phi;
        
        double x2_ = quadraticEquation(A, B, C)[0]; //but which one?
        double y1_ = star12.getLuminosity() + ((star12.getLuminosity() - star11.getLuminosity()) / (star22.getTemperature() - star21.getTemperature())) * (x2_ - star22.getTemperature());
        double x1_ = star11.getTemperature() + ((star12.getTemperature() - star11.getTemperature()) / (star22.getTemperature() - star21.getTemperature())) * (x2_ - star21.getTemperature());
        double y2_ = star22.getLuminosity() + ((star22.getLuminosity() - star21.getLuminosity()) / (star22.getTemperature() - star21.getTemperature())) * (x2_ - star22.getTemperature());
        return new double[]{x1_, y1_, x2_, y2_};
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
     * Repeatedly fits atributes into equations and estimates characteristics
     * for given [x, y] coordinates, following "Parametrization of single and
     * binary stars" (11, 13, 14)
     * @param star11 Upper star 1 in figure
     * @param star12 Upper star 2 in figure
     * @param star22 Lower star 1 in figure
     * @param star21 Lower star 2 in figure
     * @param evolutionaryStatus {x1_, y1_, x2_, y2_}
     * @param x X coordinate of selected point
     * @param y Y coordinate of selected point
     * @return Returns all characteristics as defined in Star class
     * in respective order estimated for given [x,y] coordinate
     */
    public static Star interpolateAllCharacteristics(Star star11, Star star12, Star star22, Star star21, double[] evolutionaryStatus, double x, double y) {
        double[] att11 = star11.getAllAttributes();
        double[] att12 = star12.getAllAttributes();
        double[] att21 = star21.getAllAttributes();
        double[] att22 = star22.getAllAttributes();
        Double[] finalEstimation = new Double[att11.length];
        finalEstimation[0] = x;
        finalEstimation[1] = y;
        for (int index = 2; index < star11.getAllAttributes().length; index++) {
            Double numerator = evolutionaryStatus[2] - att21[0]; //(x2_ - x21)
            Double denominator = att22[0] - att21[0]; //(x22 - x21)            
            Double result1_ = att11[index] + ((att12[index] - att11[index]) / denominator) * numerator; //(13)
            Double result2_ = att21[index] + ((att22[index] - att21[index]) / denominator) * numerator; //(14)
            Double result = result1_ + ((result2_ - result1_) / (evolutionaryStatus[3] - evolutionaryStatus[1])) * (y - evolutionaryStatus[1]);
            finalEstimation[index] = result;
        }
        
        return new Star(finalEstimation);
    }
}
