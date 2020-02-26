// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class EclipticToEquatorialConversionTest {

    private final ZonedDateTime when = ZonedDateTime.of(
            LocalDate.of(2009, Month.JULY, 6), LocalTime.of(14, 36, 51),
            ZoneOffset.UTC);

    private final EclipticCoordinates ecl = EclipticCoordinates
            .of((Angle.ofDMS(139, 41, 10)), Angle.ofDMS(4, 52, 31));

    private final EclipticToEquatorialConversion eclToEqu = new EclipticToEquatorialConversion(
            when);

    private final EquatorialCoordinates equ = eclToEqu.apply(ecl);

    @Test
    void conversionReturnsCorrectRightAscension() {
        // p.53
        assertEquals(Angle.ofHr(9.581478), equ.ra(), 1e-2);
    }

    @Test
    void conversionReturnsCorrectDeclination() {
        // p.55
        assertEquals(Angle.ofDeg(19.535003), equ.dec(), 1e-2);

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
            new EclipticToEquatorialConversion(when).hashCode();
        });
    }
}