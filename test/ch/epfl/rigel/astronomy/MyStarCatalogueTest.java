package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MyStarCatalogueTest {

    static StarCatalogue CATALOGUE;

    static {
        try {
            CATALOGUE = buildCatalogue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void catalogueContainsACorrectNumberOfStarsAndAsterisms() throws IOException{
        assertEquals(5067, CATALOGUE.stars().size());
        assertEquals(153, CATALOGUE.asterisms().size());
    }
    @Test
    void stars() throws IOException {

    }

    @Test
    void asterisms() throws IOException {

        // Checks if the number of indices of each asterism is equal to the number of stars composing it
        for (Asterism asterism : CATALOGUE.asterisms()) {
            int nbIndices = CATALOGUE.asterismIndices(asterism).size();
            int nbStars = asterism.stars().size();
            assertEquals(nbIndices, nbStars);
        }
    }

    @Test
    void asterismIndices() throws IOException {
    }

    private static StarCatalogue buildCatalogue() throws IOException {
        try (InputStream astStream = MyStarCatalogueTest.class.getResourceAsStream(MyAsterismLoaderTest.AST_CATALOGUE_NAME);
             InputStream hygStream = MyStarCatalogueTest.class.getResourceAsStream(MyHygDatabaseLoaderTest.HYG_CATALOGUE_NAME)) {

            return new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE) // Loads the stars
                    .loadFrom(astStream, AsterismLoader.INSTANCE) // Loads the asterisms
                    .build(); // Builds the catalogue
        }
    }
}