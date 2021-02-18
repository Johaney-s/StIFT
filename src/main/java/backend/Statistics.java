package backend;


import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Class responsible for uncertainty computation
 */
public abstract class Statistics {

    /**
     * Performs Monte Carlo simulation, changes output parameters' uncertainties in star
     *
     */
    public static void computeUncertainty(ComputationStats stats) {
        double mean_x = stats.getX();
        double mean_y = stats.getY();
        double unc_x = stats.getResult().getUncertainties()[0];
        double unc_y = stats.getResult().getUncertainties()[1];
        if (unc_x == 0 || unc_y == 0) { //no input uncertainty
            if (stats.getX1_() == null) {
                return; //star match, no extra uncertainty
            }

            return;
            //figure out SD from evolutionary status (<= also side match)
        }

        NormalDistribution xDistribution = new NormalDistribution(mean_x, unc_x);
        NormalDistribution yDistribution = new NormalDistribution(mean_y, unc_y);

        for (int i = 0; i < 1000; i++) {
            double rand_x = xDistribution.sample();
            double rand_y = yDistribution.sample();
        }

    }
}
