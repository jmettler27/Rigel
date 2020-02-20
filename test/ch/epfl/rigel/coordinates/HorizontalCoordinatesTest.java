package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HorizontalCoordinatesTest {

    @Test
    void constructionTestThrows(){
        assertThrows(IllegalArgumentException.class, () -> {
            var g = HorizontalCoordinates.of(-5,90);
        });
    }

    @Test
    void azOctantName() {
        assertEquals("SO",HorizontalCoordinates.ofDeg(225, 55)
                .azOctantName("N", "E", "S", "O"));
    }
    /*
    @Test
    void angularDistanceTo() {
        HorizontalCoordinates h = HorizontalCoordinates.ofDeg(6.5682,46.5183);
        HorizontalCoordinates h1 = HorizontalCoordinates.ofDeg(8.5476,47.3763);

        assertEquals( "0.027935461189288496",h.angularDistanceTo(h1));
    }

     */

    @Test
    void testToString() {
        HorizontalCoordinates h = HorizontalCoordinates.ofDeg(350,7.2);
        assertEquals("(az=350.0000°, alt=7.2000°)",h.toString());
    }

}