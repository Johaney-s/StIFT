package backend;

public abstract class UnitsConverter {

    public static double toDex(double value) {
        return Math.pow(10, value);
    }

    public static double fromDex(double value) {
        return Math.log10(value);
    }
}
