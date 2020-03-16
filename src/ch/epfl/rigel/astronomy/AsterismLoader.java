package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public enum AsterismLoader implements StarCatalogue.Loader{
    INSTANCE();


    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
       /* BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String stringLine = reader.readLine();
        String[] splittedString = stringLine.split(",");
        EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.of(Double.parseDouble(splittedString[RARAD - 1]), Double.parseDouble(splittedString[DECRAD - 1]));

        String name = "" + splittedString[PROPER - 1];
        if(name == ""){
            if(splittedString[BAYER - 1] == ""){
                name += ("? " + splittedString[CON - 1]);
            } else{
                name += (splittedString[BAYER - 1] + " " + splittedString[CON - 1]);
            }
        }

        builder.addStar(new Star(
                Integer.parseInt(splittedString[HIP-1]),
                name,
                equatorialCoordinates,
                (float) Double.parseDouble(splittedString[MAG-1]),
                (float) Double.parseDouble(splittedString[CI - 1])));*/
    }
}
