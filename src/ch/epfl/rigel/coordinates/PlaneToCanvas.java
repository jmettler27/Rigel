package ch.epfl.rigel.coordinates;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

public final class PlaneToCanvas {

    public static CartesianCoordinates applyToPoint(CartesianCoordinates cartesianPos, Transform transform) {
        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());
        Transform translation = Transform.translate(transform.getTx(), transform.getTy());
        Transform concatenation = dilatation.createConcatenation(translation);
        Point2D point2D = concatenation.transform(cartesianPos.x(), cartesianPos.y());

        return CartesianCoordinates.of(point2D.getX(), point2D.getY());
    }

    public static double applyToDistance(double x, Transform transform) {
        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());
        Point2D point2D = dilatation.deltaTransform(x, 0);

        return Math.abs(point2D.getX());
    }

    public static void applyToArray(double[][] array, Transform transform, double[][] transformed) {

        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());
        Transform translation = Transform.translate(transform.getTx(), transform.getTy());
        Transform concatenation = dilatation.createConcatenation(translation);

        for (int i = 0; i < array[0].length; ++i) {
            Point2D point2D = concatenation.transform(array[0][i], array[1][i]);
            transformed[0][i] = point2D.getX();
            transformed[1][i] = point2D.getY();
        }
    }

}
