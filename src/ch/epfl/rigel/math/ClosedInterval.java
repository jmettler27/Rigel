package ch.epfl.rigel.math;

import java.util.Locale;

/**
 * A closed interval.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class ClosedInterval extends Interval {

    /**
     * Constructs a closed interval with the given low bound and high bound.
     * 
     * @param lowBound
     *            The low bound of this interval
     * @param highBound
     *            The high bound of this interval
     */
    private ClosedInterval(double lowBound, double highBound) {
        super(lowBound, highBound);
    }

    /**
     * Returns a closed interval with the given low bound and high bound.
     * 
     * @param low
     *            The low bound of the interval
     * @param high
     *            The high bound of the interval
     * @return the constructed closed interval
     * 
     * @throws IllegalArgumentException
     *             if the low bound is not strictly smaller than the high bound
     */
    public static ClosedInterval of(double low, double high) {
        if (low < high) {
            return new ClosedInterval(low, high);
        } else {
            throw new IllegalArgumentException("low bound >= high bound");
        }
    }

    /**
     * Constructs a closed interval centered in 0.
     * 
     * @param size
     *            The size of the interval
     * @return the constructed closed interval
     * 
     * @throws IllegalArgumentException
     *             if the size is not strictly positive
     */
    public static ClosedInterval symmetric(double size) {
        if (size > 0.0) {
            return new ClosedInterval(-size / 2.0, size / 2.0);
        } else {
            throw new IllegalArgumentException("size <= 0");
        }
    }

    @Override
    public boolean contains(double v) {
        return (v >= low() && v <= high());
    }

    /**
     * Clips the value v to the interval [low(); high()].
     * 
     * @param v
     *            The value to be clipped to the interval
     * @return the value of the interval that corresponds to the given value v
     */
    public double clip(double v) {
        if (v <= low()) {
            return low();
        } else if (v >= high()) {
            return high();
        } else {
            return v;
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s]", low(), high());
    }

}
