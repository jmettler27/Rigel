package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySkyCanvasPainterTest {

    @Test
    void diameterForMagnitude() {
        Star Rigel = new Star(1, "Rigel", EquatorialCoordinates.of(0,0), 0.18f, 0f);
        //assertEquals(2.99e-3, SkyCanvasPainter.diameterForMagnitude(Rigel), 1e-5);
    }
}