package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.List;

import static java.lang.Math.*;

/**
 * A model of a planet, based on its observed position at the epoch J2010.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627, 0.387098, 7.0051,48.449, 6.74, -0.42),

    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812, 0.723329, 3.3947,76.769, 16.92, -4.40),

    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671, 0.999985, 0, 0, 0,0),

    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348, 1.523689, 1.8497,49.632, 9.36, -1.52),

    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907, 5.20278,1.3035, 100.595, 196.74, -9.40),

    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853, 9.51134, 2.4873,113.752, 165.60, -8.88),

    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321, 19.21814,0.773059, 73.926961, 65.80, -7.19),

    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483, 30.1985, 1.7673,131.879, 62.20, -6.87);

    private final String frenchName;

    private final double tropicalYear, epsilon, omega, eccentricity, a, i, bigOmega, theta0, v0;

    // The planets of the solar system, following elliptical orbits around the Sun
    public final static List<PlanetModel> ALL = List.of(MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, URANUS, NEPTUNE);

    // The planets that orbit closer to the Sun than the Earth
    private final static List<PlanetModel> INNER_PLANETS = ALL.subList(0, 2);

    // The planets that orbit further to the Sun than the Earth
    private final static List<PlanetModel> OUTER_PLANETS = ALL.subList(3, 8);

    /**
     * Constructs the model of a planet through planetary constants.
     *
     * @param frenchName
     *            The planet's french name
     * @param tropicalYear
     *            The planet's period (in tropical years)
     * @param epsilon
     *            The planet's longitude at J2010 (in radians)
     * @param omega
     *            The planet's longitude at the perigee (in radians)
     * @param eccentricity
     *            The eccentricity of the planet's orbit (unitless)
     * @param a
     *            The semi-major axis of the planet's orbit (in astronomical
     *            unit AU)
     * @param i
     *            The inclination of the planet's orbit to the ecliptic (in
     *            radians)
     * @param bigOmega
     *            The longitude of the ascending node (in radians)
     * @param theta0
     *            The angular size (in arcseconds) of the planet - except Earth
     *            - at a distance of 1 AU
     * @param v0
     *            The magnitude (unitless) of the planet seen from a distance of 1 AU
     */
    PlanetModel(String frenchName, double tropicalYear, double epsilon, double omega, double eccentricity, double a, double i, double bigOmega, double theta0, double v0) {

        this.frenchName = frenchName;
        this.tropicalYear = tropicalYear;
        this.epsilon = epsilon;
        this.omega = omega;
        this.eccentricity = eccentricity;
        this.a = a;
        this.i = i;
        this.bigOmega = bigOmega;
        this.theta0 = Angle.ofArcsec(theta0);
        this.v0 = v0;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // 1st step : The position of the planet in its own orbit

        // The planet's mean anomaly (in radians)
        double meanAnomaly = (Angle.TAU / 365.242191) * (daysSinceJ2010 / tropicalYear) + epsilon - omega;

        // The planet's true anomaly (in radians)
        double trueAnomaly = meanAnomaly + 2.0 * eccentricity * sin(meanAnomaly);

        // The planet's orbital radius (distance to the Sun) (in AU)
        double r = (a * (1.0 - eccentricity * eccentricity)) / (1.0 + eccentricity * cos(trueAnomaly));

        // The planet's orbital longitude (in radians)
        double l = trueAnomaly + omega;

        // 2nd step : The position of the planet is projected on the ecliptic
        // plane and then expressed in heliocentric ecliptic coordinates

        // The planet's heliocentric ecliptic latitude (in radians)
        double psi = asin(sin(l - bigOmega) * sin(i));

        // The planet's ecliptic radius (in AU)
        double rPrime = r * cos(psi);

        double numerator = sin(l - bigOmega) * cos(i);
        double denominator = cos(l - bigOmega);
        double lPrime_notNormalized = atan2(numerator, denominator) + bigOmega;

        // The planet's heliocentric ecliptic longitude (in radius)
        double lPrime = Angle.normalizePositive(lPrime_notNormalized);

        // 3rd step : The position of the Earth is determined

        // The Earth's mean anomaly (in radians)
        double earthMeanAnomaly = (Angle.TAU / 365.242191) * (daysSinceJ2010 / EARTH.tropicalYear) + EARTH.epsilon- EARTH.omega;

        // The Earth's true anomaly (in radians)
        double earthTrueAnomaly = earthMeanAnomaly + 2.0 * EARTH.eccentricity * sin(earthMeanAnomaly);

        // The Earth's orbital radius (in AU)
        double R = (EARTH.a * (1.0 - EARTH.eccentricity * EARTH.eccentricity)) / (1.0 + EARTH.eccentricity * cos(earthTrueAnomaly));

        // The Earth's orbital longitude (in radians)
        double L = EARTH.omega + earthTrueAnomaly;

        // 4th step : The position of the Earth and the planet are combined to
        // obtain the position of the planet in geocentric ecliptic coordinates.

        // The planet's geocentric ecliptic coordinates
        EclipticCoordinates eclipticCoordinates;

        if (INNER_PLANETS.contains(this)) {
            eclipticCoordinates = innerPlanetsCoords(R, L, rPrime, lPrime, psi);
        }

        else if (OUTER_PLANETS.contains(this)) {
            eclipticCoordinates = outerPlanetsCoords(R, L, rPrime, lPrime, psi);
        }

        else {
            // The Earth is the origin of the geocentric ecliptic coordinates
            // system
            eclipticCoordinates = EclipticCoordinates.of(0, 0);
        }

        // The planet's equatorial coordinates
        EquatorialCoordinates equatorialCoordinates = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        // The distance between the planet and the Earth (in AU)
        double rho = sqrt(R * R + r * r - 2.0 * R * r * cos(l - L) * cos(psi));

        // The planet's angular size (in radians)
        double angularSize = theta0 / rho;

        // The planet's phase, i.e. the illuminated percentage of the planet's
        // "disc" illuminated by the Sun, as seen from the Earth
        double F = (1 + cos(eclipticCoordinates.lon() - l)) / 2.0;

        // The planet's magnitude (unitless)
        double magnitude = v0 + 5.0 * log10((r * rho) / sqrt(F));

        return new Planet(frenchName, equatorialCoordinates, (float) angularSize, (float) magnitude);
    }

    /**
     * Returns the geocentric ecliptic coordinates of an inner planet.
     *
     * @param R
     *            The Earth's orbital radius (in AU)
     * @param L
     *            The Earth's orbital longitude (in radians)
     * @param rPrime
     *            The planet's ecliptic radius (in AU)
     * @param lPrime
     *            The planet's heliocentric ecliptic longitude (in radius)
     * @param psi
     *            The planet's heliocentric ecliptic latitude (in radians)
     *
     * @return the geocentric ecliptic coordinates of an inner planet
     */
    private EclipticCoordinates innerPlanetsCoords(double R, double L,  double rPrime, double lPrime, double psi) {

        double numeratorLambda = rPrime * sin(L - lPrime);
        double denominatorLambda = R - rPrime * cos(L - lPrime);
        double lambda_notNormalized = PI + L + atan2(numeratorLambda, denominatorLambda);

        // The planet's geocentric ecliptic longitude normalized in [0, 2*PI[
        double lambda = Angle.normalizePositive(lambda_notNormalized);

        double numeratorBeta = rPrime * tan(psi) * sin(lambda - lPrime);
        double denominatorBeta = R * sin(lPrime - L);

        // The planet's geocentric ecliptic latitude, in [-PI/2, PI/2]
        double beta = atan2(numeratorBeta, denominatorBeta);

        return EclipticCoordinates.of(lambda, beta);
    }

    /**
     * Returns the geocentric ecliptic coordinates of an outer planet.
     *
     * @param R
     *            The Earth's orbital radius (in AU)
     * @param L
     *            The Earth's orbital longitude (in radians)
     * @param rPrime
     *            The planet's ecliptic radius (in AU)
     * @param lPrime
     *            The planet's heliocentric ecliptic longitude (in radius)
     * @param psi
     *            The planet's heliocentric ecliptic latitude (in radians)
     *
     * @return the geocentric ecliptic coordinates of an outer planet
     */
    private EclipticCoordinates outerPlanetsCoords(double R, double L, double rPrime, double lPrime, double psi) {

        double numeratorLambda = R * sin(lPrime - L);
        double denominatorLambda = rPrime - R * cos(lPrime - L);
        double lambda_notNormalized = lPrime + atan2(numeratorLambda, denominatorLambda);

        // The planet's geocentric ecliptic longitude normalized in [0, 2*PI[
        double lambda = Angle.normalizePositive(lambda_notNormalized);

        double numeratorBeta = rPrime * tan(psi) * sin(lambda - lPrime);

        // The planet's geocentric ecliptic latitude, in [-PI/2, PI/2]
        double beta = atan2(numeratorBeta, numeratorLambda);

        return EclipticCoordinates.of(lambda, beta);
    }
}
