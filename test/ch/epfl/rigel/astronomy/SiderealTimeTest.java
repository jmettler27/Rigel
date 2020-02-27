// Rigel stage 3

package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class SiderealTimeTest {

    @Test
    void greenwichWorks() {
        // GST at 14h 36m 51.67s UT on Greenwich date 22 April 1980 (p.23)

        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 52), ZoneOffset.UTC);

        assertEquals(4.668, Angle.toHr(SiderealTime.greenwich(d)), 1e-3);

    }

    @Test
    void localSideralTimeWorks() {
        // Local ST on the longitude 64° W when the GST is 4h 40m 5.23s (p.27)
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 52), ZoneOffset.UTC);

        assertEquals(0.401, Angle.toHr(
                SiderealTime.local(d, GeographicCoordinates.ofDeg(-64, 0.0))),
                1e-3);
    }
}