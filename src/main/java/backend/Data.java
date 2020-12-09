
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
     * @param stats Computation stats including necessary [x,y] input coordinates
     * @return true if 4 neighbours found, false otherwise
     */
    public boolean findNearestStars(ComputationStats stats) {
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
                    if ((first.getTemperature() <= stats.getX() && second.getTemperature() > stats.getX()) ||
                            (first.getTemperature() > stats.getX() && second.getTemperature() <= stats.getX())) {
                        if (intersection(first, second, stats.getX(), stats.getY())[1] <= stats.getY()) {
                            if (lowerRight == null || intersection(lowerLeft, lowerRight, stats.getX(), stats.getY())[1] <
                                    intersection(first, second, stats.getX(), stats.getY())[1]) {
                                lowerLeft = first;
                                lowerRight = second;
                            }
                        } else if (upperRight == null || intersection(upperLeft, upperRight, stats.getX(), stats.getY())[1] >
                                intersection(first, second, stats.getX(), stats.getY())[1]) {
                                upperLeft = first;
                                upperRight = second;
                        }
                        break;
                    }
                    index++;
                }
            }
        }

        stats.setStar11(upperLeft);
        stats.setStar12(upperRight);
        stats.setStar21(lowerLeft);
        stats.setStar22(lowerRight);
        return (upperLeft != null && upperRight != null && lowerLeft != null && lowerRight != null);
    }
    
    /**
     * Estimates characteristics for given input [x,y]
     * @param x X coordinate of user input
     * @param y Y coordinate of user input
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     * @return Estimated characteristics as a Star object (can contain null attributes)
     */
    public Star estimate(double x, double y, double temp_unc, double lum_unc) {
        ComputationStats stats = new ComputationStats(x, y);
        if (!findNearestStars(stats)) {
            Star result = new Star(x, y, null, null, null, null);
            result.setInputUncertainties(temp_unc, lum_unc);
            return result;
        }

        Interpolator.determineEvolutionaryStatus(stats);
        Interpolator.interpolateAllCharacteristics(stats);
        stats.getResult().setInputUncertainties(temp_unc, lum_unc);
        return stats.getResult();
    }

    /**
     * @return the groupedData
     */
    public static Map<Double, ArrayList<Star>> getGroupedData() {
        return groupedData;
    }    
}
