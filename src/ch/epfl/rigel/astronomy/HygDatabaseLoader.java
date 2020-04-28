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
            HIP = 1, // The index of the star's Hipparcos identification number
            PROPER = 6, // The index of the star's proper name
            MAG = 13, // The index of the star's magnitude
            CI = 16, // The index of the star's B-V color index
            RARAD = 23, // The index of the star's right ascension (in radians)
            DECRAD = 24, // The index of the star's declination (in radians)
            BAYER = 27, // The index of the star's Bayer designation
            CON = 29; // The index of the short name of the constellation

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
                int hipparcosId = columns[HIP].isEmpty() ? 0 : Integer.parseInt(columns[HIP]);

                // The star's name
                String name;

                if (columns[PROPER].isEmpty()) {
                    StringBuilder nameBuilder = new StringBuilder();
                    if (columns[BAYER].isEmpty()) {
                        nameBuilder.append("?");
                    } else {
                        nameBuilder.append(columns[BAYER]);
                    }
                    nameBuilder.append(" ").append(columns[CON]);
                    name = nameBuilder.toString();
                } else {
                    name = columns[PROPER];
                }

                // The star's right ascension and declination (in radians)
                double raRad = Double.parseDouble(columns[RARAD]);
                double decRad = Double.parseDouble(columns[DECRAD]);

                // The star's equatorial position (in radians)
                EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.of(raRad, decRad);

                // The star's magnitude (0 by default)
                float magnitude = defaultCases(columns, MAG);

                // The star's color index (0 by default)
                float colorIndex = defaultCases(columns, CI);

                builder.addStar(new Star(hipparcosId, name, equatorialCoordinates, magnitude, colorIndex));
            }
        }
    }

    /**
     * Additional method.
     * Returns the float value of the string at the given index of the given array.
     *
     * @param columns The array of strings
     * @param index The index of the string
     * @return the float value of the string
     */
    private static float defaultCases(String[] columns, int index) {
        return columns[index].isEmpty() ? 0f : (float) Double.parseDouble(columns[index]);
    }
}
