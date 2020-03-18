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
            MEAN_LONGITUDE = Angle.ofDeg(91.929336),   // The mean longitude (in radians)
            MEAN_LONGITUDE_PERIGEE = Angle.ofDeg(130.143076),  // The mean longitude at the perigee (in radians)
            ASCENDING_NODE_LON = Angle.ofDeg(291.682547),  // The longitude of the ascending node (in radians)
            ORBIT_INCLINATION = Angle.ofDeg(5.145396),     // The inclination of the orbit (in radians)
            ECCENTRICITY = 0.0549,         // The eccentricity of the orbit (unitless)

            // The angular size (in radians) of the Moon as seen from the Earth at a distance
            // equal to the semi-major axis of the orbit
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

    /**
     * Default constructor.
     */
    MoonModel() {
    }

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);

        double sunMeanAnomaly = Angle.normalizePositive(sun.meanAnomaly()); // The Sun's mean anomaly
        // System.out.println("M0 = " + Angle.toDeg(sunMeanAnomaly) + " degrees");
        double sunLon = sun.eclipticPos().lon(); // The Sun's geocentric ecliptic longitude
        // System.out.println("lambda0 = " + Angle.toDeg(sunLon) + " degrees");

        // Step 1 : Deriving the Moon's orbital longitude

        // The Moon's mean orbital longitude
        double moonOrbitalLon = Angle.normalizePositive(ANGLE_ORBITAL_LONGITUDE * daysSinceJ2010 + MEAN_LONGITUDE);
        //System.out.println("l = " + Angle.toDeg(l) + " degrees");

        // The Moon's mean anomaly
        double moonMeanAnomaly = Angle.normalizePositive(moonOrbitalLon - ANGLE_MOON_ANOMALY * daysSinceJ2010 - MEAN_LONGITUDE_PERIGEE);
       // System.out.println("Mm = " + Angle.toDeg(moonMeanAnomaly) + " degrees");

        // The evection
        double evection = ANGLE_EVECTION * sin(2.0 * (moonOrbitalLon - sunLon) - moonMeanAnomaly);
        //System.out.println("Ev = " + Angle.toDeg(evection) + " degrees");

        // The correction of the annual equation
        double annualEquationCor = ANGLE_ANNUAL_EQUATION * sin(sunMeanAnomaly);
       // System.out.println("Ae = " + Angle.toDeg(aE) + " degrees");

        // The 3rd correction
        double correction3 = ANGLE_CORRECTION_3 * sin(sunMeanAnomaly);
        //System.out.println("A3 = " + Angle.toDeg(a3) + " degrees");

        // The Moon's corrected anomaly
        double moonCorrectedAnomaly = moonMeanAnomaly + evection - annualEquationCor - correction3;
      //  System.out.println("Mm' = " + Angle.toDeg(moonCorrectedAnomaly) + " degrees");

        // The correction of the center equation
        double centerEquationCor = ANGLE_CENTER_EQUATION * sin(moonCorrectedAnomaly);
       // System.out.println("Ec = " + Angle.toDeg(eC) + " degrees");

        // The 4th correction
        double correction4 = ANGLE_CORRECTION_4 * sin(2.0 * moonCorrectedAnomaly);
       // System.out.println("A4 = " + Angle.toDeg(a4) + " degrees");

        // The Moon's corrected orbital longitude
        double moonOrbitalLon_Cor = moonOrbitalLon + evection + centerEquationCor - annualEquationCor + correction4;
       //System.out.println("l' = " + Angle.toDeg(lPrime) + " degrees");

        // The variation
        double variation = ANGLE_VARIATION * sin(2.0 * (moonOrbitalLon_Cor - sunLon));
       // System.out.println("V = " + Angle.toDeg(variation) + " degrees");

        // The Moon's true orbital longitude
        double moonOrbitalLon_True = moonOrbitalLon_Cor + variation;
       // System.out.println("l'' = " + Angle.toDeg(l2) + " degrees");


        // Step 2 : Deriving the Moon's ecliptic position

        // The Moon's mean longitude of the ascending node
        double moonMeanLon_ascend = Angle.normalizePositive(ASCENDING_NODE_LON - ANGLE__MEAN_LON * daysSinceJ2010);
       // System.out.println("N = " + Angle.toDeg(n) + " degrees");

        // The Moon's corrected latitude of the ascending node
        double moonLatAscend_Cor = moonMeanLon_ascend - ANGLE_CORRECTED_LON * sin(sunMeanAnomaly);
       // System.out.println("N' = " + Angle.toDeg(nPrime) + " degrees");

        // Derivation of the Moon's ecliptic longitude
        double numeratorLambda = sin(moonOrbitalLon_True - moonLatAscend_Cor) * cos(ORBIT_INCLINATION);
       // System.out.println("y = " + numeratorLambda);
        double denominatorLambda = cos(moonOrbitalLon_True - moonLatAscend_Cor);
       // System.out.println("x = " + denominatorLambda);

        // The Moon's ecliptic longitude (in radians)
        double moonEclipticLon = Angle.normalizePositive(atan2(numeratorLambda, denominatorLambda) + moonLatAscend_Cor);
       // System.out.println("lambdaM = " + Angle.toDeg(lambda) + " degrees");

        // The Moon's ecliptic latitude (in radians)
        double moonEclipticLat = asin(sin(moonOrbitalLon_True - moonLatAscend_Cor) * sin(ORBIT_INCLINATION));
       // System.out.println("betaM = " + Angle.toDeg(beta) + " degrees");

        // The Moon's ecliptic position (in radians)
        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(moonEclipticLon, moonEclipticLat);

        // The Moon's equatorial position (in radians)
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        // The Moon's phase (unitless)
        double phase = (1.0 - cos(moonOrbitalLon_True - sunLon)) / 2.0;

        // Derivation of the distance between the Earth and the Moon
        double numeratorRho = 1.0 - ECCENTRICITY * ECCENTRICITY;
        double denominatorRho = 1.0 + ECCENTRICITY * cos(moonCorrectedAnomaly + centerEquationCor);
        double earthMoonDistance = numeratorRho / denominatorRho; // The distance between the Earth and the Moon

        // The Moon's angular size (in radians)
        double angularSize = ANGULAR_SIZE_ORBIT / earthMoonDistance;

        return new Moon(equatorialPos, (float) angularSize, 0f, (float) phase);
    }
}
