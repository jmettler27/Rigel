// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class EquatorialToHorizontalConversionTest {

    @Test
    void apply() {

        ZonedDateTime when = ZonedDateTime.of(
                LocalDate.of(1980, Month.APRIL, 22), LocalTime.of(18, 36, 51),
                ZoneOffset.UTC);

        GeographicCoordinates where = GeographicCoordinates.ofDeg(50, 52);

        EquatorialCoordinates equ = EquatorialCoordinates.of(Angle.ofHr(18.53),
                Angle.ofDMS(23, 13, 10));

        EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(
                when, where);

        HorizontalCoordinates hor = equToHor.apply(equ);

        // assertEquals(Angle.ofHr(9.581478), equ.ra(), 1e-2);
    }
}