package ch.epfl.rigel.gui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String BLACK_BODY_COLOR__NAME = "/bbr_color.txt";

        // Key : The color temperature (in degrees Kelvin)
        // Value : The color, in hexadecimal notation
        Map<Integer, String> map = new HashMap<>();

        // The buffered reader of the given input stream (i.e. the color temperatures' database)
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     Main.class.getResourceAsStream(BLACK_BODY_COLOR__NAME)))) {

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

                    // System.out.println(String.format("temperature = %dK, color = %s", temperature, colorString));

                    map.put(temperature, colorString);
                }
            }
            System.out.println(map.size()); // 391

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
