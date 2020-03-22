package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MyAsterismLoaderTest {
    private static final String AST_CATALOGUE_NAME = "/asterisms.txt", HYG_CATALOGUE_NAME = "/hygdata_v3.csv";

    @Test
    void asterismCatalogueIsCorrectlyInstalled() throws IOException {
        try (InputStream astStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME)) {
            assertNotNull(astStream);
        }
    }

    @Test
    void asterismCatalogueContainsRigel() throws IOException {
        try (InputStream astStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME);
             InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {

            StarCatalogue.Builder builder = new StarCatalogue.Builder();
            builder.loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
            builder.loadFrom(astStream, AsterismLoader.INSTANCE);

            StarCatalogue catalogue = builder.build();

            Star rigel = null;
            for (Star s : catalogue.stars()) {
                if (s.name().equalsIgnoreCase("rigel"))
                    rigel = s;
            }
            assertNotNull(rigel);
        }
    }
}