package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

public final class Moon extends CelestialObject {

    private final float phase;

    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        if (ClosedInterval.of(0, 1).contains(phase)) {
            this.phase = phase;
        } else {
            throw new IllegalArgumentException("The phase must be contained in [0,1]");
        }

    }

    @Override
    public String info() {
        return super.info() + String.format(Locale.ROOT, " (%.1f", phase * 100.0) + "%)";

    }
}
