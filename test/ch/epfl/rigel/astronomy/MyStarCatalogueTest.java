package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MyStarCatalogueTest {

    @Test
    void stars() throws IOException {
        try (InputStream astStream = getClass().getResourceAsStream(MyAsterismLoaderTest.AST_CATALOGUE_NAME);
             InputStream hygStream = getClass().getResourceAsStream(MyHygDatabaseLoaderTest.HYG_CATALOGUE_NAME)) {

            StarCatalogue.Builder builder = new StarCatalogue.Builder();
            builder.loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
            builder.loadFrom(astStream, AsterismLoader.INSTANCE);

            StarCatalogue catalogue = builder.build();

            System.out.println(catalogue.stars().get(1020).name());

        }
    }

    @Test
    void asterisms() {
    }

    @Test
    void asterismIndices() {
    }
}