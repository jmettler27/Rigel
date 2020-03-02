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
    void greenwichWorksAtVeryLowTolerance() {
        // GST at 14h 36m 51.67s UT on Greenwich date 22 April 1980 (p.23)

        // Note : The values of S0 (T0 in the book) and S1 (UT in the book) are rounded to the nearest millionth in the
        // book, and so is their addition (i.e. the GST) which explains the difference found for this value, only to the 3rd decimal
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51, 670000000), ZoneOffset.UTC);
        assertEquals(4.668119444, Angle.toHr(SiderealTime.greenwich(d)), 1e-6);

        ZonedDateTime d1 = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22), LocalTime.of(14, 36, 51, 67), ZoneOffset.UTC);
        assertEquals(1.2220619247737088, SiderealTime.greenwich(d1), 1e-13);
        System.out.println(SiderealTime.greenwich(d1));

        ZonedDateTime d2 = ZonedDateTime.of(LocalDate.of(2001, Month.JANUARY, 27), LocalTime.of(12, 0, 0, 0), ZoneOffset.UTC);
        assertEquals(5.355270290366605, SiderealTime.greenwich(d2), 1e-14);
        System.out.println(SiderealTime.greenwich(d2));

        ZonedDateTime d3 = ZonedDateTime.of(LocalDate.of(2004, Month.SEPTEMBER, 23), LocalTime.of(11, 0, 0, 0), ZoneOffset.UTC);
        assertEquals(2.9257399567031235, SiderealTime.greenwich(d3), 1e-13);

        ZonedDateTime d4 = ZonedDateTime.of(LocalDate.of(2001, Month.SEPTEMBER, 11), LocalTime.of(8, 14, 0, 0), ZoneOffset.UTC);
        assertEquals(1.9883078130455532, SiderealTime.greenwich(d4), 1e-14);
        System.out.println(SiderealTime.greenwich(d4));


    }

    @Test
    void localSiderealTimeWorks() {
        // Local ST on the longitude 64° W when the GST is 4h 40m 5.23s (p.27)
        ZonedDateTime d = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22), LocalTime.of(14, 36, 51, 670000000), ZoneOffset.UTC);
        assertEquals(0.401452778, Angle.toHr(SiderealTime.local(d, GeographicCoordinates.ofDeg(-64, 0.0))), 1e-6);

        ZonedDateTime d1 = ZonedDateTime.of(LocalDate.of(1980, 4, 22), LocalTime.of(14, 36, 51, 270000000), ZoneOffset.UTC);
        assertEquals(1.74570958832716, SiderealTime.local(d1, GeographicCoordinates.ofDeg(30, 45)), 1e-4);


    }
}