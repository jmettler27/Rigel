package ch.epfl.rigel.math;

/**
 * An interval.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public abstract class Interval {

    private final double lowBound, highBound;

    /**
     * Constructs an interval with the given low bound and high bound.
     * 
     * @param lowBound
     *            The low bound of this interval
     * @param highBound
     *            The high bound of this interval
     */
    protected Interval(double lowBound, double highBound) {
        this.lowBound = lowBound;
        this.highBound = highBound;
    }

    /**
     * Returns the low bound of this interval.
     * @return the low bound of this interval
     */
    public double low() {
        return lowBound;
    }

    /**
     * Returns the high bound of this interval.
     * @return the high bound of this interval
     */
    public double high() {
        return highBound;
    }

    /**
     * Returns the size of this interval.
     * @return the size of this interval
     */
    public double size() {
        return highBound - lowBound;
    }

    /**
     * Returns true if this interval contains the value v.
     * 
     * @param v
     *            The value
     * @return true if this interval contains the value v
     */
    public abstract boolean contains(double v);

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
