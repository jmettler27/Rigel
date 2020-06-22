package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The color of a black body, i.e. a celestial object that emits light solely because of its color temperature.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class BlackBodyColor {

    private static final String COLOR_FILE_NAME = "/bbr_color.txt";
    private static final int
            NUMBER_SIGN_INDEX = 0, // Index for the # character
            TEMPERATURE_INDEX_END = 6, // Index for the last digit of the temperature
            TWO_DEG_INDEX = 10, // Index for the text "2deg"
            COLOR_INDEX_BEGIN = 80, COLOR_INDEX_END = 87; // Indices for the first and last characters of the color (hexadecimal)

    private static final Map<Integer, Color> TEMPERATURE_COLOR_MAP = temperatureColorMap();

    /**
     * Default constructor.
     */
    private BlackBodyColor() {}

    /**
     * Constructs ecliptic coordinates (in radians) with the given longitude and latitude (in radians).
     *
     * @param temperature
     *            The color temperature (in degrees Kelvin)
     * @throws IllegalArgumentException
     *            if the closest multiple of 100 to the given temperature does not correspond to a temperature in the map
     * @return the color of the black body associated to the given color temperature
     */
    public static Color colorForTemperature(double temperature) {
        int closestTemperature = (int) Math.round(temperature / 100.0) * 100;
        Preconditions.checkArgument(TEMPERATURE_COLOR_MAP.containsKey(closestTemperature));

        return TEMPERATURE_COLOR_MAP.get(closestTemperature);
    }

    /**
     * Additional method.
     * Constructs a map that associates to each color temperature (in degrees Kelvin) its corresponding color
     * (in hexadecimal notation), as listed in the given text file.
     *
     * @return the map
     */
    private static Map<Integer, Color> temperatureColorMap() {
        // Key : The color temperature (in degrees Kelvin)
        // Value : The color (in hexadecimal notation)
        Map<Integer, Color> map = new HashMap<>();

        // The buffered reader of the given input stream (i.e. the color temperatures' text file)
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     BlackBodyColor.class.getResourceAsStream(COLOR_FILE_NAME),
                                     StandardCharsets.US_ASCII))) {

            String line; // The current line of data (i.e. the characteristics of the color temperature)
            while ((line = reader.readLine()) != null) {

                // Ignores the comment lines (beginning with the # character) and those containing the text "2deg"
                if (line.charAt(NUMBER_SIGN_INDEX) != '#' && line.charAt(TWO_DEG_INDEX) != ' ') {

                    // Selects the substring corresponding to the temperature according to the latter's value
                    // (i.e. whether there is a space at position 1 before the value or not)
                    String temperatureString = (line.charAt(1) == ' ') ?
                            line.substring(2, TEMPERATURE_INDEX_END) : // The temperature is between 1_000K and 9_000K
                            line.substring(1, TEMPERATURE_INDEX_END);  // The temperature is between 10_000K and 40_000K

                    int temperature = Integer.parseInt(temperatureString); // The temperature (in degrees Kelvin)
                    String colorString = line.substring(COLOR_INDEX_BEGIN, COLOR_INDEX_END); // The color (in hexadecimal notation)

                    map.put(temperature, Color.web(colorString));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return map;
    }
}
