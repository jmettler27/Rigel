package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

        Asterism Orion = new Asterism(stars);
        assertEquals("[Rigel, Betelgeuse, Bellatrix]", Orion.stars().toString());
    }
}