package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.List;

import static java.lang.Math.*;

/**
 * A model of a planet of the solar system, based on its observed position at the epoch J2010.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,0.387098, 7.0051,48.449, 6.74, -0.42),

    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,0.723329, 3.3947,76.769, 16.92, -4.40),

    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,0.999985, 0, 0, 0,0),

    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,1.523689, 1.8497,49.632, 9.36, -1.52),

    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,5.20278, 1.3035, 100.595, 196.74, -9.40),

    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,9.51134, 2.4873,113.752, 165.60, -8.88),

    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,19.21814,0.773059, 73.926961, 65.80, -7.19),

    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,30.1985, 1.7673,131.879, 62.20, -6.87);

    private final String frenchName;

    private final double tropicalYear, lonJ2010, lonPerigee, eccentricity, axis, inclination, lonAscending,
            angularSize1AU, magnitude1AU, cosInclination, sinInclination, eccentricityTemp;

    private final double eccentricityDoubled;


    // The average angular velocity of the Earth's rotation around the Sun
    private static final double ANGULAR_VELOCITY = Angle.TAU / 365.242191;

    // The eight planets of the solar system, following elliptical orbits around the Sun
    public static final List<PlanetModel> ALL = List.copyOf(List.of(values()));

    // A ENLEVER
    // The planets that orbit closer to the Sun than the Earth (i.e. Mercury and Venus)
    public static final List<PlanetModel> INNER_PLANETS = List.copyOf(ALL.subList(MERCURY.ordinal(), EARTH.ordinal()));

    // The planets that orbit closer to the Sun than the Earth (i.e. Mars, Jupiter, Saturn, Uranus, Neptune)
    public static final List<PlanetModel> OUTER_PLANETS = List.copyOf(ALL.subList(MARS.ordinal(), NEPTUNE.ordinal() + 1));

    /**
     * Constructs the model of a planet through planetary constants.
     *
     * @param frenchName
     *            The planet's french name
     * @param tropicalYear
     *            The planet's period of revolution (in tropical years)
     * @param lonJ2010Deg
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
    PlanetModel(String frenchName, double tropicalYear, double lonJ2010Deg, double lonPerigeeDeg, double eccentricity,
                double axis, double inclinationDeg, double lonAscendingDeg, double angularSize1AUArc, double magnitude1AU) {

        this.frenchName = frenchName;
        this.tropicalYear = tropicalYear;
        this.lonJ2010 = Angle.ofDeg(lonJ2010Deg);
        this.lonPerigee = Angle.ofDeg(lonPerigeeDeg);
        this.eccentricity = eccentricity;
        this.axis = axis;
        this.inclination = Angle.ofDeg(inclinationDeg);
        this.lonAscending = Angle.ofDeg(lonAscendingDeg);
        this.angularSize1AU = Angle.ofArcsec(angularSize1AUArc);
        this.magnitude1AU = magnitude1AU;

        this.cosInclination = cos(inclination);
        this.sinInclination = sin(inclination);
        this.eccentricityTemp = 1.0 - eccentricity * eccentricity;
        this.eccentricityDoubled = 2.0 * eccentricity;
    }

    /**
     * @see CelestialObjectModel#at(double, EclipticToEquatorialConversion)
     */
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        // 1st step : The position of the planet in its own orbit

        double trueAnomaly = trueAnomaly(daysSinceJ2010);

        // The planet's orbital radius (distance to the Sun) (in AU)
        double orbitalRadius = orbitalRadius(trueAnomaly);

        // The planet's heliocentric longitude (in radians)
        double helioLon = heliocentricLongitude(trueAnomaly);
        double helioLonMinusLon = helioLon - lonAscending;
        double sinHelioLonMinusLon = sin(helioLonMinusLon);

        // 2nd step : The position of the planet is projected on the ecliptic plane and then expressed in
        // heliocentric ecliptic coordinates

        // The planet's heliocentric ecliptic latitude (in radians, in its valid interval [-PI/2,PI/2])
        double helioEclipticLat = asin(sinHelioLonMinusLon * sinInclination);

        // The projection of the orbital radius on the ecliptic plane (in AU)
        double eclipticRadius = orbitalRadius * cos(helioEclipticLat);

        // Calculation of the planet's heliocentric ecliptic longitude (in radians)
        double numeratorEclipticLon = sinHelioLonMinusLon * cosInclination;
        double denominatorEclipticLon = cos(helioLonMinusLon);
        double helioEclipticLon = Angle.normalizePositive(atan2(numeratorEclipticLon, denominatorEclipticLon)
                + lonAscending);

        // 3rd step : The position of the Earth is determined

        // The Earth's true anomaly (in radians)
        double earthTrueAnomaly = EARTH.trueAnomaly(daysSinceJ2010);

        // The Earth's orbital radius (in AU)
        double earthOrbitalRadius = EARTH.orbitalRadius(earthTrueAnomaly);

        // The Earth's heliocentric longitude (in radians)
        double earthHelioLon = EARTH.heliocentricLongitude(earthTrueAnomaly);

        // 4th step : The position of the Earth and the planet are combined to obtain the position of the planet
        // in geocentric ecliptic coordinates.

        // The planet's geocentric ecliptic coordinates
        // Note : We assume that the method at will never be applied to the Earth
        EclipticCoordinates eclipticPos = (axis < 1.0) ?
                innerPlanetsCoords(earthOrbitalRadius, earthHelioLon, eclipticRadius, helioEclipticLon, helioEclipticLat) :
                outerPlanetsCoords(earthOrbitalRadius, earthHelioLon, eclipticRadius, helioEclipticLon, helioEclipticLat);

        // The planet's equatorial position (in radians)
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticPos);

        // Calculation of the distance between the planet and the Earth (in AU)
        double tempDistance = 2.0 * earthOrbitalRadius * orbitalRadius * cos(helioLon - earthHelioLon)
                * cos(helioEclipticLat);
        double earthPlanetDistance = sqrt(earthOrbitalRadius * earthOrbitalRadius + orbitalRadius * orbitalRadius
                - tempDistance);

        // The planet's angular size (in radians)
        double angularSize = angularSize1AU / earthPlanetDistance;

        // The planet's phase, i.e. the illuminated percentage of the planet's "disc" illuminated by the Sun,
        // as seen from the Earth
        double phase = (1.0 + cos(eclipticPos.lon() - helioLon)) / 2.0;

        // The planet's magnitude (unitless)
        double magnitude = magnitude1AU + 5.0 * log10((orbitalRadius * earthPlanetDistance) / sqrt(phase));

        return new Planet(frenchName, equatorialPos, (float) angularSize, (float) magnitude);
    }

    /**
     * Returns the planet's true anomaly (in radians).
     *
     * @param daysSinceJ2010
     *            The number of days elapsed from the epoch J2010 to the epoch of the observed position
     *            of the celestial object (may be negative).
     * @return the planet's true anomaly (in radians)
     */
    private double trueAnomaly(double daysSinceJ2010) {
        double temp = ANGULAR_VELOCITY * (daysSinceJ2010 / tropicalYear);

        // The planet's mean anomaly (in radians)
        double meanAnomaly = temp + lonJ2010 - lonPerigee;

        // The planet's true anomaly (in radians)
        return meanAnomaly + eccentricityDoubled * sin(meanAnomaly);
    }

    /**
     * Returns the planet's orbital radius (i.e. the distance to the Sun) (in AU).
     *
     * @param trueAnomaly
     *            The planet's true anomaly (in radians)
     * @return the planet's orbital radius (in AU)
     */
    private double orbitalRadius(double trueAnomaly) {
        double numeratorRadius = axis * eccentricityTemp;
        double denominatorRadius = 1.0 + eccentricity * cos(trueAnomaly);
        return numeratorRadius / denominatorRadius;
    }

    /**
     * Returns the planet's heliocentric longitude (in radians, normalized to [0,2*PI[).
     *
     * @param trueAnomaly
     *            The planet's true anomaly (in radians)
     * @return the planet's heliocentric longitude (in radians, normalized to [0,2*PI[)
     */
    private double heliocentricLongitude(double trueAnomaly) {
        return trueAnomaly + lonPerigee;
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
     * @return the geocentric ecliptic coordinates of an inner planet
     */
    private EclipticCoordinates innerPlanetsCoords(double earthOrbitalRadius, double earthOrbitalLon, double eclipticRadius,
                                                   double helioEclipticLon, double helioEclipticLat) {
        double earthLonMinusLon = earthOrbitalLon - helioEclipticLon;
        double sin1 = sin(earthLonMinusLon);

        // Calculation of the planet's geocentric ecliptic longitude (in radians)
        double numeratorLon = eclipticRadius * sin1;
        double denominatorLon = earthOrbitalRadius - eclipticRadius * cos(earthLonMinusLon);
        double geoEclipticLon = Angle.normalizePositive(PI + earthOrbitalLon + atan2(numeratorLon, denominatorLon));

        // Calculation of the planet's geocentric ecliptic latitude (in radians, in its valid interval [-PI/2,PI/2])
        double numeratorLat = eclipticRadius * tan(helioEclipticLat) * sin(geoEclipticLon - helioEclipticLon);
        double denominatorLat = - earthOrbitalRadius * sin1;
        double geoEclipticLat = atan(numeratorLat / denominatorLat);

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
     * @return the geocentric ecliptic coordinates of an outer planet
     */
    private EclipticCoordinates outerPlanetsCoords(double earthOrbitalRadius, double earthOrbitalLon, double eclipticRadius,
                                                   double helioEclipticLon, double helioEclipticLat) {

        double helioLonMinusLon = helioEclipticLon - earthOrbitalLon;

        // Calculation of the planet's geocentric ecliptic longitude (in radians)
        double numeratorLon = earthOrbitalRadius * sin(helioLonMinusLon);
        double denominatorLon = eclipticRadius - earthOrbitalRadius * cos(helioLonMinusLon);
        double geoEclipticLon = Angle.normalizePositive(helioEclipticLon + atan2(numeratorLon, denominatorLon));

        // Calculation of the planet's geocentric ecliptic latitude (in radians, in its valid interval [-PI/2,PI/2])
        // The numerator of the longitude is the same as the denominator of the latitude in the formula
        double numeratorLat = eclipticRadius * tan(helioEclipticLat) * sin(geoEclipticLon - helioEclipticLon);
        double geoEclipticLat = atan(numeratorLat / numeratorLon);

        return EclipticCoordinates.of(geoEclipticLon, geoEclipticLat);
    }
}
