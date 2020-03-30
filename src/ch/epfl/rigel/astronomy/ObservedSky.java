package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import static ch.epfl.rigel.astronomy.PlanetModel.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 */
public final class ObservedSky {

    private final ZonedDateTime when;
    private final StereographicProjection projection;
    private final GeographicCoordinates where;
    private final StarCatalogue catalogue;

    private final Sun sun;
    private final Moon moon;
    private final List<Planet> solarPlanets;

    /**
     * @param when
     * @param projection
     * @param where
     * @param catalogue
     */
    public ObservedSky(ZonedDateTime when, StereographicProjection projection, GeographicCoordinates where, StarCatalogue catalogue) {
        this.when = when;
        this.projection = projection;
        this.where = where;
        this.catalogue = catalogue;

        double daysSinceJ2010 = Epoch.J2010.daysUntil(when);
        EclipticToEquatorialConversion conversion = new EclipticToEquatorialConversion(when);

        sun = SunModel.SUN.at(daysSinceJ2010, conversion);
        moon = MoonModel.MOON.at(daysSinceJ2010, conversion);
        solarPlanets = List.of(
                MERCURY.at(daysSinceJ2010, conversion), VENUS.at(daysSinceJ2010, conversion), MARS.at(daysSinceJ2010, conversion),
                JUPITER.at(daysSinceJ2010, conversion), SATURN.at(daysSinceJ2010, conversion), URANUS.at(daysSinceJ2010, conversion),
                NEPTUNE.at(daysSinceJ2010, conversion));
    }

    /**
     * @return
     */
    public Sun sun() {
        return sun;
    }

    /**
     * @return
     */
    public CartesianCoordinates sunPosition() {
        return equatorialToCartesianCoordinates(sun.equatorialPos());

    }

    /**
     * @return
     */
    public Moon moon() {
        return moon;
    }

    /**
     * @return
     */
    public CartesianCoordinates moonPosition() {
        return equatorialToCartesianCoordinates(moon.equatorialPos());

    }

    /**
     * @return
     */
    public List<Planet> planets() {
        return List.copyOf(solarPlanets);
    }

    /**
     * @return
     */
    public double[][] planetPositions() {

        double[][] planetPositions = new double[2][7];
        int index = 0;

        for (Planet planet : solarPlanets) {

            CartesianCoordinates cartesianCoordinates = equatorialToCartesianCoordinates(planet.equatorialPos());
            planetPositions[0][index] = cartesianCoordinates.x();
            planetPositions[1][index] = cartesianCoordinates.y();
            ++index;
        }
        return planetPositions;
    }

    /**
     * @return
     */
    public Set<Asterism> asterisms() {
        return catalogue.asterisms();
    }

    /**
     * @param asterism
     * @return
     */
    public List<Integer> asterismsIndices(Asterism asterism) {
        return catalogue.asterismIndices(asterism);
    }

    /**
     * @param cartesianCoordinates
     * @param maxDistance
     * @return
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates cartesianCoordinates, double maxDistance) {
        CelestialObject closestObject = sun();
        double minDistance = distance(sunPosition(), cartesianCoordinates);
        double moonDistance = distance(moonPosition(), cartesianCoordinates);

        if (moonDistance < minDistance) {
            minDistance = moonDistance;
            closestObject = moon();
        }

        for (Planet planet : planets()) {
            double planetDistance = distance(equatorialToCartesianCoordinates(planet.equatorialPos()), cartesianCoordinates);
            if (planetDistance < minDistance) {
                minDistance = planetDistance;
                closestObject = planet;
            }
        }

        if (minDistance < maxDistance) {
            return Optional.of(closestObject);
        }
        return Optional.empty();
    }

    /**
     * @param coords1
     * @param coord2
     * @return
     */
    private double distance(CartesianCoordinates coords1, CartesianCoordinates coord2) {
        return Math.sqrt(Math.pow(coords1.x() - coord2.x(), 2) + Math.pow(coords1.y() - coord2.y(), 2));
    }

    /**
     * @param equatorialPos
     * @return
     */
    private CartesianCoordinates equatorialToCartesianCoordinates(EquatorialCoordinates equatorialPos) {
        EquatorialToHorizontalConversion conversion = new EquatorialToHorizontalConversion(when, where);
        HorizontalCoordinates horizontalPosition = conversion.apply(equatorialPos);
        return projection.apply(horizontalPosition);
    }
}
