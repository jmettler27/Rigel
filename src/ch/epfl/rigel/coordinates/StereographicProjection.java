package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A stereographic projection on a plane of the horizontal coordinates of a celestial object visible in the sky.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    // The center of the projection, projected at the origin of the plane
    private final HorizontalCoordinates center;

    // The cosine and sine of the center's latitude
    private final double cosCenterAlt, sinCenterAlt;

    /**
     * Constructs a stereographic projection centered in the given center point.
     *
     * @param center
     *            The center of this projection
     */
    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        this.cosCenterAlt = cos(center.alt());
        this.sinCenterAlt = sin(center.alt());
    }

    /**
     * Returns the Cartesian coordinates of the center of the circle corresponding to the projection of
     * the parallel passing through the given point hor.
     *
     * @param hor
     *            The point on the parallel
     *
     * @return the Cartesian coordinates of the center of the circle
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        // The given point's altitude, i.e. the latitude of the parallel
        double latitude = hor.alt();

        // The ordinate of the center of the circle (may be infinite)
        double centerY = cosCenterAlt / (sin(latitude) + sinCenterAlt);

        return CartesianCoordinates.of(0, centerY);
    }

    /**
     * Returns the radius of the circle corresponding to the projection of the parallel passing through
     * the given point hor.
     *
     * @param parallel
     *            The point on the parallel
     *
     * @return the radius of the circle (may be infinite)
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double parallelAltitude = parallel.alt();
        return cos(parallelAltitude) / (sin(parallelAltitude) + sinCenterAlt);
    }

    /**
     * Returns the projected diameter of a sphere of angular size rad centered at the projection center,
     * assuming that the latter is on the horizon.
     *
     * @param rad
     *            The angular size of the sphere, i.e. its apparent diameter
     *
     * @return the projected diameter of the sphere
     */
    public double applyToAngle(double rad) {
        return 2.0 * tan(rad / 4.0);
    }

    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        // The longitude of the center of the projection
        double centerLon = center.az();

        // The azimuth (lambda) and the altitude (phi) of the point to be projected
        double az = azAlt.az();
        double alt = azAlt.alt();

        double lambdaDelta = az - centerLon;
        double d = 1.0 / (1.0 + sin(alt) * sinCenterAlt + cos(alt) * cosCenterAlt * cos(lambdaDelta));

        double x = d * cos(alt) * sin(lambdaDelta);
        double y = d * (sin(alt) * cosCenterAlt - cos(alt) * sinCenterAlt * cos(lambdaDelta));

        return CartesianCoordinates.of(x, y);
    }

    /**
     * Returns the horizontal coordinates of the point whose projection is the given Cartesian coordinate point.
     *
     * @param xy
     *            The Cartesian coordinate point, i.e. the projection
     *
     * @return the horizontal coordinates of the corresponding point, before projection
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double x = xy.x(); // The abscissa of xy
        double y = xy.y(); // The ordinate of xy

        // The radius of the projected parallel (a circle) centered in (x,y)
        double radius = sqrt(x * x + y * y);

        double sinC = (2.0 * radius) / (radius * radius + 1.0);
        double cosC = (1.0 - radius * radius) / (radius * radius + 1.0);

        // The longitude of the center of the projection
        double centerLon = center.az();

        // Derivation of the azimuth (the first horizontal coordinate):
        double numeratorAz = x * sinC;
        double denominatorAz = radius * cosCenterAlt * cosC - y * sinCenterAlt * sinC;

        // The azimuth, normalized in its valid interval [0, 2*PI[
        double azRad = Angle.normalizePositive(atan2(numeratorAz, denominatorAz) + centerLon);

        // The second horizontal coordinates, the altitude, in its valid interval [-PI/2, PI/2]
        double altRad = asin(cosC * sinCenterAlt + (y * sinC * cosCenterAlt) / radius);

        return HorizontalCoordinates.of(azRad, altRad);
    }

    @Override
    public String toString() {
        return "StereographicProjection centered in " + center;
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}