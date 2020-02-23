package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * Ecliptic coordinates.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class EclipticCoordinates extends SphericalCoordinates {

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
        return new EclipticCoordinates(lon, lat);
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
