package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyMoonTest {

    @Test
    void constructorThrowsWhenInvalidArguments() {
        assertThrows(NullPointerException.class, () -> {
            new Moon( null, 0.5f, 0, 0.3752f);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Moon(EquatorialCoordinates.of(0, 0), -0.0000001f, 0, 0.3752f);
        });
            }

    @Test
    void equatorialPosReturnsCorrectCoordinates(){
        double[] ra = { 0, Angle.TAU / 24.0, Angle.TAU / 12.0, Angle.TAU / 8.0, Angle.TAU / 6.0, Angle.TAU / 4.0, Angle.TAU / 3.0,Angle.TAU / 2.0 };

        double[] dec = { -Angle.TAU / 4.0, Angle.TAU / 4.0, -Angle.TAU / 24.0,Angle.TAU / 24.0, -Angle.TAU / 8.0, Angle.TAU / 8.0, Angle.TAU / 12.0, Angle.ofDeg(55.0) };

        String[] hr = {
                "(ra=0.0000h, dec=-90.0000°)", "(ra=1.0000h, dec=90.0000°)", "(ra=2.0000h, dec=-15.0000°)", "(ra=3.0000h, dec=15.0000°)",
                "(ra=4.0000h, dec=-45.0000°)", "(ra=6.0000h, dec=45.0000°)","(ra=8.0000h, dec=30.0000°)", "(ra=12.0000h, dec=55.0000°)" };

        for (int i = 0; i < ra.length; ++i) {
            EquatorialCoordinates e = EquatorialCoordinates.of(ra[i], dec[i]);
            assertEquals(hr[i], new Moon(e, 0.5f, 0, 0).equatorialPos().toString());
        }
    }
    @Test
    void info() {
        assertEquals("Lune (37.5%)", new Moon(EquatorialCoordinates.of(0, 0), 0.5f, 0.1f,  0.3752f).toString());
    }
}