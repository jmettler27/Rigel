package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * Cartesian coordinates.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class CartesianCoordinates {

    // The abscissa x and the ordinate y
    private final double x, y;

    /**
     * Constructs Cartesian coordinates with the given abscissa and ordinate.
     * 
     * @param x
     *            The abscissa
     * @param y
     *            The ordinate
     */
    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the Cartesian coordinates with the given abscissa and ordinate
     * 
     * @param x
     *            The abscissa, unitless
     * @param y
     *            The ordinate, unitless
     * @return
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * Returns the abscissa, unitless.
     * 
     * @return the abscissa, unitless
     */
    public double x() {
        return x;
    }

    /**
     * Returns the ordinate, unitless.
     * 
     * @return the ordinate, unitless
     */
    public double y() {
        return y;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f°, y=%.4f°)", x, y);
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
