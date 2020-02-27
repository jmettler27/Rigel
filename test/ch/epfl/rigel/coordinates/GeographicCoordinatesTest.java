// Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;

import static org.junit.jupiter.api.Assertions.*;

class GeographicCoordinatesTest {

    private final double[] validLonDeg = { 0, -180, -125, -100, -50, 25, 50,
            125, 179.9999999999 };

    private final double[] invalidLonDeg = { -360, -180.0000000001,
            180.0000000001, 180, 225, 360 };

    private final double[] validLatDeg = { 0, -90, 90, 25, 36, 76.678934 };

    private final double[] invalidLatDeg = { -90.0000000001, 90.0000000001,
            -180, 180 };

    @Test
    void ofDegThrowsWithInvalidLongitude() {
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(-360, 45.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(-180.0000000001, 45.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(180, 45.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(225, 45.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(360, 45.0);
        });

    }

    @Test
    void ofDegThrowsWithInvalidLatitude() {
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(45.0, -90.0000000001);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(45.0, 90.0000000001);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(45.0, -180);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(45.0, 180);
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
    void latDegReturnsCorrectValue() {
        GeographicCoordinates g = GeographicCoordinates.ofDeg(50, 89);
        assertEquals(89, g.latDeg());
    }

    @Test
    void testToString() {
        GeographicCoordinates g = GeographicCoordinates.ofDeg(50, 50);
        assertEquals("(lon=50.0000°, lat=50.0000°)", g.toString());

        GeographicCoordinates g1 = GeographicCoordinates
                .ofDeg(Angle.toDeg(Math.PI / 2.0), Angle.toDeg(Math.PI / 4.0));
        assertEquals("(lon=90.0000°, lat=45.0000°)", g1.toString());
    }
    
    @Test
    void equalsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            var geoCoords = GeographicCoordinates.ofDeg(45.0, 45.0);
            geoCoords.equals(geoCoords);
        });
    }

    @Test
    void hashCodeThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            GeographicCoordinates.ofDeg(45.0, 45.0).hashCode();
        });
    }

}