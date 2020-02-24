package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 *
 */
public enum Epoch {

    J2000(2000, Month.JANUARY, 1, 12, 0, ZoneOffset.UTC),
    J2010(2009, Month.DECEMBER, 31, 0, 0, ZoneOffset.UTC);

    private ZonedDateTime zonedDateTime;

    /**
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param zoneOffset
     */
    Epoch(int year, Month month, int dayOfMonth, int hour, int minute, ZoneOffset zoneOffset) {
        this.zonedDateTime = ZonedDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.of(hour, minute), zoneOffset);
    }

    /**
     *
     *
     * @param when
     * @return
     */
    public double daysUntil(ZonedDateTime when) {
        double nbMillis = zonedDateTime.until(when, ChronoUnit.MILLIS);
        return ((nbMillis / 1000.0) / 3600.0) / 24.0;
    }

    /**
     *
     *
     * @param when
     * @return
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        double nbDays = daysUntil(when);

        return (nbDays / 36525.0);
    }


}
