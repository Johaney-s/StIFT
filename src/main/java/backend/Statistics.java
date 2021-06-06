package backend;

import backend.objects.ResultStar;
import backend.objects.Star;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Class responsible for uncertainty computation */
public abstract class Statistics {
    private static int N = 1000; //number of points to be generated for monte carlo simulation

    /** Performs Monte Carlo simulation, changes output parameters' uncertainties */
    public static void computeUncertainty(ComputationStats stats) {
        double mean_x = stats.getX();
        double mean_y = stats.getY();
        double unc_x = stats.getX_unc();
        double unc_y = stats.getY_unc();
        ResultStar result = stats.getResult();

        if ((unc_x == 0 && unc_y == 0) || stats.getResultType() == ResultType.NONE) { //no input uncertainty / result
            switch(stats.getResultType()) {
                case NONE:
                    return;
                case STAR_MATCH: //set uncertainty to 0
                    for (int i = 2; i < 6; i++) {
                        result.setUncertainty(i, 0, 0);
                    }
                    return;
                case ZAMS_OUTSIDER:
                    return;
            }
        }

        Star first = stats.getResult1_();
        Star second = stats.getResult2_();
        if ((unc_x == 0 || unc_y == 0) && first != null && second != null) { //set missing uncertainty
            double[] deviation = getDeviation(result, first, second);
            unc_x = (unc_x == 0) ? deviation[0] : unc_x;
            unc_y = (unc_y == 0) ? deviation[1] : unc_y;
        }

        NormalDistribution xDistribution = (unc_x * unc_x > 0) ? new NormalDistribution(mean_x, unc_x * unc_x) : null;
        NormalDistribution yDistribution = (unc_y * unc_y > 0) ? new NormalDistribution(mean_y, unc_y * unc_y) : null;
        SynchronizedDescriptiveStatistics[] statistics = new SynchronizedDescriptiveStatistics[]{
                new SynchronizedDescriptiveStatistics(), //0 - age
                new SynchronizedDescriptiveStatistics(), //1 - radius
                new SynchronizedDescriptiveStatistics(), //2 - mass
                new SynchronizedDescriptiveStatistics(), //3 - phase
        };

        Data model = Data.getCurrentData();
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < N; i++) {
            es.execute(() -> {
                double rand_x = (xDistribution == null) ? mean_x : xDistribution.sample();
                double rand_y = (yDistribution == null) ? mean_y : yDistribution.sample();
                Double[] attributes = model.estimateStar(rand_x, rand_y, 0, 0, stats.getIgnoredPhases()).getResult().getAllAttributes();

                if (attributes != null && attributes[2] != null){
                    statistics[0].addValue(UnitsConverter.fromDex(attributes[2]));
                    statistics[1].addValue(attributes[3]);
                    statistics[2].addValue(attributes[4]);
                    statistics[3].addValue(attributes[5]);
                }
            });
        }

        es.shutdown();
        try {
            if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
                es.shutdownNow();
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (statistics[0].getN() > (N / 3)) { //more than 33% estimated points required
            fillStatistics(statistics, result);
        } else {
            return;
        }

        if (statistics[0].getN() < (N * 0.95)) { //more than 5% points are not estimated -> mirror uncertainties
            for (int i = 3; i < 6; i++) {
                Double[] uncertainty = result.getUncertainty(i);
                double maxUncertainty = Math.max(Math.abs(uncertainty[0]), Math.abs(uncertainty[1]));
                result.setUncertainty(i, -maxUncertainty, maxUncertainty);
            }
            //special handling for age
            Double[] ageUncertainty = result.getUncertainty(2);
            double ageAbsUncertainty = Math.max(
                    Math.abs(UnitsConverter.fromDex(result.getAge() + ageUncertainty[0]) - UnitsConverter.fromDex(result.getAge())),
                    Math.abs(UnitsConverter.fromDex(result.getAge() + ageUncertainty[1]) - UnitsConverter.fromDex(result.getAge())));
            double agePositiveError = UnitsConverter.toDex(UnitsConverter.fromDex(result.getAge()) + ageAbsUncertainty) - result.getAge();
            double ageNegativeError = UnitsConverter.toDex(UnitsConverter.fromDex(result.getAge()) - ageAbsUncertainty) - result.getAge();
            result.setUncertainty(2, ageNegativeError, agePositiveError);
        }

        fixToZero(result);
    }

    private static void fillStatistics(SynchronizedDescriptiveStatistics[] statistics, ResultStar result) {
        Double[] attributes = result.getAllAttributes();
        //special handling for age in dex
        double lowerBound = statistics[0].getPercentile(25);
        double upperBound = statistics[0].getPercentile(75);
        result.setUncertainty(2, UnitsConverter.toDex(lowerBound) - attributes[2], UnitsConverter.toDex(upperBound) - attributes[2]);


        for (int i = 3; i < 6; i++) { //handling rest of output parameters
            lowerBound = statistics[i - 2].getPercentile(25);
            upperBound = statistics[i - 2].getPercentile(75);
            result.setUncertainty(i, lowerBound - attributes[i], upperBound - attributes[i]);
        }
    }

    /** Deviation of input's mean (x,y) values from two points */
    private static double[] getDeviation(ResultStar input, Star first, Star second) {
        double teffUnc = Math.sqrt(Math.pow(Math.abs(input.getTemperature() - first.getTemperature()), 2)
                + Math.pow(Math.abs(input.getTemperature() - second.getTemperature()), 2) / 2);
        double lumUnc = Math.sqrt(Math.pow(Math.abs(input.getLuminosity() - first.getLuminosity()), 2)
                + Math.pow(Math.abs(input.getLuminosity() - second.getLuminosity()), 2) / 2);
        return new double[]{teffUnc, lumUnc};
    }

    /**
     * Fix one-sided uncertainty results
     * @param result Result to be checked for correction
     */
    private static void fixToZero(ResultStar result) {
        for (int i = 2; i < 6; i++) {
            Double[] uncertainty = result.getUncertainty(i);
            if (uncertainty[0] > 0) {
                uncertainty[0] = 0.0; //reset lower uncertainty
            }

            if (uncertainty[1] < 0) {
                uncertainty[1] = 0.0; //reset upper uncertainty
            }
            result.setUncertainty(i, uncertainty[0], uncertainty[1]);
        }
    }
}
