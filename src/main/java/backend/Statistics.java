package backend;

import backend.objects.ResultStar;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Class responsible for uncertainty computation */
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
        SynchronizedDescriptiveStatistics[] statistics = new SynchronizedDescriptiveStatistics[]{
                new SynchronizedDescriptiveStatistics(), //0 - age
                new SynchronizedDescriptiveStatistics(), //1 - radius
                new SynchronizedDescriptiveStatistics(), //2 - mass
                new SynchronizedDescriptiveStatistics(), //3 - phase
        };

        Data model = GridFileParser.getCurrentData();
        //double start = System.currentTimeMillis();
        ExecutorService es = Executors.newCachedThreadPool();

        for (int i = 0; i < 1000; i++) {
            es.execute(() -> {
                double rand_x = xDistribution.sample();
                double rand_y = yDistribution.sample();
                Double[] attributes = model.estimateStar(rand_x, rand_y, 0, 0).getResult().getAllAttributes();
                //System.out.println(Arrays.toString(attributes));

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
            es.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //double end = System.currentTimeMillis();
        //System.out.println("Execution time: " + (end - start));

        //System.out.println("N: " + statistics[0].getN());
        ResultStar result = stats.getResult();
        Double[] attributes = result.getAllAttributes();

        //special handling for age in dex
        double lowerBound = statistics[0].getPercentile(25);
        double upperBound = statistics[0].getPercentile(75);
        /*System.out.println("Param: " + 2 + ".\t" + UnitsConverter.toDex(statistics[0].getPercentile(25)) + "\t"
                + UnitsConverter.toDex(statistics[0].getPercentile(75)));*/
        result.setDeviation(2, UnitsConverter.toDex(lowerBound) - attributes[2], UnitsConverter.toDex(upperBound) - attributes[2]);
        //System.out.println("Min: " + UnitsConverter.toDex(statistics[0].getMin()) + " max: " + UnitsConverter.toDex(statistics[0].getMax()));


        for (int i = 3; i < 6; i++) { //handling rest of output parameters
            lowerBound = statistics[i - 2].getPercentile(25);
            upperBound = statistics[i - 2].getPercentile(75);
            //System.out.println("Param: " + i + ".\t" + statistics[i - 2].getPercentile(25) + "\t" + statistics[i - 2].getPercentile(75));
            result.setDeviation(i, lowerBound - attributes[i], upperBound - attributes[i]);
            //System.out.println("Min: " + statistics[i - 2].getMin() + " max: " + statistics[i - 2].getMax());
        }
    }
}
