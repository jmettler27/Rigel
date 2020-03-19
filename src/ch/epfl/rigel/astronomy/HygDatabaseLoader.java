package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A loader of a HYG catalogue, containing only stars with a magnitude less than or equal to 6.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE();

    private final static int
            HIP = 2, // Index of the star's Hipparcos identification number
            PROPER = 7, // Index of the star's proper name
            MAG = 14, // Index of the star's magnitude
            CI = 17, // Index of the star's B-V color index
            RARAD = 24, // Index of the star's right ascension (in radians)
            DECRAD = 25, // Index of the star's declination (in radians)
            BAYER = 28, // Index of the star's Bayer designation
            CON = 30; // Index of the short name of the constellation

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        // The buffered reader of the given input stream (i.e. the HYG database)
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // The header line, giving the names of the column (which is thus unusable for this loader)
        reader.readLine();

        String line;

        while ((line = reader.readLine()) != null) {
            // Array of 37 columns resulting from the split of the current line of the database
            String[] columns = line.split(",");

            // The star's Hipparcos identification number (0 by default)
            int hipparcosId = columns[HIP - 1].equals("") ? 0 : Integer.parseInt(columns[HIP - 1]);

            // The star's name
            String name;

            if (columns[PROPER - 1].equals("")) {
                StringBuilder nameBuilder = new StringBuilder();
                if (columns[BAYER - 1].equals("")) {
                    nameBuilder.append("?");
                } else {
                    nameBuilder.append(columns[BAYER - 1]);
                }
                nameBuilder.append(" ").append(columns[CON - 1]);
                name = nameBuilder.toString();
            } else {
                name = columns[PROPER - 1];
            }

            // The star's equatorial position
            EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.of(Double.parseDouble(columns[RARAD - 1]), Double.parseDouble(columns[DECRAD - 1]));

            // The star's magnitude (0 by default)
            float magnitude = columns[MAG - 1].equals("") ? 0f : (float) Double.parseDouble(columns[MAG - 1]);

            // The star's color index (0 by default)
            float colorIndex = columns[CI - 1].equals("") ? 0f : (float) Double.parseDouble(columns[CI - 1]);

            builder.addStar(new Star(hipparcosId, name, equatorialCoordinates, magnitude, colorIndex));

        }
    }
}
