// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MyEclipticToEquatorialConversionTest {

    private final ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDate.of(2009, Month.JULY, 6), LocalTime.of(14, 36, 51), ZoneOffset.UTC);

    private final EclipticCoordinates ecl = EclipticCoordinates.of((Angle.ofDMS(139, 41, 10)), Angle.ofDMS(4, 52, 31));

    private final EclipticToEquatorialConversion eclToEqu = new EclipticToEquatorialConversion(zonedDateTime);

    private final EquatorialCoordinates equ = eclToEqu.apply(ecl);

    @Test
    void constructionDerivesCorrectObliquity() {
        // p.52
        ZonedDateTime when = ZonedDateTime.of(LocalDate.of(2009, Month.JULY, 6), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC);

        // Expected T : 0.095099247          centuries
        // Actual T :   0.095099247 09103354 centuries
        double T = Epoch.J2000.julianCenturiesUntil(when);
        assertEquals(0.095099247, T, 1e-10);

        double coeff0 = Angle.ofArcsec(0.00181);
        double coeff1 = -Angle.ofArcsec(0.0006);
        double coeff2 = -Angle.ofArcsec(46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);

        // Expected epsilon (book) : 23.43805 531        degrees
        // Actual epsilon :          23.43805 49791 3273 degrees
        // Correction  :             23.43805 49791      degrees (source : https://piazza.com/class/k6kxkvdcio3266?cid=27)
        double obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(T);
        assertEquals(23.43805531, Angle.toDeg(obliquity), 1e-6);

        // Expected epsilon (book) : 23◦ 26' 17''
        // Actual epsilon :          23◦ 26' 16.9979''
        assertEquals(Angle.ofDMS(23,26,17), obliquity, 1e-7);
    }

    @Test
    void conversionReturnsCorrectRightAscension() {
        // p.53
        // Expected lambda : 139.686111         degrees
        // Actual lambda   : 139.686111 1111111 degrees

        // Expected beta : 4.87527 8 degrees
        // Actual beta :   4.87527 7777777778 degrees

        // Expected obliquity : 23.43805 5          degrees
        // Actual obliquity :   23.43805 4762334506 degrees

        // Expected y : 0.559666
        // Actual y :   0.559666 2232545904

        // Expected x : −0.76251 2
        // Actual x :   -0.76251 15210226636

        // Expected alpha' : −36.277 799         degrees
        // Actual alpha' :   -36.277 82750900687 degrees

        // Expected alpha : 143.72217 3         degrees
        // Actual alpha :   143.72217 249099313 degrees

        // Expected alpha : 9.581478           hours ; alpha = 9h 34m 53.32          s
        // Actual alpha :   9.581478 166066209 hours ; alpha = 9h 34m 53.32 139783838s
        assertEquals(9.581478, equ.raHr(), 1e-6);

        // Expected delta : 19◦ 32' 6.01          '' ; delta = 19.535002 77777777 degrees
        // Actual delta :   19◦ 32' 6.01 011230724'' ; delta = 19.535002 80897423 degrees
        assertEquals(19.535003, equ.decDeg(), 1e-6);
    }

    @Test
    void equalsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            eclToEqu.equals(eclToEqu);
        });
    }

    @Test
    void hashCodeThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new EclipticToEquatorialConversion(zonedDateTime).hashCode();
        });
    }
}