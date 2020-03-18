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

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051,48.449, 6.74, -0.42),

    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947,76.769, 16.92, -4.40),

    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0,0),

    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497,49.632, 9.36, -1.52),

    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278,1.3035, 100.595, 196.74, -9.40),

    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873,113.752, 165.60, -8.88),

    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321,
            19.21814,0.773059, 73.926961, 65.80, -7.19),

    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673,131.879, 62.20, -6.87);

    private final String frenchName;

    private final double tropicalYear, lon2010, lonPerigee, eccentricity, axis, inclination, lonAscending, angularSize1AU, magnitude1AU;

    // The planets of the solar system, following elliptical orbits around the Sun
    public final static List<PlanetModel> ALL = List.of(MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, URANUS, NEPTUNE),
            INNER_PLANETS = ALL.subList(0, 2), // The planets that orbit closer to the Sun than the Earth
            OUTER_PLANETS = ALL.subList(3, 8); // The planets that orbit further to the Sun than the Earth

    /**
     * Constructs the model of a planet through planetary constants.
     *
     * @param frenchName
     *            The planet's french name
     * @param tropicalYear
     *            The planet's period (in tropical years)
     * @param lon2010Deg
     *            The planet's longitude at J2010 (in degrees)
     * @param lonPerigeeDeg
     *            The planet's longitude at the perigee (in degrees)
     * @param eccentricity
     *            The eccentricity of the planet's orbit (unitless)
     * @param axis
     *            The semi-major axis of the planet's orbit (in astronomical unit AU)
     * @param inclinationDeg
     *            The inclination of the planet's orbit to the ecliptic (in degrees)
     * @param lonAscendingDeg
     *            The longitude of the ascending node (in degrees)
     * @param angularSize1AUArc
     *            The angular size (in arcseconds) of the planet - except Earth - at a distance of 1 AU
     * @param magnitude1AU
     *            The magnitude (unitless) of the planet seen from a distance of 1 AU
     */
    PlanetModel(String frenchName, double tropicalYear, double lon2010Deg, double lonPerigeeDeg, double eccentricity,
                double axis, double inclinationDeg, double lonAscendingDeg, double angularSize1AUArc,
                double magnitude1AU) {

        this.frenchName = frenchName;
        this.tropicalYear = tropicalYear;
        this.lon2010 = Angle.ofDeg(lon2010Deg);
        this.lonPerigee = Angle.ofDeg(lonPerigeeDeg);
        this.eccentricity = eccentricity;
        this.axis = axis;
        this.inclination = Angle.ofDeg(inclinationDeg);
        this.lonAscending = Angle.ofDeg(lonAscendingDeg);
        this.angularSize1AU = Angle.ofArcsec(angularSize1AUArc);
        this.magnitude1AU = magnitude1AU;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // 1st step : The position of the planet in its own orbit
        double Np = Angle.normalizePositive((Angle.TAU / 365.242191) * (daysSinceJ2010 / tropicalYear));
       // System.out.println("Np = " + Angle.toDeg(Np) + " degrees");

        // The planet's mean anomaly (in radians)
        double meanAnomaly = Np + lon2010 - lonPerigee;
       // System.out.println("Mp = " + Angle.toDeg(meanAnomaly) + " degrees");


        // The planet's true anomaly (in radians)
        double trueAnomaly = Angle.normalizePositive(meanAnomaly + 2.0 * eccentricity * sin(meanAnomaly));
       // System.out.println("vp = " + Angle.toDeg(normalized_TrueAnomaly) + " degrees");

        // The planet's orbital radius (distance to the Sun) (in AU)
        double orbitalRadius = (axis * (1.0 - eccentricity * eccentricity)) / (1.0 + eccentricity * cos(trueAnomaly));
       // System.out.println("r = " + r + " AU");

        // The planet's orbital longitude (in radians)
        double orbitalLon = trueAnomaly + lonPerigee;
       // System.out.println("lp = " + Angle.toDeg(l) + " degrees");

        // 2nd step : The position of the planet is projected on the ecliptic
        // plane and then expressed in heliocentric ecliptic coordinates

        // The planet's heliocentric ecliptic latitude (in radians)
        double helioEclipticLat = asin(sin(orbitalLon - lonAscending) * sin(inclination));
       // System.out.println("psi = " + Angle.toDeg(psi) + " degrees");

        // The planet's ecliptic radius (in AU)
        double eclipticRadius = orbitalRadius * cos(helioEclipticLat);
       // System.out.println("r' = " + rPrime + " AU");

        double numerator = sin(orbitalLon - lonAscending) * cos(inclination);
        double denominator = cos(orbitalLon - lonAscending);
        // The planet's ecliptic longitude (in AU)
        double eclipticLon = Angle.normalizePositive(atan2(numerator, denominator) + lonAscending);
       // System.out.println("y = " + numerator);
       // System.out.println("x = " + denominator);
        //System.out.println("l' = " + Angle.toDeg(normalized_LPrime) + " degrees");


        // 3rd step : The position of the Earth is determined
        double Ne = Angle.normalizePositive((Angle.TAU / 365.242191) * (daysSinceJ2010 / EARTH.tropicalYear));
        //System.out.println("Ne = " + Angle.toDeg(Ne) + " degrees");

        // The Earth's mean anomaly (in radians)
        double earthMeanAnomaly = Ne + EARTH.lon2010 - EARTH.lonPerigee;
        //System.out.println("Me = " + Angle.toDeg(earthMeanAnomaly) + " degrees");


        // The Earth's true anomaly (in radians)
        double earthTrueAnomaly = Angle.normalizePositive(earthMeanAnomaly + 2.0 * EARTH.eccentricity
                * sin(earthMeanAnomaly));
        //System.out.println("ve = " + Angle.toDeg(normalized_EarthTA) + " degrees");

        // The Earth's orbital radius (in AU)
        double earthOrbitalRadius = (EARTH.axis * (1.0 - EARTH.eccentricity * EARTH.eccentricity))
                / (1.0 + EARTH.eccentricity * cos(earthTrueAnomaly));
        //System.out.println("R = " + R + " AU");

        // The Earth's orbital longitude (in radians)
        double earthOrbitalLon = Angle.normalizePositive(EARTH.lonPerigee + earthTrueAnomaly);
        //System.out.println("L = " + Angle.toDeg(normalized_L) + " degrees");

        // 4th step : The position of the Earth and the planet are combined to
        // obtain the position of the planet in geocentric ecliptic coordinates.

        // The planet's geocentric ecliptic coordinates
        // Note : We assume that the method at will never be applied to the Earth
        EclipticCoordinates eclipticCoordinates = INNER_PLANETS.contains(this) ?
                innerPlanetsCoords(earthOrbitalRadius, earthOrbitalLon, eclipticRadius, eclipticLon, helioEclipticLat) :
                outerPlanetsCoords(earthOrbitalRadius, earthOrbitalLon, eclipticRadius, eclipticLon, helioEclipticLat);

        // The planet's equatorial coordinates
        EquatorialCoordinates equatorialCoordinates = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        // The distance between the planet and the Earth (in AU)
        double earthPlanetDistance = sqrt(earthOrbitalRadius * earthOrbitalRadius + orbitalRadius * orbitalRadius
                - 2.0 * earthOrbitalRadius * orbitalRadius * cos(orbitalLon - earthOrbitalLon) * cos(helioEclipticLat));

        // The planet's angular size (in radians)
        double angularSize = angularSize1AU / earthPlanetDistance;

        // The planet's phase, i.e. the illuminated percentage of the planet's
        // "disc" illuminated by the Sun, as seen from the Earth
        double phase = (1.0 + cos(eclipticCoordinates.lon() - orbitalLon)) / 2.0;

        // The planet's magnitude (unitless)
        double magnitude = magnitude1AU + 5.0 * log10((orbitalRadius * earthPlanetDistance) / sqrt(phase));

        return new Planet(frenchName, equatorialCoordinates, (float) angularSize, (float) magnitude);
    }

    /**
     * Returns the geocentric ecliptic coordinates of an inner planet.
     *
     * @param earthOrbitalRadius
     *            The Earth's orbital radius (in AU)
     * @param earthOrbitalLon
     *            The Earth's orbital longitude (in radians)
     * @param eclipticRadius
     *            The planet's ecliptic radius (in AU)
     * @param helioEclipticLon
     *            The planet's heliocentric ecliptic longitude (in radius)
     * @param helioEclipticLat
     *            The planet's heliocentric ecliptic latitude (in radians)
     *
     * @return the geocentric ecliptic coordinates of an inner planet
     */
    private EclipticCoordinates innerPlanetsCoords(double earthOrbitalRadius, double earthOrbitalLon,
                                                   double eclipticRadius, double helioEclipticLon, double helioEclipticLat) {

        // Derivation of the planet's geocentric ecliptic longitude:
        double numeratorLon = eclipticRadius * sin(earthOrbitalLon - helioEclipticLon);
        double denominatorLon = earthOrbitalRadius - eclipticRadius * cos(earthOrbitalLon - helioEclipticLon);

        // The planet's geocentric ecliptic longitude normalized in [0, 2*PI[
        double geoEclipticLon = Angle.normalizePositive(PI + earthOrbitalLon + atan2(numeratorLon, denominatorLon));
        //System.out.println("lambda = " + Angle.toDeg(normalized_Lambda) + " degrees");

        // Derivation of the planet's geocentric ecliptic latitude:
        double numeratorBeta = eclipticRadius * tan(helioEclipticLat) * sin(geoEclipticLon - helioEclipticLon);
        double denominatorBeta = earthOrbitalRadius * sin(helioEclipticLon - earthOrbitalLon);

        // The planet's geocentric ecliptic latitude, in [-PI/2, PI/2]
        // Note : We use here the method atan since it returns an angle in the range [-PI/2, PI/2], which is
        // the valid latitude range
        double geoEclipticLat = atan(numeratorBeta/ denominatorBeta);
       // System.out.println("beta = " + Angle.toDeg(beta) + " degrees");

        return EclipticCoordinates.of(geoEclipticLon, geoEclipticLat);
    }

    /**
     * Returns the geocentric ecliptic coordinates of an outer planet.
     *
     * @param earthOrbitalRadius
     *            The Earth's orbital radius (in AU)
     * @param earthOrbitalLon
     *            The Earth's orbital longitude (in radians)
     * @param eclipticRadius
     *            The planet's ecliptic radius (in AU)
     * @param helioEclipticLon
     *            The planet's heliocentric ecliptic longitude (in radius)
     * @param helioEclipticLat
     *            The planet's heliocentric ecliptic latitude (in radians)
     *
     * @return the geocentric ecliptic coordinates of an outer planet
     */
    private EclipticCoordinates outerPlanetsCoords(double earthOrbitalRadius, double earthOrbitalLon,
                                                   double eclipticRadius, double helioEclipticLon, double helioEclipticLat) {

        // Derivation of the planet's geocentric ecliptic longitude:
        double numeratorLon = earthOrbitalRadius * sin(helioEclipticLon - earthOrbitalLon);
        double denominatorLon = eclipticRadius - earthOrbitalRadius * cos(helioEclipticLon - earthOrbitalLon);

        // The planet's geocentric ecliptic longitude normalized in [0, 2*PI[
        double geoEclipticLon = Angle.normalizePositive(helioEclipticLon + atan2(numeratorLon, denominatorLon));
        //System.out.println("lambda = " + Angle.toDeg(normalized_Lambda) + " degrees");

        // Derivation of the planet's geocentric ecliptic latitude:
        double numeratorLat = eclipticRadius * tan(helioEclipticLat) * sin(geoEclipticLon - helioEclipticLon);

        // The planet's geocentric ecliptic latitude
        // Note : The method atan returns an angle in the range [-PI/2, PI/2], which is the valid latitude range
        double geoEclipticLat = atan(numeratorLat / numeratorLon);
        //System.out.println("beta = " + Angle.toDeg(beta) + " degrees");

        return EclipticCoordinates.of(geoEclipticLon, geoEclipticLat);
    }
}
