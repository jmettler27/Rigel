package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySunTest {

    @Test
    void constructorThrowsNullPointerExceptionWhenNull() {
        assertThrows(NullPointerException.class, () -> {
            new Sun(EclipticCoordinates.of(0,0), null, 0.5f, 0);
        });

        assertThrows(NullPointerException.class, () -> {
            new Sun(null, EquatorialCoordinates.of(0,0), 0.5f, 0);
        });

        assertThrows(NullPointerException.class, () -> {
           new Sun(null, null, 0.5f, 0);
        });
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionWithNegativeAngularSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Sun(EclipticCoordinates.of(Angle.ofDeg(45),Angle.ofDeg(45)), EquatorialCoordinates.of(Angle.ofDeg(24.768), Angle.ofDeg(67.899)), -0.0000001f, 0);
        });
    }
    @Test
    void magnitude(){
        assertEquals(-26.7f, new Sun(EclipticCoordinates.of(Angle.ofDeg(45),Angle.ofDeg(45)), EquatorialCoordinates.of(Angle.ofDeg(24.768), Angle.ofDeg(67.899)), 0.5f, 0).magnitude());
        assertEquals(-26.7f, new Sun(EclipticCoordinates.of(0,0), EquatorialCoordinates.of(0, 0), 0.5f, 0).magnitude());
    }
    @Test
    void eclipticPosReturnsCorrectCoordinates() {
        assertEquals("(λ=22.5000°, β=18.0000°)",
                new Sun(EclipticCoordinates.of(Angle.ofDeg(22.5),Angle.ofDeg(18.0)),EquatorialCoordinates.of(Angle.ofDeg(24.768), Angle.ofDeg(67.899)),
                        0.5f,0)
                        .eclipticPos().toString());
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
            assertEquals(hr[i], new Sun(EclipticCoordinates.of(Angle.ofDeg(22.5),Angle.ofDeg(18.0)), e, 0.5f, 0).equatorialPos().toString());
        }
    }
    @Test
    void meanAnomaly() {
        assertEquals(0.5f, new Sun(EclipticCoordinates.of(0,0), EquatorialCoordinates.of(0,0), 0.3f, 0.5f).meanAnomaly());
    }

    @Test
    void info(){
        assertEquals("Soleil", new Sun(EclipticCoordinates.of(0,0), EquatorialCoordinates.of(0,0), 0.5f, 0.5f).toString());
    }
}