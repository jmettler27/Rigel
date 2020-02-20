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
 * 
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

    // The north octant
    public final static RightOpenInterval N = RightOpenInterval.symmetric(45.0);

    /**
     * Constructs horizontal coordinates with the given azimuth and altitude.
     * 
     * @param az
     *            The azimuth
     * 
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
     * 
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
     * 
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
     * Return the azimuth, in degrees.
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
     * 
     * @return the String representation of the octant
     */
    public String azOctantName(String n, String e, String s, String w) {

        StringBuilder octantName = new StringBuilder("");

        // North and North-Est coordinates (strings which begin with "N")
        if (RightOpenInterval.of(315, 360).contains(azDeg())
                || RightOpenInterval.of(0, 45).contains(azDeg())) {
            octantName.append('N');

            if (RightOpenInterval.of(0, 45).contains(azDeg())) {
                octantName.append('E');
            }

            else {
                octantName.append('O');
            }

        }
        return octantName.toString();
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
     * @return the angular distance between the receiver and the given point
     */
    public double angularDistanceTo(HorizontalCoordinates that) {

        double lambda1 = this.az();
        double phi1 = this.alt();

        double lambda2 = that.az();
        double phi2 = that.alt();

        return acos(sin(phi1) * sin(phi2)
                + cos(phi1) * cos(phi2) * cos(lambda1 - lambda2));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(),
                altDeg());
    }

}
