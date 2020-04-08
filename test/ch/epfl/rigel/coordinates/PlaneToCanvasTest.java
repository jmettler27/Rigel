package ch.epfl.rigel.coordinates;

import javafx.scene.transform.Transform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaneToCanvasTest {
    private static final Transform PLANE_TO_CANVAS = Transform.affine(1300, 0, 0, -1300, 400, 300);

    @Test
    void applyToPoint() {
    }

    @Test
    void applyToAllPoints() {
        double[] cartesianPositions = {10.0, 15.0};
        double[] canvasPositions = PlaneToCanvas.applyToAllPoints(cartesianPositions, PLANE_TO_CANVAS);

        CartesianCoordinates cartesianPos = CartesianCoordinates.of(cartesianPositions[0], cartesianPositions[1]);
        CartesianCoordinates canvasPos = PlaneToCanvas.applyToPoint(cartesianPos, PLANE_TO_CANVAS);

        assertEquals(canvasPositions[0], canvasPos.x());
        assertEquals(canvasPositions[1], cartesianPos.y());
    }

    @Test
    void applyToDistance() {
    }
}