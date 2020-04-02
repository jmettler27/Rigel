package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * A change of coordinate system from equatorial to Cartesian coordinates, at a given astronomical epoch and location.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class EquatorialToCartesianConversion implements Function<EquatorialCoordinates, CartesianCoordinates> {

    private final EquatorialToHorizontalConversion equToHor;
    private final StereographicProjection projection;

    /**
     * Constructs a change of coordinate system between equatorial and Cartesian coordinates for the given
     * date/time pair and location.
     *
     * @param when
     *            The given date/time pair
     * @param where
     *            The given location
     */
    public EquatorialToCartesianConversion(ZonedDateTime when, GeographicCoordinates where,
                                           StereographicProjection projection){
        equToHor = new EquatorialToHorizontalConversion(when, where); // Conversion from equatorial to horizontal coordinates
        this.projection = projection;
    }

    /**
     * @see Function#apply(Object)
     */
    @Override
    public CartesianCoordinates apply(EquatorialCoordinates equ) {
        // Converts equatorial to horizontal coordinates, and then projects the horizontal coordinates on the plan
        return equToHor.andThen(projection).apply(equ);
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
