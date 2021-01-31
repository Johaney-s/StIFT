
package backend;

import java.util.*;

/**
 * Data represented by a Map of stars (values) grouped by initial mass (key)
 */
public class Data {
    private final Map<Double, ArrayList<Star>> groupedData;
    private ArrayList<Star> currentGroup;
    public static double TRACKS_DELIMITER = 0.01;
    private final HashSet<Short> currentPhases;
    private final ZAMS zams = new ZAMS();
    
    public Data() {
       groupedData = new HashMap<>();
       currentGroup = new ArrayList<>();
       currentPhases =  new HashSet<>();
    }
   
    /**
     * Add star to relevant group
     * @param star New star
     */
    public void addStar(Star star){        
        if (!currentGroup.isEmpty() && Math.abs(currentGroup.get(currentGroup.size() - 1).getMass() - star.getMass())
                > TRACKS_DELIMITER) {
            addCurrentGroupToGroupedData();
            currentGroup = new ArrayList<>();
        }

        currentGroup.add(star);
        currentPhases.add(star.getPhase().shortValue());
    }

    /**
     * Close current isochrones group and add to data if larger than 1
     */
    public void addCurrentGroupToGroupedData() {
        if (currentGroup.size() > 1) {
            if (groupedData.isEmpty()) {
                zams.set_phase(currentGroup.get(0).getPhase());
            }
            if (currentGroup.get(0).getPhase() == zams.get_phase()) {
                zams.add(currentGroup.get(0));
            }
            groupedData.put(currentGroup.get(0).getMass(), currentGroup);
        }
    }
    
    /**
     * Finds coordinates of intersection on a line connecting two stars
     * @param first First star on the line
     * @param second Second star on the line
     * @param x Given point, x coordinate
     * @param y Given point, y coordinate
     * @return coordinates [x,y] of intersection on y and x axis
     */
    public double[] intersection(Star first, Star second, double x, double y) {
        double x_ratio = (x - first.getTemperature()) / (second.getTemperature() - first.getTemperature());
        double y_intersection = (second.getLuminosity() - first.getLuminosity()) * x_ratio + first.getLuminosity();
        double y_ratio = (y - first.getLuminosity()) / (second.getLuminosity() - first.getLuminosity());
        double x_intersection = (second.getTemperature() - first.getTemperature()) * y_ratio + first.getTemperature();
        return new double[]{x_intersection, y_intersection};
    }
    
    /**
     * Finds four-angled figure nearest to given coordinates, sets corresponding attributes in stats
     * @param stats Computation stats including necessary [x,y] input coordinates
     * @return true if 4 neighbours found, false otherwise (including star match)
     */
    public boolean findNearestStars(ComputationStats stats, HashSet<Short> ignoredPhases) {
        if (ignoredPhases.size() == GridFileParser.getCurrentData().getCurrentPhases().size()) { return false; }
        Star upperLeft = null;
        Star upperRight = null;
        Star lowerRight = null;
        Star lowerLeft = null;
        Star upperZAMS = null;

        for (ArrayList<Star> list : getGroupedData().values()) {
            if (!ignoredPhases.contains(list.get(0).getPhase().shortValue()) && starsMatch(stats, list.get(0))){
                return false; ///NO NEIGHBOURS returned, BUT MATCH
            }

            if (list.size() > 1) {
                for(int index = 0; index + 1 < list.size(); index++) {
                    Star first = list.get(index);
                    Star second = list.get(index + 1);
                    if (ignoredPhases.contains(first.getPhase().shortValue())) { continue; } //ignore ignored phase
                    if (starsMatch(stats, second)) { return false; }///NO NEIGHBOURS returned, BUT MATCH

                    if ((first.getTemperature() <= stats.getX() && second.getTemperature() > stats.getX()) ||
                            (first.getTemperature() > stats.getX() && second.getTemperature() <= stats.getX())) {
                       if (intersection(first, second, stats.getX(), stats.getY())[1] < stats.getY()) {
                            if (lowerRight == null || intersection(lowerLeft, lowerRight, stats.getX(), stats.getY())[1]
                                    < intersection(first, second, stats.getX(), stats.getY())[1]) {
                                lowerLeft = first;
                                lowerRight = second;
                            }
                       } else {
                            if (upperRight == null || intersection(upperLeft, upperRight, stats.getX(), stats.getY())[1]
                                    >= intersection(first, second, stats.getX(), stats.getY())[1]) {
                                upperLeft = first;
                                upperRight = second;
                                upperZAMS = list.get(0);
                            }
                       }
                    }
                }
            }
        }

        stats.setStar11(upperLeft);
        stats.setStar12(upperRight);
        stats.setStar21(lowerLeft);
        stats.setStar22(lowerRight);

        if (upperLeft != null && lowerRight == null && !ignoredPhases.contains((short)zams.get_phase())) { //give ZAMS a chance
            fillWithZAMS(stats, upperZAMS);
        }

        return (stats.getStar11() != null && stats.getStar21() != null);
    }

