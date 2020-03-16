package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE();


    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        reader.readLine();
        String line = "";
        List<Star> stars = builder.stars();

        while (line != null) {
            line = reader.readLine();
            if (line != null) {
                String[] splittedString = line.split(",");

                List<Star> starsAsterism = new ArrayList<>();

                for (int i = 0; i < splittedString.length; ++i) {
                    int hipparcosID = (Integer.parseInt(splittedString[i]));
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
