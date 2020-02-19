package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Spherical coordinates.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
abstract class SphericalCoordinates {

    private double longitude;
    private double latitude;

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
     * Return the longitude, in degrees.
     * 
     * @return the longitude, in degrees
     */
    double lonDeg() {
        return Angle.toDeg(longitude);
    }

    /**
     * Returns the latitude, in radians.
     * 
     * @return the latitude, in radians
     */
    double lat() {
        return latitude;
    }

    /**
     * Returns the latitude, in degrees.
     * 
     * @return the latitude, in degrees
     */
    double latDeg() {
        return Angle.toDeg(latitude);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

}
