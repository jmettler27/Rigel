package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.*;

public class Test {

    public static void main(String[] args) {
        ZonedDateTime when = ZonedDateTime.of(LocalDate.of(2009, Month.JULY, 6),
                LocalTime.of(14, 36, 51), ZoneOffset.UTC);
        double T = Epoch.J2000.julianCenturiesUntil(when);

        // The coefficients of the obliquity's polynomial
        double coeff0 = Angle.ofDMS(0, 0, 0.00181);
        double coeff1 = - Angle.ofDMS(0, 0, 0.0006);
        double coeff2 = - Angle.ofDMS(0, 0, 46.815);
        double coeff3 = Angle.ofDMS(23, 26, 21.45);

        double obliquity = Polynomial.of(coeff0, coeff1, coeff2, coeff3).at(T);
        System.out.println(Angle.toDeg(obliquity));
        
        System.out.println(Angle.toHr(Angle.TAU) + "h");

    }
}
