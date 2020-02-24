package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * .
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public class TestsTime {

    public static void main(String[] args) {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 55),
                ZoneOffset.UTC);
        System.out.println(Epoch.J2000.daysUntil(d));

        double t = d.truncatedTo(ChronoUnit.HOURS).getHour();
        System.out.println(t);

    }

}
