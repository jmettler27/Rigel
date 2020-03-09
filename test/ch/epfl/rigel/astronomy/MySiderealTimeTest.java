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

class MySiderealTimeTest {

    @Test
    void greenwichWorks() {
        // GST at 14h 36m 51.67s UT on Greenwich date 22 April 1980 (p.23)

        // Expected (book) : GST = 4.668119 444h       ; GST = 4h 40m 5.229 9984s
        // Actual          : GST = 4.668119 326877547h ; GST = 4h 40m 5.229 57675918s

        // Expected T : -0.196947296
        // Actual T   : -0.196947296 3723477

        // Expected UT : 14.614352 89
        // Actual UT   : 14.614352 777777777

        // Expected A : 14.654365 66
        // Actual A   : 14.654365 545777228
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22), LocalTime.of(14, 36, 51, 670000000), ZoneOffset.UTC);
        assertEquals(4.668119444, Angle.toHr(SiderealTime.greenwich(d)), 1e-6);

        ZonedDateTime d1 = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22), LocalTime.of(14, 36, 51, 67), ZoneOffset.UTC);
        assertEquals(1.2220619247737088, SiderealTime.greenwich(d1), 1e-13);

        ZonedDateTime d2 = ZonedDateTime.of(LocalDate.of(2001, Month.JANUARY, 27), LocalTime.of(12, 0, 0, 0), ZoneOffset.UTC);
        assertEquals(5.355270290366605, SiderealTime.greenwich(d2), 1e-14);

        ZonedDateTime d3 = ZonedDateTime.of(LocalDate.of(2001, Month.SEPTEMBER, 11), LocalTime.of(8, 14, 0, 0), ZoneOffset.UTC);
        assertEquals(1.9883078130455532, SiderealTime.greenwich(d3), 1e-14);

        // Expected : GST = 4h 34m 07.6 49s         ; GST = 4.568791 3888889h
        // Actual   : GST = 4h 34m 07.6 5088552506s ; GST = 4.568791 912645853
        ZonedDateTime now = ZonedDateTime.of(LocalDate.of(2020, Month.MARCH, 5), LocalTime.of(17, 38, 25, 0), ZoneOffset.UTC);
        assertEquals(4 + 34/60.0 + 7.649/3600.0, Angle.toHr(SiderealTime.greenwich(now)), 1e-6);

        // Expected : GST = 20h 29m 02.340s          ; GST = 20.483983 333333335h
        // Actual   : GST = 20h 29m 02.340 89845434s ; GST = 20.483983 582903978h
        ZonedDateTime d4 = ZonedDateTime.of(LocalDate.of(2015, Month.JUNE, 9), LocalTime.of(3, 20, 18, 0), ZoneOffset.UTC);
        assertEquals(20 + 29.0/60.0 + 2.340/3600.0, Angle.toHr(SiderealTime.greenwich(d4)), 1e-6);
    }

    @Test
    void localSiderealTimeWorks() {
        // Local ST on the longitude 64° W when the GST is 4h 40m 5.23s (p.27)
        // Expected (book) : LST = 0.401452 778 h            ; LST = 0h 24m 5.23s
        // Actual          : LST = 0.401452 660210881h   ; LST = 0h 24m 5.22957675918s
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22), LocalTime.of(14, 36, 51, 670000000), ZoneOffset.UTC);
        assertEquals(0.401452778, Angle.toHr(SiderealTime.local(d, GeographicCoordinates.ofDeg(-64, 0.0))), 1e-6);
    }
}