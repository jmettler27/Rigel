package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyAsterismTest {

    @Test
    void constructionThrowsWhenEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Asterism(new ArrayList<>());
        });
    }


    @Test
    void stars() {
        List<Star> stars = new ArrayList<>();
        stars.add(MyStarTest.RIGEL);
        stars.add(MyStarTest.BETELGEUSE);
        stars.add(MyStarTest.BELLATRIX);

        Asterism orion = new Asterism(stars);
        assertEquals("[Rigel, Betelgeuse, Bellatrix]", orion.stars().toString());
    }
}