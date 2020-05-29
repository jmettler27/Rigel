package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;

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

    // The angle (in radians) between two opposite points on the periphery of the "disc" (i.e. the object) as seen
    // by the observer
    private final float angularSize;

    // The apparent magnitude (unitless), i.e. the luminosity of the object as perceived from the Earth
    private final float magnitude;

    // The diameter of the disc representing this celestial object, according to its magnitude
    private final double discSize;

    private final boolean isBright;

    // Used to clip this celestial object's magnitude to [-2,5]
    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);

    // The diameter of the disc of a celestial object with an angular size of 0.5 degrees.
    private static final double DISC_SIZE_HALF_DEGREE = 2.0 * Math.tan(Angle.ofDeg(0.5) / 4.0);

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
     *             if the angular size is < 0
     * @throws NullPointerException
     *             if the name and/or the equatorial position are null
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);

        Preconditions.checkArgument(angularSize >= 0);
        this.angularSize = angularSize;

        this.magnitude = magnitude;
        this.isBright = magnitude < 1.0;

        // The magnitude is clipped to [-2, 5]
        double clippedMagnitude = MAGNITUDE_INTERVAL.clip(magnitude);

        // The size factor (between 10% and 95% of the diameter of an object whose angular size is 0.5 degrees)
        double sizeFactor = (99.0 - 17.0 * clippedMagnitude) / 140.0;

        this.discSize = sizeFactor * DISC_SIZE_HALF_DEGREE;
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
     * Additional method.
     * Returns the size of the disc corresponding to this celestial object
     * @return the size of the disc corresponding to this celestial object
     */
    public double discSize() {
        return discSize;
    }

    /**
     * Additional method (bonus).
     * Tells if the celestial object is bright, i.e. if its magnitude is < 2.0.
     * @return true if the celestial object is bright, false otherwise
     */
    public boolean isBright() {
        return isBright;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public final String toString() {
        return info();
    }
}
