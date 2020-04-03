package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MyMoonModelTest {

    // Book p.166: The position of the Moon on 1 September 2003 at 0h UT
    private static final ZonedDateTime ZDT_20030901 = ZonedDateTime.of(
            LocalDate.of(2003, 9, 1),
            LocalTime.of(0, 0, 0, 0),
            ZoneOffset.UTC);

    private static final Moon MOON_2003 = MoonModel.MOON.at(
            Epoch.J2010.daysUntil(ZDT_20030901),
            new EclipticToEquatorialConversion(ZDT_20030901));

    // The position of the Moon on 1 September 1979 at 0h UT
    private static final ZonedDateTime ZDT_19790901 = ZonedDateTime.of(
            LocalDate.of(1979, 9, 1),
            LocalTime.of(0, 0),
            ZoneOffset.UTC);

    private static final Moon MOON_1979 = MoonModel.MOON.at(
            Epoch.J2010.daysUntil(ZDT_19790901),
            new EclipticToEquatorialConversion(ZDT_19790901));

    @Test
    void atWorks() {
        /*
        Sun Mean Anomaly not normalized in SunModel
        M0 = 23 6.6424 0680253826 degrees
        lambda0 = 158.1 559834896293 degrees
        l = 214.924000 20000326 degrees ==> correct
        Mm = 342.458607 5000033 degrees ==> correct
        Ev = 0.960 29427179309 degrees
        Ae = -0.15 51903913473046 degrees
        A3 = -0.3 0904437458828155 degrees
        Mm' = 343.8 83136537732 degrees
        Ec = -1.74 56991393875063 degrees
        A4 = -0.114 14209845392813 degrees
        l' = 214.1 7964362530222 degrees
        V = 0.610 1612548183397 degrees
        l'' = 214.7 8980488012056 degrees
        N = 54.164917 700000004 degrees
        N' = 54. 29855851063277 degrees
        y = 0.332 6051569728363
        x = -0.942 5904811878699
        lambdaM = 214.86 251480609556 degrees
        betaM = 1.716 257424835272 degrees
         */

        // Expected lambda (correction) : lambda = 214.86251 5         degrees
        // Actual lambda :                lambda = 214.86251 480609556 degrees


        // Expected beta (correction) : beta = 1.716257           degrees
        // Actual beta :                beta = 1.716257 424835272 degrees

        // Expected alpha (book) : alpha = 14.211 666666666666 h
        // Actual alpha :          alpha = 14.211 4564 57836277 h
        assertEquals(14.0 + 12.0 / 60.0 + 42.0 / 3600.0, MOON_2003.equatorialPos().raHr(), 1.0 / 3600.0);

        // Expected delta (book) : delta = -0.2011 8798138683525 radians
        // Actual delta :          delta = -0.2011 41713 46019355 radians
        assertEquals(-Angle.ofDMS(11, 31, 38), MOON_2003.equatorialPos().dec(), 1e-4);
    }

    @Test
    void angularSizeWorks(){
        assertEquals(0.009225908666849136, MOON_1979.angularSize());
    }

    @Test
    void infoWorks(){
        assertEquals("Lune (22.5%)", MOON_2003.info());
    }
}
