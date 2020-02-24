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
 * Conversion from ecliptic to equatorial coordinates.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class EclipticToEquatorialConversion
        implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final ZonedDateTime when; //
    private final double obliquity; //

    /**
     * 
     * @param when
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        this.when = when;
        double T = Epoch.J2000.julianCenturiesUntil(when);

        double coeff0 = Angle.ofDMS(0, 0, 0.00181);
        double coeff1 = Angle.ofDMS(0, 0, -0.0006);
        double coeff2 = Angle.ofDMS(0, 0, -46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);

        obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(T);
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double lambda = ecl.lon();
        double beta = ecl.lat();

        double alpha = 0; // atan2(sin(lambda) * cos(obliquity) - tan(beta) *
                          // sin(obliquity), cos(lambda));
        double delta = asin(sin(beta) * cos(beta)
                + cos(beta) * sin(obliquity) * sin(lambda));

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
