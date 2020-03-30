package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class MyAsterismLoaderTest {
    static final String AST_CATALOGUE_NAME = "/asterisms.txt";

    @Test
    void asterismCatalogueIsCorrectlyInstalled() throws IOException {
        try (InputStream astStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME)) {
            assertNotNull(astStream);
        }
    }

    @Test
    void asterismCatalogueContainsRigel() throws IOException {
        try (InputStream astStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME);
             InputStream hygStream = getClass().getResourceAsStream(MyHygDatabaseLoaderTest.HYG_CATALOGUE_NAME)) {

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

    @Test
    void asterismLoader() throws IOException {
        try (InputStream astStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME);
             InputStream hygStream = getClass().getResourceAsStream(MyHygDatabaseLoaderTest.HYG_CATALOGUE_NAME)) {

            StarCatalogue.Builder builder = new StarCatalogue.Builder();
            builder.loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
            builder.loadFrom(astStream, AsterismLoader.INSTANCE);

            StarCatalogue catalogue = builder.build();

            Queue<Asterism> a = new ArrayDeque<>();
            Star betelgeuse = null;
            for (Asterism ast : catalogue.asterisms()) {
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Rigel")) {
                        a.add(ast);
                    }
                }
            }
            int astCount = 0;
            for (Asterism ast : a) {
                ++astCount;
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Betelgeuse")) {
                        betelgeuse = s;
                    }
                }
            }
            assertNotNull(betelgeuse);
            assertEquals(2, astCount);
        }
    }
}