//Rigel stage 2

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;

import static org.junit.jupiter.api.Assertions.*;

class EquatorialCoordinatesTest {

    @Test
    void ofThrows() {
        double[] invalidRaDeg = { -0.000000001, 360.00000001 };

        double[] invalidDecDeg = { -90.000000001, 90.0000000001 };

        assertThrows(IllegalArgumentException.class, () -> {

            for (int i = 0; i < invalidRaDeg.length; ++i) {
                for (int j = 0; j < invalidDecDeg.length; ++j) {
                    EquatorialCoordinates.of(invalidRaDeg[i], invalidDecDeg[j]);
                }
            }
        });
    }

    @Test
    void testToString() {
        double[] ra = { 0, Angle.TAU / 24.0, Angle.TAU / 12.0, Angle.TAU / 8.0,
                Angle.TAU / 6.0, Angle.TAU / 4.0, Angle.TAU / 3.0,
                Angle.TAU / 2.0 };

        double[] dec = { -Angle.TAU / 4.0, Angle.TAU / 4.0, -Angle.TAU / 24.0,
                Angle.TAU / 24.0, -Angle.TAU / 8.0, Angle.TAU / 8.0,
                Angle.TAU / 12.0, Angle.ofDeg(55.0) };

        String[] hr = { "(ra=0.0000h, dec=-90.0000°)",
                "(ra=1.0000h, dec=90.0000°)", "(ra=2.0000h, dec=-15.0000°)",
                "(ra=3.0000h, dec=15.0000°)", "(ra=4.0000h, dec=-45.0000°)",
                "(ra=6.0000h, dec=45.0000°)", "(ra=8.0000h, dec=30.0000°)",
                "(ra=12.0000h, dec=55.0000°)" };

        for (int i = 0; i < ra.length; ++i) {
            EquatorialCoordinates e = EquatorialCoordinates.of(ra[i], dec[i]);
            assertEquals(hr[i], e.toString());
        }
    }
}