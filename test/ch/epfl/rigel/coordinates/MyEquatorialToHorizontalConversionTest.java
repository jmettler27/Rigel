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
        // Book p.47 : The azimuth and altitude of a star whose hour angle is 5h 51m 44s and declination is +23◦ 13' 10".
        // The observer's latitude is 52° N.

        double H = Angle.ofHr(5.0 + 51.0 / 60.0 + 44.0 / 3600.0); // The hour angle (in radians)
        // Actual :     1.53472618892 0347 radians
        // Correction : 1.53472618892      radians (source : https://piazza.com/class/k6kxkvdcio3266?cid=26)

        double delta = Angle.ofDMS(23, 13, 10); // The declination (in radians)
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
        // Actual :     283.27102 7267 27486 degrees
        // Correction : 283.27102 7267       degrees (source : https://piazza.com/class/k6kxkvdcio3266?cid=26)
        assertEquals(283.271027267, Angle.toDeg(normalized_Azimuth), 1e-9);

        // Book values :
        // Expected azimuth : 4.9440121 10230538 radians
        // Actual azimuth   : 4.9440121 01320582 radians
        assertEquals(Angle.ofDMS(283, 16, 15.70), normalized_Azimuth,1e-7);

        // Expected altitude : 0.3374479 692702294 radians
        // Actual altitude   : 0.3374479 8288268113 radians
        assertEquals(Angle.ofDMS(19, 20, 3.64), h, 1e-7);
    }
}