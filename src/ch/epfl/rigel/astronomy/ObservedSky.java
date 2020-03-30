package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import static java.lang.Math.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * A set of celestial objects projected in the plane by stereographic projection, at a given epoch and place of observation.
 */
public final class ObservedSky {

    private final ZonedDateTime when;
    private final StereographicProjection projection;
    private final GeographicCoordinates where;
    private final StarCatalogue catalogue;

    private final Sun sun;
    private final Moon moon;
    private final List<Planet> planets;

    private final CartesianCoordinates sunPosition;
    private final CartesianCoordinates moonPosition;
    private final double[][] planetPositions;
    private final double[][] starPositions;


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
        this.when = when;
        this.projection = projection;
        this.where = where;
        this.catalogue = catalogue;

        // The number of days elapsed from the epoch J2010 to the epoch of the observed position
        double daysSinceJ2010 = Epoch.J2010.daysUntil(when);

        // Conversion from ecliptic to equatorial coordinates
        EclipticToEquatorialConversion conversion = new EclipticToEquatorialConversion(when);

        // The Sun and the Moon as observed at the given epoch and place of observation
        sun = SunModel.SUN.at(daysSinceJ2010, conversion);
        moon = MoonModel.MOON.at(daysSinceJ2010, conversion);

        // The models of the extraterrestrial planets of the solar system
        List<PlanetModel> models = new ArrayList<>();
        models.addAll(PlanetModel.INNER_PLANETS);
        models.addAll(PlanetModel.OUTER_PLANETS);

        // The extraterrestrial planets of the solar system as observed at the given epoch and place of observation
        planets = new ArrayList<>();
        for (PlanetModel model : models) {
            planets.add(model.at(daysSinceJ2010, conversion));
        }

        sunPosition = equToCartConversion(sun.equatorialPos());
        moonPosition = equToCartConversion(moon.equatorialPos());
        planetPositions = planetPositions();
        starPositions = starPositions();
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
        return List.copyOf(planets);
    }

    /**
     * Returns the Cartesian coordinates of the extraterrestrial planets of the solar system as projected in the plan.
     * @return the Cartesian coordinates of the extraterrestrial planets of the solar system as projected in the plan
     */
    public double[][] planetPositions() {
        double[][] planetPositions = new double[2][7]; // Immutable table of coordinates
        double[][] temp = new double[2][7]; // Mutable table of coordinates

        int planetIndex = 0;
        for (Planet planet : planets) {
            CartesianCoordinates cartesianPos = equToCartConversion(planet.equatorialPos());
            temp[0][planetIndex] = cartesianPos.x();
            temp[1][planetIndex] = cartesianPos.y();
            ++planetIndex;
        }
        planetPositions[0] = Arrays.copyOf(temp[0], 7); // Immutable table of x coordinates
        planetPositions[1] = Arrays.copyOf(temp[1], 7); // Immutable table of y coordinates

        return planetPositions;
    }

    /**
     * Returns the list of the stars of the catalogue.
     * @return the list of the stars of the catalogue
     */
    public List<Star> stars(){
        return catalogue.stars();
    }

    /**
     * Returns the Cartesian coordinates of the stars of the catalogue in the plan.
     * @return the Cartesian coordinates of the stars of the catalogue in the plan
     */
    public double[][] starPositions(){
        int nbStars = stars().size(); // The number of stars in the catalogue
        double[][] starPositions = new double[2][nbStars]; // Immutable table of coordinates
        double[][] temp = new double[2][nbStars]; // Mutable table of coordinates

        int starIndex = 0;
        for(Star star : stars()){
            CartesianCoordinates cartesianPos = equToCartConversion(star.equatorialPos());
            temp[0][starIndex] = cartesianPos.x();
            temp[1][starIndex] = cartesianPos.y();
            ++starIndex;
        }
        starPositions[0] = Arrays.copyOf(temp[0], nbStars); // Immutable table of x coordinates
        starPositions[1] = Arrays.copyOf(temp[1], nbStars); // Immutable table of y coordinates

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
        double minDistance = distanceBetween(sunPosition(), cartesianPos);
        CelestialObject closestObject = sun();

        // Checks if the Moon is closer than the Sun to the given point
        double moonDistance = distanceBetween(moonPosition(), cartesianPos);
        if (moonDistance < minDistance) {
            minDistance = moonDistance;
            closestObject = moon();
        }

        // Checks if one of the planets is closer than the Sun or the Moon to the given point
        int planetIndex = 0;
        for (Planet planet : planets()) {
            // The Cartesian coordinates of the planet
            CartesianCoordinates planetCartesianPos = CartesianCoordinates.of(
                    planetPositions[0][planetIndex],
                    planetPositions[1][planetIndex]);

            double distanceToPlanet = distanceBetween(planetCartesianPos, cartesianPos);
            if (distanceToPlanet < minDistance) {
                minDistance = distanceToPlanet;
                closestObject = planet;
            }
            ++planetIndex;
        }

        // Checks if one of the stars is closer than the Sun or the Moon or the planets to the given point
        int starIndex = 0;
        for (Star star : stars()) {
            // The Cartesian coordinates of the star
            CartesianCoordinates starCartesianPos = CartesianCoordinates.of(
                    starPositions[0][starIndex],
                    starPositions[1][starIndex]);

            double distanceToStar = distanceBetween(starCartesianPos, cartesianPos);
            if (distanceToStar < minDistance) {
                minDistance = distanceToStar;
                closestObject = star;
            }
            ++starIndex;
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

    /**
     * Additional method.
     * Converts the given equatorial coordinates to Cartesian coordinates.
     *
     * @param equ
     *            The given equatorial coordinates
     * @return the Cartesian coordinates corresponding to the given equatorial coordinates
     */
    private CartesianCoordinates equToCartConversion(EquatorialCoordinates equ) {
        // Conversion from equatorial to horizontal coordinates
        EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(when, where);

        // Conversion from equatorial to horizontal coordinates, and then from horizontal to Cartesian coordinates
        // (using the stereographic projection of the sky for the second conversion)
        Function<EquatorialCoordinates, CartesianCoordinates> equToCart = equToHor.andThen(projection);

        return equToCart.apply(equ);
    }
}
