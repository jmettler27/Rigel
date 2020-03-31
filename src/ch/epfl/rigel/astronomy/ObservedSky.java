package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import static java.lang.Math.*;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * A set of celestial objects projected on the plane by stereographic projection, at a given epoch and place of observation.
 */
public final class ObservedSky {

    private final StarCatalogue catalogue;

    private final Sun sun;
    private final Moon moon;
    private final List<Planet> planets;

    // The projected positions on the plan of all of the celestial objects in the observed sky
    private final CartesianCoordinates sunPosition, moonPosition;
    private final double[][] planetPositions, starPositions;
    private final Map<CelestialObject, CartesianCoordinates> positions;

    /**
     * Constructs a representation of the sky at a given epoch and place of observation.
     *
     * @param when
     *            The epoch of observation, in the UTC time-zone
     * @param projection
     *            The stereographic projection of the celestial objects
     * @param where
     *            The place of observation
     * @param catalogue
     *            The catalogue of the observed stars
     */
    public ObservedSky(ZonedDateTime when, StereographicProjection projection, GeographicCoordinates where,
                       StarCatalogue catalogue) {
        this.catalogue = catalogue;

        // The number of days elapsed from the epoch J2010 to the epoch of the observation
        double daysSinceJ2010 = Epoch.J2010.daysUntil(when);

        // Conversion from ecliptic to equatorial coordinates
        EclipticToEquatorialConversion eclToEqu = new EclipticToEquatorialConversion(when);

        // The Sun and the Moon as observed at the given epoch and place of observation
        sun = SunModel.SUN.at(daysSinceJ2010, eclToEqu);
        moon = MoonModel.MOON.at(daysSinceJ2010, eclToEqu);

        // The models of the extraterrestrial planets of the solar system
        List<PlanetModel> models = new ArrayList<>();
        models.addAll(PlanetModel.INNER_PLANETS);
        models.addAll(PlanetModel.OUTER_PLANETS);

        // The extraterrestrial planets of the solar system as observed at the given epoch and place of observation
        List<Planet> planetsList = new ArrayList<>();
        for (PlanetModel model : models) {
            planetsList.add(model.at(daysSinceJ2010, eclToEqu));
        }
        planets = List.copyOf(planetsList);

        // Conversion from equatorial to Cartesian coordinates
        EquatorialToCartesianConversion equToCart = new EquatorialToCartesianConversion(when, where, projection);

        // Map which associates to each celestial object in the observed sky its Cartesian coordinates on the plan
        positions = new HashMap<>();

        // Derives the projected position of the Sun on the plan
        sunPosition = equToCart.apply(sun.equatorialPos());
        positions.put(sun, sunPosition);

        // Derives the projected position of the Moon on the plan
        moonPosition = equToCart.apply(moon.equatorialPos());
        positions.put(moon, moonPosition);

        // Derives the projected positions of the extraterrestrial planets of the solar system on the plan
        planetPositions = new double[2][7]; // Immutable table of coordinates
        double[][] tempPlanets = new double[2][7]; // Mutable table of coordinates

        int planetIndex = 0;
        for (Planet planet : planets) {
            CartesianCoordinates planetPos = equToCart.apply(planet.equatorialPos());
            tempPlanets[0][planetIndex] = planetPos.x();
            tempPlanets[1][planetIndex] = planetPos.y();
            positions.put(planet, CartesianCoordinates.of(planetPos.x(), planetPos.y()));
            ++planetIndex;
        }
        planetPositions[0] = Arrays.copyOf(tempPlanets[0], 7); // Immutable table of x coordinates
        planetPositions[1] = Arrays.copyOf(tempPlanets[1], 7); // Immutable table of y coordinates

        // Derives the projected positions of the stars of the catalogue on the plan
        int nbStars = stars().size(); // The number of stars in the catalogue
        starPositions = new double[2][nbStars]; // Immutable table of coordinates
        double[][] tempStars = new double[2][nbStars]; // Mutable table of coordinates

        int starIndex = 0;
        for (Star star : stars()) {
            CartesianCoordinates starPos = equToCart.apply(star.equatorialPos());
            tempStars[0][starIndex] = starPos.x();
            tempStars[1][starIndex] = starPos.y();
            positions.put(star, CartesianCoordinates.of(starPos.x(), starPos.y()));
            ++starIndex;
        }
        starPositions[0] = Arrays.copyOf(tempStars[0], nbStars); // Immutable table of x coordinates
        starPositions[1] = Arrays.copyOf(tempStars[1], nbStars); // Immutable table of y coordinates
    }

