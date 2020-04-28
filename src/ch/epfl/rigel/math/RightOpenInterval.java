package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * A right open interval.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class RightOpenInterval extends Interval {

    /**
     * Constructs a right open interval with the given low bound and high bound.
     * 
     * @param lowBound
     *            The low bound of this interval
     * @param highBound
     *            The high bound of this interval
     */
    private RightOpenInterval(double lowBound, double highBound) {
        super(lowBound, highBound);
    }

    /**
     * Returns a right open interval with the given low bound and high bound.
     * 
     * @param low
     *            The low bound of the interval
     * @param high
     *            The high bound of the interval
     * @throws IllegalArgumentException
     *             is the low bound is greater than or equal to the high bound
     * @return the constructed right open interval
     */
    public static RightOpenInterval of(double low, double high) {
        Preconditions.checkArgument(low < high);
        return new RightOpenInterval(low, high);
    }

    /**
     * Constructs a right open interval centered in 0.
     * 
     * @param size
     *            The size of the interval
     * @throws IllegalArgumentException
     *             if the size is <= 0
     * @return the constructed right open interval centered in 0
     */
    public static RightOpenInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        return new RightOpenInterval(-size / 2.0, size / 2.0);
    }

    /**
     * @see Interval#contains(double)
     */
    @Override
    public boolean contains(double v) {
        return (v >= low() && v < high());
    }

    /**
     * Brings the given angle to the standard interval [low(),high()[.
     * 
     * @param v
     *            The value to be brought to the interval
     * @return the value of the interval that corresponds to the given value v
     */
    public double reduce(double v) {
        return low() + floorMod(v - low(), high() - low());
    }

    /**
     * Returns the remain of the floor.
     * 
     * @param x
     *            The first value
     * @param y
     *            The second value
     * @return the remain of the floor
     */
    private static double floorMod(double x, double y) {
        return x - y * Math.floor(x / y);
    }

    /**
     * @see Interval#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%f,%f[", low(), high());
    }
}
