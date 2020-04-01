package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void variousTestsAndReadablePrintfOnCompletelyFinishedStarCatalogue() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(MyHygDatabaseLoaderTest.HYG_CATALOGUE_NAME);
             InputStream asterismStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME)) {

            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .build();

            Star rigel = null;

            for (Star s : catalogue.stars()) {
                if (s.name().equalsIgnoreCase("rigel"))
                    rigel = s;
            }
            assertNotNull(rigel);

            List<Star> allStar = new ArrayList<>(catalogue.stars());

            System.out.println("LIST OF STARS :");
            for (Star s : allStar) {
                System.out.print(s.hipparcosId() + " ");
            } // should print out the same star IDS as in the text file (check visually)
            System.out.println();
            System.out.println();

            System.out.println("ASTERISMS : ");
            int i;

            // vérifier visuellement en utilisant CTRL-F que les astérismes contenu dans ASTERISMS sont bien les memes
            // flemme de coder une méthode qui vérifie automatiquement
            for (Asterism asterism : catalogue.asterisms()) {
                List<Integer> cAstInd = catalogue.asterismIndices(asterism);
                i = 0;
                for (Star star : asterism.stars()) {
                    System.out.print("Hip : ");
                    System.out.print(star.hipparcosId());
                    System.out.print("  foundHipparcos : ");
                    System.out.print(allStar.get(cAstInd.get(i)).hipparcosId());

                    //TEST : l'index stocké dans asterismIndices renvoie le meme hipparcosId que l'index stocké dans
                    // l'astérisme voulu :
                    assertEquals(allStar.get(cAstInd.get(i)).hipparcosId(), star.hipparcosId());
                    System.out.print(" ||| ");
                    i++;
                }
                System.out.println();
            }
        }
    }
}