package backend;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import static backend.ResultType.*;

/**
 * Class responsible for uncertainty computation
 */
public abstract class Statistics {

    /** Performs Monte Carlo simulation, changes output parameters' uncertainties
     * Result in stats must be estimated */
    public static void computeUncertainty(ComputationStats stats) {
        double mean_x = stats.getX();
        double mean_y = stats.getY();
        double unc_x = stats.getX_unc();
        double unc_y = stats.getY_unc();
        if (unc_x == 0 || unc_y == 0) { //no input uncertainty
            return; //todo
        }

        NormalDistribution xDistribution = new NormalDistribution(mean_x, unc_x);
        NormalDistribution yDistribution = new NormalDistribution(mean_y, unc_y);
        DescriptiveStatistics[] statistics = new DescriptiveStatistics[]{
                new DescriptiveStatistics(), //age
                new DescriptiveStatistics(), //radius
                new DescriptiveStatistics(), //mass
                new DescriptiveStatistics(), //phase
        };

        Data model = GridFileParser.getCurrentData();
        for (int i = 0; i < 1000; i++) {
            double rand_x = xDistribution.sample();
            double rand_y = yDistribution.sample();
            Double[] attributes = model.estimateStar(rand_x, rand_y, 0, 0).getResult().getAllAttributes();
            statistics[0].addValue(UnitsConverter.fromDex(attributes[2]));
            statistics[1].addValue(attributes[3]);
            statistics[2].addValue(attributes[4]);
            statistics[3].addValue(attributes[5]);
        }

        for (int i = 0; i < 4; i++) {
            System.out.println(statistics[i].getPercentile(25) + "\t" + statistics[i].getPercentile(75));
        }
    }
}
