package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * A planet of the solar system.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Planet extends CelestialObject {

    /**
     * Constructs a planet with the given name, equatorial position, angular size and magnitude.
     * 
     * @param name
     *            The planet's name
     * @param equatorialPos
     *            The planet's equatorial positions
     * @param angularSize
     *            The planet's angular size (in radians)
     * @param magnitude
     *            The object's magnitude (unitless)
     * @throws IllegalArgumentException
     *             if the angular size is < 0
     * @throws NullPointerException
     *             if the name and/or the equatorial position are null
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }
}
