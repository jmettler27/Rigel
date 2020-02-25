// Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SphericalCoordinatesTest {

    @Test
    void lon() {
    }

    @Test
    void lonDeg() {
    }

    @Test
    void lat() {
    }

    @Test
    void latDeg() {
    }

    @Test
    void equalsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            SphericalCoordinates geoCoords = GeographicCoordinates.ofDeg(45.0, 45.0);
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