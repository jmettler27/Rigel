package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.function.Function;

import static java.lang.Math.*;

/**
 * A stereographic projection, used to project the position (from horizontal to
 * Cartesian coordinates) of celestial objects visible in the sky.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class StereographicProjection
        implements Function<HorizontalCoordinates, CartesianCoordinates> {

    // The center of the projection, projected at the origin of the plan
    private final HorizontalCoordinates center;

    private final double cosPhi1; // The cosinus of the center's latitude
    private final double sinPhi1; // The sinus of the center's latitude

    /**
     * Constructs a stereographic projection centered in the given center point.
     * 
     * @param center
     *            The given center point
     */
    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        this.cosPhi1 = cos(center.lat());
        this.sinPhi1 = sin(center.lat());
    }

    /**
     * Returns the cartesian coordinates of the center of the circle
     * corresponding to the projection of the parallel passing through the given
     * point hor.
     * 
     * @param hor
     *            The given point on the parallel
     * @return the cartesian coordinates of the center of the circle
     */
    public CartesianCoordinates circleCenterForParallel(
            HorizontalCoordinates hor) {
        // The given point's latitude
        double phi = hor.lat();

        // The ordinate of the center of the circle
        double cy = (cosPhi1 / (sin(phi) + sinPhi1));

        return CartesianCoordinates.of(0, cy);

    }

    /**
     * Returns the radius of the circle corresponding to the projection of the
     * parallel passing through the given point hor.
     * 
     * @param parallel
     *            The given point on the parallel
     * @return the radius of the circle (the parallel)
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double phi1 = center.lat();
        double phi = parallel.lat();

        return ((cos(phi)) / (sin(phi) + sin(phi1)));
    }

    /**
     * Returns the projected diameter of a sphere of apparent diameter rad
     * centered at the projection center, assuming that it is on the horizon.
     * 
     * @param rad
     *            The apparent diameter of the sphere
     * @return the projected diameter of the sphere
     */
    public double applyToAngle(double rad) {
        return 2.0 * tan(rad / 4.0);
    }

    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        // The longitude of the center of the projection
        double lambda0 = center.lon();

        double lambda = azAlt.lon();
        double phi = azAlt.lat();

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

        double rho = Math.sqrt(x * x + y * y);
        double sinC = 2.0 * (rho * rho + 1);
        double cosC = (1 - rho * rho) / (rho * rho + 1);

        // The longitude of the center of the projection
        double lambda0 = center.lon();

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
        return "Stereographic Projection: " + center.toString();
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
