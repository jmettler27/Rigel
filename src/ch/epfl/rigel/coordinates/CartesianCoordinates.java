package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

import static java.lang.Math.hypot;

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
     * Additional method.
     * Checks if this point is contained in a square centered in the search point and whose side is twice
     * the maximum search distance.
     *
     * @param squareCenter
     *            The center of the square, i.e. the search point
     * @param halfSide
     *            The half side of the square, i.e. the maximum search distance
     * @return true if this point is contained in the square
     */
    public boolean isContainedInSquare(CartesianCoordinates squareCenter, double halfSide) {
        ClosedInterval xSide = ClosedInterval.of(squareCenter.x() - halfSide, squareCenter.x() + halfSide);
        ClosedInterval ySide = ClosedInterval.of(squareCenter.y() - halfSide, squareCenter.y() + halfSide);
        return (xSide.contains(this.x()) && ySide.contains(this.y()));
    }

    /**
     * Additional method.
     * Derives the distance between this point and the given point on the plane.
     *
     * @param that
     *            The Cartesian coordinates of the other point on the plane
     * @return the distance between this point and the given point on the plane
     */
    public double distanceTo(CartesianCoordinates that) {
        return hypot(x - that.x(), y - that.y());
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
