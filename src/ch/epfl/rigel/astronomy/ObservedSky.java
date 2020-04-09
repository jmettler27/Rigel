package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * A set of celestial objects projected on the plane by a stereographic projection, at a given epoch and place of
 * observation.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class ObservedSky {

    private final StarCatalogue catalogue;

    private final Sun sun;
    private final Moon moon;
    private final List<Planet> planets;

    // The projected positions on the plane of all of the celestial objects in the observed sky
    private final CartesianCoordinates sunPosition, moonPosition;
    private final double[] planetPositions, starPositions;
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
    public ObservedSky(ZonedDateTime when, GeographicCoordinates where, StereographicProjection projection,
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
        List<PlanetModel> planetModels = new ArrayList<>();
        planetModels.addAll(PlanetModel.INNER_PLANETS);
        planetModels.addAll(PlanetModel.OUTER_PLANETS);

        // The extraterrestrial planets of the solar system as observed at the given epoch and place of observation
        List<Planet> planetsList = new ArrayList<>();
        for (PlanetModel model : planetModels) {
            planetsList.add(model.at(daysSinceJ2010, eclToEqu));
        }
        planets = List.copyOf(planetsList);

        // Conversion from equatorial to Cartesian coordinates
        EquatorialToCartesianConversion equToCart = new EquatorialToCartesianConversion(when, where, projection);

        // Modifiable map which associates to each celestial object in the observed sky its Cartesian coordinates on the plan
        Map<CelestialObject, CartesianCoordinates> allObjectsPositions = new HashMap<>();

        // Derives the projected position of the Sun on the plane and puts them in the map
        sunPosition = equToCart.apply(sun.equatorialPos());
        allObjectsPositions.put(sun, sunPosition);

        // Derives the projected position of the Moon on the plane and puts them in the map
        moonPosition = equToCart.apply(moon.equatorialPos());
        allObjectsPositions.put(moon, moonPosition);

        // Derives the projected positions of the planets of the solar system on the plane and puts them in the map
        double[] tempPlanetPositions = multiplePositions(planets, equToCart, allObjectsPositions);
        planetPositions = Arrays.copyOf(tempPlanetPositions, 2 * planets.size());

        // Derives the projected positions of the stars of the catalogue on the plane and puts them in the map
        double[] tempStarPositions = multiplePositions(stars(), equToCart, allObjectsPositions);
        starPositions = Arrays.copyOf(tempStarPositions, 2 * stars().size());

        positions = Map.copyOf(allObjectsPositions);  // Unmodifiable map
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
    public double[] planetPositions() {
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
    public double[] starPositions() {
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
     * @param searchPoint
     *            The given search point on the plane (in Cartesian coordinates), i.e. the position of the mouse pointer
     * @param maxDistance
     *            The maximum search distance
     * @return the closest celestial object to the given point
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates searchPoint, double maxDistance) {

        // The positions on the plane of the celestial objects of the sky who are close to the given search point
        Map<CelestialObject, CartesianCoordinates> closePositions = new HashMap<>();

        // Adds to the map only the celestial objects who are contained in a square centered in the given search point
        // and whose sides are of length (2 * maxDistance)
        for (CelestialObject object : positions.keySet()) {
            CartesianCoordinates objectPos = positions.get(object);

            if (objectPos.isContainedInSquare(searchPoint, maxDistance)) {
                closePositions.put(object, objectPos);
            }
        }

        double minDistance = Double.MAX_VALUE; // The distance between the closest object and the search point
        CelestialObject closestObject = null; // The closest object to the search point

        // Determines which of the celestial objects on the map is closest to the given point
        for (CelestialObject nearObject : closePositions.keySet()) {
            double distanceToObject = searchPoint.distanceTo(closePositions.get(nearObject));

            if (distanceToObject < minDistance) {
                minDistance = distanceToObject;
                closestObject = nearObject;
            }
        }
        // Returns a full cell when a non null celestial object closer than the maximum distance from the given point
        // has been found, or an empty cell otherwise.
        return (closestObject != null && minDistance < maxDistance) ? Optional.of(closestObject) : Optional.empty();
    }

    /**
     * Additional method.
     * Adds the Cartesian coordinates of the given list of celestial objects to the map of projected positions, using
     * the given conversion.
     *
     * @param list
     *            The list of celestial objects
     * @param equToCart
     *            The conversion from equatorial to Cartesian coordinates of one celestial object
     * @param positions
     *            The map which associates to each Celestial object its position on the plane
     */
    private double[] multiplePositions(List<? extends CelestialObject> list, EquatorialToCartesianConversion equToCart,
                                         Map<CelestialObject, CartesianCoordinates> positions) {

        double[] tempPositions = new double[2 * list.size()];

        for (int i = 0; i < list.size(); i++) {
            CelestialObject object = list.get(i);

            CartesianCoordinates cartesianPos = equToCart.apply(object.equatorialPos());
            tempPositions[2 * i] = cartesianPos.x();
            tempPositions[2 * i + 1] = cartesianPos.y();

            positions.put(object, cartesianPos);
        }
        return tempPositions;
    }
}
