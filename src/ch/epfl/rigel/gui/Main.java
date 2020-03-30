package ch.epfl.rigel.gui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();

        try (InputStream inputStream = Main.class.getResourceAsStream("/bbr_color.txt")) {
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    inputStream, StandardCharsets.US_ASCII));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) != '#') {
                    if (line.charAt(10) != ' ') {
                        String temperature;
                        if (line.charAt(1) == ' ') {
                            temperature = line.substring(2, 6);
                        } else {
                            temperature = line.substring(1, 6);
                        }
                        map.put(Integer.parseInt(temperature), line.substring(80, 87));
                    }
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
