package backend;

import backend.objects.ResultStar;
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
            if (attributes[2] != null) {
                statistics[0].addValue(UnitsConverter.fromDex(attributes[2]));
                statistics[1].addValue(attributes[3]);
                statistics[2].addValue(attributes[4]);
                statistics[3].addValue(attributes[5]);
            }
        }

        System.out.println("N: "+ statistics[0].getN() + "\t1st quantile\t2nd quantile");
        ResultStar result = stats.getResult();
        Double[] attributes = result.getAllAttributes();

        //age in dex
        double lowerBound = statistics[0].getPercentile(25);
        double upperBound = statistics[0].getPercentile(75);
        System.out.println("Param: " + 2+ ".\t" + UnitsConverter.toDex(statistics[0].getPercentile(25)) + "\t"
                + UnitsConverter.toDex(statistics[0].getPercentile(75)));
        result.setDeviation(2, UnitsConverter.toDex(lowerBound) - attributes[2], UnitsConverter.toDex(upperBound) - attributes[2]);

        for (int i = 3; i < 6; i++) {
            lowerBound = statistics[i - 2].getPercentile(25);
            upperBound = statistics[i - 2].getPercentile(75);
            System.out.println("Param: " + i + ".\t" + statistics[i - 2].getPercentile(25) + "\t" + statistics[i - 2].getPercentile(75));
            result.setDeviation(i, lowerBound - attributes[i], upperBound - attributes[i]);
        }
    }
}
