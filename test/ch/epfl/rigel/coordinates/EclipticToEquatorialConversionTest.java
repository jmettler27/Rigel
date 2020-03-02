// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class EclipticToEquatorialConversionTest {

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
        // The number of Julian centuries elapsed since January 1st, 2000 at
        // 12h00 UTC.
        double T = Epoch.J2000.julianCenturiesUntil(when);

        // The coefficients of the obliquity's polynomial
        double coeff0 = Angle.ofArcsec(0.00181);
        double coeff1 = -Angle.ofArcsec(0.0006);
        double coeff2 = -Angle.ofArcsec(46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);
        double obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(T);
        assertEquals(0.40907122964931697, obliquity);
    }

    @Test
    void conversionReturnsCorrectRightAscension() {
        // p.53
        assertEquals(9.581478, Angle.toHr(equ.ra()), 1e-6); // The expected value is rounded to the nearest millionth in the book

        assertEquals(9.581478170200256, (new EclipticToEquatorialConversion(ZonedDateTime.of(2009, 7, 6, 0, 0, 0, 0, ZoneOffset.UTC)))
                .apply(EclipticCoordinates.of(Angle.ofDMS(139, 41, 10), Angle.ofDMS(4, 52, 31))).raHr());


    }

    @Test
    void conversionReturnsCorrectDeclination() {
        // p.55
        assertEquals(19.535003, Angle.toDeg(equ.dec()), 1e-5); // The expected value is rounded to the nearest millionth in the book

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