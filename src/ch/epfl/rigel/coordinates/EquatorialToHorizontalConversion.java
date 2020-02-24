package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * Conversion from equatorial to horizontal coordinates.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class EquatorialToHorizontalConversion
        implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private ZonedDateTime when; //
    private GeographicCoordinates where;
    private double phi;

    /**
     * Constructs a system of coordinates
     * 
     * @param when
     * @param where
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when,
            GeographicCoordinates where) {
        this.when = when;
        this.where = where;
        phi = where.lat();

    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {
        double H = SiderealTime.local(when, where) - equ.ra(); // angle horaire

        double numerator = sin(equ.dec()) - sin(phi) * sin(equ.ra());
        double denominator = cos(phi) * cos(equ.ra());
        double A = acos(numerator / denominator);

        double h = asin(
                sin(equ.dec() * sin(phi) + cos(equ.dec() * cos(phi) * cos(H))));

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
