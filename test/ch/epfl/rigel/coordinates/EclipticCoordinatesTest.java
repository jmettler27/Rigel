//Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;

import static org.junit.jupiter.api.Assertions.*;

class EclipticCoordinatesTest {

    @Test
    void testToString() {

        EclipticCoordinates e = EclipticCoordinates.of(Angle.ofDeg(22.5000),
                Angle.ofDeg(18.0000));

        assertEquals("(λ=22.5000°, β=18.0000°)", e.toString());
    }

    @Test
    void lonTest() {
        EclipticCoordinates e = EclipticCoordinates.of(-0.5, 0.5);
        assertEquals(-0.5, e.lon());
    }

    @Test
    void latTest() {
        EclipticCoordinates e = EclipticCoordinates.of(0, 25);
        assertEquals(25, e.lat());
    }
}
