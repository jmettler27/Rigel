//Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicCoordinatesTest {

    @Test
    void isValidLonDeg() {
        assertTrue(GeographicCoordinates.isValidLatDeg(45));
    }

    @Test
    void isNotValidLonDeg() {
        assertFalse(GeographicCoordinates.isValidLatDeg(360));
    }

    @Test
    void isValidLatDeg() {
        assertTrue(GeographicCoordinates.isValidLatDeg(85));
    }

    @Test
    void isNotValidLatDeg() {
        assertFalse(GeographicCoordinates.isValidLatDeg(120));
    }

    @Test
    void testToString() {
        GeographicCoordinates g = GeographicCoordinates.ofDeg(50, 50);
        assertEquals("(lon=50.0000°, lat=50.0000°)", g.toString());
    }

    @Test
    void constructionTestThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(180, 90);
        });
    }

    @Test
    void getterWorks() {
        GeographicCoordinates g = GeographicCoordinates.ofDeg(50, 89);
        assertEquals(89, g.latDeg());
    }
}