package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A change of coordinate system from equatorial to horizontal coordinates, at a given astronomical epoch and location.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double localSiderealTime, cosLat, sinLat;
    /**
     * Constructs a change of coordinate system between equatorial and horizontal coordinates for the given
     * date/time pair and location.
     *
     * @param when
     *            The astronomical epoch of the conversion
     * @param where
     *            The location of the conversion
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        this.localSiderealTime = SiderealTime.local(when, where);

        double latitude = where.lat(); // The observer's latitude (in radians)
        cosLat = cos(latitude);
        sinLat = sin(latitude);
    }

    /**
     * @see Function#apply(Object)
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {
        double raRad = equ.ra(); // The right ascension (in radians)
        double decRad = equ.dec(); // The declination (in radians)
        double cosDecRad = cos(decRad), sinDecRad = sin(decRad);

        // The hour angle (in radians)
        double hourAngle = localSiderealTime - raRad;

        // Calculation of the altitude (second horizontal coordinate, in radians, in its valid interval [-PI/2, PI/2])
        double tempAlt = sinDecRad * sinLat + cosDecRad * cosLat * cos(hourAngle);
        double altRad = asin(tempAlt);

        // Calculation of the azimuth (first horizontal coordinate)
        double numeratorAz = -cosDecRad * cosLat * sin(hourAngle);
        double denominatorAz = sinDecRad - sinLat * tempAlt;
        double azRad = Angle.normalizePositive(atan2(numeratorAz, denominatorAz));

        return HorizontalCoordinates.of(azRad, altRad);
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
