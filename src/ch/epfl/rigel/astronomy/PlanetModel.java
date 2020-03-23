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
            5.20278, 1.3035, 100.595, 196.74, -9.40),

    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873,113.752, 165.60, -8.88),

    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321,
            19.21814,0.773059, 73.926961, 65.80, -7.19),

    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673,131.879, 62.20, -6.87);

    private final String frenchName;

    private final double tropicalYear, lonJ2010, lonPerigee, eccentricity, axis, inclination, lonAscending,
            angularSize1AU, magnitude1AU;

    private final static double ANGULAR_VELOCITY = Angle.TAU / 365.242191;

    // The planets of the solar system, following elliptical orbits around the Sun
    public final static List<PlanetModel> ALL = List.of(MERCURY, VENUS, EARTH,MARS, JUPITER, SATURN, URANUS, NEPTUNE);

    private final static List<PlanetModel>
            INNER_PLANETS = ALL.subList(0, 2), // The planets that orbit closer to the Sun than the Earth
            OUTER_PLANETS = ALL.subList(3, 8); // The planets that orbit further to the Sun than the Earth

    /**
     * Constructs the model of a planet through planetary constants.
     *
     * @param frenchName
     *            The planet's french name
     * @param tropicalYear
     *            The planet's period (in tropical years)
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
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        // 1st step : The position of the planet in its own orbit
        double trueAnomaly = trueAnomaly(daysSinceJ2010);

        // The planet's orbital radius (distance to the Sun) (in AU)
        double orbitalRadius = orbitalRadius(trueAnomaly);

        // The planet's heliocentric longitude (in radians)
        double helioLon = heliocentricLongitude(trueAnomaly);

        // 2nd step : The position of the planet is projected on the ecliptic plane and then expressed in
        // heliocentric ecliptic coordinates

        // The planet's heliocentric ecliptic latitude (in radians, in the interval [-PI/2, PI/2])
        double helioEclipticLat = asin(sin(helioLon - lonAscending) * sin(inclination));

        // The projection of the orbital radius on the ecliptic plane (in AU)
        double eclipticRadius = orbitalRadius * cos(helioEclipticLat);

        double helioEclipticLonNumerator = sin(helioLon - lonAscending) * cos(inclination);
        double helioEclipticLonDenominator = cos(helioLon - lonAscending);
        // The planet's heliocentric ecliptic longitude (in radians, in the interval [0, 2*PI[)
        double helioEclipticLon = Angle.normalizePositive(atan2(helioEclipticLonNumerator, helioEclipticLonDenominator)
                + lonAscending);

        // 3rd step : The position of the Earth is determined

        // The Earth's true anomaly (in radians)
        double earthTrueAnomaly = EARTH.trueAnomaly(daysSinceJ2010);

        // The Earth's orbital radius (in AU)
        double earthOrbitalRadius = EARTH.orbitalRadius(earthTrueAnomaly);

        // The Earth's heliocentric longitude (in radians, in the interval [0, 2*PI[)
        double earthHelioLon = Angle.normalizePositive(EARTH.heliocentricLongitude(earthTrueAnomaly));

        // 4th step : The position of the Earth and the planet are combined to obtain the position of the planet
        // in geocentric ecliptic coordinates.

        // The planet's geocentric ecliptic coordinates
        // Note : We assume that the method at will never be applied to the Earth
        EclipticCoordinates eclipticCoordinates = INNER_PLANETS.contains(this) ?
                innerPlanetsCoords(earthOrbitalRadius, earthHelioLon, eclipticRadius, helioEclipticLon, helioEclipticLat) :
                outerPlanetsCoords(earthOrbitalRadius, earthHelioLon, eclipticRadius, helioEclipticLon, helioEclipticLat);

        // The planet's equatorial coordinates
        EquatorialCoordinates equatorialCoordinates = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        double tempDistance = 2.0 * earthOrbitalRadius * orbitalRadius * cos(helioLon - earthHelioLon) * cos(helioEclipticLat);

        // The distance between the planet and the Earth (in AU)
        double earthPlanetDistance = sqrt(earthOrbitalRadius * earthOrbitalRadius + orbitalRadius * orbitalRadius - tempDistance);

        // The planet's angular size (in radians)
        double angularSize = angularSize1AU / earthPlanetDistance;

        // The planet's phase, i.e. the illuminated percentage of the planet's
        // "disc" illuminated by the Sun, as seen from the Earth
        double phase = (1.0 + cos(eclipticCoordinates.lon() - helioLon)) / 2.0;

        // The planet's magnitude (unitless)
        double magnitude = magnitude1AU + 5.0 * log10((orbitalRadius * earthPlanetDistance) / sqrt(phase));

        return new Planet(frenchName, equatorialCoordinates, (float) angularSize, (float) magnitude);
    }

    /**
     * Returns the planet's true anomaly (in radians).
     * 
     * @param daysSinceJ2010
     *            The number of days elapsed from the epoch J2010 to the epoch of the observed position
     *            of the celestial object (may be negative).
     *
     * @return the planet's true anomaly (in radians)
     */
    private double trueAnomaly(double daysSinceJ2010) {
        double planetTemp = Angle.normalizePositive(ANGULAR_VELOCITY * (daysSinceJ2010 / tropicalYear));

        // The planet's mean anomaly (in radians)
        double meanAnomaly = planetTemp + lonJ2010 - lonPerigee;

        // The planet's true anomaly (in radians)
        return Angle.normalizePositive(meanAnomaly + 2.0 * eccentricity * sin(meanAnomaly));
    }

    /**
     * Returns the planet's orbital radius (i.e. the distance to the Sun) (in AU).
     *
     * @param trueAnomaly
     *            The planet's true anomaly (in radians)
     *
     * @return the planet's orbital radius (in AU)
     */
    private double orbitalRadius(double trueAnomaly) {
        double numeratorRadius = axis * (1.0 - eccentricity * eccentricity);
        double denominatorRadius = 1.0 + eccentricity * cos(trueAnomaly);
        return numeratorRadius / denominatorRadius;
    }

    /**
     * Returns the planet's heliocentric longitude (in radians).
     * 
     * @param trueAnomaly
     *            The planet's true anomaly (in radians)
     *
     * @return the planet's heliocentric longitude (in radians)
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
     *
     * @return the geocentric ecliptic coordinates of an inner planet
     */
    private EclipticCoordinates innerPlanetsCoords(double earthOrbitalRadius, double earthOrbitalLon, double eclipticRadius,
                                                   double helioEclipticLon, double helioEclipticLat) {

        // Derivation of the planet's geocentric ecliptic longitude:
        double numeratorLon = eclipticRadius * sin(earthOrbitalLon - helioEclipticLon);
        double denominatorLon = earthOrbitalRadius - eclipticRadius * cos(earthOrbitalLon - helioEclipticLon);

        // The planet's geocentric ecliptic longitude normalized in [0, 2*PI[
        double geoEclipticLon = Angle.normalizePositive(PI + earthOrbitalLon + atan2(numeratorLon, denominatorLon));

        // Derivation of the planet's geocentric ecliptic latitude:
        double numeratorLat = eclipticRadius * tan(helioEclipticLat) * sin(geoEclipticLon - helioEclipticLon);
        double denominatorLat = earthOrbitalRadius * sin(helioEclipticLon - earthOrbitalLon);

        // The planet's geocentric ecliptic latitude, in [-PI/2, PI/2]
        // Note : We use here the method atan since it returns an angle in the range [-PI/2, PI/2], which is
        // the valid latitude range
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
     *
     * @return the geocentric ecliptic coordinates of an outer planet
     */
    private EclipticCoordinates outerPlanetsCoords(double earthOrbitalRadius, double earthOrbitalLon, double eclipticRadius,
                                                   double helioEclipticLon, double helioEclipticLat) {

        // Derivation of the planet's geocentric ecliptic longitude:
        double numeratorLon = earthOrbitalRadius * sin(helioEclipticLon - earthOrbitalLon);
        double denominatorLon = eclipticRadius - earthOrbitalRadius * cos(helioEclipticLon - earthOrbitalLon);

        // The planet's geocentric ecliptic longitude normalized in [0, 2*PI[
        double geoEclipticLon = Angle.normalizePositive(helioEclipticLon + atan2(numeratorLon, denominatorLon));

        // Derivation of the planet's geocentric ecliptic latitude:
        double numeratorLat = eclipticRadius * tan(helioEclipticLat) * sin(geoEclipticLon - helioEclipticLon);

        // The planet's geocentric ecliptic latitude
        // Note : The method atan returns an angle in the range [-PI/2, PI/2], which is the valid latitude range
        double geoEclipticLat = atan(numeratorLat / numeratorLon);

        return EclipticCoordinates.of(geoEclipticLon, geoEclipticLat);
    }
}
