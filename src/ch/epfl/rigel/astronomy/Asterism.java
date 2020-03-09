package ch.epfl.rigel.astronomy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class Asterism {

    private final List<Star> stars;

    Asterism(List<Star> stars) {
        if (stars.isEmpty()) {
            throw new IllegalArgumentException("The list of stars is empty.");
        }
        this.stars = List.copyOf(stars); // Immutable list
    }

    public List<Star> stars() {
        return List.copyOf(stars);
    }
}
