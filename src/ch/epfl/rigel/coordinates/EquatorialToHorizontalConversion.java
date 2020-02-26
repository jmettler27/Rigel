package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A change of coordinate system from equatorial to horizontal coordinates at a
 * given epoch and location.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class EquatorialToHorizontalConversion
        implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final ZonedDateTime when; // The given epoch
    private final GeographicCoordinates where; // The given location

    private final double phi; // The observer's latitude

    /**
     * Constructs a change of coordinate system between equatorial and
     * horizontal coordinates for the given date/time pair and location.
     *
     * @param when
     *            The given date/time pair
     * @param where
     *            The given location
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when,
            GeographicCoordinates where) {
        this.when = when;
        this.where = where;
        phi = where.lat(); // The given location's latitude
    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {
        double alpha = equ.ra();
        double delta = equ.dec();

        // The hour angle (sidereal time - right ascension). Is equal to 0 if
        // the astronomical object lies due south
        double H = SiderealTime.local(when, where) - alpha; // hour angle

        double tempAltitude = sin(delta) * sin(phi)
                + cos(delta) * cos(phi) * cos(H);

        // The second horizontal coordinate, the altitude
        double h = asin(tempAltitude);

        double numerator = -cos(delta) * cos(phi) * sin(H);
        double denominator = sin(delta) - sin(phi) * sin(h);

        // The first horizontal coordinate, the azimuth
        double A = atan2(numerator, denominator);

        return HorizontalCoordinates.of(A, h);
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
