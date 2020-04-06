package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.coordinates.PlaneToCanvas;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.util.List;

/**
 * A painter of the observed sky.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class SkyCanvasPainter {

    private final Canvas canvas;
    private final GraphicsContext ctx;

    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);

    // The diameter of the disc of a celestial object with an angular size of 0.5 degrees.
    private static final double DIAMETER = 2.0 * Math.tan(Angle.ofDeg(0.5) / 4.0);

    /**
     * Constructs a painter of the observed sky.
     *
     * @param canvas
     *            The canvas on which the observed sky is drawn
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getGraphicsContext2D();
    }

    /**
     * Clears the canvas.
     */
    public void clear() {
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Constructs a representation of the sky at a given epoch and place of observation.
     *
     * @param sky
     *            The observed sky
     * @param planeToCanvas
     *            The affine transform
     */
    public void drawStars(ObservedSky sky, Transform planeToCanvas) {
        List<Star> stars = sky.stars();
        double[] starPositions = sky.starPositions(); // The positions of the stars on the plan

        double[] transformedPositions = transformedPositions(starPositions, planeToCanvas);

        drawAsterisms(sky, transformedPositions);

        int index = 0;
        for (Star s : stars) {
            CartesianCoordinates imagePosition = CartesianCoordinates.of(
                    transformedPositions[index * 2], transformedPositions[index * 2 + 1]);

            // The diameter of the image of the star
            double imageDiameter = PlaneToCanvas.applyToDistance(diameterForMagnitude(s), planeToCanvas);

            ctx.setFill(BlackBodyColor.colorForTemperature(s.colorTemperature()));
            drawCircle(imagePosition, imageDiameter);

            ++index;
        }
    }

    /**
     * Draws the observed extraterrestrial planets of the solar system on the canvas, using an affine transform.
     *
     * @param sky
     *            The observed sky
     * @param planeToCanvas
     *            The affine transform
     */
    public void drawPlanets(ObservedSky sky, Transform planeToCanvas) {
        List<Planet> planets = sky.planets();
        double[] planetPositions = sky.planetPositions(); // The positions of the planets on the plan

        // The positions of the images of the planets
        double[] transformedPositions = transformedPositions(planetPositions, planeToCanvas);

        int index = 0;
        for (Planet p : planets) {
            // The diameter of the image of the planet
            double imageDiameter = PlaneToCanvas.applyToDistance(diameterForMagnitude(p), planeToCanvas);

            CartesianCoordinates imagePosition = CartesianCoordinates.of(
                    transformedPositions[index * 2], transformedPositions[index * 2 + 1]);

            ctx.setFill(Color.LIGHTGRAY);
            drawCircle(imagePosition, imageDiameter);

            ++index;
        }
    }

    /**
     * Draws the observed Sun on the canvas, using a stereographic projection and an affine transform.
     *
     * @param sky
     *            The observed sky
     * @param projection
     *            The stereographic projection
     * @param planeToCanvas
     *            The affine transform
     */
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Sun sun = sky.sun(); // The observed Sun

        CartesianCoordinates sunPosition = sky.sunPosition(); // The position of the Sun on the plan
        double diameter = projection.applyToAngle(sun.angularSize()); // The projected diameter of the Sun on the plan

        // The position and diameter of the image of the Sun
        CartesianCoordinates imagePosition = PlaneToCanvas.applyToPoint(sunPosition, planeToCanvas);
        double imageDiameter = PlaneToCanvas.applyToDistance(diameter, planeToCanvas);

        // Draws the three concentric discs composing the image of the Sun, from the largest to the Sun

        ctx.setFill(Color.YELLOW.deriveColor(0, 0, 0, 0.25));
        drawCircle(imagePosition, imageDiameter * 2.2);

        ctx.setFill(Color.YELLOW);
        drawCircle(imagePosition, imageDiameter + 2.0);

        ctx.setFill(Color.WHITE);
        drawCircle(imagePosition, imageDiameter);
    }

    /**
     * Draws the observed Moon on the canvas, using a stereographic projection and an affine transform.
     *
     * @param sky
     *            The observed sky
     * @param projection
     *            The stereographic projection
     * @param planeToCanvas
     *            The affine transform
     */
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Moon moon = sky.moon(); // The observed Moon

        // The position and projected diameter of the Moon on the plan
        CartesianCoordinates moonPosition = sky.moonPosition();
        double diameter = projection.applyToAngle(moon.angularSize());

        // The position and diameter of the image of the Moon
        CartesianCoordinates imagePosition = PlaneToCanvas.applyToPoint(moonPosition, planeToCanvas);
        double imageDiameter = PlaneToCanvas.applyToDistance(diameter, planeToCanvas);

        ctx.setFill(Color.WHITE);
        drawCircle(imagePosition, imageDiameter);
    }

    /**
     * Draws the horizon and the cardinal and intercardinal points on the canvas, using a stereographic projection
     * and an affine transform.
     *
     * @param projection
     *            The stereographic projection
     * @param planeToCanvas
     *            The affine transform
     */
    public void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        HorizontalCoordinates hor = HorizontalCoordinates.ofDeg(0, 0);

        CartesianCoordinates center = PlaneToCanvas.applyToPoint(projection.circleCenterForParallel(hor), planeToCanvas);
        double diameter = 2.0 * PlaneToCanvas.applyToDistance(projection.circleRadiusForParallel(hor), planeToCanvas);
        System.out.println(projection.circleRadiusForParallel(hor));
        System.out.println("Center : " + center + ", diameter : " + diameter);

        ctx.setFill(Color.RED);
        ctx.setLineWidth(4); // Trait de largeur 2

        drawCircle(center, diameter);
    }

    /**
     * Additional method.
     * Returns the positions of the images of the given celestial objects, using an affine transform.
     *
     * @param objectPositions
     *            The positions of the celestial objects on the plan
     * @param planeToCanvas
     *            The affine transform
     * @return the positions of the images of the celestial objects
     */
    private static double[] transformedPositions (double[] objectPositions, Transform planeToCanvas){
        double[] transformed = new double[objectPositions.length];
        Transform concatenation = PlaneToCanvas.concatenation(planeToCanvas);
        concatenation.transform2DPoints(objectPositions, 0, transformed, 0,
                objectPositions.length / 2);

        // The positions of the images of the celestial objects
        double[] transformedPositions = new double[objectPositions.length];
        System.arraycopy(transformed, 0, transformedPositions,0, objectPositions.length / 2);

        return transformedPositions;
    }

    /**
     * Draws the asterisms on the canvas, using the positions of its stars in the canvas coordinate system.
     *
     * @param sky
     *            The observed sky
     * @param transformedPositions
     *            The positions of the asterisms' stars expressed in the canvas coordinate system
     */
    private void drawAsterisms(ObservedSky sky, double[] transformedPositions) {
        Bounds borders = canvas.getBoundsInLocal(); // The borders of the canvas

        ctx.setLineWidth(1.0);
        ctx.setStroke(Color.BLUE);

        for (Asterism ast : sky.asterisms()) {
            ctx.beginPath(); // Resets the current path to empty
            List<Integer> asterismIndices = sky.asterismsIndices(ast);

            for (int i = 0; i < asterismIndices.size() - 1; ++i) {
                // The index (in the catalogue) and position (on the canvas) of the star at the beginning of the segment
                int index1 = asterismIndices.get(i);
                Point2D starPos1 = new Point2D(transformedPositions[index1 * 2], transformedPositions[index1 * 2 + 1]);

                // The index (in the catalogue) and position (on the canvas) of the star at the end of the segment
                int index2 = asterismIndices.get(i + 1);
                Point2D starPos2 = new Point2D(transformedPositions[index2 * 2], transformedPositions[index2 * 2 + 1]);

                ctx.moveTo(starPos1.getX(), starPos1.getY()); // Starts the segment at the position of the first star

                // Add a segment between the two stars if they are both within the limits of the canvas.
                if (borders.contains(starPos1) && borders.contains(starPos2)) {
                    ctx.lineTo(starPos2.getX(), starPos2.getY());
                }
                ctx.fill(); // Colors the segment in blue
            }
            ctx.closePath();
            ctx.stroke();
        }
    }

    /**
     * Draws a filled circle of given diameter.
     *
     * @param upperLeftBound
     *            The coordinates of the upper left bound of the circle
     * @param diameter
     *            The diameter of the circle
     */
    private void drawCircle(CartesianCoordinates upperLeftBound, double diameter) {
        ctx.fillOval(upperLeftBound.x() - diameter / 2.0, upperLeftBound.y() - diameter / 2.0,
                diameter / 2.0, diameter / 2.0);
    }

    /**
     * Returns the diameter of the disc representing the given celestial object (star or planet) according to its
     * magnitude.
     *
     * @param object
     *            The celestial object (star or planet)
     * @return the diameter of the disc
     */
    private static double diameterForMagnitude(CelestialObject object) {
        double clippedMagnitude = MAGNITUDE_INTERVAL.clip(object.magnitude()); // The magnitude is clipped to [-2, 5]

        // The size factor (between 10% and 95% of the diameter of an object whose angular size is 0.5 degrees.
        double sizeFactor = (99.0 - 17.0 * clippedMagnitude) / 140.0;
        return sizeFactor * DIAMETER;
    }
}
