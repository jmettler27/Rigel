package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.*;

class MyObservedSkyTest {


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
    void planetPositions() {
    }

    @Test
    void stars() {
    }

    @Test
    void starPositions() {
    }

    @Test
    void asterisms() {
    }

    @Test
    void asterismsIndices() {
    }

    @Test
    void objectClosestTo() {
    }

    @Test
    void addPositionsWorks(){
        List< ? extends CelestialObject> list = new ArrayList<>();

        Planet planet = new Planet("name", EquatorialCoordinates.of(0,0), 0f, 0f);
        Map<CelestialObject, EquatorialCoordinates> map = new HashMap<>();
        map.put(planet, planet.equatorialPos());


    }

}