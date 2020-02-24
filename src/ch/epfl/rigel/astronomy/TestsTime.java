package ch.epfl.rigel.astronomy;

import java.time.*;

/**
 * .
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public class TestsTime {

    public TestsTime() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        System.out.println(Epoch.J2000.daysUntil(d));

    }

}
