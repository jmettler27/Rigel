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

    J2000(2000, Month.JANUARY, 1, 12, 0, ZoneOffset.UTC),

    J2010(2009, Month.DECEMBER, 31, 0, 0, ZoneOffset.UTC);

    private final ZonedDateTime zonedDateTime; // The date/time pair associated to its time-zone

    // The number of milliseconds per day and the number of days per Julian century
    private static final double MILLIS_PER_DAY = 1000.0 * 3600 * 24.0;
    private static final double DAYS_PER_JULIAN_CENTURY = 36525.0;


    /**
     * Constructs an astronomical epoch through its time and spatial information.
     *
     * @param year
     *            The year of the reference time
     * @param month
     *            The month of the reference time
     * @param dayOfMonth
     *            The day of the reference time
     * @param hour
     *            The hour of the reference time (in the associated time-zone)
     * @param minute
     *            The minute of the reference time (in the associated time-zone)
     * @param zoneOffset
     *            The associated time-zone
     */
    Epoch(int year, Month month, int dayOfMonth, int hour, int minute, ZoneOffset zoneOffset) {
        zonedDateTime = ZonedDateTime.of(
                LocalDate.of(year, month, dayOfMonth),
                LocalTime.of(hour, minute),
                zoneOffset);
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
