package ch.epfl.rigel.gui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MyBlackBodyColorTest {

    private static final String COLOR_DATABASE_NAME = "/bbr_color.txt";

    @Test
    void colorDatabaseIsCorrectlyInstalled() throws IOException {
        try (InputStream colorStream = getClass().getResourceAsStream(COLOR_DATABASE_NAME)) {
            assertNotNull(colorStream);
        }
    }
}