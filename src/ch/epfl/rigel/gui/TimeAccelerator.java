package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@FunctionalInterface
public interface TimeAccelerator {

    ZonedDateTime adjust(ZonedDateTime T0, long deltaTNano);

    static TimeAccelerator continuous(int alpha) {
        return (T0, deltaTNano) -> T0.plus(alpha * deltaTNano, ChronoUnit.NANOS);
    }

    static TimeAccelerator discrete(int frequence, Duration S) {
        return (T0, deltaTNano) -> T0.plus(S.multipliedBy((long) Math.floor(frequence * deltaTNano)));
    }
}
