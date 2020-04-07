package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A change of coordinate system from ecliptic to equatorial coordinates, at a given astronomical epoch.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final double cosObliquity, sinObliquity; // The cosine and sine of the obliquity of the ecliptic

    /**
     * Constructs a change of coordinate system between ecliptic and equatorial coordinates for the given date/time pair.
     *
     * @param when
     *            The given date/time pair
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        // The number of Julian centuries elapsed since January 1st, 2000 at 12h00 UTC.
        double nbJulianCenturies = Epoch.J2000.julianCenturiesUntil(when);

        // Calculation of the obliquity of the ecliptic, i.e. the angle of inclination of the Earth's axis of rotation
        // relative to the ecliptic
        double coeff0 = Angle.ofArcsec(0.00181);
        double coeff1 = -Angle.ofArcsec(0.0006);
        double coeff2 = -Angle.ofArcsec(46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);
        double obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(nbJulianCenturies);

        cosObliquity = cos(obliquity);
        sinObliquity = sin(obliquity);
    }

    /**
     * @see Function#apply(Object)
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double eclipticLon = ecl.lon(); // The ecliptic longitude (in radians)
        double eclipticLat = ecl.lat(); // The ecliptic latitude (in radians)

        // Calculation of the right ascension (first equatorial coordinate, in radians):
        double numeratorRa = sin(eclipticLon) * cosObliquity - tan(eclipticLat) * sinObliquity;
        double denominatorRa = cos(eclipticLon);
        double raRad = Angle.normalizePositive(atan2(numeratorRa, denominatorRa));

        // Calculation of the declination (second equatorial coordinate, in radians, in its valid interval [-PI/2, PI/2])
        double tempDec = sin(eclipticLat) * cosObliquity + cos(eclipticLat) * sinObliquity * sin(eclipticLon);
        double decRad = asin(tempDec);

        // The equatorial coordinates corresponding to the given ecliptic coordinates
        return EquatorialCoordinates.of(raRad, decRad);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
