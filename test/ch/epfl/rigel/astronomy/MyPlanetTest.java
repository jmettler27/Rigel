package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyPlanetTest {

    @Test
    void constructorThrowsNullPointerExceptionWhenNull() {
        assertThrows(NullPointerException.class, () -> {
            new Planet(null, null, 0, 0);
        });
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionWithNegativeAngularSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Planet("Naboo", EquatorialCoordinates.of(0, 0), -0.0000001f, 0);
        });
    }

    @Test
    void info() {
        assertEquals("Naboo", new Planet("Naboo", EquatorialCoordinates.of(0, 0), 0, 0).toString());
    }
}