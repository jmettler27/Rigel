package ch.epfl.rigel.math;

/**
 * The methods and constants allowing to work on angles represented by double
 * values.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class Angle {

    public final static double TAU = 2 * Math.PI;

    private final static double RAD_PER_HOUR = TAU / 24.0; // hour --> rad
    private final static double HOUR_PER_RAD = 24.0 / TAU; // rad --> hour

    /**
     * Default constructor.
     */
    private Angle() {
    }

    /**
     * Normalizes the rad angle by reducing it to the interval [0,TAU[.
     * 
     * @param rad
     *            The angle to be normalized
     * @return the normalized angle
     */
    public static double normalizePositive(double rad) {
        return RightOpenInterval.of(0.0, TAU).reduce(rad);
    }

    /**
     * Returns the angle corresponding to the given number of arc seconds, in
     * radians.
     * 
     * @param sec
     *            The number of arc seconds
     * @return the angle in radians
     */
    public static double ofArcsec(double sec) {
        return Math.toRadians(sec / 3600);
    }

    /**
     * Returns the angle corresponding to the given angle deg​° min​′ sec​″, in
     * radians.
     * 
     * @param deg
     *            The given number of degrees
     * @param min
     *            The given number of minutes
     * @param sec
     *            The given number of seconds
     * @return the angle in radians
     * 
     * @throws IllegalArgumentException
     *             if the interval [0,60[ does not contain the number of minutes
     *             or the number of seconds
     */
    public static double ofDMS(int deg, int min, double sec) {
        RightOpenInterval interval = RightOpenInterval.of(0, 60);

        if (interval.contains(min) && interval.contains(sec)) {
            double deg1 = (double) deg;
            double min1 = (double) min;
            double sec1 = (double) sec;

            return Math.toRadians(deg1 + min1 / 60 + sec1 / 3600);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the angle corresponding to the given angle in degrees, in
     * radians.
     * 
     * @param deg
     *            The given angle in degrees
     * @return the angle in radians
     */
    public static double ofDeg(double deg) {
        return Math.toRadians(deg);
    }

    /**
     * Returns the angle corresponding to the given angle in radians, in
     * degrees.
     * 
     * @param rad
     *            The given angle in radians
     * @return the angle in degrees
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad);
    }

    /**
     * Returns the angle corresponding to the given angle in hours, in radians.
     * 
     * @param hr
     *            The given angle in hours
     * @return the angle in radians
     */
    public static double ofHr(double hr) {
        return hr * RAD_PER_HOUR;
    }

    /**
     * Returns the angle corresponding to the given angle in radians, in hours.
     * 
     * @param rad
     *            The given angle in radians
     * @return the angle in hours
     */
    public static double toHr(double rad) {
        return rad * HOUR_PER_RAD;
    }

}
