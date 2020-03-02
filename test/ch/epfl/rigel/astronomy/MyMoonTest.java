package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyMoonTest {

    @Test
    void info() {
        assertEquals("Lune (37.5%)", new Moon(EquatorialCoordinates.of(0, 0), 0.5f, 0.1f,  0.3752f).toString());
    }
}