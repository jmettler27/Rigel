package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * A loader of a HYG catalogue, containing only stars with a magnitude less than or equal to 6.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {

    INSTANCE();

    private static final int
            HIP = 2, // The column number of the star's Hipparcos identification number
            PROPER = 7, // The column number of the star's proper name
            MAG = 14, // The column number of the star's magnitude
            CI = 17, // The column number of the star's B-V color index
            RARAD = 24, // The column number of the star's right ascension (in radians)
            DECRAD = 25, // The column number of the star's declination (in radians)
            BAYER = 28, // The column number of the star's Bayer designation
            CON = 30; // The column number of the short name of the constellation

    /**
     * @see StarCatalogue.Loader#load(InputStream, StarCatalogue.Builder)
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        // Reads the HYG database, encoded in ASCII
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream, StandardCharsets.US_ASCII))) {

            reader.readLine(); // Ignores the header line, giving the names of the columns (which is thus unusable)

            String line; // The current line of data (i.e. the current star in the HYG catalogue)
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(","); // The 37 informations on the current star

                // The star's Hipparcos identification number (0 by default)
                int hipparcosId = columns[HIP - 1].isEmpty() ? 0 : Integer.parseInt(columns[HIP - 1]);

                // The star's name
                String name;

                if (columns[PROPER - 1].isEmpty()) {
                    StringBuilder nameBuilder = new StringBuilder();
                    if (columns[BAYER - 1].isEmpty()) {
                        nameBuilder.append("?");
                    } else {
                        nameBuilder.append(columns[BAYER - 1]);
                    }
                    nameBuilder.append(" ").append(columns[CON - 1]);
                    name = nameBuilder.toString();
                } else {
                    name = columns[PROPER - 1];
                }

                // The star's right ascension and declination (in radians)
                double raRad = Double.parseDouble(columns[RARAD - 1]);
                double decRad = Double.parseDouble(columns[DECRAD - 1]);

                // The star's equatorial position (in radians)
                EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.of(raRad, decRad);

                // The star's magnitude (0 by default)
                float magnitude = columns[MAG - 1].isEmpty() ? 0f : (float) Double.parseDouble(columns[MAG - 1]);

                // The star's color index (0 by default)
                float colorIndex = columns[CI - 1].isEmpty() ? 0f : (float) Double.parseDouble(columns[CI - 1]);

                builder.addStar(new Star(hipparcosId, name, equatorialCoordinates, magnitude, colorIndex));
            }
        }
    }
}
