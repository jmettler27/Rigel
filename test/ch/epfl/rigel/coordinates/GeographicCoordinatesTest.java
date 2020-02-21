//Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicCoordinatesTest {

    private final double[] validLonDeg = { 0, -180, -125, -100, -50, 25, 50,
            125, 179.99558, 179.988898 };

    private final double[] invalidLonDeg = { -360, -180.0000111, 180.0000111,
            225, 360 };

    private final double[] validLatDeg = { 0, -90, 90, 25, 36, 76.678934 };

    private final double[] invalidLatDeg = { -90.0000000001, 90.0000000001,
            -180, 180 };

    @Test
    void ofDegThrows1() {
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < invalidLonDeg.length; ++i) {
                GeographicCoordinates.ofDeg(invalidLonDeg[i], 90);
            }
        });
    }

    @Test
    void ofDegThrows2() {
        assertThrows(IllegalArgumentException.class, () -> {
            for (int j = 0; j < invalidLatDeg.length; ++j) {
                GeographicCoordinates.ofDeg(90, invalidLatDeg[j]);
            }
        });
    }

    @Test
    void isValidLonDeg() {
        for (int i = 0; i < validLonDeg.length; ++i) {
            assertTrue(GeographicCoordinates.isValidLonDeg(validLonDeg[i]));
        }
    }

    @Test
    void isNotValidLonDeg() {
        for (int i = 0; i < invalidLonDeg.length; ++i) {
            assertFalse(GeographicCoordinates.isValidLonDeg(invalidLonDeg[i]));
        }
    }

    @Test
    void isValidLatDeg() {
        for (int j = 0; j < validLatDeg.length; ++j) {
            assertTrue(GeographicCoordinates.isValidLatDeg(validLatDeg[j]));
        }
    }

    @Test
    void isNotValidLatDeg() {
        for (int j = 0; j < invalidLatDeg.length; ++j) {
            assertFalse(GeographicCoordinates.isValidLatDeg(invalidLatDeg[j]));
        }
    }

    @Test
    void latDegTest() {
        GeographicCoordinates g = GeographicCoordinates.ofDeg(50, 89);
        assertEquals(89, g.latDeg());
    }

    @Test
    void testToString() {
        GeographicCoordinates g = GeographicCoordinates.ofDeg(50, 50);
        assertEquals("(lon=50.0000°, lat=50.0000°)", g.toString());
    }

}