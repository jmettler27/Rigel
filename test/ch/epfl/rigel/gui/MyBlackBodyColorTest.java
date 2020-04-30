package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyBlackBodyColorTest {

    private static final String COLOR_FILE_NAME = "/bbr_color.txt";

    @Test
    void colorDatabaseIsCorrectlyInstalled() throws IOException {
        try (InputStream colorStream = getClass().getResourceAsStream(COLOR_FILE_NAME)) {
            assertNotNull(colorStream);
        }
    }

    @Test
    void colorForTemperatureWorks() {
        assertEquals(Color.web("#c8d9ff"), BlackBodyColor.colorForTemperature(10500));
        assertEquals(Color.web("#ffcc99"), BlackBodyColor.colorForTemperature(3798));
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(40_051));
        assertEquals(Color.web("#a0bfff"), BlackBodyColor.colorForTemperature(28623));

        // Limit cases
        assertEquals(Color.web("#a0bfff"), BlackBodyColor.colorForTemperature(29249));
        assertEquals(Color.web("#9fbfff"), BlackBodyColor.colorForTemperature(29250));

        assertEquals(Color.web("#cddcff"), BlackBodyColor.colorForTemperature(9949));
        assertEquals(Color.web("#ccdbff"), BlackBodyColor.colorForTemperature(9950));

        assertEquals(Color.web("#ff3800"), BlackBodyColor.colorForTemperature(1000));
        assertEquals(Color.web("#ff8912"), BlackBodyColor.colorForTemperature(2000));
        assertEquals(Color.web("#ffdbba"), BlackBodyColor.colorForTemperature(4500));
        assertEquals(Color.web("#ccdbff"), BlackBodyColor.colorForTemperature(10000));
        assertEquals(Color.web("#9bbcff"), BlackBodyColor.colorForTemperature(40000));

        assertEquals(Color.web("#ffcc99"), BlackBodyColor.colorForTemperature(3802));

        assertEquals(Color.web("#ff3800"), BlackBodyColor.colorForTemperature(1049));
        assertEquals(Color.web("#ff8912"), BlackBodyColor.colorForTemperature(1951));

        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(949));
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(40050));

    }
}