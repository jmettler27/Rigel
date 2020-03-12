package ch.epfl.rigel.astronomy;

import java.util.List;

/**
 * An asterism, i.e. a figure drawn by a group of particularly bright stars.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public final class Asterism {

    private final List<Star> stars;

    /**
     * Constructs an asterism with the list of stars composing it.
     * 
     * @param stars
     *            The list of stars composing this asterism
     * 
     * @throws IllegalArgumentException
     *             if the list contains no stars
     */
    public Asterism(List<Star> stars) {
        if (stars.isEmpty()) { throw new IllegalArgumentException("The list of stars is empty.");}
        this.stars = List.copyOf(stars); // Immutable list
    }

    /**
     * Returns the list of stars.
     * 
     * @return the list of stars
     */
    public List<Star> stars() {
        return List.copyOf(stars);
    }

}