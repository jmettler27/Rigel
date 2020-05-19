package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class SatelliteDatabaseLoaderTest {

    @Test
    void load() {
        try(InputStream sa = getClass().getResourceAsStream("/active_satellites.csv")){
            SatelliteCatalogue catalogue = new SatelliteCatalogue.Builder()
                    .loadFrom(sa, SatelliteDatabaseLoader.INSTANCE)
                    .build();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}