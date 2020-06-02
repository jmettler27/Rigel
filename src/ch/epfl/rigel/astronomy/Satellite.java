package ch.epfl.rigel.astronomy;

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
     * @param lonRad
     *            The satellite's longitude (in radians)
     */
    public Satellite(String name, String country, String purpose, double lonRad) {
        super(name, EquatorialCoordinates.of(lonRad, 0), 0, 0);
        this.country = Objects.requireNonNull(country);
        this.purpose = Objects.requireNonNull(purpose);
    }
    
    /**
     * @see CelestialObject#info()
     */
    @Override
    public String info() {
        return String.format("%s (%s, %s)", name(), purpose, country);
    }
}
