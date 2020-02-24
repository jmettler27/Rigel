package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * A sidereal time.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class SiderealTime {

    /**
     * Default constructor
     */
    private SiderealTime() {
    }

    /**
     * Returns Greenwich sidereal time, in radians and in the interval [0, π[,
     * for the date/time pair
     *
     * @param when
     *            The given date/time pair
     * @return
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime whenUTC = when.withZoneSameInstant(ZoneOffset.UTC);

        //
        double T = Epoch.J2000
                .julianCenturiesUntil(whenUTC.truncatedTo(ChronoUnit.DAYS));

        //
        double t = when.getHour();

        double S0 = Polynomial.of(0.000025862, 2400.051336, 6.697374558).at(T);
        double S1 = Polynomial.of(1.002737909, 0).at(t);

        double Sg = S0 + S1;
        double Sg_rad = Angle.ofHr(Sg);
        double normalizedSg_Rad = RightOpenInterval.of(0.0, Math.PI)
                .reduce(Sg_rad);

        return normalizedSg_Rad;
    }

    /**
     * 
     * 
     * @param when
     * @param where
     * @return
     */
    public static double local(ZonedDateTime when,
            GeographicCoordinates where) {
        double sideralGreenwich = greenwich(when);

        double Sl = sideralGreenwich + where.lon();
        double normalized_Sl = RightOpenInterval.of(0, Math.PI).reduce(Sl);

        return normalized_Sl;
    }

}
