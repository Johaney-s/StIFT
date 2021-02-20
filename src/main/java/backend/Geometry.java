package backend;

import backend.objects.Star;

/** Helper class for intersections and similar geometrical computations */
public abstract class Geometry {

    /**
     * Finds coordinates of intersection on a line connecting two stars
     * @param first First star on the line
     * @param second Second star on the line
     * @param x Given point, x coordinate
     * @param y Given point, y coordinate
     * @return coordinates [x,y] of intersection on y and x axis
     */
    public static double[] intersection(Star first, Star second, double x, double y) {
        double x_ratio = (x - first.getTemperature()) / (second.getTemperature() - first.getTemperature());
        double y_intersection = (second.getLuminosity() - first.getLuminosity()) * x_ratio + first.getLuminosity();
        double y_ratio = (y - first.getLuminosity()) / (second.getLuminosity() - first.getLuminosity());
        double x_intersection = (second.getTemperature() - first.getTemperature()) * y_ratio + first.getTemperature();
        return new double[]{x_intersection, y_intersection};
    }

    /** Returns coordinates of intersection of lines A-B and C-D */
    public static double[] lineIntersection(Star A, Star B, double[] C, double[] D) {
        double a1 = B.getLuminosity() - A.getLuminosity();
        double b1 = A.getTemperature() - B.getTemperature();
        double c1 = a1 * (A.getTemperature()) + b1 * (A.getLuminosity());

        double a2 = D[1] - C[1];
        double b2 = C[0] - D[0];
        double c2 = a2 * (C[0]) + b2 * (C[1]);

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
        }

        double x = (b2 * c1 - b1 * c2) / determinant;
        double y = (a1 * c2 - a2 * c1) / determinant;
        return new double[]{x, y};
    }

    /**
     * Approximately checks, if X fits in figure
     * @param UL Upper left neighbour
     * @param UR Upper right neighbour
     * @param LL Lower left neighbour
     * @param LR Lower right neighbour
     * @return true if X lies in figure and detected as fitting, false if not fitting / not detected
     */
    public static boolean fitsIn(Star UL, Star UR, Star LL, Star LR, double x, double y) {
        boolean upperFitX = (UL.getTemperature() <= x && UR.getTemperature() > x)
                || (UL.getTemperature() > x && UR.getTemperature() <= x);
        boolean lowerFitX = (LL.getTemperature() <= x && LR.getTemperature() > x)
                || (LL.getTemperature() > x && LR.getTemperature() <= x);
        boolean upperFitY = y < Math.min(UL.getLuminosity(), UR.getLuminosity());
        boolean lowerFitY = y > Math.min(UL.getLuminosity(), UR.getLuminosity());
        return upperFitX && lowerFitX && upperFitY && lowerFitY;
    }
}
