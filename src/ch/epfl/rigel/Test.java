package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.*;

public class Test {

    public static void main(String[] args) {
         int NANOS_PER_MILLIS = 1000000;

        ZonedDateTime when = ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51, 67), ZoneOffset.UTC);
        double T = Epoch.J2000.julianCenturiesUntil(when);

        System.out.println(SiderealTime.greenwich(when));


    }
}