    /** Computes missing ZAMS, call if upper neighbours found, but lower neighbours unknown */
    private void fillWithZAMS(ComputationStats stats, Star upper_zams) {
        Star lower_zams = null;
        double TOLERATED_DISTANCE = -0.0001; //below the intersection
        for (Star zams_star : zams.getTrack()) {
            if (zams_star.getLuminosity() < stats.getY() && zams_star != stats.getStar12()
                    && zams_star.getTemperature() < stats.getX()) {
                if (lower_zams == null || lower_zams.getLuminosity() < zams_star.getLuminosity()) {
                    lower_zams = zams_star;
                }
            }
        }

        if (lower_zams != null) {
            Star upperLeft = (stats.getStar11().getTemperature() < stats.getStar12().getTemperature()) ? stats.getStar11() : stats.getStar12();
            double intersection_y = intersection(lower_zams, upper_zams, stats.getX(), stats.getY())[1];
            if (stats.getY() - intersection_y > TOLERATED_DISTANCE) {
                Star interstar = Interpolator.interpolateStars(lower_zams, upper_zams, stats.getX(), intersection_y);
                stats.setStar21(lower_zams);
                stats.setStar22(interstar);
                stats.setStar11(upperLeft);
                stats.setStar12(upper_zams);
            }
        }
    }

    /** Return true, if input point is too close to a star from grid and set stats' error and mean values */
    private boolean starsMatch(ComputationStats stats, Star star) {
        double x_error = Math.abs(star.getTemperature() - stats.getX());
        double y_error = Math.abs(star.getLuminosity() - stats.getY());
        if (x_error < 0.0001 && y_error < 0.0001) {
            Double[] attributes = star.getAllAttributes();
            double error_const = Math.sqrt(x_error * x_error + y_error * y_error);
            stats.setResult(new Star(star.getAllAttributes()));
            stats.getResult().setErrors(new
                double[]{
                    attributes[2] * error_const,
                    attributes[3] * error_const,
                    attributes[4] * error_const,
                    attributes[5] * error_const});
            return true;
    }
        return false;
    }

    /**
     * Estimates characteristics for given input [x,y]
     * @param x X coordinate of user input
     * @param y Y coordinate of user input
     * @return Stats with either result containing null values or computed stats parameters
     */
    public ComputationStats estimate_star(double x, double y, HashSet<Short> ignoredPhases) {
        ComputationStats stats = new ComputationStats(x, y);
        if (!findNearestStars(stats, ignoredPhases)) {
            if (stats.getResult() != null) {return stats;} //match was found
            stats.setResult(new Star(x, y, null, null, null, null));
            sidesMatch(stats, x, y); //give pairs a chance
            return stats;
        }

        if (sidesMatch(stats, x, y)) {
            return stats;
        }

        if (!Interpolator.determineEvolutionaryStatus(stats)) {
            stats.setResult(new Star(x, y, null, null, null, null));
            return stats;
        }
        Interpolator.interpolateAllCharacteristics(stats);
        return stats;
    }

