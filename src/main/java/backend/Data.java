
package backend;

import backend.objects.ResultStar;
import backend.objects.Star;

import java.util.*;

import static backend.Geometry.intersection;
import static backend.Geometry.lineIntersection;

/**
 * Data divided into lists of isochrones
 */
public class Data {
    private static Data currentData;
    private final ArrayList<ArrayList<Star>> groupedData;
    private ArrayList<Star> currentGroup;
    private HashSet<Short> currentPhases;
    private int currentLabel = 0;
    private ZAMS zams;
    
    public Data() {
       groupedData = new ArrayList<>();
       currentGroup = new ArrayList<>();
       currentPhases =  new HashSet<>();
    }
   
    /**
     * Add star to relevant group
     * @param star New star
     */
    public void addStar(Star star){
        if (!currentGroup.isEmpty() && currentGroup.get(currentGroup.size() - 1).getPhase() > star.getPhase()) {
            addCurrentGroupToGroupedData();
            currentGroup = new ArrayList<>();
        }

        currentGroup.add(star);
        currentPhases.add(star.getPhase().shortValue());
    }

    public void addStar(Star star, int label) {
        if (!currentGroup.isEmpty() && label != currentLabel) {
            addCurrentGroupToGroupedData();
            currentGroup = new ArrayList<>();
        }

        currentGroup.add(star);
        currentPhases.add(star.getPhase().shortValue());
        currentLabel = label;
    }

    /**
     * Close current isochrones group and add to data if larger than 1
     */
    public void addCurrentGroupToGroupedData() {
        if (currentGroup.size() > 1) {
            groupedData.add(currentGroup);
        }
    }

    /**
     * Remove unwanted phases and set ZAMS if applicable
     */
    public Data applySettings(Settings settings) {
        Short zamsPhase = settings.getPhaseZams();
        ZAMS newZams = new ZAMS();
        newZams.setPhase(zamsPhase);

        // no changes to be made
        if (settings.getPhases().containsAll(this.getCurrentPhases())
                && settings.getPhaseZams() == null) {
            this.zams = newZams;
            return this;
        }

        List<ArrayList<Star>> removeIsochrones = new ArrayList<>();
        for (ArrayList<Star> line : this.getGroupedData()) {

            //remove every star with unwanted phase
            List<Star> removeStars = new ArrayList<>();
            for (Star star : line) {
                if (!settings.getPhases().contains(star.getPhase().shortValue())) {
                    removeStars.add(star);
                }
            }
            line.removeAll(removeStars);

            //remove isochrones containing less than 2 stars
            if (line.size() < 2) {
                removeIsochrones.add(line);
                continue;
            }

            //add ZAMS if applicable
            if (zamsPhase != null && line.get(0).getPhase() == zamsPhase.doubleValue()) {
                    newZams.add(line.get(0));
            }
        }
        this.removeIsochrones(removeIsochrones);
        this.setZAMS(newZams);
        this.setCurrentPhases(settings.getPhases());
        return this;
    }
    
