package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * The conditions that must be satisfied by the arguments of a method before the latter is called.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Preconditions {

    /**
     * Default constructor.
     */
    private Preconditions() {}

    /**
     * Checks if the given condition is satisfied.
     * 
     * @param isTrue
     *            The condition
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
     * @throws IllegalArgumentException
     *             if the given interval does not contain the given value
     * @return The given value if contained in the given interval
     */
    public static double checkInInterval(Interval interval, double value) {
        if (!interval.contains(value)) {
            throw new IllegalArgumentException("The given interval does not contain the given value");
        }
        return value;
    }
}
