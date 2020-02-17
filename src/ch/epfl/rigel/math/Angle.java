package ch.epfl.rigel.math;

public final class Angle {

    public final static double TAU = 2 * Math.PI;

    private Angle() {
    }

    public static double normalizePositive(double rad) {
        return RightOpenInterval.of(0, TAU).reduce(rad);
    }

    public static double ofArcsec(double sec) {
        return ((sec / 3600) * TAU / 360);
    }

    public static double ofDMS(int deg, int min, double sec) {
        return sec;

    }

}
