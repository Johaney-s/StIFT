
package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Data represented by a Map of stars (values) grouped by initial mass (key)
 */
public class Data {
    private static Map<Double, ArrayList<Star>> groupedData;
    private ArrayList<Star> currentGroup;
    
    public Data() {
       groupedData = new HashMap<>();
       currentGroup = new ArrayList<>();
    }
   
    /**
     * Add star to relevant group
     * @param star New star
     */
    public void addStar(Star star){        
        if (currentGroup.isEmpty() || 
                Math.abs(currentGroup.get(currentGroup.size() - 1).getMass() - star.getMass()) < 0.05) {
            currentGroup.add(star);
        } else {
            if (currentGroup.size() > 1) { addCurrentGroupToGroupedData(); }
            currentGroup = new ArrayList<>();
        }
    }

    public void addCurrentGroupToGroupedData() {
        groupedData.put(currentGroup.get(0).getMass(), currentGroup);
    }
    
    /**
     * Finds coordinates of intersection on a line connecting two stars
     * @param first First star on the line
     * @param second Second star on the line
     * @param x Given point, x coordinate
     * @param y Given point, y coordinate
     * @return coordinates [x,y] of intersection
     */
    public double[] intersection(Star first, Star second, double x, double y) {
        double ratio = (x - first.getTemperature()) / (second.getTemperature() - first.getTemperature());
        double intersectionY = (second.getLuminosity() - first.getLuminosity()) * ratio + first.getLuminosity();
        return new double[]{x, intersectionY};
    }
    
    /**
     * Finds four-angled figure nearest to given coordinates
     * @param x X coordinate of a point
     * @param y Y coordinate of a point
     * @return Array of four stars, upper neighbours first, lower neighbours second,
     *         null if no such exists
     */
    public Star[] findNearestStars(double x, double y) {
        Star upperLeft = null;
        Star upperRight = null;
        Star lowerRight = null;
        Star lowerLeft = null;
        for (ArrayList<Star> list : getGroupedData().values()) {
            if (list.size() > 1) {
                int index = 0;
                while (index + 1 < list.size()) {
                    Star first = list.get(index);
                    Star second = list.get(index + 1);
                    if ((first.getTemperature() <= x && second.getTemperature() > x) || (first.getTemperature() > x && second.getTemperature() <= x)) {
                        if (intersection(first, second, x, y)[1] <= y) {
                            if (lowerRight == null || intersection(lowerLeft, lowerRight, x, y)[1] < intersection(first, second, x, y)[1]) {
                                lowerLeft = first;
                                lowerRight = second;
                            }
                        } else if (upperRight == null || intersection(upperLeft, upperRight, x, y)[1] > intersection(first, second, x, y)[1]) {
                                upperLeft = first;
                                upperRight = second;
                        }
                        break;
                    }
                    index++;
                }
            }
        }
        return new Star[]{upperLeft, upperRight, lowerRight, lowerLeft};
    }
    
    /**
     * Estimates characteristics for given input [x,y]
     * @param x X coordinate of user input
     * @param y Y coordinate of user input
     * @return Estimated characteristics as a Star object (can contain null attributes)
     */
    public Star estimate(double x, double y) {
        Star[] neighbours = findNearestStars(x, y);
        for (Star s : neighbours) {
            if (s == null) {
                return new Star(x, y, null, null, null, null);
            }
        }
        double[] line = Interpolator.determineEvolutionaryStatus(neighbours[0], neighbours[1], neighbours[2], neighbours[3], x, y);
        Star result = Interpolator.interpolateAllCharacteristics(neighbours[0], neighbours[1], neighbours[2], neighbours[3], line, x, y);
        return result;
    }

    /**
     * @return the groupedData
     */
    public static Map<Double, ArrayList<Star>> getGroupedData() {
        return groupedData;
    }    
}
