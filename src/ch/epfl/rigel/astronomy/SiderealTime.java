package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The methods allowing to derive the sidereal time, a unit used to measure the time it takes for the Earth
 * to take a spin on itself (24 hours of sidereal time). 1 hour of sidereal time corresponds to a 15° angle.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class SiderealTime {

    // The number of milliseconds per hour
    private static final double MILLIS_PER_HOUR = 1000.0 * 3600.0;

    // Used for the calculations of the Greenwich sidereal time
    private static final Polynomial
            POLYNOMIAL_S0 = Polynomial.of(0.000025862, 2400.051336, 6.697374558),
            POLYNOMIAL_S1 = Polynomial.of(1.002737909, 0);

    // Used for the normalization of values to the hours of a day
    private static final RightOpenInterval DAY_NORMALIZATION = RightOpenInterval.of(0,24);

    /**
     * Default constructor.
     */
    private SiderealTime() {}

    /**
     * Returns the Greenwich sidereal time (the one at longitude 0°) in radians, in the interval [0, 2*PI[,
     * for a given date/time pair.
     *
     * @param when
     *            The given date/time pair (an epoch)
     * @return the Greenwich sidereal time (in radians)
     */
    public static double greenwich(ZonedDateTime when) {
        // The given epoch expressed in the UTC time-zone
        ZonedDateTime whenUTC = when.withZoneSameInstant(ZoneOffset.UTC);

        // The beginning of the day containing the epoch (i.e. 0h00 that day)
        ZonedDateTime dayStart = whenUTC.truncatedTo(ChronoUnit.DAYS);

        // The number of Julian centuries between the epoch J2000 and the beginning of the day
        double nbJulianCenturies = Epoch.J2000.julianCenturiesUntil(dayStart);

        // The number of milliseconds between the beginning of the day containing the epoch and the epoch itself.
        double nbMillis = dayStart.until(whenUTC, ChronoUnit.MILLIS);

        // The previous result in hours
        double nbMillis_hr = nbMillis / MILLIS_PER_HOUR;

        // S0 and S1 (in hours)
        double S0 = POLYNOMIAL_S0.at(nbJulianCenturies);
        double S1 = POLYNOMIAL_S1.at(nbMillis_hr);
        double normalized_S0 = DAY_NORMALIZATION.reduce(S0);
        double normalized_S1 = DAY_NORMALIZATION.reduce(S1);

        // The Greenwich sidereal time (in hours)
        double siderealGreenwich_hr = DAY_NORMALIZATION.reduce(normalized_S0 + normalized_S1);

        // The Greenwich sidereal time (in radians)
        return Angle.normalizePositive(Angle.ofHr(siderealGreenwich_hr));
    }

    /**
     * Returns the local sidereal time (in radians) for a given date/time pair and specific to the given location.
     *
     * @param when
     *            The date-time pair
     * @param where
     *            The location
     * @return the local sidereal time (in radians) for the given date/time pair and specific to the given location
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        // The sidereal time (in radians) specific to Greenwich
        double siderealGreenwich = greenwich(when);

        // The local sidereal time (in radians), specific to the given location
        return Angle.normalizePositive(siderealGreenwich + where.lon());
    }
}
