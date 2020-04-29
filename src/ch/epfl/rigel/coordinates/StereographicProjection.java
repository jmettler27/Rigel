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

    private final HorizontalCoordinates center; // The center of the projection, projected at the origin of the plane
    private final double cosCenterAlt, sinCenterAlt; // The cosine and sine of the center's latitude

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
     * Returns the radius of the circle corresponding to the projection of the parallel passing through the given point
     *
     * @param parallel
     *            The point on the parallel
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
     * @return the projected diameter of the sphere
     */
    public double applyToAngle(double rad) {
        return 2.0 * tan(rad / 4.0);
    }

    /**
     * @see Function#apply(Object)
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        // The longitude of the center of the projection
        double centerLon = center.az();

        // The azimuth and the altitude of the point to be projected
        double az = azAlt.az();
        double alt = azAlt.alt();
        double cosAlt = cos(alt), sinAlt = sin(alt);

        // Calculation variables
        double lambdaDelta = az - centerLon;
        double cosLambdaDelta = cos(lambdaDelta);
        double d = 1.0 / (1.0 + sinAlt * sinCenterAlt + cosAlt * cosCenterAlt * cosLambdaDelta);

        double abscissa = d * cosAlt * sin(lambdaDelta);
        double ordinate = d * (sinAlt * cosCenterAlt - cosAlt * sinCenterAlt * cosLambdaDelta);

        return CartesianCoordinates.of(abscissa, ordinate);
    }

    /**
     * Returns the horizontal coordinates of the point whose projection on the plane is the given Cartesian coordinate point.
     *
     * @param xy
     *            The Cartesian coordinate point, i.e. the projection of the horizontal coordinates on the plane
     * @return the horizontal coordinates of the corresponding point, before projection
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        // The abscissa and ordinate of the projected point
        double x = xy.x(), y = xy.y();
        if (x == 0 && y == 0) {
            return HorizontalCoordinates.of(center.az(), center.alt());
        }

        // The squared radius and radius of the projected parallel (a circle) centered in (x,y)
        double squaredRadius = x * x + y * y;
        double squaredRadiusIncremented = 1.0 + squaredRadius;
        double radius = sqrt(squaredRadius);

        double sinC = (2.0 * radius) / squaredRadiusIncremented;
        double cosC = (1.0 - squaredRadius) / squaredRadiusIncremented;

        // Calculation of the azimuth (first horizontal coordinate, in radians)
        double numeratorAz = x * sinC;
        double denominatorAz = radius * cosCenterAlt * cosC - y * sinCenterAlt * sinC;
        double centerLon = center.az(); // The longitude of the center of the projection
        double azRad = Angle.normalizePositive(atan2(numeratorAz, denominatorAz) + centerLon);

        // The altitude (second horizontal coordinate, in radians, in its valid interval[-PI/2,PI/2])
        double altRad = asin(cosC * sinCenterAlt + (y * sinC * cosCenterAlt) / radius);

        return HorizontalCoordinates.of(azRad, altRad);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "StereographicProjection centered in " + center;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}