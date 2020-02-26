package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A change of coordinate system from ecliptic to equatorial coordinates at a
 * given epoch.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class EclipticToEquatorialConversion
        implements Function<EclipticCoordinates, EquatorialCoordinates> {

    // The obliquity of the ecliptic, i.e. the angle of inclination of the
    // Earth's axis of rotation relative to the ecliptic.
    // 23.5°
    private final double obliquity;

    /**
     * Constructs a change of coordinate system between ecliptic and equatorial
     * coordinates for the given date/time pair.
     *
     * @param when The given date/time pair
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {

        // The number of Julian centuries elapsed since January 1st, 2000 at
        // 12h00 UTC.
        double T = Epoch.J2000.julianCenturiesUntil(when);

        // The coefficients of the obliquity's polynomial
        double coeff0 = Angle.ofDMS(0, 0, 0.00181);
        double coeff1 = -Angle.ofDMS(0, 0, 0.0006);
        double coeff2 = -Angle.ofDMS(0, 0, 46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);
        obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(T);
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double lambda = ecl.lon();
        double beta = ecl.lat();

        // The numerator
        double num = sin(lambda) * cos(obliquity) - tan(beta) * sin(obliquity);

        // The denominator
        double denom = cos(lambda);

        // The first equatorial coordinate (the right ascension)
        double alpha = atan2(num, denom);

        double tempDelta = sin(beta) * cos(obliquity)
                + cos(beta) * sin(obliquity) * sin(lambda);

        // The second equatorial coordinate (the declination)
        double delta = asin(tempDelta);

        // The equatorial coords corresponding to the given ecliptic coords
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
