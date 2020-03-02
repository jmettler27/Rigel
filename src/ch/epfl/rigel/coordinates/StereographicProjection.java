package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.Locale;
import java.util.function.Function;

import static java.lang.Math.*;

public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    private final HorizontalCoordinates center;
    private final double cosPhi1;
    private final double sinPhi1;


    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        this.cosPhi1 = cos(center.lat());
        this.sinPhi1 = sin(center.lat());
    }


    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        double phi = hor.lat();
        double cy = (cosPhi1 / (sin(phi) + sinPhi1));

        return CartesianCoordinates.of(0, cy);

    }

    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double phi1 = center.lat();
        double phi = parallel.lat();


        return ((cos(phi)) / (sin(phi) + sin(phi1)));
    }

    public double applyToAngle(double rad) {
        return 2.0 * tan(rad / 4.0);
    }

    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        double lambda0 = center.lon();

        double lambda = azAlt.lon();
        double phi = azAlt.lat();

        double lambdaDelta = lambda - lambda0;
        double d = 1.0 / (1.0 + sin(phi) * sinPhi1 + cos(phi) * cosPhi1 * cos(lambdaDelta));

        double x = d * cos(phi) * sin(lambdaDelta);
        double y = d * (sin(phi) * cosPhi1 - cos(phi) * sinPhi1 * cos(lambdaDelta));

        return CartesianCoordinates.of(x, y);
    }

    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double x = xy.x();
        double y = xy.y();

        double rho = Math.sqrt(x * x + y * y);
        double sinC = 2.0 * (rho * rho + 1);
        double cosC = (1 - rho * rho) / (rho * rho + 1);

        double lambda0 = center.lon();

        double numerator = x * sinC;
        double denominator = rho * cosPhi1 * cosC - y * sinPhi1 * sinC;
        double lambda = atan2(numerator, denominator) + lambda0;
        double normalized_Lambda = Angle.normalizePositive(lambda);

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
