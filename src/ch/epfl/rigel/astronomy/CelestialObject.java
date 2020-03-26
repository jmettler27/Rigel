package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * A celestial object.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;

    // The angle (in radians) between two opposite points on the periphery of the "disc" (i.e. the object)
    // as seen by the observer
    private final float angularSize;

    // The apparent magnitude (unitless), i.e. the luminosity of the object as perceived from the Earth
    private final float magnitude;

    /**
     * Constructs a celestial object with the given name, equatorial position, angular size and magnitude.
     *
     * @param name
     *            The object's name
     * @param equatorialPos
     *            The object's equatorial positions
     * @param angularSize
     *            The object's angular size (in radians)
     * @param magnitude
     *            The object's magnitude (unitless)
     * @throws IllegalArgumentException
     *             if the angular size is strictly negative
     * @throws NullPointerException
     *             if the name and/or the equatorial position are null
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);

        Preconditions.checkArgument(angularSize >= 0);
        this.angularSize = angularSize;

        this.magnitude = magnitude;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the angular size.
     * @return the angular size
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * Returns the magnitude.
     * @return the magnitude
     */
    public double magnitude() {
        return magnitude;
    }

    /**
     * Returns the equatorial position.
     * @return the equatorial position
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * Returns an informative text about the object.
     * @return an informative text about the object
     */
    public String info() {
        return name();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public final String toString() {
        return info();
    }
}
