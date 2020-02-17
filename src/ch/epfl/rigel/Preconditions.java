package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

public final class Preconditions {

    public Preconditions() {
        // TODO Auto-generated constructor stub
    }

    public static void checkArgument(boolean isTrue) {
        if (!isTrue) {
            throw new IllegalArgumentException();
        }
    }

    public static double checkInInterval(Interval interval, double value) {
        return 0;

    }

}
