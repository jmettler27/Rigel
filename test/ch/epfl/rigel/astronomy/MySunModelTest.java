package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MySunModelTest {

    // Book p.105 : The right ascension and declination of the Sun on Greenwich date 27 July 2003 at 0 h UT
    private static final ZonedDateTime ZDT_20030727 = ZonedDateTime.of(
            LocalDate.of(2003, Month.JULY, 27),
            LocalTime.of(0, 0, 0, 0),
            ZoneOffset.UTC);

    private static final Sun SUN_2003 = SunModel.SUN.at(Epoch.J2010.daysUntil(ZDT_20030727), new EclipticToEquatorialConversion(ZDT_20030727));

    private static final ZonedDateTime ZDT_19880727 = ZonedDateTime.of(
            LocalDate.of(1988, Month.JULY, 27),
            LocalTime.of(0, 0, 0, 0),
            ZoneOffset.UTC);

    private static final Sun SUN_1988 = SunModel.SUN.at(Epoch.J2010.daysUntil(ZDT_19880727), new EclipticToEquatorialConversion(ZDT_19880727));


    @Test
    void atWorks() {
        assertEquals(5.9325494700300885, SunModel.SUN.at(27 + 31,
                new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(2010, Month.FEBRUARY, 27),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC)))
                .equatorialPos().ra());

        // Expected ra (book) : 8h 23m 34s             = 8.392 777777777777h
        // Actual ra          : 8h 23m 33.65810987214h = 8.392 682808297806h
        //                                               8.392 6828082978  h
        assertEquals((8.0 + 23.0 / 60.0 + 34.0 / 3600.0), SUN_2003.equatorialPos().raHr(), 10.0 / 3600.0);

        // Expected dec (book) : 19◦ 21' 10'             = 19.352 77777777777°
        // Actual dec          : 19◦ 21' 10.38143150466' = 19.352 88373097352°
        assertEquals(Angle.ofDMS(19, 21, 10), SUN_2003.equatorialPos().dec(), Angle.ofDMS(0, 0, 1 / 2d));

        assertEquals(19.35288373097352, SUN_2003.equatorialPos().decDeg());
        assertEquals(0.3353207024580374, SUN_1988.equatorialPos().dec());
    }

    @Test
    void angularSizeWorks(){
        assertEquals(0.009162353351712227, SUN_1988.angularSize());
    }
}