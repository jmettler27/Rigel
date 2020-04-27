package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.CelestialObject;
import javafx.geometry.Point2D;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.Arrays;

/**
 * Additional class.
 * The methods allowing to transform Cartesian coordinates produced by a stereographic projection into coordinates of
 * the canvas (i.e. the coordinates of an image).
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class PlaneToCanvas {

    /**
     * Expresses the coordinates of the given point in the canvas coordinate system, using an affine transform.
     *
     * @param cartesianPos
     *            The point coordinates, as described in the stereographic projection coordinate system
     * @param transform
     *            The affine transform
     * @return the coordinates of the given point in the canvas coordinate system.
     */
    public static CartesianCoordinates applyToPoint(CartesianCoordinates cartesianPos, Transform transform) {
        Transform concatenation = concatenation(transform);
        Point2D canvasPoint = concatenation.transform(cartesianPos.x(), cartesianPos.y());

        return CartesianCoordinates.of(canvasPoint.getX(), canvasPoint.getY());
    }

    /**
     * Returns an array containing the positions of the given points in the canvas coordinate system, using an affine
     * transform.
     *
     * @param cartesianPositions
     *            The coordinates of the given points on the plane
     * @param transform
     *            The affine transform
     * @return the coordinates of the given points in the canvas coordinate system
     */
    public static double[] applyToAllPoints(double[] cartesianPositions, Transform transform) {
        double[] canvasPositions = new double[cartesianPositions.length];
        Transform concatenation = concatenation(transform);
        concatenation.transform2DPoints(cartesianPositions, 0, canvasPositions, 0,
                cartesianPositions.length / 2);

        // The positions of the images of the celestial objects
        return Arrays.copyOf(canvasPositions, cartesianPositions.length);
    }

    /**
     * Expresses the magnitude of the given horizontal vector (a radius or a diameter) in the canvas coordinate system,
     * using an affine transform.
     *
     * @param x
     *            The vector magnitude in the direction of the X axis of the stereographic projection coordinate system
     * @param transform
     *            The affine transform
     * @return the magnitude of the given horizontal vector in the canvas coordinate system
     */
    public static double applyToDistance(double x, Transform transform) {
        Transform concatenation = concatenation(transform);
        Point2D canvasVector = concatenation.deltaTransform(x, 0);

        return canvasVector.magnitude(); // The magnitude of the vector
    }

    /**
     *
     *
     * @param canvasPosition
     * @param transform
     * @return
     * @throws NonInvertibleTransformException
     */
    public static CartesianCoordinates inverseApplyTo(CartesianCoordinates canvasPosition, Transform transform)
            throws NonInvertibleTransformException {
            Transform inverse = transform.createInverse();
            Point2D point2D = inverse.transform(new Point2D(canvasPosition.x(), canvasPosition.y()));
            return CartesianCoordinates.of(point2D.getX(), point2D.getY());
    }

    /**
     * Composition of a dilatation and then a translation, i.e. a change of coordinate system from that of the
     * stereographic projection to that of the canvas, using an affine transform.
     *
     * @param transform
     *            The affine transform
     * @return the concatenation of the dilatation and translation transforms
     */
    private static Transform concatenation(Transform transform) {
        // Scales the image and reverses the direction of the Y axis
        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());

        // Translates the origin of the coordinate system
        Transform translation = Transform.translate(transform.getTx(), transform.getTy());

        // Concatenates the translation transform to the dilatation transform
        return translation.createConcatenation(dilatation);
    }
}
