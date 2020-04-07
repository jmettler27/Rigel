package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * Cartesian coordinates system.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class CartesianCoordinates {

    private final double x, y; // The abscissa x and the ordinate y

    /**
     * Constructs Cartesian coordinates with the given abscissa and ordinate.
     *
     * @param x
     *            The abscissa (unitless)
     * @param y
     *            The ordinate (unitless)
     */
    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the Cartesian coordinates with the given abscissa and ordinate.
     *
     * @param x
     *            The abscissa (unitless)
     * @param y
     *            The ordinate (unitless)
     * @return the Cartesian coordinates of abscissa x and ordinate y
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * Returns the abscissa.
     * @return the abscissa
     */
    public double x() {
        return x;
    }

    /**
     * Returns the ordinate.
     * @return the ordinate
     */
    public double y() {
        return y;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x, y);
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
