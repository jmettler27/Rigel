package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * A celestial object.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;

    // The angle (in radians) between two opposite points on the periphery of the "disc"
    // (i.e. the object) as seen by the observer
    private final float angularSize;

    // The apparent magnitude, i.e. the luminosity of the object as perceived
    // from the Earth (unitless)
    private final float magnitude;

    /**
     * Constructs a celestial object with the given name, equatorial position,
     * angular size and magnitude.
     *
     * @param name
     *            The object's name
     * @param equatorialPos
     *            The object's equatorial positions
     * @param angularSize
     *            The object's angular size (in radians)
     * @param magnitude
     *            The object's magnitude (unitless)
     *
     * @throws IllegalArgumentException
     *             if the angular size is strictly negative
     * @throws NullPointerException
     *             if the name or the equatorial position are null
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos,
            float angularSize, float magnitude) {
        if (angularSize < 0) {
            throw new IllegalArgumentException("The angular size must be > 0");
        }
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public final String name() {
        return name;
    }

    /**
     * Returns the angular size.
     *
     * @return the angular size
     */
    public final double angularSize() {
        return angularSize;
    }

    /**
     * Returns the magnitude.
     *
     * @return the magnitude
     */
    public final double magnitude() {
        return magnitude;
    }

    /**
     * Returns the equatorial position.
     *
     * @return the equatorial position
     */
    public final EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * Returns an informative text about the object.
     *
     * @return an informative text about the object
     */
    public String info() {
        return name;
    }

    @Override
    public final String toString() {
        return info();
    }

}
