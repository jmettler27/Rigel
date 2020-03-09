package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.List;

import static java.lang.Math.*;

public enum PlanetModel implements CelestialObjectModel<Planet> {

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),

    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),

    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),

    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),

    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),

    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),

    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),

    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    private final String frenchName;
    private final double tropicalYear, epsilon, omega, excentricity, a, i, bigOmega, angularSize1UA, magnitude1UA;
    public static final List<PlanetModel> ALL = List.of(MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, URANUS, NEPTUNE);
    private static final List<PlanetModel> inferior = ALL.subList(0, 2);
    private static final List<PlanetModel> superior = ALL.subList(3, 8);


    private PlanetModel(String frenchName, double tropicalYear, double epsilon, double omega, double excentricity, double a, double i, double bigOmega, double angularSize1UA, double magnitude1UA) {
        this.frenchName = frenchName;
        this.tropicalYear = tropicalYear;
        this.epsilon = epsilon;
        this.omega = omega;
        this.excentricity = excentricity;
        this.a = a;
        this.i = i;
        this.bigOmega = bigOmega;
        this.angularSize1UA = Angle.ofArcsec(angularSize1UA);
        this.magnitude1UA = magnitude1UA;

    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double earthMeanAnomaly = (Angle.TAU / 365.242191) * (daysSinceJ2010 / EARTH.tropicalYear) + EARTH.epsilon - EARTH.omega;
        double earthTrueAnomaly = earthMeanAnomaly + 2 * EARTH.excentricity * sin(earthMeanAnomaly);
        double R = (EARTH.a * (1.0 - EARTH.excentricity * EARTH.excentricity)) / (1 + EARTH.excentricity * cos(earthTrueAnomaly));
        double L = EARTH.omega + earthTrueAnomaly;

        // r : orbital radius
        // l : orbital longitude
        // psi : heliocentric ecliptic latitude
        // rPrime : ecliptic radius
        // lPrime : ecliptic longitude

        double meanAnomaly = (Angle.TAU / 365.242191) * (daysSinceJ2010 / tropicalYear) + epsilon - omega;
        double trueAnomaly = meanAnomaly + 2 * excentricity * sin(meanAnomaly);

        double r = (a * (1 - excentricity * excentricity)) / (1 + excentricity * cos(trueAnomaly));

        double l = trueAnomaly + omega;
        double psi = asin(sin(l - bigOmega) * sin(i));

        double rPrime = r * cos(psi);

        double numerator = sin(l - bigOmega) * cos(i);
        double denominator = cos(l - bigOmega);
        double eclipticLon_notNormalized = atan2(numerator, denominator) + bigOmega;
        double lPrime = Angle.normalizePositive(eclipticLon_notNormalized);

        EclipticCoordinates eclipticCoordinates;
        if (inferior.contains(this)) {
            eclipticCoordinates = inferiorPlanetCoords(R, L, rPrime, lPrime, psi);
        } else {
            eclipticCoordinates = superiorPlanetCoords(R, L, rPrime, lPrime, psi);
        }

        double rho = sqrt(R * R + r * r - 2.0 * R * r * cos(l - L) * cos(psi));
        double angularSize = angularSize1UA / rho;

        double F = (1 + cos(eclipticCoordinates.lon() - l)) / 2.0;
        double magnitude = magnitude1UA + 5.0 * log10((r * rho) / sqrt(F));

        EquatorialCoordinates equatorialCoordinates = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        return new Planet(frenchName, equatorialCoordinates, (float) angularSize, (float) magnitude);
    }

    private EclipticCoordinates inferiorPlanetCoords(double R, double L, double rPrime, double lPrime, double psi) {

        double numeratorLambda = rPrime * sin(L - lPrime);
        double denominatorLambda = R - rPrime * cos(L - lPrime);
        double lambda_notNormalized = PI + L + atan2(numeratorLambda, denominatorLambda);
        double lambda = Angle.normalizePositive(lambda_notNormalized);

        double numeratorBeta = rPrime * tan(psi) * sin(lambda - lPrime);
        double denominatorBeta = R * sin(lPrime - L);
        double beta = atan2(numeratorBeta, denominatorBeta);

        return EclipticCoordinates.of(lambda, beta);
    }

    private EclipticCoordinates superiorPlanetCoords(double R, double L, double rPrime, double lPrime, double psi) {

        double numeratorLambda = R * sin(lPrime - L);
        double denominatorLambda = rPrime - R * cos(lPrime - L);

        double lambda_notNormalized = lPrime + atan2(numeratorLambda, denominatorLambda);
        double lambda = Angle.normalizePositive(lambda_notNormalized);

        double numeratorBeta = rPrime * tan(psi) * sin(lambda - lPrime);
        double denominatorBeta = R * sin(lPrime - L);
        double beta = atan2(numeratorBeta, denominatorBeta);

        return EclipticCoordinates.of(lambda, beta);
    }

}
