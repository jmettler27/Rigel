package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.*;

class MyStereographicProjectionTest {

    @Test
    void circleCenterForParallel() {
        // If the latitude of the center of projection and of the parallel are 0, then the ordinate of the center of the circle is infinite : This "circle" is a straight line
        StereographicProjection projection = new StereographicProjection(HorizontalCoordinates.ofDeg(33,0));
        assertEquals(Double.POSITIVE_INFINITY, projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(56.7,0)).y());
    }

    @Test
    void circleRadiusForParallel() {
        // If the latitude of the center of projection and of the parallel are 0, then the radius of the circle is infinite : This "circle" is a straight line
        StereographicProjection projection = new StereographicProjection(HorizontalCoordinates.of(0,0));
        assertEquals(Double.POSITIVE_INFINITY, projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(56.7, 0)));
    }

    @Test
    void applyToAngle() {

        // p.176
        double moonAngularSize = Angle.ofDMS(0,32,49);
        StereographicProjection projection = new StereographicProjection(HorizontalCoordinates.ofDeg(0,90));
        System.out.println(projection.applyToAngle(moonAngularSize));

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
    }

    @Test
    void inverseApply() {

    }

    @Test
    void testToString() {
        HorizontalCoordinates center = HorizontalCoordinates.ofDeg(25.45,
                5.2436789);
        StereographicProjection stereographicProjection = new StereographicProjection(center);
        assertEquals("Stereographic Projection: center's coordinates: (az=25.4500°, alt=5.2437°)", stereographicProjection.toString());
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