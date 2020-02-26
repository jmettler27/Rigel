// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class EclipticToEquatorialConversionTest {

    @Test
    void apply() {
        ZonedDateTime when = ZonedDateTime.of(LocalDate.of(2009, Month.JULY, 6),
                LocalTime.of(14, 36, 51), ZoneOffset.UTC);

        EclipticCoordinates ecl = EclipticCoordinates.of((Angle.ofDMS(139, 41, 10)),
                Angle.ofDMS(4, 52, 31));

        EclipticToEquatorialConversion eclToEqu = new EclipticToEquatorialConversion(when);

        EquatorialCoordinates equ = eclToEqu.apply(ecl);

        assertEquals(Angle.ofHr(9.581478), equ.ra(), 1e-2);
        //assertEquals(Angle.ofDeg(19.53), equ.dec(), 1e-2);
    }

    @Test
    void equalsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {

        });
    }

    @Test
    void hashCodeThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
        });
    }
}