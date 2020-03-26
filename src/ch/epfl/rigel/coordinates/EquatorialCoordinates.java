package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Equatorial coordinates.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    // The valid right open interval [0, 2*PI[ (in radians) for the right ascension
    private final static RightOpenInterval RA_INTERVAL_RAD = RightOpenInterval.of(0, Angle.TAU);

    // The valid closed interval [-PI/2, PI/2] (in radians) for the declination
    private final static ClosedInterval DEC_INTERVAL_RAD = ClosedInterval.of(Angle.ofDeg(-90), Angle.ofDeg(90));

    /**
     * Constructs equatorial coordinates (in radians) with the given right ascension and declination (in radians).
     *
     * @param ra
     *            The right ascension (in radians)
     * @param dec
     *            The declination (in radians)
     */
    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }

    /**
     * Returns the equatorial coordinates (in radians) with the given right ascension and declination (in radians).
     *
     * @param ra
     *            The right ascension (in radians)
     * @param dec
     *            The declination (in radians)
     * @throws IllegalArgumentException
     *             if at least one of the coordinates is not contained in its valid interval
     * @return the equatorial coordinates (right ascension and declination) in radians
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        double validRaRad = Preconditions.checkInInterval(RA_INTERVAL_RAD, ra);
        double validDecRad = Preconditions.checkInInterval(DEC_INTERVAL_RAD, dec);

        return new EquatorialCoordinates(validRaRad, validDecRad);
    }

    /**
     * Returns the right ascension, in radians.
     * @return the right ascension, in radians
     */
    public double ra() {
        return lon();
    }

    /**
     * Returns the right ascension, in degrees.
     * @return the right ascension, in degrees
     */
    public double raDeg() {
        return lonDeg();
    }

    /**
     * Returns the right ascension, in hours.
     * @return the right ascension, in hours
     */
    public double raHr() {
        return Angle.toHr(lon());
    }

    /**
     * Returns the declination, in radians.
     * @return the declination, in radians
     */
    public double dec() {
        return lat();
    }

    /**
     * Returns the declination, in degrees.
     * @return the declination, in degrees
     */
    public double decDeg() {
        return latDeg();
    }

    /**
     * @see SphericalCoordinates#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
}
