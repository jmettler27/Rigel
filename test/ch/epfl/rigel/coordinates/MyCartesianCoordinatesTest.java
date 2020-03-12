package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyCartesianCoordinatesTest {

    @Test
    void x() {
        assertEquals(25.8, CartesianCoordinates.of(25.8, 46.5).x());
        assertEquals(0.0, CartesianCoordinates.of(0,81.5).x());
    }

    @Test
    void y() {
        assertEquals(46.5, CartesianCoordinates.of(25.8, 46.5).y());
    }

    @Test
    void testToString() {
        assertEquals("(x=34.5679, y=205.9900)", CartesianCoordinates.of(34.56785, 205.99).toString());
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