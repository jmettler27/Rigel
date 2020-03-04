// Rigel stage 3

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

//import java.time.*;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

class MyEquatorialToHorizontalConversionTest {

    @Test
    void conversionReturnsCorrectHorizontalCoordinates() {
        // Book example p.47 : The azimuth and altitude of a star whose hour
        // angle is 5h 51m 44s and declination is +23◦ 13' 10".
        // The observer's latitude is 52° N.

        // double H = Angle.ofHr(5.862222222222222222222222);
        double H_Manual = Angle.ofHr(5.0 + 51.0 / 60.0 + 44.0 / 3600.0); // The hour angle (in radians)
        // Avant :   1.5347261 8892 034 12 radians
        // Actuel :  1.5347261 8892 034 7 radians
        // Corrige : 1.5347261 8892       radians (source : https://piazza.com/class/k6kxkvdcio3266?cid=26)

        double delta = Angle.ofDMS(23, 13, 10); // The declination (in radians)
        double phi = Angle.ofDeg(52); // The observer's latitude (in radians)

        // Step 4
        double tempAltitude = sin(delta) * sin(phi)
                + cos(delta) * cos(phi) * cos(H_Manual);

        // Step 5
        double h = asin(tempAltitude);

        double numerator = -cos(delta) * cos(phi) * sin(H_Manual);
        double denominator = sin(delta) - sin(phi) * sin(h);

        // Step 7
        double A = atan2(numerator, denominator);

        // Normalizes the azimuth in its valid interval [0, 2*PI[
        double normalized_Azimuth = Angle.normalizePositive(A);
        // Avant :   283.27102 7267 27463 degres (ofHr)
        // Actuel :  283.27102 7267 27486 degres (manual)
        // Corrige : 283.27102 7267       degres (source : https://piazza.com/class/k6kxkvdcio3266?cid=26)
        assertEquals(283.271027267, Angle.toDeg(normalized_Azimuth), 1e-9);

        // Valeurs de livre
        assertEquals(Angle.ofDMS(283, 16, 15.70), normalized_Azimuth, 1e-7); // 283.27102 777777776 degres
        assertEquals(Angle.ofDMS(19, 20, 3.64), h, 1e-7);


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