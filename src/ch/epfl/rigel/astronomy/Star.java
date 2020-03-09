package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final float colorIndex;

    /**
     * Constructs a celestial object with the given name, equatorial position,
     * angular size and magnitude.
     *
     * @param name          The object's name
     * @param equatorialPos The object's equatorial positions
     * @param angularSize   The object's angular size : is 0
     * @param magnitude     The object's magnitude
     * @throws IllegalArgumentException if the angular size is strictly negative, and/or if the hipparcos ID is negative, and/or the color index is not contained in [-0.5, 5.5]
     * @throws NullPointerException     if the name or the equatorial position are null
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);


        if (hipparcosId < 0 || !(ClosedInterval.of(-0.5, 5.5).contains(colorIndex))) {
            throw new IllegalArgumentException();
        }
        this.hipparcosId = hipparcosId;
        this.colorIndex = colorIndex;
    }

    public int hipparcosId() {
        return hipparcosId;
    }

    public int colorTemperature() {
        float c = colorIndex;
        double T = 4600.0 * (1.0 / (0.92 * c + 1.7) + 1.0 / (0.92 * c + 0.62));

        return (int) Math.floor(T);
    }
}
