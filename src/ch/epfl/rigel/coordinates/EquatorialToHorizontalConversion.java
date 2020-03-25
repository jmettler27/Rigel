package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A change of coordinate system from equatorial to horizontal coordinates, at a
 * given astronomical epoch and location.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final ZonedDateTime when; // The astronomical epoch of the conversion
    private final GeographicCoordinates where; // The location of the conversion

    private final double cosLat, sinLat; // The cosine and sine of the observer's latitude

    /**
     * Constructs a change of coordinate system between equatorial and horizontal coordinates
     * for the given date/time pair and location.
     *
     * @param when
     *            The given date/time pair
     * @param where
     *            The given location
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        this.when = when;
        this.where = where;

        double latitude = where.lat(); // The observer's latitude (in radians)
        cosLat = cos(latitude);
        sinLat = sin(latitude);
    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {
        double raRad = equ.ra(); // The right ascension (in radians)
        double decRad = equ.dec(); // The declination (in radians)

        // The hour angle. Is equal to 0 if the astronomical object lies due south
        double hourAngle = SiderealTime.local(when, where) - raRad;

        // Derivation of the altitude (The second horizontal coordinate) :
        double tempAltitude = sin(decRad) * sinLat + cos(decRad) * cosLat * cos(hourAngle);
        double altRad = asin(tempAltitude); // The altitude, in its valid range [-PI/2, PI/2]

        // Derivation of the azimuth (the first horizontal coordinate) :
        double numeratorAz = -cos(decRad) * cosLat * sin(hourAngle);
        double denominatorAz = sin(decRad) - sinLat * sin(altRad);

        // The azimuth, normalized in its valid interval [0, 2*PI[
        double azRad = Angle.normalizePositive(atan2(numeratorAz, denominatorAz));

        return HorizontalCoordinates.of(azRad, altRad);
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
