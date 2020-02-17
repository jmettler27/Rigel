package ch.epfl.rigel.math;

public abstract class Interval {

    private final double lowBound;
    private final double highBound;

    protected Interval(double lowBound, double highBound) {
        this.lowBound = lowBound;
        this.highBound = highBound;
    }

    public double low() {
        return lowBound;
    }

    public double high() {
        return highBound;
    }

    public double size() {
        return highBound - lowBound;
    }

    public abstract boolean contains(double v);

    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    public final boolean equals() {
        throw new UnsupportedOperationException();
    }
}
