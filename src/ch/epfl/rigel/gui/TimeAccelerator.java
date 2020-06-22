package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * A time accelerator.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
@FunctionalInterface
public interface TimeAccelerator {

    /**
     * Returns the simulated time, i.e. the time that determines the instant of observation for which the sky is drawn.
     *
     * @param T0
     *            The initial simulated time (at the beginning of the animation)
     * @param deltaNano
     *            The real time (in nanoseconds) elapsed since the beginning of the animation
     * @return the simulated time
     */
    ZonedDateTime adjust(ZonedDateTime T0, long deltaNano);

    /**
     * Returns a continuous accelerator as a function of the acceleration factor.
     *
     * @param alpha
     *            The acceleration factor (unitless)
     * @return the continuous accelerator
     */
     static TimeAccelerator continuous(int alpha) {
         return (T0, deltaNano) -> T0.plusNanos(alpha * deltaNano);
    }

    /**
     * Returns a discrete accelerator as a function of the frequency and the step of the simulated time.
     *
     * @param frequency
     *            The stepping frequency of the simulated time
     * @param S
     *            The discrete step (in seconds) of the simulated time
     * @return the discrete accelerator
     */
    static TimeAccelerator discrete(int frequency, Duration S) {
        return (t0, deltaNano) -> {
            long accelerationFactor = (long) (frequency * 1e-9 * deltaNano);
            return t0.plusNanos(accelerationFactor * S.toNanos());
        };
    }
}
