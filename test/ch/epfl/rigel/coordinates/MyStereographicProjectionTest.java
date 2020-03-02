package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyStereographicProjectionTest {

    @Test
    void circleCenterForParallel() {

    }

    @Test
    void circleRadiusForParallel() {

    }

    @Test
    void applyToAngle() {

    }

    @Test
    void apply() {

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