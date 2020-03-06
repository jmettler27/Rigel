package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A stereographic projection, used to project on a plane (in Cartesian coordinates) the position (initially in horizontal coordinates) of celestial objects visible in the sky.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public final class StereographicProjection
        implements Function<HorizontalCoordinates, CartesianCoordinates> {

    // The center of the projection, projected at the origin of the plane
    private final HorizontalCoordinates center;

    // The cosine and sine of the center's latitude
    private final double cosPhi1, sinPhi1;


    /**
     * Constructs a stereographic projection centered in the given center point.
     *
     * @param center
     *            The given center point
     */
    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        this.cosPhi1 = cos(center.alt());
        this.sinPhi1 = sin(center.alt());
    }

    /**
     * Returns the Cartesian coordinates of the center of the circle
     * corresponding to the projection of the parallel passing through the given
     * point hor.
     *
     * @param hor
     *            The given point on the parallel
     * @return the Cartesian coordinates of the center of the circle
     */
    public CartesianCoordinates circleCenterForParallel(
            HorizontalCoordinates hor) {
        // The given point's altitude, i.e. the latitude of the parallel
        double phi = hor.alt();

        // The ordinate of the center of the circle
        double cy = cosPhi1 / (sin(phi) + sinPhi1);

        return CartesianCoordinates.of(0, cy);
    }

    /**
     * Returns the radius of the circle corresponding to the projection of the
     * parallel passing through the given point hor.
     *
     * @param parallel
     *            The given point on the parallel
     * @return the radius of the circle (i.e. the radius of the parallel)
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double phi = parallel.alt();

        return cos(phi) / (sin(phi) + sinPhi1);
    }

    /**
     * Returns the projected diameter of a sphere of angular size rad
     * centered at the projection center, assuming that the latter is on the horizon.
     *
     * @param rad
     *            The angular size of the sphere, i.e. its apparent diameter
     * @return the projected diameter of the sphere
     */
    public double applyToAngle(double rad) {
        return 2.0 * tan(rad / 4.0);
    }

    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        // The longitude of the center of the projection
        double lambda0 = center.az();

        // The azimuth (lambda) and the altitude (phi) of the point to be projected
        double lambda = azAlt.az();
        double phi = azAlt.alt();

        double lambdaDelta = lambda - lambda0;
        double d = 1.0 / (1.0 + sin(phi) * sinPhi1
                + cos(phi) * cosPhi1 * cos(lambdaDelta));

        double x = d * cos(phi) * sin(lambdaDelta);
        double y = d
                * (sin(phi) * cosPhi1 - cos(phi) * sinPhi1 * cos(lambdaDelta));

        return CartesianCoordinates.of(x, y);
    }

    /**
     * Returns the horizontal coordinates of the point whose projection is the
     * Cartesian coordinate point xy.
     *
     * @param xy
     *            The given Cartesian coordinate point, i.e. the projection
     * @return the horizontal coordinates of the corresponding point, before
     *         projection
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double x = xy.x(); // The abscissa of xy
        double y = xy.y(); // The ordinate of xy

        // The radius of the projected parallel (a circle) centered in (x,y)
        double rho = sqrt(x * x + y * y);

        double sinC = (2.0 * rho) * (rho * rho + 1);
        double cosC = (1 - rho * rho) / (rho * rho + 1);

        // The longitude of the center of the projection
        double lambda0 = center.az();

        double numerator = x * sinC;
        double denominator = rho * cosPhi1 * cosC - y * sinPhi1 * sinC;

        // The first horizontal coordinates, the azimuth
        double lambda = atan2(numerator, denominator) + lambda0;

        // The azimuth normalized in its valid interval [0, 2*PI[
        double normalized_Lambda = Angle.normalizePositive(lambda);

        // The second horizontal coordinates, the altitude
        double phi = asin(cosC * sinPhi1 + (y * sinC * cosPhi1) / rho);

        return HorizontalCoordinates.of(normalized_Lambda, phi);
    }

    @Override
    public String toString() {
        return "Stereographic Projection: center's coordinates: " + center.toString();
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
