package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * The Sun.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Sun extends CelestialObject {

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    /**
     * Constructs the Sun at a point in time with the given ecliptic position quatorial position, angular size
     * and mean anomaly.
     * 
     * @param eclipticPos
     *            The Sun's ecliptic position
     * @param equatorialPos
     *            The Sun's equatorial position
     * @param angularSize
     *            The Sun's angular size
     * @param meanAnomaly
     *            The Sun's mean anomaly
     * 
     * @throws IllegalArgumentException
     *             if the angular size is strictly negative
     * @throws NullPointerException
     *             if the ecliptic and/or the equatorial position are null
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, -26.7f);

        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * Returns the ecliptic position.
     * 
     * @return the ecliptic position
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * Returns the mean anomaly.
     * 
     * @return the mean anomaly
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }
}
