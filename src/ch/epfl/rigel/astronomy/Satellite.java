package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.Objects;

/**
 * A celestial object.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Satellite {

    private final String name, country, purpose;
    private final int noradID;
    private final EquatorialCoordinates equatorialPos;

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


    /**
     * Constructs a celestial object with the given name, country, purpose, NORAD identfication number and longitude of
     * geosynchronous orbit.
     *
     * @param name
     *            The object's name
     * @param country
     *            The object's name
     * @param purpose
     *            The object's name
     * @param noradID
     *            The object's name
     * @param lon
     *            The object's name
     */
    public Satellite(String name, String country, String purpose, int noradID, double lon) {
        this.name = Objects.requireNonNull(name);
        this.country = Objects.requireNonNull(country);
        this.purpose = Objects.requireNonNull(purpose);
        Preconditions.checkArgument(noradID >= 0);
        this.noradID = noradID;
        this.equatorialPos = EquatorialCoordinates.of(lon, Angle.ofDeg(60));
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String name() {
        return name;
    }


    public String country() {
        return country;
    }

    public String purpose() {
        return purpose;
    }

    public int noradID() {
        return noradID;
    }

    /**
     * Returns the geographic position.
     * @return the geographic position
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * Returns an informative text about the object.
     * @return an informative text about the object
     */
    public String info() {
        return String.format("%s (%s, %s)", name, purpose, country);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public final String toString() {
        return info();
    }
}
