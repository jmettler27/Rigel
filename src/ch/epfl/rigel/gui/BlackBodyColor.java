package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The color of a black body, i.e. a celestial object that emits light solely because of its color temperature.
 */
public abstract class BlackBodyColor {

    private static final String COLOR_FILE_NAME = "/bbr_color.txt";
    private static final Map<Integer, String> TEMPERATURE_COLOR_MAP = temperatureColorMap();

    /**
     * Constructs ecliptic coordinates (in radians) with the given longitude and latitude (in radians).
     *
     * @param temperature
     *            The color temperature (in degrees Kelvin)
     * @throws IllegalArgumentException
     *            if the closest multiple of 100 to the given temperature does not correspond to a temperature in the map
     * @return the color of the black body associated to the given color temperature
     */
    public static Color colorForTemperature(int temperature) {
        int closestTemperature = closestMultipleTo(temperature);
        Preconditions.checkArgument(TEMPERATURE_COLOR_MAP.containsKey(closestTemperature));

        return Color.web(TEMPERATURE_COLOR_MAP.get(closestTemperature));
    }

    /**
     * Additional method.
     * Constructs a map that associates to each color temperature (in degrees Kelvin) its corresponding color
     * (in hexadecimal notation), as listed in the given text file.
     *
     * @return the map
     */
    private static Map<Integer, String> temperatureColorMap() {
        // Key : The color temperature (in degrees Kelvin)
        // Value : The color (in hexadecimal notation)
        Map<Integer, String> map = new HashMap<>();

        // The buffered reader of the given input stream (i.e. the color temperatures' text file)
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     BlackBodyColor.class.getResourceAsStream(COLOR_FILE_NAME),
                                     StandardCharsets.US_ASCII))) {

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
                    String colorString = line.substring(80, 87); // The color (in hexadecimal notation)
                    map.put(temperature, colorString);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Map.copyOf(map);
    }

    /**
     * Additional method.
     * Returns the closest multiple of 100 to the given number.
     *
     * @param number
     *            The given number
     * @throws IllegalArgumentException
     *            if the given number is < 0
     * @return the closest multiple of 100 to the given number
     */
    private static int closestMultipleTo(int number) {
        Preconditions.checkArgument(number >= 0);
        int closestMultiple = number; // The closest multiple is the number itself if it is divisible by 100

        // The number is not divisible by 100
        if (number % 100 != 0) {
            int temp = number / 100;
            int remain = number % 100;

            // Rounds the number to its closest lower or upper multiple of 100 according to its remainder in the
            // euclidean division by 100.
            closestMultiple = (0 < remain && remain < 50) ? (temp) * 100 : (temp + 1) * 100;
        }
        return closestMultiple;
    }
}
