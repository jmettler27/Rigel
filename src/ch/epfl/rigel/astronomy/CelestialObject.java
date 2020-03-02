package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Locale;
import java.util.Objects;

public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;

    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        if (angularSize < 0) {
            throw new IllegalArgumentException("The angular size must be >= 0");
        }
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }

    public final String name() {
        return name;
    }

    public final double angularSize() {
        return angularSize;
    }

    public final double magnitude() {
        return magnitude;
    }

    public final EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    public String info() {
        return name; //"Celestial object : " + name + ", located at " + equatorialPos.toString() + ", of angular size " + angularSize + ", of magnitude " + magnitude;
    }

    @Override
    public final String toString() {
        return info();
    }


}