    /**
     * Finds four-angled figure nearest to given coordinates, sets corresponding attributes in stats
     * @param stats Computation stats including necessary [x,y] input coordinates
     * @return true if 4 neighbours found, false otherwise (including star match)
     */
    public boolean findNearestStars(ComputationStats stats, HashSet<Short> ignoredPhases) {
        if (ignoredPhases.size() == currentData.getCurrentPhases().size()) { return false; }
        Star upperLeft = null;
        Star upperRight = null;
        Star lowerRight = null;
        Star lowerLeft = null;
        Star upperZAMS = null;

        for (ArrayList<Star> list : groupedData) {
            if (!ignoredPhases.contains(list.get(0).getPhase().shortValue()) && starsMatch(stats, list.get(0))) {
                return false; //NO NEIGHBOURS returned, BUT MATCH
            }

            if (list.size() > 1) {
                for (int index = 0; index + 1 < list.size(); index++) {
                    Star first = list.get(index);
                    Star second = list.get(index + 1);
                    if (ignoredPhases.contains(first.getPhase().shortValue())) {
                        continue;
                    } //ignore ignored phase
                    if (starsMatch(stats, second)) {
                        return false;
                    } //NO NEIGHBOURS returned, BUT MATCH

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
                                if (zams.getTrack().contains(list.get(0))) {
                                    upperZAMS = list.get(0);
                                }
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

        //give ZAMS a chance
        if (upperZAMS != null && upperLeft != null && lowerRight == null && !ignoredPhases.contains((short)zams.getPhase())) {
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
            stats.changeResultType(ResultType.ZAMS_INSIDER);
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
        if (x_error < 0.0001 && y_error < 0.0001) {stats.setResult(new ResultStar(star.getAllAttributes()));
            stats.changeResultType(ResultType.STAR_MATCH);
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
            if (stats.getResult() != null) { //match was found
                stats.getResult().setResultType(stats.getResultType());
                return stats;
            }

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
                        stats.changeResultType(ResultType.ZAMS_OUTSIDER);
                        stats.getResult().setResultType(stats.getResultType());
                        return stats;
                    }
                }
            }

            stats.setResult(new ResultStar(x, y, null, null, null, null));
            sidesMatch(stats, x, y); //give pairs a chance
            stats.getResult().setResultType(stats.getResultType());
            return stats;
        }

        if (sidesMatch(stats, x, y)) {
            stats.getResult().setResultType(stats.getResultType());
            return stats;
        }

        //give ZAMS a chance
        if (!Interpolator.determineEvolutionaryStatus(stats)) {
            stats.setResult(new ResultStar(x, y, null, null, null, null));
            stats.getResult().setResultType(stats.getResultType());
            return stats;
        }

        Interpolator.interpolateAllCharacteristics(stats);
        stats.changeResultType(ResultType.FULL_ESTIMATION);
        stats.getResult().setResultType(stats.getResultType());
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
    public ComputationStats estimateStats(double x, double y, double temp_unc, double lum_unc,
                                          boolean includeDeviation, short rounding, HashSet<Short> ignoredPhases) {
        ComputationStats meanValueStats = estimateStar(x, y, temp_unc, lum_unc, ignoredPhases);
        meanValueStats.setIgnoredPhases(ignoredPhases);
        ResultStar mean = meanValueStats.getResult();
        mean.setInputUncertainties(temp_unc, lum_unc);
        mean.changeUncertaintyPrecision(rounding);

        if (mean.getAge() == null) {
            return meanValueStats;
        }

        if (includeDeviation) {
            Statistics.computeUncertainty(meanValueStats);
        }
        
        return meanValueStats;
    }

    public ComputationStats estimateStats(double x, double y, double temp_unc, double lum_unc, short rounding) {
        return estimateStats(x, y, temp_unc, lum_unc, true, rounding, new HashSet<>());
    }

    /** Returns completely estimated star including uncertainties */
    public ResultStar estimate(double x, double y, double x_unc, double y_unc,
                         boolean includeDeviation, short rounding, HashSet<Short> ignoredPhases) {
        return estimateStats(x, y, x_unc, y_unc, includeDeviation, rounding, ignoredPhases).getResult();
    }

    public ResultStar estimate(double x, double y, double x_unc, double y_unc, short rounding) {
        return estimate(x, y, x_unc, y_unc, true, rounding, new HashSet<>());
    }

    public ArrayList<ArrayList<Star>> getGroupedData() {
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
            Star l_up = (neighbours[0].getTemperature() < neighbours[1].getTemperature()) ? neighbours[0] : neighbours[1];
            Star r_up = (neighbours[0].getTemperature() < neighbours[1].getTemperature()) ? neighbours[1] : neighbours[0];
            Star l_lo = (neighbours[2].getTemperature() < neighbours[3].getTemperature()) ? neighbours[2] : neighbours[3];
            Star r_lo = (neighbours[2].getTemperature() < neighbours[3].getTemperature()) ? neighbours[3] : neighbours[2];
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
            stats.setEvolutionaryLine(usedNeighbours[0], usedNeighbours[1]);
            stats.changeResultType(ResultType.SIDE_MATCH);
            return true;
        }

        return false;
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

    public static void setCurrentData(Data newData) {
        Data.currentData = newData;
    }

    public static Data getCurrentData() {
        return currentData;
    }

    public void setZAMS(ZAMS zams) {
        this.zams = zams;
    }

    /**
     * Remove groups of stars (isochrones) from data
     */
    public void removeIsochrones(List<ArrayList<Star>> removeIsochrones) {
        this.groupedData.removeAll(removeIsochrones);
    }

    public void setCurrentPhases(HashSet<Short> phases) {
        this.currentPhases = phases;
    }
}
