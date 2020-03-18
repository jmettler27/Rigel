package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A change of coordinate system from ecliptic to equatorial coordinates, at a
 * given astronomical epoch.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final double cosEpsilon, sinEpsilon;

    /**
     * Constructs a change of coordinate system between ecliptic and equatorial
     * coordinates for the given date/time pair.
     *
     * @param when The given date/time pair
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        // The number of Julian centuries elapsed since January 1st, 2000 at 12h00 UTC.
        double T = Epoch.J2000.julianCenturiesUntil(when);

        // The coefficients of the obliquity's polynomial
        double coeff0 = Angle.ofArcsec(0.00181);
        double coeff1 = -Angle.ofArcsec(0.0006);
        double coeff2 = -Angle.ofArcsec(46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);

        // The obliquity of the ecliptic, i.e. the angle of inclination of the
        // Earth's axis of rotation relative to the ecliptic.
        double obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(T);

        cosEpsilon = cos(obliquity);
        sinEpsilon = sin(obliquity);
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double lambda = ecl.lon(); // The ecliptic longitude
        double beta = ecl.lat(); // The ecliptic latitude

        // Derivation of the right ascension (the first equatorial coordinate):
        double y = sin(lambda) * cosEpsilon - tan(beta) * sinEpsilon; // The numerator of the right ascension's formula
        double x = cos(lambda); // The denominator of the right ascension's formula

        // The right ascension, normalized in its valid interval [0, 2*PI[
        double alpha = Angle.normalizePositive(atan2(y, x));

        // Derivation of the declination (the second equatorial coordinate):
        double tempDelta = sin(beta) * cosEpsilon + cos(beta) * sinEpsilon * sin(lambda);

        // The declination
        // Note : The method asin returns an angle in the range [-PI/2, PI/2], which is the valid declination's range
        double delta = asin(tempDelta);

        // The equatorial coordinates corresponding to the given ecliptic coordinates
        return EquatorialCoordinates.of(alpha, delta);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
