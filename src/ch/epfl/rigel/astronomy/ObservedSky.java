package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;
import ch.epfl.rigel.math.ClosedInterval;

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
    private final Map<CelestialObject, CartesianCoordinates> positions; // Immutable map

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

        // Modifiable map which associates to each celestial object in the observed sky its Cartesian coordinates on the plan
        Map<CelestialObject, CartesianCoordinates> allObjectsPositions = new HashMap<>();

        // Derives the projected position of the Sun on the plan and puts them in the map
        sunPosition = equToCart.apply(sun.equatorialPos());
        allObjectsPositions.put(sun, sunPosition);

        // Derives the projected position of the Moon on the plan and puts them in the map
        moonPosition = equToCart.apply(moon.equatorialPos());
        allObjectsPositions.put(moon, moonPosition);

        // Derives the projected positions of the extraterrestrial planets of the solar system on the plan and puts them
        // in the map
        planetPositions = new double[2][7]; // Immutable table of coordinates
        double[][] tempPlanets = new double[2][7]; // Mutable table of coordinates

        addPositions(planets, tempPlanets, equToCart, allObjectsPositions);
        planetPositions[0] = Arrays.copyOf(tempPlanets[0], 7); // Immutable table of x coordinates
        planetPositions[1] = Arrays.copyOf(tempPlanets[1], 7); // Immutable table of y coordinates

        // Derives the projected positions of the stars of the catalogue on the plan and puts them in the map
        int nbStars = stars().size(); // The number of stars in the catalogue
        starPositions = new double[2][nbStars]; // Immutable table of coordinates
        double[][] tempStars = new double[2][nbStars]; // Mutable table of coordinates

        addPositions(stars(), tempStars, equToCart, allObjectsPositions);
        starPositions[0] = Arrays.copyOf(tempStars[0], nbStars); // Immutable table of x coordinates
        starPositions[1] = Arrays.copyOf(tempStars[1], nbStars); // Immutable table of y coordinates

        positions = Map.copyOf(allObjectsPositions);  // Immutable map
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
     * Additional method.
     * Adds the Cartesian coordinates of the given list of celestial objects to the double entry array and to the map
     * of projected positions, using the given conversion.
     *
     * @param list
     *            The list of celestial objects
     * @param tempPositions
     *            The array containing the abscissa and ordinate of the celestial objects on the plan
     * @param equToCart
     *            The conversion from equatorial to Cartesian coordinates of one celestial object
     * @param allObjectsPositions
     *            The map which associated to each Celestial object its position on the plan
     */
    private void addPositions(List<? extends CelestialObject> list, double[][] tempPositions,
                              EquatorialToCartesianConversion equToCart,
                              Map<CelestialObject, CartesianCoordinates> allObjectsPositions) {
        int objectIndex = 0;
        for (CelestialObject object : list) {
            CartesianCoordinates objectPos = equToCart.apply(object.equatorialPos());
            tempPositions[0][objectIndex] = objectPos.x();
            tempPositions[1][objectIndex] = objectPos.y();
            allObjectsPositions.put(object, CartesianCoordinates.of(objectPos.x(), objectPos.y()));
            ++objectIndex;
        }
    }

    /**
     * Returns the closest celestial object to the given point on the plan, as long as it is within the maximum distance.
     *
     * @param searchPoint
     *            The given search point on the plan (in Cartesian coordinates)
     * @param maxDistance
     *            The maximum search distance
     * @return the closest celestial object to the given point
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates searchPoint, double maxDistance) {

        // The positions on the plan of the celestial objects of the sky who are close to the given search point
        Map<CelestialObject, CartesianCoordinates> closePositions = new HashMap<>();

        // Adds to the map only the celestial objects who are contained in a square centered in the given search point
        // and whose sides are of length (2 * maxDistance)
        for (CelestialObject object : positions.keySet()) {
            CartesianCoordinates objectPos = positions.get(object);

            if (squareContains(objectPos, searchPoint, maxDistance)) {
                closePositions.put(object, objectPos);
            }
        }

        double minDistance = Double.MAX_VALUE; // The distance between the closest object and the search point
        CelestialObject closestObject = null; // The closest object to the search point

        // Determines which of the celestial objects on the map is closest to the given point
        for (CelestialObject object : closePositions.keySet()) {
            double distanceToObject = distanceBetween(closePositions.get(object), searchPoint);

            if (distanceToObject < minDistance) {
                minDistance = distanceToObject;
                closestObject = object;
            }
        }
        // Returns a full cell when a celestial object closer than the maximum distance from the given point has been
        // found, or an empty cell otherwise.
        return (closestObject != null && minDistance < maxDistance) ? Optional.of(closestObject) : Optional.empty();
    }

    /**
     * Additional method.
     * Checks if another celestial object is contained a square centered in the search point and whose side is twice
     * the maximum search distance.
     *
     * @param otherPos
     *            The other celestial object's position on the plan
     * @param center
     *            The center of the square, i.e. the search point
     * @param halfSide
     *            The half side of the square, i.e. the maximum search distance
     * @return true if the other celestial object is contained in the square
     */
    private boolean squareContains(CartesianCoordinates otherPos, CartesianCoordinates center, double halfSide) {
        ClosedInterval xSide = ClosedInterval.of(center.x() - halfSide, center.x() + halfSide);
        ClosedInterval ySide = ClosedInterval.of(center.y() - halfSide, center.y() + halfSide);
        return (xSide.contains(otherPos.x()) && ySide.contains(otherPos.y()));
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
