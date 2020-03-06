package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * An astronomical epoch, i.e. a date/time pair corresponding to a reference
 * time (a moment).
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public enum Epoch {

    J2000(2000, Month.JANUARY, 1, 12, 0, ZoneOffset.UTC),

    J2010(2009, Month.DECEMBER, 31, 0, 0, ZoneOffset.UTC);

    // The date-time pair associated to its time-zone
    private final ZonedDateTime zonedDateTime;

    private final double JULIAN_CENTURIES_DAYS = 36525.0;

    /**
     * Constructs an astronomical epoch through its time and spatial
     * informations.
     * 
     * @param year
     *            The year of the reference time
     * @param month
     *            The month of the reference time
     * @param dayOfMonth
     *            The day of the reference time
     * @param hour
     *            The hour of the reference time (in the UTC time-zone)
     * @param minute
     *            The minute of the reference time (in the UTC time-zone)
     * @param zoneOffset
     *            The associated time-zone
     */
    private Epoch(int year, Month month, int dayOfMonth, int hour, int minute,
            ZoneOffset zoneOffset) {

        zonedDateTime = ZonedDateTime.of(LocalDate.of(year, month, dayOfMonth),
                LocalTime.of(hour, minute), zoneOffset);
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

        return ((nbMillis / 1000.0) / 3600.0) / 24.0;
    }

    /**
     * Returns the number of Julian centuries (36525 days) between this epoch
     * and the given epoch.
     *
     * @param when
     *            The given epoch
     * @return the number of Julian centuries between this epoch and the given
     *         epoch
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        // The number of days between this epoch and the given epoch
        double nbDays = daysUntil(when);

        return (nbDays / JULIAN_CENTURIES_DAYS);
    }
}
