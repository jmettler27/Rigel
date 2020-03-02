// Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;

import static org.junit.jupiter.api.Assertions.*;

class MyEclipticCoordinatesTest {

    
    @Test
    void ofThrowsWithInvalidLongitude() {
        
    }
    @Test
    void lonReturnsCorrectValue() {
        EclipticCoordinates e = EclipticCoordinates.of(0.5, 0.5);
        assertEquals(0.5, e.lon());
    }

    @Test
    void latReturnsCorrectValue() {
        EclipticCoordinates e = EclipticCoordinates.of(0, Angle.ofDeg(4.5));
        assertEquals(Angle.ofDeg(4.5), e.lat());
    }

    @Test
    void testToString() {

        EclipticCoordinates e = EclipticCoordinates.of(Angle.ofDeg(22.5),
                Angle.ofDeg(18.0));

        assertEquals("(λ=22.5000°, β=18.0000°)", e.toString());
    }
}
