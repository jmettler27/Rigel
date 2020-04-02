package ch.epfl.rigel.coordinates;

import java.util.Locale;

import static java.lang.Math.*;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Horizontal coordinates.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {

    // The valid right open interval [0°, 360°[ (in degrees) for the azimuth
    private final static RightOpenInterval AZ_INTERVAL_DEG = RightOpenInterval.of(0, 360);

    // The valid right open interval [0, 2*PI[ (in radians) for the azimuth
    private final static RightOpenInterval AZ_INTERVAL_RAD = RightOpenInterval.of(0, Angle.ofDeg(360));

    // The valid closed interval [-90°, 90°] (in degrees) for the altitude
    private final static ClosedInterval ALT_INTERVAL_DEG = ClosedInterval.of(-90, 90);

    // The valid closed interval [-PI/2, PI/2] (in radians) for the altitude
    private final static ClosedInterval ALT_INTERVAL_RAD = ClosedInterval.of(Angle.ofDeg(-90), Angle.ofDeg(90));

    /**
     * Constructs horizontal coordinates (in radians) with the given azimuth and altitude (in radians).
     *
     * @param az
     *            The azimuth (in radians)
     * @param alt
     *            The altitude (in radians)
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * Returns the horizontal coordinates (in radians) with the given azimuth and altitude (in degrees).
     *
     * @param azDeg
     *            The azimuth (in degrees)
     * @param altDeg
     *            The altitude (in degrees)
     * @throws IllegalArgumentException
     *             if at least one of the coordinates is not contained in its valid interval
     * @return the horizontal coordinates (azimuth and altitude) in radians
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        double validAzDeg = Preconditions.checkInInterval(AZ_INTERVAL_DEG, azDeg);
        double validAltDeg = Preconditions.checkInInterval(ALT_INTERVAL_DEG, altDeg);

        return new HorizontalCoordinates(Angle.ofDeg(validAzDeg), Angle.ofDeg(validAltDeg));
    }

    /**
     * Returns the horizontal coordinates (in radians) with the given azimuth and altitude (in radians).
     *
     * @param az
     *            The azimuth (in radians)
     * @param alt
     *            The altitude (in radians)
     * @throws IllegalArgumentException
     *             if at least one of the coordinates is not contained in its valid interval
     * @return the horizontal coordinates (azimuth and altitude) in radians
     */
    public static HorizontalCoordinates of(double az, double alt) {
        double validAzRad = Preconditions.checkInInterval(AZ_INTERVAL_RAD, az);
        double validAltRad = Preconditions.checkInInterval(ALT_INTERVAL_RAD, alt);

        return new HorizontalCoordinates(validAzRad, validAltRad);
    }

    /**
     * Returns the azimuth, in radians.
     * @return the azimuth, in radians
     */
    public double az() {
        return lon();
    }

    /**
     * Returns the azimuth, in degrees.
     * @return the azimuth, in degrees
     */
    public double azDeg() {
        return lonDeg();
    }

    /**
     * Displays the octant in which the azimuth of the receiver is located, with the four cardinal points
     * (north, east, south and west).
     *
     * @param n
     *            The north cardinal point
     * @param e
     *            The east cardinal point
     * @param s
     *            The south cardinal point
     * @param w
     *            The west cardinal point
     * @return the String representation of the octant
     */
    public String azOctantName(String n, String e, String s, String w) {

        // The two ranges for the North octant
        final RightOpenInterval NORTH_INTERVAL_LEFT = RightOpenInterval.of(360 - 45.0 / 2.0, 360.0);
        final RightOpenInterval NORTH_INTERVAL_RIGHT = RightOpenInterval.of(0,45.0 / 2.0);

        // North range
        if (NORTH_INTERVAL_LEFT.contains(azDeg()) || NORTH_INTERVAL_RIGHT.contains(azDeg())) {
            return n;
        }

        // North-East range
        else if (centeredInterval(45.0).contains(azDeg())) {
            return n + e;
        }

        // East range
        else if (centeredInterval(90.0).contains(azDeg())) {
            return e;
        }

        // South-East range
        else if (centeredInterval(135.0).contains(azDeg())) {
            return s + e;
        }

        // South range
        else if (centeredInterval(180.0).contains(azDeg())) {
            return s;
        }

        // South-West range
        else if (centeredInterval(225.0).contains(azDeg())) {
            return s + w;
        }

        // West range
        else if (centeredInterval(270.0).contains(azDeg())) {
            return w;
        }

        // North-West range
        else {
            return n + w;
        }
    }

    /**
     * Returns the altitude, in radians.
     * @return the altitude, in radians
     */
    public double alt() {
        return lat();
    }

    /**
     * Returns the altitude, in degrees.
     * @return the altitude, in degrees
     */
    public double altDeg() {
        return latDeg();
    }

    /**
     * Returns the angular distance between the receiver (this) and the given point (that).
     *
     * @param that
     *            The given point
     * @return the angular distance (in radians) between the receiver and the given point
     */
    public double angularDistanceTo(HorizontalCoordinates that) {

        // The receiver's horizontal coordinates
        double az1 = this.az();
        double alt1 = this.alt();

        // The given point's horizontal coordinates
        double az2 = that.az();
        double alt2 = that.alt();

        return acos(sin(alt1) * sin(alt2) + cos(alt1) * cos(alt2) * cos(az1 - az2));
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }

    /**
     * Additional method.
     *
     * Returns a right open interval containing azimuth values (in degrees), of size 45.0 and centered in
     * the given azimuth (center).
     *
     * @param center
     *            The given center of the right open interval (in degrees)
     * @throws IllegalArgumentException
     *             if the interval is not valid
     * @return a right open interval centered in the given azimuth (in degrees)
     */
    private RightOpenInterval centeredInterval(double center) {
        final double HALVED_SIZE = 45.0 / 2.0;

        double interval1Deg = Preconditions.checkInInterval(AZ_INTERVAL_DEG, center - HALVED_SIZE);
        double interval2Deg = Preconditions.checkInInterval(AZ_INTERVAL_DEG, center + HALVED_SIZE);

        return RightOpenInterval.of(interval1Deg,interval2Deg);
    }
}
