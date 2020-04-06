package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.coordinates.PlanToCanvas;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
     * @param transform
     *            The affine transform
     */
    public void drawStars(ObservedSky sky, Transform transform) {
        List<Star> stars = sky.stars();
        double[] starPositions = sky.starPositions(); // The positions of the stars on the plan

        double[] transformedPositions = transformedPositions(starPositions, transform);

        drawAsterisms(sky, transformedPositions);

        int index = 0;
        for (Star s : stars) {
            double imageDiameter = PlanToCanvas.applyToDistance(diameterForMagnitude(s), transform);

            CartesianCoordinates imagePosition = CartesianCoordinates.of(
                    transformedPositions[index * 2], transformedPositions[index * 2 + 1]);

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
     * @param transform
     *            The affine transform
     */
    public void drawPlanets(ObservedSky sky, Transform transform) {
        List<Planet> planets = sky.planets();
        double[] planetPositions = sky.planetPositions(); // The positions of the planets on the plan

        double[] transformedPositions = transformedPositions(planetPositions, transform);

        int index = 0;
        for (Planet p : planets) {
            double imageDiameter = PlanToCanvas.applyToDistance(diameterForMagnitude(p), transform);

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
     * @param transform
     *            The affine transform
     */
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform transform) {
        Sun sun = sky.sun(); // The observed Sun

        CartesianCoordinates sunPosition = sky.sunPosition(); // The position of the Sun on the plan
        double diameter = projection.applyToAngle(sun.angularSize()); // The projected diameter of the Sun on the plan

        // The position and diameter of the image of the Sun
        CartesianCoordinates imagePosition = PlanToCanvas.applyToPoint(sunPosition, transform);
        double imageDiameter = PlanToCanvas.applyToDistance(diameter, transform);

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
     * @param transform
     *            The affine transform
     */
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform transform) {
        Moon moon = sky.moon(); // The observed Moon

        // The position and projected diameter of the Moon on the plan
        CartesianCoordinates moonPosition = sky.moonPosition();
        double diameter = projection.applyToAngle(moon.angularSize());

        // The position and diameter of the image of the Moon
        CartesianCoordinates imagePosition = PlanToCanvas.applyToPoint(moonPosition, transform);
        double imageDiameter = PlanToCanvas.applyToDistance(diameter, transform);

        ctx.setFill(Color.WHITE);
        drawCircle(imagePosition, imageDiameter);
    }

    /**
     * Draws the horizon and the cardinal and intercardinal points on the canvas, using a stereographic projection
     * and an affine transform.
     *
     * @param projection
     *            The stereographic projection
     * @param transform
     *            The affine transform
     */
    public void drawHorizon(StereographicProjection projection, Transform transform) {
        HorizontalCoordinates hor = HorizontalCoordinates.ofDeg(45, 0);
        CartesianCoordinates center = PlanToCanvas.applyToPoint(projection.circleCenterForParallel(hor), transform);
        double radius = PlanToCanvas.applyToDistance(projection.circleRadiusForParallel(hor), transform);

        ctx.setLineWidth(2); // Trait de largeur 2

        ctx.setFill(Color.RED);
        ctx.fillOval(center.x() - radius, center.y() - radius, radius, radius);

        //drawCircle(ctx, radiusForParallel * 2.0, centerForParallel);
    }

    /**
     * Additional method.
     * Returns the positions of the images of the given celestial objects, using an affine transform.
     *
     * @param objectPositions
     *            The positions of the celestial objects on the plan
     * @param transform
     *            The affine transform
     * @return the positions of the images of the celestial objects
     */
    private static double[] transformedPositions (double[] objectPositions,
                                                  Transform transform){


        double[] transformed = new double[objectPositions.length];
        Transform concatenation = PlanToCanvas.concatenation(transform);
        concatenation.transform2DPoints(objectPositions, 0, transformed, 0, objectPositions.length/2);

        double[] transformedPositions = new double[objectPositions.length];
        System.arraycopy(transformed, 0, transformedPositions,0, objectPositions.length/2);

        return transformedPositions;
    }

    /**
     * Draws the asterisms on the canvas, using the positions of its stars in the canvas coordinate system.
     *
     * @param sky
     *            The observed sky
     * @param starPositions
     *            The positions of the asterisms' stars in the canvas coordinate system
     */
    private void drawAsterisms(ObservedSky sky, double[] starPositions) {
        Set<Asterism> asterisms = sky.asterisms();

        ctx.setFill(Color.BLUE);
        ctx.setLineWidth(1);

        for (Asterism ast : asterisms) {
            ctx.beginPath();
            List<Integer> asterismIndices = sky.asterismsIndices(ast);

            for (int index = 0; index < asterismIndices.size(); ++index) {
                int starIndex = asterismIndices.get(index);
                ctx.moveTo(starPositions[starIndex * 2], starPositions[starIndex * 2 + 1]);
                int starIndex1 = asterismIndices.get(index + 1);

                Point2D firstPoint = new Point2D(starPositions[starIndex * 2], starPositions[starIndex * 2 + 1]);
                Point2D secondPoint = new Point2D(starPositions[starIndex1 * 2], starPositions[starIndex1 * 2 + 1]);
                if (canvas.getBoundsInLocal().contains(firstPoint) && canvas.getBoundsInLocal().contains(secondPoint)) {
                    ctx.lineTo(starPositions[starIndex1 * 2], starPositions[starIndex1 * 2 + 1]);
                }
            }
            ctx.stroke(); // Draws the path
        }
    }

    /**
     * Draws a filled circle of given diameter.
     *
     * @param diameter
     *            The diameter of the circle
     * @param upperLeftBound
     *            The coordinates of the upper left bound of the circle
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
