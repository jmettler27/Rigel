package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquatorialCoordinatesTest {


    @Test
    void testToString() {
        EquatorialCoordinates e = EquatorialCoordinates.of(Math.PI,Math.PI/4);
        assertEquals("(ra=12.0000h, dec=45.0000°)", e.toString());
    }

    @Test
    void constructionTestThrows(){
        assertThrows(IllegalArgumentException.class, () -> {
            var g = EquatorialCoordinates.of(0.5,3);
        });
    }
}