    /**
     * Returns the Sun at the given epoch and place of observation.
     * @return the Sun at the given epoch and place of observation
     */
    public Sun sun() {
        return sun;
    }

    /**
     * Returns the Cartesian coordinates of the Sun as projected in the plan.
     * @return the Cartesian coordinates of the Sun as projected in the plan
     */
    public CartesianCoordinates sunPosition() {
        return sunPosition;
    }

    /**
     * Returns the Moon at the given epoch and place of observation.
     * @return the Moon at the given epoch and place of observation
     */
    public Moon moon() {
        return moon;
    }

    /**
     * Returns the Cartesian coordinates of the Moon as projected in the plan.
     * @return the Cartesian coordinates of the Moon as projected in the plan
     */
    public CartesianCoordinates moonPosition() {
        return moonPosition;
    }

    /**
     * Returns the list of the extraterrestrial planets of the solar system at the given epoch and place of observation.
     * @return the list of the extraterrestrial planets of the solar system at the given epoch and place of observation
     */
    public List<Planet> planets() {
        return planets;
    }

    /**
     * Returns the Cartesian coordinates of the extraterrestrial planets of the solar system as projected in the plan.
     * @return the Cartesian coordinates of the extraterrestrial planets of the solar system as projected in the plan
     */
    public double[][] planetPositions() {
        return planetPositions;
    }

    /**
     * Returns the list of the stars of the catalogue.
     * @return the list of the stars of the catalogue
     */
    public List<Star> stars() {
        return catalogue.stars();
    }

    /**
     * Returns the Cartesian coordinates of the stars of the catalogue in the plan.
     * @return the Cartesian coordinates of the stars of the catalogue in the plan
     */
    public double[][] starPositions() {
        return starPositions;
    }

    /**
     * Returns an immutable view on the set of the asterisms of the catalogue.
     * @return an immutable view on the set of the asterisms of the catalogue
     */
    public Set<Asterism> asterisms() {
        return catalogue.asterisms();
    }

    /**
     * Returns the list of the indices (in the catalogue) of the stars composing the given asterism
     *
     * @param asterism
     *            The asterism of the catalogue
     * @throws IllegalArgumentException
     *             if the given asterism is not contained in the catalogue
     * @return an immutable list of the indices of the stars composing the given asterism
     */
    public List<Integer> asterismsIndices(Asterism asterism) {
        return catalogue.asterismIndices(asterism);
    }

    /**
     * Returns the closest celestial object to the given point on the plan, as long as it is within the maximum distance.
     *
     * @param cartesianPos
     *            The given point on the plan (in Cartesian coordinates)
     * @param maxDistance
     *            The maximum distance
     * @return the closest celestial object to the given point
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates cartesianPos, double maxDistance) {
        // By default, we choose the Sun to be the closest celestial object to the given point
        double minDistance = distanceBetween(sunPosition, cartesianPos); // The minimum distance
        CelestialObject closestObject = sun;

        // Checks if one of the celestial objects is closer than the Sun to the given point
        for (CelestialObject object : positions.keySet()) {
            double distanceToObject = distanceBetween(positions.get(object), cartesianPos);
            if (distanceToObject < minDistance) {
                minDistance = distanceToObject;
                closestObject = object;
            }
        }
        // Returns a full container (cell) when a celestial object closer than the maximum distance from the given point
        // has been found, or an empty cell otherwise.
        return (minDistance < maxDistance) ? Optional.of(closestObject) : Optional.empty();
    }

    /**
     * Additional method.
     * Derives the distance between the two given points on the plan.
     *
     * @param point1
     *            The Cartesian coordinates of the first point on the plan
     * @param point2
     *            The Cartesian coordinates of the second point on the plan
     * @return the distance between the two points on the plan
     */
    private double distanceBetween(CartesianCoordinates point1, CartesianCoordinates point2) {
        double x1 = point1.x(), x2 = point2.x(), y1 = point1.y(), y2 = point2.y();
        return hypot((x1 - x2), (y1 - y2));
    }
}
