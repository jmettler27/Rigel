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
 *
 */
public enum SunModel implements CelestialObjectModel<Sun> {

    SUN();

    // The longitude of the Sun at the epoch J2010 (in radians)
    private static final double EPSILON_G = Angle.ofDeg(279.557208);

    // The longitude of the Sun at the perigee (in radians)
    private static final double OMEGA_G = Angle.ofDeg(283.112438);

    // The eccentricity of the Sun/Earth orbit (unitless)
    private static final double ECCENTRICITY = 0.016705;

    // The number of days it takes for the Earth to go around the Sun.
    private static final double TROPICAL_YEAR = 365.242191;

    // The angular size (in radians) of the Sun at a distance of 1 AU
    private static final double THETA_0 = Angle.ofDeg(0.533128);

    /**
     * Default constructor.
     */
    SunModel() {}

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // The Sun's mean anomaly (for which the Sun occupies a circular orbit)
        double meanAnomaly = ((Angle.TAU / TROPICAL_YEAR) * daysSinceJ2010) + EPSILON_G - OMEGA_G;

        // The Sun's true anomaly (for which the Sun occupies an elliptical
        // orbit)
        double trueAnomaly = meanAnomaly + 2.0 * ECCENTRICITY * sin(meanAnomaly);

        // The geocentric ecliptic longitude of the Sun
        double lambda = Angle.normalizePositive(trueAnomaly + OMEGA_G);

        // The Sun's angular size (as seen from Earth)
        double angularSize = THETA_0 * ((1.0 + ECCENTRICITY * cos(trueAnomaly)) / (1.0 - ECCENTRICITY * ECCENTRICITY));

        // The approximate position of the Sun in geocentric ecliptic
        // coordinates at the given epoch.
        // The reference plane is the ecliptic in which the Earth and the Sun
        // are located.
        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(lambda,0);

        return new Sun(eclipticCoordinates, eclipticToEquatorialConversion.apply(eclipticCoordinates),(float) angularSize, (float) meanAnomaly);
    }
}
