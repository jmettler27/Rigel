package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyCartesianCoordinatesTest {

    @Test
    void of() {
    }

    @Test
    void x() {
        CartesianCoordinates cartesianCoordinates = CartesianCoordinates.of(25.8, 46.5);
        assertEquals(25.8, cartesianCoordinates.x());
    }

    @Test
    void y() {
        CartesianCoordinates cartesianCoordinates = CartesianCoordinates.of(25.8, 46.5);
        assertEquals(46.5, cartesianCoordinates.y());
    }

    @Test
    void testToString() {
        CartesianCoordinates cartesianCoordinates = CartesianCoordinates.of(34.5678, 205.99);
        assertEquals("(x=34.5678, y=205.9900)", cartesianCoordinates.toString());
    }

    @Test
    void testHashCode() {
        assertThrows(UnsupportedOperationException.class, () -> {
            CartesianCoordinates.of(0, 0).hashCode();
        });
    }

    @Test
    void testEquals() {
        assertThrows(UnsupportedOperationException.class, () -> {
            var c = CartesianCoordinates.of(25.4567, 23.78);
            c.equals(c);
        });
    }
}