package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * A star.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final float colorIndex;

    // The valid closed interval [-0.5,5.5] (unitless) for the star's color index
    private static final ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval.of(-0.5f, 5.5f);

    /**
     * Constructs a star with the given name, equatorial position, angular size and magnitude.
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
     * @throws IllegalArgumentException
     *             if the Hipparcos ID is < 0,
     *             and/or the color index is not contained in [-0.5,5.5]
     * @throws NullPointerException
     *             if the name and/or the equatorial position are null
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);

        Preconditions.checkArgument(hipparcosId >= 0);
        this.colorIndex = (float) Preconditions.checkInInterval(COLOR_INDEX_INTERVAL, colorIndex);
        this.hipparcosId = hipparcosId;
    }

    /**
     * Returns the identification number of this star in the Hipparcos catalogue.
     * @return the identification number of this star in the Hipparcos catalogue
     */
    public int hipparcosId() {
        return hipparcosId;
    }

    /**
     * Returns the color temperature of this star (in degrees Kelvin).
     * @return the color temperature of this star (in degrees Kelvin)
     */
    public int colorTemperature() {
        double T = 4600.0 * (1.0 / (0.92 * colorIndex + 1.7) + 1.0 / (0.92 * colorIndex + 0.62));
        return (int) T;
    }
}
