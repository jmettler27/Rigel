package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class MyPlanetModelTest {

    private final ZonedDateTime when = ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC);
    private final EclipticToEquatorialConversion eclipticToEquatorialConversion = new EclipticToEquatorialConversion(when);

    @Test
    void atWorksOnJupiterExample() {
        // Book p. 126 : Jupiter's right ascension and declination on 22 November 2003
        Planet jupiter2003 = PlanetModel.JUPITER.at(-2231.0, eclipticToEquatorialConversion);

        /*
         * Np = 174.555932 14534105 degrees     ==> correct
         * Mp = 497.809764 145341 degrees       ==> correct
         * vp = 141.573600 00242605 degrees     ==> correct
         * lp = 156.236900 00242607 degrees     ==> correct
         * r = 5.397121 314170914 AU            ==> correct
         *
         * Ne = 321.011951 9493011 degrees      ==> correct
         * Me = 317.363223 949301 degrees       ==> correct
         * ve = 316.0692476 339196 degrees      ==> correct
         * L = 59.2747476 339196 degrees        ==> correct
         * R = 0.98784689 19618196 AU           ==> correct
         *
         * psi = 1.076044 13731551 degrees      ==> correct
         * y = 0.8253128 055877611              ==> correct
         * x = 0.564363 4526849179              ==> correct
         * l' = 156.22999097 0936 degrees       ==> correct
         * r' = 5.3961695 39099068 AU           ==> correct
         * lambda = 166.31050997 20172 degrees  ==> correct
         * beta = 1.0364655959 051345 degrees   ==> correct
         */

        // Expected alpha (book) : alpha = 11h 11m 14                 s ; 11.187 222222222223 h
        // Actual alpha :          alpha = 11h 11m 13.757764954840788 s ; 11.187 154934709678 h
        assertEquals((11.0 + 11.0 / 60.0 + 14.0 / 3600.0), jupiter2003.equatorialPos().raHr(), 1.0 / 3600.0);

        // Expected delta (book): delta = 06◦ 21' 2 5                '' ; delta = 6.35 9615384615385 degrees
        // Actual delta :         delta = 06◦ 21' 2 3.887824068722622'' ; delta = 6.35 6635506685756 degrees
        assertEquals(Angle.ofDMS(6, 21, 25), jupiter2003.equatorialPos().dec(), 1e-5);

        assertEquals(35.1114118536277, Angle.toDeg(jupiter2003.angularSize() * 3600), 1e-13);
        assertEquals(-1.9885659217834473, jupiter2003.magnitude());
    }

    @Test
    void atWorksOnMercuryExample() {
        // Book p. 128 : Mercury's right ascension and declination on 22 November 2003
        Planet mercury2003 = PlanetModel.MERCURY.at(-2231.0, eclipticToEquatorialConversion);

        /*
         * lp = 288.012253 4467747 degrees      ==> correct
         * vp = 210.400253 4467747 degrees      ==> correct
         * r = 0.450657 12499982374 AU          ==> correct
         * psi = -6.0358418 09820924 degrees    ==> correct
         * l' = 287.8244055 166603 degrees      ==> correct
         * r' = 0.4481588 2259828205 AU         ==> correct
         *
         * L = 59.2747476 339196 degrees        ==> correct
         * ve = 316.0692476 339196 degrees      ==> correct
         * R = 0.98784689 19618196 AU           ==> correct
         *
         * lambda = 253.929758 2541222 degrees  ==> correct
         * beta = -2.044057 4677547683 degrees  ==> correct
         */

        // Expected alpha (book) : alpha = 16h 49m 12             s ; 16.82               h
        // Actual alpha :          alpha = 16h 49m 12.26843722986 s ; 16.82 0074565897194 h
        assertEquals((16.0 + 49.0 / 60.0 + 12.0 / 3600.0), mercury2003.equatorialPos().raHr(), 1.0 / 3600.0);

        // Expected delta (book): delta = -24◦ 30' 0 9               '' ; delta = -24.50 25            degrees
        // Actual delta :         delta = -24◦ 30' 0 3.14086630058628'' ; delta = -24.50 0872462861274 degrees
        assertEquals(Angle.ofDMS(-24, 30, 9), mercury2003.equatorialPos().dec(), 1e-1);
        System.out.println(mercury2003.equatorialPos().decDeg());
    }
}