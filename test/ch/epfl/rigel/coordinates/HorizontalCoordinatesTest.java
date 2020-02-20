//Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HorizontalCoordinatesTest {

    @Test
    void constructionTestThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            var g = HorizontalCoordinates.of(-5, 90);
        });
    }

    @Test
    void azOctantName() {
        assertEquals("SO", HorizontalCoordinates.ofDeg(225, 55)
                .azOctantName("N", "E", "S", "O"));
    }

    @Test
    void testToString() {
        HorizontalCoordinates h = HorizontalCoordinates.ofDeg(350, 7.2);
        assertEquals("(az=350.0000°, alt=7.2000°)", h.toString());
    }

}