package ch.epfl.rigel.gui;

import org.junit.jupiter.api.Test;

import java.io.*;
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
    void readerWorks() {
        // Key : The color temperature (in degrees Kelvin)
        // Value : The color (in hexadecimal notation)
        Map<Integer, String> map = new HashMap<>();

        // The buffered reader of the given input stream (i.e. the color temperatures' text file)
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     getClass().getResourceAsStream(COLOR_FILE_NAME)))) {

            String line; // The current line of data (i.e. the characteristics of the color temperature)
            while ((line = reader.readLine()) != null) {
                // Ignores the comment lines (beginning with the # character) and those containing the text "2deg"
                if (line.charAt(0) != '#' && line.charAt(10) != ' ') {

                    // Selects the substring corresponding to the temperature according to the latter's value
                    // (i.e. whether there is a space at position 1 before the value or not)
                    String temperatureString = (line.charAt(1) == ' ') ?
                            line.substring(2, 6) : // The temperature is between 1_000K and 9_000K
                            line.substring(1, 6);  // The temperature is between 10_000K and 40_000K
                    int temperature = Integer.parseInt(temperatureString); // The temperature (in degrees Kelvin)

                    // The color (in hexadecimal notation)
                    String colorString = line.substring(80, 87);

                    map.put(temperature, colorString);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        assertEquals(391, map.size());
        assertEquals("#c8d9ff", map.get(10500));
    }
}