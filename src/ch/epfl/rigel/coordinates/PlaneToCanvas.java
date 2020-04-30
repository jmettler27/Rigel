package ch.epfl.rigel.coordinates;

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
     * @param planePosition
     *            The point coordinates, as described in the stereographic projection coordinate system
     * @param transform
     *            The affine transform
     * @return the coordinates of the given point in the canvas coordinate system
     */
    public static CartesianCoordinates applyToPoint(CartesianCoordinates planePosition, Transform transform) {
        Transform concatenation = concatenationOf(transform);
        Point2D canvasPoint = concatenation.transform(planePosition.x(), planePosition.y());

        return CartesianCoordinates.of(canvasPoint.getX(), canvasPoint.getY());
    }

    /**
     * Returns an array containing the positions of the given points in the canvas coordinate system, using an affine
     * transform.
     *
     * @param planePositions
     *            The coordinates of the given points on the plane
     * @param transform
     *            The affine transform
     * @return the coordinates of the given points in the canvas coordinate system
     */
    public static double[] applyToAllPoints(double[] planePositions, Transform transform) {
        int numberOfCoordinates = planePositions.length;

        double[] canvasPositions = new double[numberOfCoordinates];
        Transform concatenation = concatenationOf(transform);
        concatenation.transform2DPoints(planePositions, 0, canvasPositions, 0,
                numberOfCoordinates / 2);

        // The positions of the images of the celestial objects
        return Arrays.copyOf(canvasPositions, numberOfCoordinates);
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
        Transform concatenation = concatenationOf(transform);
        Point2D canvasVector = concatenation.deltaTransform(x, 0);

        return canvasVector.magnitude(); // The magnitude of the vector
    }

    /**
     * Expresses the coordinates of the given canvas point in the plane coordinate system, using the inverse of an
     * affine transform.
     *
     * @param canvasPosition
     *            The canvas point
     * @param transform
     *            The affine transform, to be inverted
     * @return the coordinates of the given canvas point in the plane coordinate system
     * @throws NonInvertibleTransformException
     *            if the transform cannot be inverted
     */
    public static CartesianCoordinates inverseAtPoint(CartesianCoordinates canvasPosition, Transform transform)
            throws NonInvertibleTransformException {
        // The point on the plane
        Point2D planePosition2D = transform.inverseTransform(canvasPosition.x(), canvasPosition.y());
        return CartesianCoordinates.of(planePosition2D.getX(), planePosition2D.getY());
    }

    /**
     * Expresses the distance of the given canvas point in the plane coordinate system, using the inverse of an
     * affine transform.
     *
     * @param xC
     *            The distance on the canvas
     * @param transform
     *            The affine transform, to be inverted
     * @return the distance of the given canvas point in the plane coordinate system
     * @throws NonInvertibleTransformException
     *            if the transform cannot be inverted
     */
    public static double inverseAtDistance(double xC, Transform transform)
            throws NonInvertibleTransformException {
        // The vector on the plane
        Point2D planeVector = transform.inverseDeltaTransform(xC, 0);
        return planeVector.magnitude(); // The magnitude of the vector
    }

    /**
     * Composition of a dilatation and then a translation, i.e. a change of coordinate system from that of the
     * stereographic projection to that of the canvas, using an affine transform.
     *
     * @param transform
     *            The affine transform
     * @return the concatenation of the dilatation and translation transforms
     */
    private static Transform concatenationOf(Transform transform) {
        // Scales the image and reverses the direction of the Y axis
        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());

        // Translates the origin of the coordinate system
        Transform translation = Transform.translate(transform.getTx(), transform.getTy());

        // Concatenates the translation transform to the dilatation transform
        return translation.createConcatenation(dilatation);
    }
}
