package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * The methods and constants allowing to work on angles.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Angle {

    public final static double
            TAU = 2 * Math.PI, // 2*PI
            RAD_PER_HOUR = TAU / 24.0, // Conversion from hours to radians
            HOUR_PER_RAD = 24.0 / TAU; // Conversion from radians to hours

    private final static RightOpenInterval DMS_INTERVAL = RightOpenInterval.of(0, 60);

    /**
     * Default constructor.
     */
    private Angle() {}

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
     * Returns the angle corresponding to the given number of arc seconds, in radians.
     *
     * @param sec
     *            The number of arc seconds
     * @return the angle in radians
     */
    public static double ofArcsec(double sec) {
        return Math.toRadians(sec / 3600);
    }

    /**
     * Returns the angle corresponding to the given angle deg​° min​′ sec​″, in radians.
     *
     * @param deg
     *            The given number of degrees
     * @param min
     *            The given number of minutes
     * @param sec
     *            The given number of seconds
     * @throws IllegalArgumentException
     *             if the interval [0,60[ does not contain the given number of minutes and/or seconds
     * @return the angle in radians
     */
    public static double ofDMS(int deg, int min, double sec) {
        double correctMin = Preconditions.checkInInterval(DMS_INTERVAL, min);
        double correctSec = Preconditions.checkInInterval(DMS_INTERVAL, sec);

        return Math.toRadians((double) deg + correctMin / 60 + correctSec / 3600);
    }

    /**
     * Returns the angle corresponding to the given angle in degrees, in radians.
     *
     * @param deg
     *            The given angle in degrees
     * @return the angle in radians
     */
    public static double ofDeg(double deg) {
        return Math.toRadians(deg);
    }

    /**
     * Returns the angle corresponding to the given angle in radians, in degrees.
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
