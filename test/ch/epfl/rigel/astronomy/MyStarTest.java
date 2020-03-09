package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyStarTest {

    static final Star RIGEL = new Star(24436, "Rigel", EquatorialCoordinates.of(0, 0), 0, -0.03f);
    static final Star BETELGEUSE = new Star(27989, "Betelgeuse", EquatorialCoordinates.of(0, 0), 0, 1.50f);
    static final Star BELLATRIX = new Star( 25336, "Bellatrix", EquatorialCoordinates.of(0, 0), 1.6f, -0.22f);
    static final Star VEGA = new Star( 91262, "Vega", EquatorialCoordinates.of(0, 0), 0, 0.0f);


    @Test
    void constructionThrowsWithInvalidArguments() {
        // The color index is too low
        assertThrows(IllegalArgumentException.class, () -> {
            new Star(24436, "Rigel", EquatorialCoordinates.of(0, 0), 0, -0.5000001f);
        });

        // The color index is too high
        assertThrows(IllegalArgumentException.class, () -> {
            new Star(24436, "Rigel", EquatorialCoordinates.of(0, 0), 0, 5.500001f);
        });

        // The Hipparcos ID is negative
        assertThrows(IllegalArgumentException.class, () -> {
            new Star(-1, "Rigel", EquatorialCoordinates.of(0, 0), 0, 0);
        });

        // The Hipparcos ID and color index are incorrect
        assertThrows(IllegalArgumentException.class, () -> {
            new Star(-1, "Rigel", EquatorialCoordinates.of(0, 0), 0, -0.5000001f);
        });

        // The Hipparcos ID and color index are incorrect
        assertThrows(IllegalArgumentException.class, () -> {
            new Star(-1, "Rigel", EquatorialCoordinates.of(0, 0), 0, 5.500001f);
        });

        // The name is null
        assertThrows(NullPointerException.class, () -> {
            new Star(24436, null, EquatorialCoordinates.of(0, 0), 0, 0);
        });

        // The equatorial coordinates are null
        assertThrows(NullPointerException.class, () -> {
            new Star(24436, "Rigel", null, 0, 0);
        });

        // The name and the equatorial coordinates are null
        assertThrows(NullPointerException.class, () -> {
            new Star(24436, null, null, 0, 0);
        });

        // NullPointerException has priority over IllegalArgumentException
        assertThrows(NullPointerException.class, () -> {
            new Star(-1, null, null, 0, -0.5000001f);
        });
    }

    @Test
    void hipparcosId() {
        assertEquals(24436, RIGEL.hipparcosId());
        assertEquals(27989, BETELGEUSE.hipparcosId());
        assertEquals(25336, BELLATRIX.hipparcosId());
        assertEquals(91262, VEGA.hipparcosId());
    }

    @Test
    void colorTemperature() {
        assertEquals(10515, RIGEL.colorTemperature());
        assertEquals(3793, BETELGEUSE.colorTemperature());
        assertEquals(14086, BELLATRIX.colorTemperature());
        assertEquals(10125, VEGA.colorTemperature());
    }
}