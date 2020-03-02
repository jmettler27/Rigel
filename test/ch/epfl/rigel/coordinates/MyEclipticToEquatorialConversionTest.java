// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MyEclipticToEquatorialConversionTest {

    private final ZonedDateTime zonedDateTime1 = ZonedDateTime.of(
            LocalDate.of(2009, Month.JULY, 6), LocalTime.of(14, 36, 51),
            ZoneOffset.UTC);

    private final EclipticCoordinates ecl1 = EclipticCoordinates
            .of((Angle.ofDMS(139, 41, 10)), Angle.ofDMS(4, 52, 31));

    private final EclipticToEquatorialConversion eclToEqu1 = new EclipticToEquatorialConversion(
            zonedDateTime1);

    private final EquatorialCoordinates equ = eclToEqu1.apply(ecl1);

    @Test
    void constructionDerivesCorrectObliquity() {
        ZonedDateTime when = ZonedDateTime.of(LocalDate.of(2009, Month.JULY, 6), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC);

        double T = Epoch.J2000.julianCenturiesUntil(when);
        assertEquals(0.095099247, T, 1e-9);

        double coeff0 = Angle.ofArcsec(0.00181);
        double coeff1 = -Angle.ofArcsec(0.0006);
        double coeff2 = -Angle.ofArcsec(46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);
        double obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(T);

        // Note : The value of T in the book in rounded to the nearest billionth, which explains the difference found for the value of the obliquity, only to the 5th decimal.
        assertEquals(23.43805531, Angle.toDeg(obliquity), 1e-5);

    }

    @Test
    void conversionReturnsCorrectRightAscension() {
        // p.53
        // Note : The expected value in the book is rounded to the nearest millionth
        // Found right ascension : alpha = 9.581478166066209 degrees
        assertEquals(9.581478, equ.raHr(), 1e-6);
        System.out.println(equ.raHr());
    }

    @Test
    void conversionReturnsCorrectDeclination() {
        // p.55
        // Note : The expected value in the book is rounded to the nearest millionth
        // Found declination : delta = 19.53500280897423 degrees
        assertEquals(19.535003, equ.decDeg(), 1e-6);

        assertEquals(0.34095012064184566, (new EclipticToEquatorialConversion(ZonedDateTime.of(2009, 7, 6, 0, 0, 0, 0, ZoneOffset.UTC)))
                .apply(EclipticCoordinates.of(Angle.ofDMS(139, 41, 10), Angle.ofDMS(4, 52, 31))).dec());
    }

    @Test
    void equalsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            eclToEqu1.equals(eclToEqu1);
        });
    }

    @Test
    void hashCodeThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new EclipticToEquatorialConversion(zonedDateTime1).hashCode();
        });
    }
}