package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public enum HygDatabaseLoader implements StarCatalogue.Loader {

    INSTANCE();

    private final static int HIP = 2, PROPER = 7, MAG = 14, CI = 17, RARAD = 24, DECRAD = 25, BAYER = 28, CON = 30;

    HygDatabaseLoader() {
    }


    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        reader.readLine();
        String line = "";

        while (line != null) {
            line = reader.readLine();

            if(line != null){
                String[] splittedString = line.split(",");
                EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.of(Double.parseDouble(splittedString[RARAD - 1]), Double.parseDouble(splittedString[DECRAD - 1]));

                String name = "" + splittedString[PROPER - 1];
                if (name.equals("")) {
                    if (splittedString[BAYER - 1] == "") {
                        name += ("? " + splittedString[CON - 1]);
                    } else {
                        name += (splittedString[BAYER - 1] + " " + splittedString[CON - 1]);
                    }
                }
                float ci = splittedString[CI - 1].equals("")  ? 0f : (float) Double.parseDouble(splittedString[CI - 1]);
                float mag = splittedString[MAG - 1].equals("") ? 0f : (float) Double.parseDouble(splittedString[MAG - 1]);
                int hipparcosID = splittedString[HIP - 1].equals("") ? 0 : Integer.parseInt(splittedString[HIP - 1]);
                builder.addStar(new Star(hipparcosID, name,equatorialCoordinates, mag, ci));
            }
        }
    }
}
