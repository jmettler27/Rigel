package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MySunModelTest {

    @Test
    void atWorks() {
        // Book p.105 : The right ascension and declination of the Sun on Greenwich date 27 July 2003 at 0 h UT
        ZonedDateTime when = ZonedDateTime.of(
                LocalDate.of(2003, Month.JULY, 27),
                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC);
        EclipticToEquatorialConversion eclipticToEquatorialConversion = new EclipticToEquatorialConversion(when);

        // The Sun at 0 h UT on Greenwich date 27 July 2003
        Sun sun = SunModel.SUN.at(Epoch.J2010.daysUntil(when), eclipticToEquatorialConversion);

        // Expected ra (book) : 8h 23m 34s             = 8.392 777777777777h
        // Actual ra          : 8h 23m 33.65810987214h = 8.392 682808297806h
        assertEquals((8.0 + 23.0 / 60.0 + 34.0 / 3600.0), sun.equatorialPos().raHr(), 10.0 / 3600.0);

        // Expected dec (book) : 19◦ 21' 10'             = 19.352 77777777777°
        // Actual dec          : 19◦ 21' 10.38143150466' = 19.352 88373097352°
        assertEquals(Angle.ofDMS(19, 21, 10), sun.equatorialPos().dec(), Angle.ofDMS(0, 0, 1 / 2d));

    }

    @Test
    void atWorks1() {
        assertEquals(5.9325494700300885, SunModel.SUN.at(27 + 31,
                new EclipticToEquatorialConversion(
                ZonedDateTime.of(LocalDate.of(2010, Month.FEBRUARY, 27),
                        LocalTime.of(0, 0), ZoneOffset.UTC)))
                .equatorialPos().ra());

        assertEquals(8.392682808297808, SunModel.SUN.at(-2349,
                new EclipticToEquatorialConversion(
                ZonedDateTime.of(LocalDate.of(2003, Month.JULY, 27),
                        LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)))
                .equatorialPos().raHr(), 1e-14);

        assertEquals(19.35288373097352, SunModel.SUN.at(-2349, new EclipticToEquatorialConversion(
                ZonedDateTime.of(LocalDate.of(2003, Month.JULY,27),
                        LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)))
                .equatorialPos().decDeg());
    }
}