    public ComputationStats estimate_star(double x, double y) {
        return estimate_star(x, y, new HashSet<Short>());
    }

    /**
     * Estimate characteristics and uncertainties for given input and uncertainties, save results to stats
     * Call this method directly if stats are further used
     * @param x input x coordinate
     * @param y input y coordinate
     * @param temp_unc Temperature uncertainty
     * @param lum_unc Luminosity uncertainty
     * @return stats with uncertainties_estimations filled out
     */
    public ComputationStats estimate_stats(double x, double y, double temp_unc, double lum_unc, boolean includeError,
                                           boolean includeDeviation, HashSet<Short> ignoredPhases) {
        boolean validSD = true;
        int NUMBER_OF_SIGMA_REGION_POINTS = 8; //except for no sigma region, then 0 / 8 = still 0
        ComputationStats mean_value_stats = estimate_star(x, y, ignoredPhases);
        if (!includeError) { mean_value_stats.getResult().setHideError(); }
        if (!includeDeviation) { mean_value_stats.getResult().setHideSD(); }
        mean_value_stats.getResult().setInputUncertainties(temp_unc, lum_unc);
        Star[] stars = new Star[9]; //sigma region
        double[] xs = {x, x - temp_unc, x + temp_unc};
        double[] ys = {y, y - lum_unc, y + lum_unc};
        int index = 0;

        //Find data points
        if (temp_unc == 0 && lum_unc == 0) {
            if (mean_value_stats.getResult().getAge() == null) {
                validSD = false;
            } else {
                stars = new Star[]{mean_value_stats.getResult()}; //no sigma region
            }
        } else {
            for (double current_x : xs) {
                for (double current_y : ys) {
                    Star star = estimate_star(current_x, current_y).getResult();
                    if (star == null || star.getAge() == null) {
                        validSD = false;
                        mean_value_stats.getResult().setInvalidSD();
                        break;
                    }
                    stars[index] = star;
                    index++;
                }
            }
        }

        //For each data point, find the square of its distance to the mean and sum the values
        if (validSD) {
            mean_value_stats.setSigmaRegion(stars);
            double[] deviation = {0, 0, 0, 0};
            for (Star star : stars) {
                double age_diff = Math.pow(10, star.getAge()) - Math.pow(10, mean_value_stats.getResult().getAge());
                deviation[0] += Math.pow(age_diff, 2);
                for (int inx = 3; inx < 6; inx++) { //except input params and age all are linear
                    deviation[inx - 2] += Math.pow(star.getAllAttributes()[inx]
                            - mean_value_stats.getResult().getAllAttributes()[inx], 2);
                }
            }
            mean_value_stats.setDeviations(deviation);


            //Divide by number of data points and find square root
            double[] uncertainties = new double[4];
            for (int i = 0; i < 4; i++) {
                uncertainties[i] = deviation[i] / NUMBER_OF_SIGMA_REGION_POINTS;
                uncertainties[i] = Math.sqrt(uncertainties[i]);
            }

            if (Math.abs(uncertainties[0]) > 0) { //special handling for age [dex]
                uncertainties[0] = Math.log10(uncertainties[0]); //back to dex
                uncertainties[0] = Math.pow(10, uncertainties[0]) / (Math.pow(10, mean_value_stats.getResult().getAge()) * Math.log(10));
            }

            mean_value_stats.getResult().setDeviations(uncertainties);
        } else {
            mean_value_stats.getResult().setInvalidSD();
        }

        if (mean_value_stats.getResult().getAge() != null && !mean_value_stats.getResult().errorIsSet()) {
            Interpolator.determineError(mean_value_stats);
        }
        mean_value_stats.countUncertainty();
        return mean_value_stats;
    }

