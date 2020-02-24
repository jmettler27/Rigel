// Rigel stage 3

package ch.epfl.rigel.astronomy;

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
    void greenwich() {
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36), ZoneOffset.UTC);

        assertEquals(23, Angle.toHr(SiderealTime.greenwich(d)));
    }

    @Test
    void local() {
    }
}