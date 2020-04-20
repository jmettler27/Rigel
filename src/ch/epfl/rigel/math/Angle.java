package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * The methods and constants allowing to work on angles.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Angle {

    public static final double TAU = 2.0 * Math.PI; // The perimeter of the trigonometric circle

    private static final double
            RAD_PER_HOUR = TAU / 24.0, // Conversion from hours to radians
            HOUR_PER_RAD = 24.0 / TAU; // Conversion from radians to hours

    // The valid right open interval [0,60[ for the number of minutes and seconds
    private static final RightOpenInterval DMS_INTERVAL = RightOpenInterval.of(0, 60);

    /**
     * Default constructor.
     */
    private Angle() {}

    /**
     * Normalizes the given angle (in radians) by reducing it to the interval [0,2*PI[.
     *
     * @param rad
     *            The angle to be normalized
     * @return the normalized angle
     */
    public static double normalizePositive(double rad) {
        return RightOpenInterval.of(0, TAU).reduce(rad);
    }

    /**
     * Returns the angle (in radians) corresponding to the given number of arc seconds.
     *
     * @param sec
     *            The number of arc seconds
     * @return the angle in radians
     */
    public static double ofArcsec(double sec) {
        return Math.toRadians(sec / 3600.0);
    }

    /**
     * Returns the angle (in radians) corresponding to the given angle deg° min′ sec″.
     *
     * @param deg
     *            The number of degrees
     * @param min
     *            The number of minutes
     * @param sec
     *            The number of seconds
     * @throws IllegalArgumentException
     *             if the number of minutes and/or seconds is not contained in its valid interval
     * @return the angle in radians
     */
    public static double ofDMS(int deg, int min, double sec) {
        Preconditions.checkArgument(deg >= 0);
        double validMin = Preconditions.checkInInterval(DMS_INTERVAL, min);
        double validSec = Preconditions.checkInInterval(DMS_INTERVAL, sec);

        return Math.toRadians((double) deg + validMin / 60.0 + validSec / 3600.0);
    }

    /**
     * Returns the angle (in radians) corresponding to the given angle in degrees.
     *
     * @param deg
     *            The angle in degrees
     * @return the angle in radians
     */
    public static double ofDeg(double deg) {
        return Math.toRadians(deg);
    }

    /**
     * Returns the angle (in degrees) corresponding to the given angle in radians.
     *
     * @param rad
     *            The angle in radians
     * @return the angle in degrees
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad);
    }

    /**
     * Returns the angle (in radians) corresponding to the given angle in hours.
     *
     * @param hr
     *            The angle in hours
     * @return the angle in radians
     */
    public static double ofHr(double hr) {
        return hr * RAD_PER_HOUR;
    }

    /**
     * Returns the angle (in hours) corresponding to the given angle in radians.
     *
     * @param rad
     *            The angle in radians
     * @return the angle in hours
     */
    public static double toHr(double rad) {
        return rad * HOUR_PER_RAD;
    }
}
