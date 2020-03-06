// Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyHorizontalCoordinatesTest {

    @Test
    void ofDegThrowsWhenInvalidAzimuth() {
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(-0.000000001, 0.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(360.000000001, 0.0);
        });
    }

    @Test
    void ofDegThrowsWhenInvalidAltitude() {
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(0.0, -90.000000001);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(0.0, 90.000000001);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(0.0, -456.8769);
        });
    }

    @Test
    void azReturnsCorrectValue() {
        HorizontalCoordinates h = HorizontalCoordinates.of(0.3453, 0);
        assertEquals(0.3453, h.az());
    }

    @Test
    void azDegReturnsCorrectValue() {
        HorizontalCoordinates h = HorizontalCoordinates.ofDeg(315, 25);
        assertEquals(315, h.azDeg());
    }

    @Test
    void altReturnsCorrectValue() {
        HorizontalCoordinates h = HorizontalCoordinates.of(0, 1);
        assertEquals(1, h.alt());
    }

    @Test
    void altDegReturnsCorrectValue() {
        HorizontalCoordinates h = HorizontalCoordinates.ofDeg(315, 25);
        assertEquals(25, h.altDeg());
    }

    @Test
    void azOctantWorksOnBasicAzimuths() {
        assertEquals("N", HorizontalCoordinates.ofDeg(0.0, 0).azOctantName("N","E", "S", "O"));

        assertEquals("N", HorizontalCoordinates.ofDeg(5.234567, 0).azOctantName("N", "E", "S", "O"));

        assertEquals("NO", HorizontalCoordinates.ofDeg(315, 0).azOctantName("N","E", "S", "O"));

        assertEquals("O", HorizontalCoordinates.ofDeg(270, 0).azOctantName("N","E", "S", "O"));

        assertEquals("SO", HorizontalCoordinates.ofDeg(225, 0).azOctantName("N","E", "S", "O"));

        assertEquals("S", HorizontalCoordinates.ofDeg(180, 0).azOctantName("N","E", "S", "O"));

        assertEquals("SE", HorizontalCoordinates.ofDeg(135, 0).azOctantName("N","E", "S", "O"));

        assertEquals("E", HorizontalCoordinates.ofDeg(90, 0).azOctantName("N","E", "S", "O"));

        assertEquals("NE", HorizontalCoordinates.ofDeg(45, 0).azOctantName("N","E", "S", "O"));

        assertEquals("NO", HorizontalCoordinates.ofDeg(315 - 22.5, 0).azOctantName("N", "E", "S", "O"));

        assertEquals("NO", HorizontalCoordinates.ofDeg(300, 0).azOctantName("N","E", "S", "O"));

        assertEquals("NO", HorizontalCoordinates.ofDeg(310, 0).azOctantName("N","E", "S", "O"));

        assertEquals("NO", HorizontalCoordinates.ofDeg(307.5, 0).azOctantName("N", "E", "S", "O"));

        assertEquals("NO", HorizontalCoordinates.ofDeg(320, 0).azOctantName("N","E", "S", "O"));

        assertEquals("NO", HorizontalCoordinates.ofDeg(315, 0).azOctantName("N","E", "S", "O"));

        assertEquals("O", HorizontalCoordinates.ofDeg(290, 0).azOctantName("N","E", "S", "O"));
    }

    @Test
    void azOctantWorksOnLimitCases() {

        // North / north-east limit (north side)
        assertEquals("N", HorizontalCoordinates.ofDeg(22.4999999, 0).azOctantName("N", "E", "S", "O"));

        // North / north-east limit (north-east side)
        assertEquals("NE", HorizontalCoordinates.ofDeg(45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));

        // North-east / east limit (north-east side)
        assertEquals("NE", HorizontalCoordinates.ofDeg(45.0 + 22.4999, 0).azOctantName("N", "E", "S", "O"));

        // North-east / east limit (east side)
        assertEquals("E", HorizontalCoordinates.ofDeg(45.0 + 45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));

        // East / south-east limit (east side)
        assertEquals("E", HorizontalCoordinates.ofDeg(90 + 22.4999, 0).azOctantName("N", "E", "S", "O"));

        // East / south-east limit (south-east side)
        assertEquals("SE", HorizontalCoordinates.ofDeg(90 + 45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));

        // South-east / south limit (south-east side)
        assertEquals("SE", HorizontalCoordinates.ofDeg(135.0 + 22.4999, 0).azOctantName("N", "E", "S", "O"));

        // South-east / south limit (south side)
        assertEquals("S", HorizontalCoordinates.ofDeg(135.0 + 45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));

        // South / south-west limit (south side)
        assertEquals("S", HorizontalCoordinates.ofDeg(180.0 + 22.4999, 0).azOctantName("N", "E", "S", "O"));

        // South / south-west limit (south-west side)
        assertEquals("SO", HorizontalCoordinates.ofDeg(180.0 + 45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));

        // South-west / west limit (south-west side)
        assertEquals("SO", HorizontalCoordinates.ofDeg(225.0 + 22.4999, 0).azOctantName("N", "E", "S", "O"));

        // South-west / west limit (west side)
        assertEquals("O", HorizontalCoordinates.ofDeg(225.0 + 45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));

        // West / north-west limit (west side)
        assertEquals("O", HorizontalCoordinates.ofDeg(270.0 + 22.4999, 0).azOctantName("N", "E", "S", "O"));

        // West / north-west limit (north-west side)
        assertEquals("NO", HorizontalCoordinates.ofDeg(270.0 + 45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));

        // North-west / north limit (north-west side)
        assertEquals("NO", HorizontalCoordinates.ofDeg(315.0 + 22.4999999, 0).azOctantName("N", "E", "S", "O"));

        // North-west / north limit (north side)
        assertEquals("N", HorizontalCoordinates.ofDeg(315.0 + 45.0 / 2.0, 0).azOctantName("N", "E", "S", "O"));
    }

    @Test
    void angularDistanceToWorksWithTenThousandthTolerance() {
        HorizontalCoordinates EPFLCoords = HorizontalCoordinates.ofDeg(6.5682,
                46.5183);

        HorizontalCoordinates EPFZCoords = HorizontalCoordinates.ofDeg(8.5476,
                47.3763);

        assertEquals(0.0279, EPFLCoords.angularDistanceTo(EPFZCoords), 1e-4);
    }

    @Test
    void testToString() {
        HorizontalCoordinates c = HorizontalCoordinates.ofDeg(350, 7.2);
        assertEquals("(az=350.0000°, alt=7.2000°)", c.toString());

        HorizontalCoordinates c1 = HorizontalCoordinates.ofDeg(25.45,5.2436789);
        assertEquals("(az=25.4500°, alt=5.2437°)", c1.toString());

        HorizontalCoordinates c2 = HorizontalCoordinates.ofDeg(25.45,5.24364999);
        assertEquals("(az=25.4500°, alt=5.2436°)", c2.toString());

    }
}