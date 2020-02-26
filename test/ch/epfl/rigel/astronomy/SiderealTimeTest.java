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
    void greenwichWorksWithOneTenthTolerance() {
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51), ZoneOffset.UTC);

        assertEquals(4.66, Angle.toHr(SiderealTime.greenwich(d)), 1e-1);
    }

    @Test
    void local() {
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51), ZoneOffset.UTC);

        assertEquals(0.40, Angle.toHr(SiderealTime.local(d, GeographicCoordinates.ofDeg(-64, 0.0))), 1e-1);
    }
}