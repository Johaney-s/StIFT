
package backend;

import backend.objects.ResultStar;
import backend.objects.Star;

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
    private void fillWithZAMS(ComputationStats stats, Star upperZAMS) {
        Star lowerZAMS = findLowerZAMS(upperZAMS);
        double TOLERATED_DISTANCE = -0.0001; //below the intersection
        if (lowerZAMS == null || !(lowerZAMS.getLuminosity() < stats.getY() && lowerZAMS != stats.getStar12()
                && lowerZAMS.getTemperature() < stats.getX())) {
            return; //candidate does not fulfill conditions to become lower right neighbour
        }

        Star upperLeft = (stats.getStar11().getTemperature() < stats.getStar12().getTemperature()) ? stats.getStar11() : stats.getStar12();
        double intersection_y = intersection(lowerZAMS, upperZAMS, stats.getX(), stats.getY())[1];
        if (stats.getY() - intersection_y > TOLERATED_DISTANCE) {
            Star interstar = Interpolator.interpolateStars(lowerZAMS, upperZAMS, stats.getX(), intersection_y);
            stats.setStar21(lowerZAMS);
            stats.setStar22(interstar);
            stats.setStar11(upperLeft);
            stats.setStar12(upperZAMS);
        }
    }

    /** Returns closest lower ZAMS star to current upper ZAMS */
    private Star findLowerZAMS(Star upper_zams) {
        Star lower_zams = null;
        for (Star zams_star : zams.getTrack()) {
            if (zams_star.getLuminosity() < upper_zams.getLuminosity()) {
                if (lower_zams == null || lower_zams.getLuminosity() < zams_star.getLuminosity()) {
                    lower_zams = zams_star;
                }
            }
        }

        return lower_zams;
    }

    /** Return true, if input point is too close to a star from grid and set stats' error and mean values */
    private boolean starsMatch(ComputationStats stats, Star star) {
        double x_error = Math.abs(star.getTemperature() - stats.getX());
        double y_error = Math.abs(star.getLuminosity() - stats.getY());
        if (x_error < 0.0001 && y_error < 0.0001) {
            Double[] attributes = star.getAllAttributes();
            double error_const = Math.sqrt(x_error * x_error + y_error * y_error);
            stats.setResult(new ResultStar(star.getAllAttributes()));
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
    public ComputationStats estimateStar(double x, double y, double x_unc, double y_unc, HashSet<Short> ignoredPhases) {
        ComputationStats stats = new ComputationStats(x, y, x_unc, y_unc);
        if (!findNearestStars(stats, ignoredPhases)) {
            if (stats.getResult() != null) {return stats;} //match was found

            if (x_unc > 0 || y_unc > 0) { //give ZAMS outsiders a chance
                Star[] zams = findBothZAMS(stats);
                if (zams != null) {
                    double[] altCoordinates = new double[]{x - x_unc, y + y_unc};
                    double[] inputCoords = new double[]{x, y};
                    double[] lineIntersection = lineIntersection(zams[0], zams[1], altCoordinates, inputCoords);
                    ComputationStats newStats = estimateStar(lineIntersection[0], lineIntersection[1], 0, 0, ignoredPhases);
                    if (newStats.getResult() != null && newStats.getResult().getAge() != null) {
                        stats.setStar11(newStats.getStar11());
                        stats.setStar12(newStats.getStar12());
                        stats.setStar21(newStats.getStar21());
                        stats.setStar22(newStats.getStar22());
                        stats.setResult1_(newStats.getResult1_());
                        stats.setResult2_(newStats.getResult2_());
                        Double[] params = newStats.getResult().getAllAttributes();
                        stats.setResult(new ResultStar(x, y, params[2], params[3], params[4], params[5]));
                        //SOMEHOW DEAL WITH ERROR LATER <-------------------------- TODO
                        return stats;
                    }
                }
            }

            stats.setResult(new ResultStar(x, y, null, null, null, null));
            sidesMatch(stats, x, y); //give pairs a chance
            return stats;
        }

        if (sidesMatch(stats, x, y)) {
            return stats;
        }

        if (!Interpolator.determineEvolutionaryStatus(stats)) {
            stats.setResult(new ResultStar(x, y, null, null, null, null));
            return stats;
        }
        Interpolator.interpolateAllCharacteristics(stats);
        return stats;
    }

    public ComputationStats estimateStar(double x, double y, double temp_unc, double lum_unc) {
        return estimateStar(x, y, temp_unc, lum_unc, new HashSet<Short>());
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
    public ComputationStats estimateStats(double x, double y, double temp_unc, double lum_unc, boolean includeError,
                                          boolean includeDeviation, HashSet<Short> ignoredPhases) {
        ComputationStats meanValueStats = estimateStar(x, y, temp_unc, lum_unc, ignoredPhases);
        ResultStar mean = meanValueStats.getResult();
        if (!includeError) { mean.setHideError(); }
        if (!includeDeviation) { mean.setHideSD(); }
        mean.setInputUncertainties(temp_unc, lum_unc);
        ArrayList<Star> stars = new ArrayList<>(); //sigma region
        double[] xs = {x, x - temp_unc, x + temp_unc};
        double[] ys = {y, y - lum_unc, y + lum_unc};

        if (mean.getAge() == null) {
            return meanValueStats;
        }

        if (temp_unc != 0 && lum_unc != 0) {
            for (double current_x : xs) {
                for (double current_y : ys) {
                    if (current_x == x && current_y == y) { continue; } //skip mean value
                    Star star = estimateStar(current_x, current_y, temp_unc, lum_unc).getResult();
                    if (star != null && star.getAge() != null) {
                        stars.add(star);
                    }
                }
            }

            if (stars.size() > 0) {
                meanValueStats.setSigmaRegion(stars);
                mean.setDeviations(computeDeviation(mean, stars));
            }
        } else {
            mean.setDeviations(new double[]{0, 0, 0, 0});
        }

        if (mean.getAge() != null && !mean.errorIsSet()) {
            mean.setErrors(computeDeviation(mean, new ArrayList<>(Arrays.asList(meanValueStats.getNeighbours()))));
        }

        meanValueStats.countUncertainty();
        return meanValueStats;
    }

    public ComputationStats estimateStats(double x, double y, double temp_unc, double lum_unc) {
        return estimateStats(x, y, temp_unc, lum_unc, true, true, new HashSet<>());
    }

    /** Returns completely estimated star including uncertainties */
    public ResultStar estimate(double x, double y, double x_unc, double y_unc, boolean includeError,
                         boolean includeDeviation, HashSet<Short> ignoredPhases) {
        return estimateStats(x, y, x_unc, y_unc, includeError, includeDeviation, ignoredPhases).getResult();
    }

    public ResultStar estimate(double x, double y, double x_unc, double y_unc) {
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

    /**
     * Computes standard deviation from region around mean value
     * @param mean Mean value (Star)
     * @param region Region of stars
     * @return [ageSD, radSD, massSD, phaseSD]
     */
    public double[] computeDeviation(Star mean, ArrayList<Star> region) {
        double[] deviation = {0, 0, 0, 0};
        for (Star star : region) {
            double age_diff = Math.pow(10, star.getAge()) - Math.pow(10, mean.getAge());
            deviation[0] += Math.pow(age_diff, 2);
            for (int inx = 3; inx < 6; inx++) { //except input params and age all are linear
                deviation[inx - 2] += Math.pow(star.getAllAttributes()[inx] - mean.getAllAttributes()[inx], 2);
            }
        }

        //Divide by number of data points and find square root
        double[] uncertainties = new double[4];
        for (int i = 0; i < 4; i++) {
            uncertainties[i] = deviation[i] / region.size();
            uncertainties[i] = Math.sqrt(uncertainties[i]);
        }

        if (uncertainties[0] > 0) { //special handling for age [dex]
            uncertainties[0] = Math.log10(uncertainties[0]);
            uncertainties[0] = Math.pow(10, uncertainties[0]) / (Math.pow(10, mean.getAge()) * Math.log(10));
        }

        return uncertainties;
    }

    /** Check, if any side intersects the input */
    private boolean sidesMatch(ComputationStats stats, double x, double y) {
        double MAX_ERROR = 0.0001;
        Star[] neighbours = stats.getNeighbours();
        Double[] params = new Double[]{x, y, null, null, null, null};
        Star[] usedNeighbours = new Star[]{};

        //check horizontal
        if (neighbours[0] != null && Math.abs(intersection(neighbours[0], neighbours[1], x, y)[1] - y) < MAX_ERROR) {
            usedNeighbours = new Star[]{neighbours[0], neighbours[1]};
            for (int i = 2; i < 6; i++) {
                params[i] = Interpolator.interpolate(x, neighbours[0].getTemperature(), neighbours[1].getTemperature(),
                        neighbours[0].getAllAttributes()[i], neighbours[1].getAllAttributes()[i]);
            }
        } else if (neighbours[2] != null && Math.abs(intersection(neighbours[2], neighbours[3], x, y)[1] - y) < MAX_ERROR) {
            usedNeighbours = new Star[]{neighbours[2], neighbours[3]};
            for (int i = 2; i < 6; i++) {
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
                usedNeighbours = new Star[]{l_lo, l_up};
                for (int i = 2; i < 6; i++) {
                    params[i] = Interpolator.interpolate(y, l_lo.getLuminosity(), l_up.getLuminosity(),
                            l_lo.getAllAttributes()[i], l_up.getAllAttributes()[i]);
                }
            } else if (Math.abs(right_insct - x) < MAX_ERROR){
                usedNeighbours = new Star[]{r_lo, r_up};
                for (int i = 2; i < 6; i++) {
                    params[i] = Interpolator.interpolate(y, r_lo.getLuminosity(), r_up.getLuminosity(),
                            r_lo.getAllAttributes()[i], r_up.getAllAttributes()[i]);
                }
            }
        }

        if (params[2] != null) {
            stats.setResult(new ResultStar(params));
            stats.setErrors(computeDeviation(stats.getResult(), new ArrayList<>(Arrays.asList(usedNeighbours))));
            stats.setEvolutionaryLine(usedNeighbours[0], usedNeighbours[1]);
            return true;
        }

        return false;
    }

    /** Returns coordinates of intersection of lines A-B and C-D or null if none */
    private double[] lineIntersection(Star A, Star B, double[] C, double[] D) {
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

    /** Searches for upper and lower ZAMS points which intersection with INPUT-UPPER_LEFT is bounded
     * by the points on y-axis and lies in the sigma region
     * @return either found ZAMS points suitable for interpolation or null
     */
    private Star[] findBothZAMS(ComputationStats stats) {
        ArrayList<Star> track = zams.getTrack();
        double[] altCoords = new double[]{stats.getX() - stats.getX_unc(), stats.getY() + stats.getY_unc()};
        double[] inputCoords = new double[]{stats.getX(), stats.getY()};
        for (int i = 0; i < track.size() - 1; i++) {
            Star lower = track.get(i);
            Star upper = track.get(i + 1);

            double[] intersection = lineIntersection(lower, upper, altCoords, inputCoords);
            if (intersection[1] >= lower.getLuminosity() && intersection[1] <= upper.getLuminosity()
                && intersection[0] >= stats.getX() - stats.getX_unc() && intersection[0] <= stats.getX() + stats.getX_unc()
                && intersection[1] >= stats.getY() - stats.getY_unc() && intersection[1] <= stats.getY() + stats.getY_unc()) {
                return new Star[]{lower, upper};
            }
        }

        return null;
    }
}
