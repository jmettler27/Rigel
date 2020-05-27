package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.math.Angle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * A loader of a satellite catalogue, containing only satellites with a geostationary orbit.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum SatelliteDatabaseLoader implements SatelliteCatalogue.Loader {

    INSTANCE();

    private static final int
            NAME = 0,
            COUN = 1,
            PURP = 5,
            LONDEG = 9,
            ORB = 7;

    /**
     * @see SatelliteDatabaseLoader#load(InputStream, SatelliteCatalogue.Builder)
     */
    @Override
    public void load(InputStream inputStream, SatelliteCatalogue.Builder builder) throws IOException {

        // Reads the satellite database, encoded in ASCII
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream, StandardCharsets.US_ASCII))) {

            reader.readLine(); // Ignores the header line, giving the names of the columns (which is thus unusable)

            String line; // The current line of data (i.e. the current satellite in the satellite catalogue)
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(","); // The 26 informations on the current satellite

                if (columns[ORB].equals("GEO") && !columns[18].contains("EOL")) {
                    String name = columns[NAME];
                    String country = columns[COUN];
                    String purpose = columns[PURP];

                    // The satellite's longitude (in degrees)
                    double lon = Angle.normalizePositive(Angle.ofDeg(defaultLonCases(columns)));

                    Satellite sat = new Satellite(name, country, purpose, lon);
                    builder.addSatellite(sat);
                }
            }
        }
    }

    /**
     * Additional method.
     * Returns the float value of the string at the given index of the given array.
     *
     * @param columns The array of strings
     * @return the float value of the string
     */
    private static double defaultLonCases(String[] columns) {
        return columns[SatelliteDatabaseLoader.LONDEG].isEmpty() ?
                0 : Double.parseDouble(columns[SatelliteDatabaseLoader.LONDEG]);
    }
}
