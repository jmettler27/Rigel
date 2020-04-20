package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

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
     * @param T0 The initial simulated time
     * @param deltaTNano The real time (in nanoseconds) elapsed since the beginning of the animation
     * @return the simulated time
     */
    ZonedDateTime adjust(ZonedDateTime T0, long deltaTNano);

    /**
     * Returns a continuous accelerator as a function of the acceleration factor.
     *
     * @param alpha The acceleration factor
     * @return the continuous accelerator
     */
     static TimeAccelerator continuous(int alpha) {
        return (T0, deltaTNano) -> T0.plus(alpha * deltaTNano, ChronoUnit.NANOS);
    }

    /**
     * Returns a discrete accelerator as a function of the frequency and the step of the simulated time.
     *
     * @param frequency The stepping frequency of the simulated time
     * @param S The discrete step of the simulated time
     * @return the discrete accelerator
     */
    static TimeAccelerator discrete(int frequency, Duration S) {
        return (T0, deltaTNano) -> T0.plus(S.multipliedBy((long) Math.floor(frequency * deltaTNano)));
    }
}
