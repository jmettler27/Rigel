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

    private static final double
            MEAN_LONGITUDE = Angle.ofDeg(91.929336), // The mean longitude (in radians)
            MEAN_LONGITUDE_PERIGEE = Angle.ofDeg(130.143076), // The mean longitude at the perigee (in radians)
            ASCENDING_NODE_LON = Angle.ofDeg(291.682547), // The longitude of the ascending node (in radians)
            ORBIT_INCLINATION = Angle.ofDeg(5.145396), // The inclination of the orbit (in radians)
            ECCENTRICITY = 0.0549,// The eccentricity of the orbit (unitless)
            ECCENTRICITY_TEMP = 1.0 - ECCENTRICITY * ECCENTRICITY,
            SIN_INCLINATION = sin(ORBIT_INCLINATION),
            COS_INCLINATION = cos(ORBIT_INCLINATION),

            // Constant angles (in radians) used in calculations
            ANGLE_ORBITAL_LONGITUDE = Angle.ofDeg(13.1763966),
            ANGLE_MOON_ANOMALY = Angle.ofDeg(0.1114041),
            ANGLE_EVECTION = Angle.ofDeg(1.2739),
            ANGLE_ANNUAL_EQUATION = Angle.ofDeg(0.1858),
            ANGLE_CORRECTION_3 = Angle.ofDeg(0.37),
            ANGLE_CENTER_EQUATION = Angle.ofDeg(6.2886),
            ANGLE_CORRECTION_4 = Angle.ofDeg(0.214),
            ANGLE_VARIATION = Angle.ofDeg(0.6583),
            ANGLE_MEAN_LON = Angle.ofDeg(0.0529539),
            ANGLE_CORRECTED_LON = Angle.ofDeg(0.16),
            ANGULAR_SIZE_ORBIT = Angle.ofDeg(0.5181);

    /**
     * @see CelestialObjectModel#at(double, EclipticToEquatorialConversion)
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        double sunMeanAnomaly = Angle.normalizePositive(sun.meanAnomaly());
        double sunLon = sun.eclipticPos().lon();
        double sinSunMeanAnomaly = sin(sunMeanAnomaly);

        // Step 1 : Deriving the Moon's orbital longitude

        // The Moon's mean orbital longitude
        double meanOrbitalLon = Angle.normalizePositive(ANGLE_ORBITAL_LONGITUDE * daysSinceJ2010 + MEAN_LONGITUDE);

        // The Moon's mean anomaly
        double meanAnomaly = Angle.normalizePositive(meanOrbitalLon - ANGLE_MOON_ANOMALY * daysSinceJ2010
                - MEAN_LONGITUDE_PERIGEE);

        // The evection
        double evection = ANGLE_EVECTION * sin(2.0 * (meanOrbitalLon - sunLon) - meanAnomaly);

        // The correction of the annual equation
        double correctionAnnualEqu = ANGLE_ANNUAL_EQUATION * sinSunMeanAnomaly;

        // The 3rd correction
        double correction3 = ANGLE_CORRECTION_3 * sinSunMeanAnomaly;

        // The Moon's corrected anomaly
        double correctedAnomaly = meanAnomaly + evection - correctionAnnualEqu - correction3;

        // The correction of the center equation
        double correctedCenterEqu = ANGLE_CENTER_EQUATION * sin(correctedAnomaly);

        // The 4th correction
        double correction4 = ANGLE_CORRECTION_4 * sin(2.0 * correctedAnomaly);

        // The Moon's corrected orbital longitude
        double correctionOrbitalLon = meanOrbitalLon + evection + correctedCenterEqu - correctionAnnualEqu + correction4;

        // The variation
        double variation = ANGLE_VARIATION * sin(2.0 * (correctionOrbitalLon - sunLon));

        // The Moon's true orbital longitude
        double trueOrbitalLon = correctionOrbitalLon + variation;


        // Step 2 : Deriving the Moon's ecliptic position

        // The Moon's mean longitude of the ascending node
        double meanLonAscending = Angle.normalizePositive(ASCENDING_NODE_LON - ANGLE_MEAN_LON * daysSinceJ2010);

        // The Moon's corrected longitude of the ascending node
        double correctionAscending = meanLonAscending - ANGLE_CORRECTED_LON * sin(sunMeanAnomaly);

        // Calculation of the Moon's ecliptic longitude (in radians)
        double sin1 = sin(trueOrbitalLon - correctionAscending);
        double numeratorLon = sin1 * COS_INCLINATION;
        double denominatorLon = cos(trueOrbitalLon - correctionAscending);
        double moonEclipticLon = Angle.normalizePositive(atan2(numeratorLon, denominatorLon) + correctionAscending);

        // The Moon's ecliptic latitude (in radians)
        double moonEclipticLat = asin(sin1 * SIN_INCLINATION);

        // The Moon's ecliptic position (in radians)
        EclipticCoordinates eclipticPos = EclipticCoordinates.of(moonEclipticLon, moonEclipticLat);

        // The Moon's equatorial position (in radians)
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticPos);

        // The Moon's phase (unitless)
        double phase = (1.0 - cos(trueOrbitalLon - sunLon)) / 2.0;

        // Calculation of the distance between the Earth and the Moon
        double denominatorDistance = 1.0 + ECCENTRICITY * cos(correctedAnomaly + correctedCenterEqu);
        double earthMoonDistance = ECCENTRICITY_TEMP / denominatorDistance;

        // The Moon's angular size (in radians)
        double angularSize = ANGULAR_SIZE_ORBIT / earthMoonDistance;

        return new Moon(equatorialPos, (float) angularSize, 0f, (float) phase);
    }
}
