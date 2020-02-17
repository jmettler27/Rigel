package ch.epfl.rigel.math;

import java.util.Locale;

public final class RightOpenInterval extends Interval {

    private RightOpenInterval(double lowBound, double highBound) {
        super(lowBound, highBound);
    }

    @Override
    public boolean contains(double v) {
        return (v >= low() && v < high());
    }

    public double reduce(double v) {
        return low() + floorMod(v - low(), high() - low());
    }

    private double floorMod(double x, double y) {
        return (x - y * Math.floor(x / y));
    }

    public static RightOpenInterval of(double low, double high) {
        if (low < high) {
            return new RightOpenInterval(low, high);
        } else {
            throw new IllegalArgumentException("low bound < high bound");
        }
    }

    public static RightOpenInterval symmetric(double size) {
        if (size > 0) {
            return new RightOpenInterval(-size / 2.0, size / 2.0);
        } else {
            throw new IllegalArgumentException("size <= 0");
        }
    }

    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s[", low(), high());
    }

}
