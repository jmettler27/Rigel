package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The methods allowing to derive the sidereal time, a unit used to measure the
 * time it takes for the Earth to take a spin on itself (24 hours of sidereal
 * time). 1 hour of sidereal time corresponds to a 15° angle.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class SiderealTime {

    /**
     * Default constructor.
     */
    private SiderealTime() {
    }

    /**
     * Returns the Greenwich sidereal time (the one at longitude 0°) in the
     * interval [0, π[, for a given date/time pair
     *
     * @param when
     *            The given date/time pair (a moment)
     * @return the Greenwich sidereal time (in radians)
     */
    public static double greenwich(ZonedDateTime when) {

        // The moment expressed in the UTC time-zone
        ZonedDateTime whenUTC = when.withZoneSameInstant(ZoneOffset.UTC);

        ZonedDateTime dayStart = ZonedDateTime.of(
                LocalDate.of(whenUTC.getYear(), whenUTC.getMonth(),
                        whenUTC.getDayOfMonth()),
                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC);

        // The number of Julian centuries between the epoch J2000 and the
        // beginning of the day containing the epoch (i.e. 0h that day)
        double T = Epoch.J2000
                .julianCenturiesUntil(whenUTC.truncatedTo(ChronoUnit.DAYS));

        System.out.println("T = " + T);
        // The number of milliseconds between the beginning of the day
        // containing the moment and the moment itself.
        double nbMillis = dayStart.until(whenUTC, ChronoUnit.MILLIS);

        // The previous result in hours
        double t = (nbMillis / 1000.0) / 3600.0;
        System.out.println("t = " + t);


        double S0 = Polynomial.of(0.000025862, 2400.051336, 6.697374558).at(T);
        double S1 = Polynomial.of(1.002737909, 0).at(t);
        System.out.println("S0 = " + S0);
        System.out.println("S1 = " + S1);


        // The Greenwich sidereal time (in hours)
        double Sg = S0 + S1;
        System.out.println("Sg = " + Sg);


        // The Greenwich sidereal time (in radians)
        double Sg_rad = Angle.ofHr(Sg);
        System.out.println("Sg_rad = " + Sg_rad);


        // The Greenwich sidereal time (in radians) normalized to [0, 12h[ = [0,
        // PI[
        double normalizedSg_Rad = Angle.normalizePositive(Sg_rad);
        System.out.println("normalizedSg_Rad = " + normalizedSg_Rad);

        return normalizedSg_Rad;
    }

    /**
     * Returns the local sidereal time (in radians) for a given date/time pair
     * and specific to the given location.
     *
     * @param when
     *            The given date-time pair
     * @param where
     *            The given location's geographic coordinates
     * @return the local sidereal time (in radians) for the given date/time pair
     *         and specific to the given location
     */
    public static double local(ZonedDateTime when,
            GeographicCoordinates where) {

        // The sidereal time specific to Greenwich
        double siderealGreenwich = greenwich(when);

        // The local sidereal time specific to the given location where
        double Sl = siderealGreenwich + where.lon();

        // The normalized local sidereal time
        double normalized_Sl = Angle.normalizePositive(Sl);

        System.out.println(Angle.toHr(normalized_Sl));
        return normalized_Sl;
    }

}
