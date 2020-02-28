// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

//import java.time.*;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

class EquatorialToHorizontalConversionTest {

    @Test
    void conversionReturnsCorrectHorizontalCoordinates() {
        // Book example p.47 : The azimuth and altitude of a star whose hour
        // angle is 5h 51m 44s and declination is +23◦ 13' 10".
        // The observer's latitude is 52° N.
        double H = Angle.ofHr(5.862222); // The hour angle (in radians)
        double delta = Angle.ofDeg(23.219444); // The declination (in radians)
        double phi = Angle.ofDeg(52); // The observer's latitude (in radians)

        // Step 4
        double tempAltitude = sin(delta) * sin(phi)
                + cos(delta) * cos(phi) * cos(H);

        // Step 5
        double h = asin(tempAltitude);

        double numerator = -cos(delta) * cos(phi) * sin(H);
        double denominator = sin(delta) - sin(phi) * sin(h);

        // Step 7
        double A = atan2(numerator, denominator);

        // Normalizes the azimuth in its valid interval [0, 2*PI[
        double normalized_Azimuth = Angle.normalizePositive(A);

        assertEquals(Angle.ofDMS(283, 16, 15.70), normalized_Azimuth, 1e-6);
        assertEquals(Angle.ofDMS(19, 20, 3.64), h, 1e-5);

        /*
         * ZonedDateTime when = ZonedDateTime.of( LocalDate.of(1980,
         * Month.APRIL, 22), LocalTime.of(18, 36, 51), ZoneOffset.UTC);
         * 
         * GeographicCoordinates where = GeographicCoordinates.ofDeg(50, 52);
         * 
         * EquatorialCoordinates equ =
         * EquatorialCoordinates.of(Angle.ofHr(18.53), Angle.ofDMS(23, 13, 10));
         * 
         * EquatorialToHorizontalConversion equToHor = new
         * EquatorialToHorizontalConversion( when, where);
         * 
         * HorizontalCoordinates hor = equToHor.apply(equ);
         * 
         * assertEquals(Angle.ofHr(9.581478), equ.ra(), 1e-2);
         */

    }
}