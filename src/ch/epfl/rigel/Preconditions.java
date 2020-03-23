package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * The conditions that must be satisfied by the arguments of the method before the latter is called.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Preconditions {

    /**
     * Default constructor.
     */
    private Preconditions() {
    }

    /**
     * Checks if the condition (argument) is satisfied.
     * 
     * @param isTrue
     *            The condition
     * 
     * @throws IllegalArgumentException
     *             if the condition is false
     */
    public static void checkArgument(boolean isTrue) {
        if (!isTrue) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks if the given value is contained in the given interval.
     * 
     * @param interval
     *            The given interval
     * @param value
     *            The given value
     * @return The given value contained in the given interval
     * 
     * @throws IllegalArgumentException
     *             if the given interval does not contain the given value
     */
    public static double checkInInterval(Interval interval, double value) {
        if (!interval.contains(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }
}
