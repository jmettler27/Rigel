package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Spherical coordinates system.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
abstract class SphericalCoordinates {

    private final double longitude, latitude;

    /**
     * Constructs spherical coordinates with the given longitude and latitude.
     * 
     * @param lon
     *            The longitude
     * @param lat
     *            The latitude
     */
    SphericalCoordinates(double lon, double lat) {
        longitude = lon;
        latitude = lat;
    }

    /**
     * Returns the longitude, in radians.
     * 
     * @return the longitude, in radians
     */
    double lon() {
        return longitude;
    }

    /**
     * Returns the longitude, in degrees.
     * @return the longitude, in degrees
     */
    double lonDeg() {
        return Angle.toDeg(longitude);
    }

    /**
     * Returns the latitude, in radians.
     * @return the latitude, in radians
     */
    double lat() {
        return latitude;
    }

    /**
     * Returns the latitude, in degrees.
     * @return the latitude, in degrees
     */
    double latDeg() {
        return Angle.toDeg(latitude);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
