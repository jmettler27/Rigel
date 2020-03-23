package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.*;

/**
 * A model of the Moon, based on its observed position at the epoch J2010.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {
    MOON();

    private final static double
            MEAN_LONGITUDE = Angle.ofDeg(91.929336), // The mean longitude (in radians)
            MEAN_LONGITUDE_PERIGEE = Angle.ofDeg(130.143076), // The mean longitude at the perigee (in radians)
            ASCENDING_NODE_LON = Angle.ofDeg(291.682547), // The longitude of the ascending node (in radians)
            ORBIT_INCLINATION = Angle.ofDeg(5.145396), // The inclination of the orbit (in radians)
            ECCENTRICITY = 0.0549, // The eccentricity of the orbit (unitless)

            // Constant angles (in radians) for calculations
            ANGULAR_SIZE_ORBIT = Angle.ofDeg(0.5181),
            ANGLE_ORBITAL_LONGITUDE = Angle.ofDeg(13.1763966),
            ANGLE_MOON_ANOMALY = Angle.ofDeg(0.1114041),
            ANGLE_EVECTION = Angle.ofDeg(1.2739),
            ANGLE_ANNUAL_EQUATION = Angle.ofDeg(0.1858),
            ANGLE_CORRECTION_3 = Angle.ofDeg(0.37),
            ANGLE_CENTER_EQUATION = Angle.ofDeg(6.2886),
            ANGLE_CORRECTION_4 = Angle.ofDeg(0.214),
            ANGLE_VARIATION = Angle.ofDeg(0.6583),
            ANGLE__MEAN_LON = Angle.ofDeg(0.0529539),
            ANGLE_CORRECTED_LON = Angle.ofDeg(0.16);

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        double sunMeanAnomaly = Angle.normalizePositive(sun.meanAnomaly()); // The Sun's mean anomaly
        double sunLon = sun.eclipticPos().lon(); // The Sun's geocentric ecliptic longitude

        // Step 1 : Deriving the Moon's orbital longitude

        // The Moon's mean orbital longitude
        double moonOrbitalLon = Angle.normalizePositive(ANGLE_ORBITAL_LONGITUDE * daysSinceJ2010 + MEAN_LONGITUDE);

        // The Moon's mean anomaly
        double moonMeanAnomaly = Angle.normalizePositive(moonOrbitalLon - ANGLE_MOON_ANOMALY * daysSinceJ2010
                - MEAN_LONGITUDE_PERIGEE);

        // The evection
        double evection = ANGLE_EVECTION * sin(2.0 * (moonOrbitalLon - sunLon) - moonMeanAnomaly);

        // The correction of the annual equation
        double annualEquationCor = ANGLE_ANNUAL_EQUATION * sin(sunMeanAnomaly);

        // The 3rd correction
        double correction3 = ANGLE_CORRECTION_3 * sin(sunMeanAnomaly);

        // The Moon's corrected anomaly
        double moonCorrectedAnomaly = moonMeanAnomaly + evection - annualEquationCor - correction3;

        // The correction of the center equation
        double centerEquationCor = ANGLE_CENTER_EQUATION * sin(moonCorrectedAnomaly);

        // The 4th correction
        double correction4 = ANGLE_CORRECTION_4 * sin(2.0 * moonCorrectedAnomaly);

        // The Moon's corrected orbital longitude
        double moonOrbitalLon_Cor = moonOrbitalLon + evection + centerEquationCor - annualEquationCor + correction4;

        // The variation
        double variation = ANGLE_VARIATION * sin(2.0 * (moonOrbitalLon_Cor - sunLon));

        // The Moon's true orbital longitude
        double moonOrbitalLon_True = moonOrbitalLon_Cor + variation;


        // Step 2 : Deriving the Moon's ecliptic position

        // The Moon's mean longitude of the ascending node
        double moonMeanLon_ascend = Angle.normalizePositive(ASCENDING_NODE_LON - ANGLE__MEAN_LON * daysSinceJ2010);

        // The Moon's corrected latitude of the ascending node
        double moonLatAscend_Cor = moonMeanLon_ascend - ANGLE_CORRECTED_LON * sin(sunMeanAnomaly);

        // Derivation of the Moon's ecliptic longitude
        double numeratorLon = sin(moonOrbitalLon_True - moonLatAscend_Cor) * cos(ORBIT_INCLINATION);
        double denominatorLon = cos(moonOrbitalLon_True - moonLatAscend_Cor);

        // The Moon's ecliptic longitude (in radians)
        double moonEclipticLon = Angle.normalizePositive(atan2(numeratorLon, denominatorLon) + moonLatAscend_Cor);

        // The Moon's ecliptic latitude (in radians)
        double moonEclipticLat = asin(sin(moonOrbitalLon_True - moonLatAscend_Cor) * sin(ORBIT_INCLINATION));

        // The Moon's ecliptic position (in radians)
        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(moonEclipticLon, moonEclipticLat);

        // The Moon's equatorial position (in radians)
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        // The Moon's phase (unitless)
        double phase = (1.0 - cos(moonOrbitalLon_True - sunLon)) / 2.0;

        // Derivation of the distance between the Earth and the Moon
        double numeratorDistance = 1.0 - ECCENTRICITY * ECCENTRICITY;
        double denominatorDistance = 1.0 + ECCENTRICITY * cos(moonCorrectedAnomaly + centerEquationCor);
        double earthMoonDistance = numeratorDistance / denominatorDistance; // The distance between the Earth and the Moon

        // The Moon's angular size (in radians)
        double angularSize = ANGULAR_SIZE_ORBIT / earthMoonDistance;

        return new Moon(equatorialPos, (float) angularSize, 0f, (float) phase);
    }
}
