package ch.epfl.rigel.math;

import java.util.Locale;

public final class ClosedInterval extends Interval {

    private ClosedInterval(double lowBound, double highBound) {
        super(lowBound, highBound);
    }

    @Override
    public boolean contains(double v) {
        return (v >= low() && v <= high());
    }

    public static ClosedInterval of(double low, double high) {
        if (low < high) {
            return new ClosedInterval(low, high);
        } else {
            throw new IllegalArgumentException("low bound < high bound");
        }
    }

    public static ClosedInterval symmetric(double size) {
        if (size > 0) {
            return new ClosedInterval(-size / 2.0, size / 2.0);
        } else {
            throw new IllegalArgumentException("size <= 0");
        }
    }

    public double clip(double v) {
        if (v <= low()) {
            return low();
        } else if (v >= high()) {
            return high();
        } else {
            return v;
        }
    }

    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s]", low(), high());
    }

}
