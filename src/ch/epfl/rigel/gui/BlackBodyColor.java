package ch.epfl.rigel.gui;

import javafx.scene.paint.Color;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class BlackBodyColor {

    /**
     * @param temperature
     * @return
     */
    public Color colorForTemperature(int temperature) {
        Map<Integer, String> mapColor = fileReader();
        return Color.web(mapColor.get(temperature));
    }

    /**
     * @return
     */
    private Map<Integer, String> fileReader() {
        Map<Integer, String> map = new HashMap<>();

        try (InputStream inputStream = Main.class.getResourceAsStream("/bbr_color.txt")) {
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    inputStream, StandardCharsets.US_ASCII));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) != '#' && line.charAt(10) != ' ') {
                    String temperature = (line.charAt(1) == ' ') ? line.substring(2, 6) : line.substring(1, 6);
                    map.put(Integer.parseInt(temperature), line.substring(80, 87));
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Map.copyOf(map);
    }
}
