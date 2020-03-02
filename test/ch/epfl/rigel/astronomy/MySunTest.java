package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySunTest {


    @Test
    void eclipticPos() {
    }

    @Test
    void meanAnomaly() {
    }

    @Test
    void info(){
        assertEquals("Soleil", new Sun(EclipticCoordinates.of(0,0), EquatorialCoordinates.of(0,0), 0.5f, 0.5f).toString());
    }
}