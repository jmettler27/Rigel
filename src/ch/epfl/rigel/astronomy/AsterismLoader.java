package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A loader of a catalogue of asterisms.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE();

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        List<Star> stars = builder.stars();

        // The buffered reader of the given input stream (i.e. the catalogue of stars)
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";

        while (line != null) {

            // The current line of the catalogue of asterisms, corresponding to the values of an asterism
            line = reader.readLine();
            System.out.println(line);

            if (line != null) {

                // Array of 37 columns resulting from the split of the current line of the database
                String[] columns = line.split(",");

                List<Star> starsAsterism = new ArrayList<>();

                for (int i = 0; i < columns.length; ++i) {
                    int hipparcosID = Integer.parseInt(columns[i]);

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
}
