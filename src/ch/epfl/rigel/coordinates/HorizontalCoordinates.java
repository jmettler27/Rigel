package ch.epfl.rigel.coordinates;

import java.util.Locale;

import static java.lang.Math.acos;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

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

   
    // The valid right open interval (in degrees) for the azimuth
    public final static RightOpenInterval AZ_INTERVAL_DEG = RightOpenInterval
            .of(0, 360);

    // The valid right open interval (in radians) for the azimuth
    public final static RightOpenInterval AZ_INTERVAL_RAD = RightOpenInterval
            .of(0, Angle.ofDeg(360));

    // The valid closed interval (in degrees) for the altitude
    public final static ClosedInterval ALT_INTERVAL_DEG = ClosedInterval.of(-90,
            90);

    // The valid closed interval (in radians) for the altitude
    public final static ClosedInterval ALT_INTERVAL_RAD = ClosedInterval
            .of(Angle.ofDeg(-90), Angle.ofDeg(90));

    // The altitude (in degrees) of the zenith, i.e. the point located
    // vertically above the observer
    public final static double ZENITH_ALTITUDE_DEG = 90;

    // The north octant, represented by a right open interval of size 45.0 and
    // centered in 0
    public final static RightOpenInterval NORTH_INTERVAL = RightOpenInterval
            .symmetric(45.0);

    /**
     * Constructs horizontal coordinates with the given azimuth and altitude.
     *
     * @param az
     *            The azimuth
     * @param alt
     *            The altitude
     */
    public HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * Returns the horizontal coordinates (in radians) with the given azimuth
     * and altitude (in degrees).
     *
     * @param azDeg
     *            The azimuth, in degrees
     * @param altDeg
     *            The altitude, in degrees
     * @return the horizontal coordinates (azimuth and altitude) in radians
     * @throws IllegalArgumentException
     *             if at least one of the coordinates is not contained in its
     *             valid interval
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {

        if (AZ_INTERVAL_DEG.contains(azDeg)
                && ALT_INTERVAL_DEG.contains(altDeg)) {
            return new HorizontalCoordinates(Angle.ofDeg(azDeg),
                    Angle.ofDeg(altDeg));
        } else {
            throw new IllegalArgumentException(
                    "The azimuth must be contained in " + AZ_INTERVAL_DEG
                            + " and the altitude must be contained in "
                            + ALT_INTERVAL_DEG + ".");
        }
    }

    /**
     * Returns the horizontal coordinates (in radians) with the given azimuth
     * and altitude (in radians).
     *
     * @param az
     *            The azimuth, in radians
     * @param alt
     *            The altitude, in radians
     * @return the horizontal coordinates (azimuth and altitude) in radians
     * @throws IllegalArgumentException
     *             if at least one of the coordinates is not contained in its
     *             valid interval
     */
    public static HorizontalCoordinates of(double az, double alt) {

        if (AZ_INTERVAL_RAD.contains(az) && ALT_INTERVAL_RAD.contains(alt)) {
            return new HorizontalCoordinates(az, alt);
        } else {
            throw new IllegalArgumentException(
                    "The azimuth must be contained in " + AZ_INTERVAL_RAD
                            + " and the altitude must be contained in "
                            + ALT_INTERVAL_RAD + ".");
        }
    }

    /**
     * Returns the azimuth, in radians.
     *
     * @return the azimuth, in radians
     */
    public double az() {
        return lon();
    }

    /**
     * Returns the azimuth, in degrees.
     *
     * @return the azimuth, in degrees
     */
    public double azDeg() {
        return lonDeg();
    }

    /**
     * Displays the octant in which the azimuth of the receiver is located, with
     * the four cardinal points (north, east, south and west).
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

        // North
        if (NORTH_INTERVAL.contains(azDeg())) {
            return n;
        }

        // North-East
        else if (centeredInterval(45.0).contains(azDeg())) {
            return n + e;
        }

        // East
        else if (centeredInterval(90.0).contains(azDeg())) {
            return e;
        }

        // South-East
        else if (centeredInterval(135.0).contains(azDeg())) {
            return s + e;
        }

        // South
        else if (centeredInterval(180.0).contains(azDeg())) {
            return s;
        }

        // South-West
        else if (centeredInterval(225.0).contains(azDeg())) {
            return s + w;
        }

        // West
        else if (centeredInterval(270.0).contains(azDeg())) {
            return w;
        }

        // North-West
        else {
            return n + w;
        }

    }

    /**
     * Returns the altitude, in radians.
     *
     * @return the altitude, in radians
     */
    public double alt() {
        return lat();
    }

    /**
     * Returns the altitude, in degrees.
     *
     * @return the altitude, in degrees
     */
    public double altDeg() {
        return latDeg();
    }

    /**
     * Returns the angular distance between the receiver (this) and the given
     * point (that).
     *
     * @param that
     *            The given point
     * @return the angular distance (in radians) between the receiver and the
     *         given point
     */
    public double angularDistanceTo(HorizontalCoordinates that) {

        double az1 = this.az();
        double alt1 = this.alt();

        double az2 = that.az();
        double alt2 = that.alt();

        return acos(
                sin(alt1) * sin(alt2) + cos(alt1) * cos(alt2) * cos(az1 - az2));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(),
                altDeg());
    }

    /**
     * Additional method
     *
     * Returns a right open interval containing azimuth values, of size 45.0 and
     * centered in the given azimuth (center);
     *
     * @param center
     *            The given center of the right open interval
     * @return a right open interval centered in the given azimuth
     * @throws IllegalArgumentException
     *             if the interval is not valid
     */
    private RightOpenInterval centeredInterval(double center) {
        final double HALVED_SIZE = 45.0 / 2.0;

        if (AZ_INTERVAL_DEG.contains(center - HALVED_SIZE)
                && AZ_INTERVAL_DEG.contains(center + HALVED_SIZE)) {
            return RightOpenInterval.of(center - HALVED_SIZE,
                    center + HALVED_SIZE);
        } else {
            throw new IllegalArgumentException(
                    "The angle must be contained in " + AZ_INTERVAL_DEG + ".");
        }

    }

}