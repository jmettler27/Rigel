package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

/**
 * The Moon.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class Moon extends CelestialObject {

    // The illuminated percentage (between 0 and 1) of the Moon's "disc" illuminated by the Sun, as seen from the Earth
    private final float phase;

    // The valid closed interval [0,1] for the phase
    private final static ClosedInterval PHASE_INTERVAL = ClosedInterval.of(0,1);

    /**
     * Constructs the Moon at a point in time with the given equatorial position, angular size, magnitude and phase.
     * 
     * @param equatorialPos
     *            The Moon's equatorial position
     * @param angularSize
     *            The Moon's angular size
     * @param magnitude
     *            The Moon's magnitude
     * @param phase
     *            The Moon's phase
     * @throws IllegalArgumentException
     *             if the angular size is < 0
     *             and/or the phase is not contained in [0,1]
     * @throws NullPointerException
     *             if the equatorial position is null
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        this.phase = (float) Preconditions.checkInInterval(PHASE_INTERVAL, phase);
    }

    /**
     * @see CelestialObject#info()
     */
    @Override
    public String info() {
        return super.info() + String.format(Locale.ROOT, " (%.1f", phase * 100.0) + "%)";
    }
}
