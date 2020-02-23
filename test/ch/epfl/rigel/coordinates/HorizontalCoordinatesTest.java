//Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;

import static org.junit.jupiter.api.Assertions.*;

class HorizontalCoordinatesTest {

    @Test
    void ofDegThrows1() {
        double[] invalidAzDeg = { -0.000000001, 0, 360.000000001 };

        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < invalidAzDeg.length; ++i) {
                HorizontalCoordinates.of(Angle.ofDeg(invalidAzDeg[i]), 0);
            }
        });
    }

    @Test
    void ofDegThrows2() {
        double[] invalidAltDeg = { -90.000000001, 0, 90.000000001 };

        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < invalidAltDeg.length; ++i) {
                HorizontalCoordinates.of(Angle.ofDeg(invalidAltDeg[i]), 0);
            }
        });
    }

    @Test
    void azTest() {
        HorizontalCoordinates h = HorizontalCoordinates.of(Math.PI, 0);
        assertEquals(Math.PI, h.az());
    }

    @Test
    void azDegTest() {
        HorizontalCoordinates h = HorizontalCoordinates.ofDeg(315, 25);
        assertEquals(315, h.azDeg());
    }

    @Test
    void altDegTest() {
        HorizontalCoordinates h = HorizontalCoordinates.ofDeg(315, 25);
        assertEquals(25, h.altDeg());
    }

    @Test
    void altTest() {
        HorizontalCoordinates h = HorizontalCoordinates.of(0, 1);
        assertEquals(1, h.alt());
    }

    @Test
    void azOctantName() {
        assertEquals("N", HorizontalCoordinates.ofDeg(0, 55).azOctantName("N",
                "E", "S", "O"));
    }

    @Test
    void angularDistanceToTest() {
        HorizontalCoordinates EPFLCoords = HorizontalCoordinates.ofDeg(6.5682,
                46.5183);

        HorizontalCoordinates EPFZCoords = HorizontalCoordinates.ofDeg(8.5476,
                47.3763);

        assertEquals(0.0279, EPFLCoords.angularDistanceTo(EPFZCoords), 1e-4);

    }

    @Test
    void testToString() {
        HorizontalCoordinates c1 = HorizontalCoordinates.ofDeg(25.45,
                5.2436789);
        assertEquals("(az=25.4500°, alt=5.2437°)", c1.toString());

        HorizontalCoordinates c2 = HorizontalCoordinates.ofDeg(25.45,
                5.24364999);
        assertEquals("(az=25.4500°, alt=5.2436°)", c2.toString());

    }

    /*
     * @Test void centeredIntervalThrows() { HorizontalCoordinates h =
     * HorizontalCoordinates.ofDeg(350, 7.2);
     * assertThrows(IllegalArgumentException.class, () -> {
     * h.centeredInterval(-55); }); }
     */

}