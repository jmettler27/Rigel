package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyStereographicProjectionTest {

    @Test
    void circleCenterForParallel() {
        // If the latitude of the center of projection and of the parallel are 0, then the ordinate of the center of the circle is infinite : This "circle" is a straight line
        StereographicProjection projection = new StereographicProjection(HorizontalCoordinates.ofDeg(33, 0));
        assertEquals(Double.POSITIVE_INFINITY, projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(56.7, 0)).y());

        HorizontalCoordinates h1 = HorizontalCoordinates.of(Math.PI / 4, Math.PI / 6);
        HorizontalCoordinates center1 = HorizontalCoordinates.of(0, 0);
        StereographicProjection s = new StereographicProjection(center1);
        CartesianCoordinates a1 = s.circleCenterForParallel(h1);
        assertEquals(0, a1.x(), 1e-10);
        assertEquals(2, a1.y(), 1e-10);

        assertEquals(0.6089987400733187, new StereographicProjection(HorizontalCoordinates.ofDeg(45, 45)).circleCenterForParallel(HorizontalCoordinates.ofDeg(0, 27)).y());
    }

    @Test
    void circleRadiusForParallel() {
        // If the latitude of the center of projection and of the parallel are 0, then the radius of the circle is infinite : This "circle" is a straight line
        StereographicProjection projection = new StereographicProjection(HorizontalCoordinates.of(0, 0));
        assertEquals(Double.POSITIVE_INFINITY, projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(56.7, 0)));

        HorizontalCoordinates h2 = HorizontalCoordinates.of(Math.PI / 2, Math.PI / 2);
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI / 4, Math.PI / 4);
        StereographicProjection e2 = new StereographicProjection(center2);
        double rho1 = e2.circleRadiusForParallel(h2);
        assertEquals(0, rho1, 1e-10);

        assertEquals(0.767383180397855, new StereographicProjection(HorizontalCoordinates.ofDeg(45, 45)).circleRadiusForParallel(HorizontalCoordinates.ofDeg(0, 27)));
    }

    @Test
    void applyToAngle() {
        assertEquals(0.00436333005262522, new StereographicProjection(HorizontalCoordinates.ofDeg(23, 45)).applyToAngle(Angle.ofDeg(1 / 2.0)));
    }

    @Test
    void apply() {
        HorizontalCoordinates azAlt1 = HorizontalCoordinates.ofDeg(80, 20);
        StereographicProjection projection1 = new StereographicProjection(HorizontalCoordinates.ofDeg(45, 15));
        assertEquals(0.29419904562328, projection1.apply(azAlt1).x(), 1e-13);
        assertEquals(0.071581167995944, projection1.apply(azAlt1).y(), 1e-14);

        HorizontalCoordinates trivialCoords = HorizontalCoordinates.ofDeg(0, 0);
        StereographicProjection projection2 = new StereographicProjection(HorizontalCoordinates.ofDeg(0, 0));
        CartesianCoordinates cartesianCoords = projection2.apply(trivialCoords);
        assertEquals(0, cartesianCoords.x());
        assertEquals(0, cartesianCoords.y());

        HorizontalCoordinates h1 = HorizontalCoordinates.of(Math.PI / 4, Math.PI / 6);
        HorizontalCoordinates center1 = HorizontalCoordinates.of(0, 0);
        StereographicProjection e = new StereographicProjection(center1);
        double p = Math.sqrt(6);
        CartesianCoordinates a1 = CartesianCoordinates.of(p / (4 + p), 2 / (4 + p));
        CartesianCoordinates c1 = e.apply(h1);
        assertEquals(a1.x(), c1.x(), 1e-8);
        assertEquals(a1.y(), c1.y(), 1e-8);

        HorizontalCoordinates h2 = HorizontalCoordinates.of(Math.PI / 2, Math.PI / 2);
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI / 4, Math.PI / 4);
        StereographicProjection e2 = new StereographicProjection(center2);
        double p2 = Math.sqrt(2);
        CartesianCoordinates a2 = CartesianCoordinates.of(0, p2 / (2 + p2));
        CartesianCoordinates c2 = e2.apply(h2);
        assertEquals(a2.x(), c2.x(), 1e-8);
        assertEquals(a2.y(), c2.y(), 1e-8);

        assertEquals(-0.13165249758739583, new StereographicProjection(HorizontalCoordinates.ofDeg(45, 45)).apply(HorizontalCoordinates.ofDeg(45, 30)).y());
    }

    @Test
    void inverseApply() {
        assertEquals(3.648704634091643, new StereographicProjection(HorizontalCoordinates.ofDeg(45, 45)).inverseApply(CartesianCoordinates.of(10, 0)).az());
    }

    @Test
    void testToString() {
        HorizontalCoordinates center = HorizontalCoordinates.ofDeg(25.45, 5.2436789);
        StereographicProjection stereographicProjection = new StereographicProjection(center);
        assertEquals("StereographicProjection centered in (az=25.4500°, alt=5.2437°)", stereographicProjection.toString());
    }

    @Test
    void testHashCode() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new StereographicProjection(HorizontalCoordinates.of(0, 0)).hashCode();
        });
    }

    @Test
    void testEquals() {
        assertThrows(UnsupportedOperationException.class, () -> {
            var s = new StereographicProjection(HorizontalCoordinates.of(0, 0));
            s.equals(s);
        });
    }
}