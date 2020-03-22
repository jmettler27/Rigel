package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A loader of a catalogue of asterisms.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE();

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        List<Star> stars = builder.stars();

        // The buffered reader of the given input stream (i.e. the catalogue of stars, encoded in ASCII)
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));

        String line;

        while ((line = reader.readLine()) != null) {
            // Array of 37 columns resulting from the split of the current line of the database
            String[] columns = line.split(",");

            List<Star> starsAsterism = new ArrayList<>();

            for (String column : columns) {
                int hipparcosID = Integer.parseInt(column);

                for (Star s : stars) {
                    if (s.hipparcosId() == hipparcosID) {
                        starsAsterism.add(s);
                    }
                }
            }
            builder.addAsterism(new Asterism(starsAsterism));
        }
    }
}
