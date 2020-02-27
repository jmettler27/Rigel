package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Ecliptic coordinates.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    // The valid right open interval [0°, 360°[ (in degrees) for the ecliptic
    // longitude
    public final static RightOpenInterval LON_INTERVAL_DEG = RightOpenInterval
            .of(0, 360);

    // The valid right open interval [0h, 24h[ (in hours) for the ecliptic
    // longitude
    public final static RightOpenInterval LON_INTERVAL_HR = RightOpenInterval
            .of(0, Angle.toHr(Angle.TAU));

    // The valid right open interval [0, 2*PI[ (in radians) for the ecliptic
    // longitude
    public final static RightOpenInterval LON_INTERVAL_RAD = RightOpenInterval
            .of(0, Angle.TAU);

    // The valid closed interval [-90°, 90°] (in degrees) for the ecliptic
    // latitude
    public final static ClosedInterval LAT_INTERVAL_DEG = ClosedInterval.of(-90,
            90);

    // The valid closed interval [-PI/2, PI/2] (in radians) for the ecliptic
    // latitude
    public final static ClosedInterval LAT_INTERVAL_RAD = ClosedInterval
            .of(Angle.ofDeg(-90), Angle.ofDeg(90));

    /**
     * Constructs ecliptic coordinates with the given longitude and latitude.
     * 
     * @param lon
     *            The longitude
     * @param lat
     *            The latitude
     */
    public EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * Returns the ecliptic coordinates (in radians) with the given longitude
     * and latitude (in radians).
     * 
     * @param lon
     *            The longitude, in radians
     * @param lat
     *            The latitude, in radians
     * @return the ecliptic coordinates (longitude and latitude) in radians
     */
    public static EclipticCoordinates of(double lon, double lat) {

        if (LON_INTERVAL_RAD.contains(lon) && LAT_INTERVAL_RAD.contains(lat)) {
            return new EclipticCoordinates(lon, lat);
        } else {
            throw new IllegalArgumentException(
                    "The longitude must be contained in " + LON_INTERVAL_RAD
                            + " and the latitude must be contained in "
                            + LAT_INTERVAL_RAD + ".");
        }
    }

    /**
     * Returns the longitude, in radians.
     * 
     * @return the longitude, in radians
     */
    public double lon() {
        return super.lon();
    }

    /**
     * Returns the longitude, in degrees.
     * 
     * @return the longitude, in degrees
     */
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * Returns the latitude, in radians.
     * 
     * @return the latitude, in radians
     */
    public double lat() {
        return super.lat();
    }

    /**
     * Returns the latitude, in degrees.
     * 
     * @return the latitude, in degrees
     */
    public double latDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(),
                latDeg());
    }

}
