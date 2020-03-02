package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * A planet.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class Planet extends CelestialObject {

    /**
     * Constructs a planet with the given name, equatorial position, angular
     * size and magnitude.
     * 
     * @param name
     *            The object's name
     * @param equatorialPos
     *            The object's equatorial positions
     * @param angularSize
     *            The object's angular size
     * @param magnitude
     *            The object's magnitude
     * 
     * @throws IllegalArgumentException
     *             if the angular size is strictly negative
     * @throws NullPointerException
     *             if the name of the equatorial position are null
     */
    public Planet(String name, EquatorialCoordinates equatorialPos,
            float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }
}
