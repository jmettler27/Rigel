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

    /**
     * @see StarCatalogue.Loader#load(InputStream, StarCatalogue.Builder)
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        // The buffered reader of the given input stream (i.e. the catalogue of stars, encoded in ASCII)
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream, StandardCharsets.US_ASCII))) {

            // The list of stars already loaded in the catalogue's builder
            List<Star> stars = builder.stars();

            String line; // The current line of data (i.e. the current asterism in the catalogue)
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(","); // The Hipparcos numbers of the current asterism's stars

                List<Star> asterismStars = new ArrayList<>(); // The list of stars of the current asterism

                // Adds the stars of each asterism, if they are present in the list of stars of the catalogue's builder
                for (String col : columns) {
                    int hipparcosID = Integer.parseInt(col);

                    for (Star s : stars) {
                        if (s.hipparcosId() == hipparcosID) {
                            asterismStars.add(s);
                        }
                    }
                }
                builder.addAsterism(new Asterism(asterismStars));
            }
        }
    }
}
