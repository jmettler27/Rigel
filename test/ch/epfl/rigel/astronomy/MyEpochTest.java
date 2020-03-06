// Rigel stage 3

package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class MyEpochTest {

    // Non-trivial case : 2000-01-03, 18h00 UTC
    private ZonedDateTime d1 = ZonedDateTime.of(
            LocalDate.of(2000, Month.JANUARY, 3), LocalTime.of(18, 0),
            ZoneOffset.UTC);

    // Trivial case : 2000-01-01, 12h00 UTC
    private ZonedDateTime trivialEpoch = ZonedDateTime.of(
            LocalDate.of(2000, Month.JANUARY, 1), LocalTime.of(12, 0),
            ZoneOffset.UTC);

    // Trivial case : 2010-12-31, 0h00 UTC
    private ZonedDateTime trivialEpoch1 = ZonedDateTime.of(
            LocalDate.of(2009, Month.DECEMBER, 31), LocalTime.of(0, 0),
            ZoneOffset.UTC);

    @Test
    void daysUntil() {
        assertEquals(2.25, Epoch.J2000.daysUntil(d1));
        assertEquals(0, Epoch.J2000.daysUntil(trivialEpoch));
        assertEquals(0, Epoch.J2010.daysUntil(trivialEpoch1));
    }

    @Test
    void julianCenturiesUntil() {
        assertEquals(0.0000616, Epoch.J2000.julianCenturiesUntil(d1), 1e-7);
        assertEquals(0, Epoch.J2000.julianCenturiesUntil(trivialEpoch));
        assertEquals(0, Epoch.J2010.julianCenturiesUntil(trivialEpoch1));
    }
}