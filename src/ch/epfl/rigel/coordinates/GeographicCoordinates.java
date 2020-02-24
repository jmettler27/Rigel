package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Geographic coordinates.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    // The valid right open interval [-180°, 180°] (in degrees) for the
    // longitude
    public final static RightOpenInterval LON_INTERVAL_DEG = RightOpenInterval
            .of(-180, 180);

    // The valid closed interval [-90°, 90°] (in degrees) for the latitude
    public final static ClosedInterval LAT_INTERVAL_DEG = ClosedInterval.of(-90,
            90);

    /**
     * Constructs geographic coordinates with the given longitude and latitude.
     * 
     * @param lon
     *            The longitude
     * 
     * @param lat
     *            The latitude
     */
    private GeographicCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * Returns the geographic coordinates (in radians) with the given longitude
     * and latitude (in degrees).
     * 
     * @param longDeg
     *            The longitude, in degrees
     * @param latDeg
     *            The latitude, in degrees
     * @return the geographic coordinates (longitude and latitude) in radians
     * 
     * @throws IllegalArgumentException
     *             if at least one of the coordinates is not contained in its
     *             valid right open interval
     */
    public static GeographicCoordinates ofDeg(double longDeg, double latDeg) {

        if (isValidLonDeg(longDeg) && isValidLatDeg(latDeg)) {
            return new GeographicCoordinates(Angle.ofDeg(longDeg),
                    Angle.ofDeg(latDeg));
        } else {
            throw new IllegalArgumentException(
                    "The longitude must be contained in " + LON_INTERVAL_DEG
                            + " and the latitude must be contained in "
                            + LAT_INTERVAL_DEG + ".");
        }
    }

    /**
     * Checks if the given longitude (in degrees) is contained in the valid
     * right open interval.
     * 
     * @param lonDeg
     *            The checked longitude
     * @return true if the given longitude is valid
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return LON_INTERVAL_DEG.contains(lonDeg);
    }

    /**
     * Checks if the given latitude (in degrees) is contained in the valid
     * closed interval.
     * 
     * @param latDeg
     *            The checked latitude
     * @return true if the given latitude is valid
     */
    public static boolean isValidLatDeg(double latDeg) {
        return LAT_INTERVAL_DEG.contains(latDeg);
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
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(),
                latDeg());
    }

}
