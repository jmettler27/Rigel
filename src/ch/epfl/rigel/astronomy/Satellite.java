package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * A satellite in geostationary orbit around Earth.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Satellite extends CelestialObject {

    private final String country, purpose;
    private final int noradID;

    /**
     * Constructs a satellite with the given name, country, purpose, NORAD identification number and longitude of
     * geosynchronous orbit.
     *
     * @param name
     *            The object's name
     * @param country
     *            The satellite's country of origin
     * @param purpose
     *            The satellite's purpose
     * @param noradID
     *            The satellite's NORAD identification number
     * @param lonRad
     *            The satellite's longitude (in radians)
     */
    public Satellite(String name, String country, String purpose, int noradID, double lonRad) {
        super(name, EquatorialCoordinates.of(lonRad, 0), 0, 0);
        this.country = Objects.requireNonNull(country);
        this.purpose = Objects.requireNonNull(purpose);
        Preconditions.checkArgument(noradID >= 0);
        this.noradID = noradID;
    }

    /**
     * Returns the country of origin of the satellite.
     * @return the country of origin of the satellite
     */
    public String country() {
        return country;
    }

    /**
     * Returns the purpose of the satellite.
     * @return the purpose of the satellite
     */
    public String purpose() {
        return purpose;
    }

    /**
     * Returns the NORAD identification number of the satellite.
     * @return the NORAD identification number of the satellite
     */
    public int noradID() {
        return noradID;
    }
}
