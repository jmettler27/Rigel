package ch.epfl.rigel.coordinates;

import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;

public final class PlaneToCanvas {

    public static CartesianCoordinates applyToPoint(CartesianCoordinates cartesianPos, Transform transform) {
        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());
        Transform translation = Transform.translate(transform.getTx(), transform.getTy());
        Transform concatenation = translation.createConcatenation(dilatation);
        Point2D point2D = concatenation.transform(cartesianPos.x(), cartesianPos.y());

        return CartesianCoordinates.of(point2D.getX(), point2D.getY());
    }

    public static double applyToDistance(double x, Transform transform) {
        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());
        Transform translation = Transform.translate(transform.getTx(), transform.getTy());
        Transform concatenation = translation.createConcatenation(dilatation);
        Point2D point2D = concatenation.deltaTransform(x, 0);

        return Math.abs(point2D.getX());
    }
}
