package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.*;

/**
 * A model of the Sun, based on its observed position at the epoch J2010.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum SunModel implements CelestialObjectModel<Sun> {
    SUN();

    // Solar constants, used to derive the position of the Sun at a given epoch
    private static final double
            LONGITUDE_J2010 = Angle.ofDeg(279.557208), // The longitude of the Sun at the epoch J2010 (in radians)
            LONGITUDE_PERIGEE = Angle.ofDeg(283.112438), // The longitude of the Sun at the perigee (in radians)
            ECCENTRICITY = 0.016705, // The eccentricity of the Sun/Earth orbit (unitless)
            TROPICAL_YEAR = 365.242191, // The number of days it takes for the Earth to revolve around the Sun
            ANGULAR_SIZE_1AU = Angle.ofDeg(0.533128), // The angular size (in radians) of the Sun at a distance of 1 AU
            ANGULAR_VELOCITY = Angle.TAU / TROPICAL_YEAR; // The average angular velocity of the Earth's rotation around the Sun

    /**
     * @see CelestialObjectModel#at(double, EclipticToEquatorialConversion)
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // The Sun's mean anomaly (in radians) (for which the Sun occupies a circular orbit)
        double meanAnomaly = Angle.normalizePositive((ANGULAR_VELOCITY * daysSinceJ2010) + LONGITUDE_J2010 - LONGITUDE_PERIGEE);

        // The Sun's true anomaly (in radians) (for which the Sun occupies an elliptical orbit)
        double trueAnomaly = meanAnomaly + 2.0 * ECCENTRICITY * sin(meanAnomaly);

        // The geocentric ecliptic longitude of the Sun (in radians, normalized to [0, 2*PI[)
        double geoEclipticLon = Angle.normalizePositive(trueAnomaly + LONGITUDE_PERIGEE);

        double tempAngularSize = (1.0 + ECCENTRICITY * cos(trueAnomaly)) / (1.0 - ECCENTRICITY * ECCENTRICITY);

        // The Sun's angular size (in radians) (as seen from Earth)
        double angularSize = ANGULAR_SIZE_1AU * tempAngularSize;

        // The approximate position of the Sun in geocentric ecliptic coordinates (in radians) at the given epoch.
        // The reference plane is the ecliptic in which the Earth and the Sun are located.
        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(geoEclipticLon,0);

        return new Sun(eclipticCoordinates, eclipticToEquatorialConversion.apply(eclipticCoordinates),
                (float) angularSize, (float) meanAnomaly);
    }
}
