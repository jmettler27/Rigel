package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * An astronomical epoch, i.e. a date/time pair corresponding to a reference time (an instant).
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum Epoch {

    J2000(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1), LocalTime.NOON, ZoneOffset.UTC)),

    J2010(ZonedDateTime.of(LocalDate.of(2009, Month.DECEMBER, 31), LocalTime.MIDNIGHT, ZoneOffset.UTC));

    private final ZonedDateTime zonedDateTime;

    // The number of milliseconds per day and the number of days per Julian century
    private static final double MILLIS_PER_DAY = 1000.0 * 3600 * 24.0;
    private static final double DAYS_PER_JULIAN_CENTURY = 36525.0;


    /**
     * Constructs an astronomical epoch through its time and spatial information.
     *
     * @param zonedDateTime
     *            The date/time pair associated to its time-zone
     */
    Epoch(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    /**
     * Returns the number of days between this epoch and the given epoch.
     *
     * @param when
     *            The given epoch
     * @return the number of days between this epoch and the given epoch
     */
    public double daysUntil(ZonedDateTime when) {
        // The number of milliseconds between this epoch and the given epoch
        double nbMillis = zonedDateTime.until(when, ChronoUnit.MILLIS);
        return nbMillis / MILLIS_PER_DAY;
    }

    /**
     * Returns the number of Julian centuries (36525 days) between this epoch and the given epoch.
     *
     * @param when
     *            The given epoch
     * @return the number of Julian centuries between this epoch and the given epoch
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        // The number of days between this epoch and the given epoch
        double nbDays = daysUntil(when);
        return nbDays / DAYS_PER_JULIAN_CENTURY;
    }
}
