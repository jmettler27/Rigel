package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MyObservedSkyTest {

    private static final ZonedDateTime ZDT_20200404 = ZonedDateTime.of(
            LocalDate.of(2020, Month.APRIL, 4),
            LocalTime.of(0, 0),
            ZoneOffset.UTC);

    private static final StereographicProjection PROJECTION = new StereographicProjection(
            HorizontalCoordinates.ofDeg(20, 22));

    private static final GeographicCoordinates WHERE = GeographicCoordinates.ofDeg(30, 45);

    EquatorialToCartesianConversion EQU_TO_CART = new EquatorialToCartesianConversion(ZDT_20200404, WHERE, PROJECTION);

    @Test
    void sun() {
    }

    @Test
    void sunPosition() {
    }

    @Test
    void moon() {
    }

    @Test
    void moonPosition() {
    }

    @Test
    void planets() {
    }

    @Test
    void planetPositions() throws IOException {
        StarCatalogue catalogue = buildCatalogue();
        ObservedSky sky = new ObservedSky(ZDT_20200404, WHERE, PROJECTION, catalogue);
    }

    @Test
    void stars() {
    }

    @Test
    void starPositions() throws IOException {
    }

    @Test
    void asterisms() {
    }

    @Test
    void asterismsIndices() {
    }

    @Test
    void objectClosestTo() throws IOException {
        StarCatalogue catalogue = buildCatalogue();
        ObservedSky sky = new ObservedSky(ZDT_20200404, WHERE, PROJECTION, catalogue);

        CartesianCoordinates searchPoint = EQU_TO_CART
                .apply(EquatorialCoordinates.of(0.004696959812148989, -0.861893035343076));

        Optional<CelestialObject> closestObject = sky.objectClosestTo(searchPoint, 0.1);
        assertEquals("Tau Phe", closestObject.get().name());

        Optional<CelestialObject> closestObject1 = sky.objectClosestTo(searchPoint, 0.001);
        assertEquals(Optional.empty(), closestObject1);
    }

    @Test
    void addPositionsWorks() {
    }


    private StarCatalogue buildCatalogue() throws IOException {
        StarCatalogue catalogue;
        try (InputStream hygStream = getClass().getResourceAsStream(MyHygDatabaseLoaderTest.HYG_CATALOGUE_NAME);
             InputStream astStream = getClass().getResourceAsStream(MyAsterismLoaderTest.AST_CATALOGUE_NAME)) {
            catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(astStream, AsterismLoader.INSTANCE)
                    .build();
        }
        return catalogue;
    }
}
