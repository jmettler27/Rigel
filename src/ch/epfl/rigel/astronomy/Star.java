package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * A star.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final float colorIndex;
    private final static ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval
            .of(-0.5, 5.5);

    /**
     * Constructs a star with the given name, equatorial position, angular size
     * and magnitude.
     * 
     * @param hipparcosId
     *            The star's identification number in the Hipparcos catalogue
     * @param name
     *            The star's name
     * @param equatorialPos
     *            The star's equatorial position
     * @param magnitude
     *            The star's magnitude (unitless)
     * @param colorIndex
     *            The star's B-V color index
     * 
     * @throws IllegalArgumentException
     *             if the angular size is strictly negative, and/or if the
     *             Hipparcos ID is strictly negative, and/or if the color index
     *             is not contained in [-0.5, 5.5]
     * 
     * @throws NullPointerException
     *             if the name or the equatorial position are null
     */
    public Star(int hipparcosId, String name,
            EquatorialCoordinates equatorialPos, float magnitude,
            float colorIndex) {
        super(name, equatorialPos, 0, magnitude);

        if (!(hipparcosId >= 0 && COLOR_INDEX_INTERVAL.contains(colorIndex))) {
            throw new IllegalArgumentException();
        }
        this.hipparcosId = hipparcosId;
        this.colorIndex = colorIndex;
    }

    /**
     * Returns the identification number in the Hipparcos catalogue.
     * 
     * @return the identification number in the Hipparcos catalogue
     */
    public int hipparcosId() {
        return hipparcosId;
    }

    /**
     * Returns the color temperature (in degrees Kelvin).
     * 
     * @return the color temperature (in degrees Kelvin)
     */
    public int colorTemperature() {
        double T = 4600.0 * (1.0 / (0.92 * colorIndex + 1.7)
                + 1.0 / (0.92 * colorIndex + 0.62));
        return (int) Math.floor(T);
    }
}