    public ComputationStats estimate_stats(double x, double y, double temp_unc, double lum_unc) {
        return estimate_stats(x, y, temp_unc, lum_unc, true, true, new HashSet<>());
    }

    /** Returns completely estimated star including uncertainties */
    public Star estimate(double x, double y, double x_unc, double y_unc, boolean includeError,
                         boolean includeDeviation, HashSet<Short> ignoredPhases) {
        return estimate_stats(x, y, x_unc, y_unc, includeError, includeDeviation, ignoredPhases).getResult();
    }

    public Star estimate(double x, double y, double x_unc, double y_unc) {
        return estimate(x, y, x_unc, y_unc, true, true, new HashSet<>());
    }

    public Map<Double, ArrayList<Star>> getGroupedData() {
        return groupedData;
    }

    public HashSet<Short> getCurrentPhases() {
        return currentPhases;
    }

    public ZAMS getZAMS() {
        return zams;
    }

    /** Check, if any side intersects the input */
    private boolean sidesMatch(ComputationStats stats, double x, double y) {
        double MAX_ERROR = 0.0001;
        Star[] neighbours = stats.getNeighbours();
        Double[] params = new Double[]{null, null, null, null, null, null};

        //check horizontal
        if (neighbours[0] != null && Math.abs(intersection(neighbours[0], neighbours[1], x, y)[1] - y) < MAX_ERROR) {
            for (int i = 0; i < 6; i++) {
                params[i] = Interpolator.interpolate(x, neighbours[0].getTemperature(), neighbours[1].getTemperature(),
                        neighbours[0].getAllAttributes()[i], neighbours[1].getAllAttributes()[i]);
            }
        } else if (neighbours[2] != null && Math.abs(intersection(neighbours[2], neighbours[3], x, y)[1] - y) < MAX_ERROR) {
            for (int i = 0; i < 6; i++) {
                params[i] = Interpolator.interpolate(x, neighbours[2].getTemperature(), neighbours[3].getTemperature(),
                        neighbours[2].getAllAttributes()[i], neighbours[3].getAllAttributes()[i]);
            }
        } else if (neighbours[2] != null && neighbours[0] != null) { //check vertical
            Star l_lo = (neighbours[0].getTemperature() < neighbours[1].getTemperature()) ? neighbours[0] : neighbours[1];
            Star r_lo = (neighbours[0].getTemperature() < neighbours[1].getTemperature()) ? neighbours[1] : neighbours[0];
            Star l_up = (neighbours[2].getTemperature() < neighbours[3].getTemperature()) ? neighbours[2] : neighbours[3];
            Star r_up = (neighbours[2].getTemperature() < neighbours[3].getTemperature()) ? neighbours[3] : neighbours[2];
            double left_insct = intersection(l_lo, l_up, x, y)[0];
            double right_insct = intersection(r_up, r_lo, x, y)[0];
            if (Math.abs(left_insct - x) < MAX_ERROR) {
                for (int i = 0; i < 6; i++) {
                    params[i] = Interpolator.interpolate(y, l_lo.getLuminosity(), l_up.getLuminosity(),
                            l_lo.getAllAttributes()[i], l_up.getAllAttributes()[i]);
                }
            } else if (Math.abs(right_insct - x) < MAX_ERROR){
                for (int i = 0; i < 6; i++) {
                    params[i] = Interpolator.interpolate(y, r_lo.getLuminosity(), r_up.getLuminosity(),
                            r_lo.getAllAttributes()[i], r_up.getAllAttributes()[i]);
                }
            }
        }

        if (params[0] != null) {
            double distance = Math.sqrt((params[0] - x) * (params[0] - x) + (params[1] - y) * (params[1] - y));
            params[0] = x;
            params[1] = y;
            stats.setResult(new Star(params));
            stats.setErrors(new double[]{params[2] * distance, params[3] * distance, params[4] * distance, params[5] * distance});
            return true;
        }

        return false;
    }

}
