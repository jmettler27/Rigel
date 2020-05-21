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
public final class Satellite extends CelestialObject {

    private final String country, purpose;
    private final int noradID;

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
        super(name, EquatorialCoordinates.of(lon, Angle.ofDeg(60)), 0, 0);
        this.country = Objects.requireNonNull(country);
        this.purpose = Objects.requireNonNull(purpose);
        Preconditions.checkArgument(noradID >= 0);
        this.noradID = noradID;
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


}
