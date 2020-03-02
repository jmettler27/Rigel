package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

public class TestMain {

    public static void main (String[] args){

        Moon moon = new Moon(EquatorialCoordinates.of(0, 0), 0.5f, 0.1f,  0.3752f);
        System.out.println(moon);
    }
}
