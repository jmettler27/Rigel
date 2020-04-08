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
    private final GraphicsContext ctx; // The graphics context associated to the canvas

    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);

    // The diameter of the disc of a celestial object with an angular size of 0.5 degrees.
    private static final double DIAMETER = 2.0 * Math.tan(Angle.ofDeg(0.5) / 4.0);

    /**
     * Constructs a painter of the observed sky.
     *
     * @param canvas
     *            The canvas on which the observed sky is drawn
     */
    SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getGraphicsContext2D();
    }

    /**
     * Clears the canvas.
     */
    void clear() {
        // Sets the color of the image to black
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
    void drawStars(ObservedSky sky, Transform planeToCanvas) {
        List<Star> stars = sky.stars();
        double[] starPositions = sky.starPositions(); // The positions of the stars on the plan

        double[] starCanvasPositions = PlaneToCanvas.applyToAllPoints(starPositions, planeToCanvas);

        drawAsterisms(sky, starCanvasPositions);

        int index = 0;
        for (Star s : stars) {
            CartesianCoordinates starCanvasPos = CartesianCoordinates.of(
                    starCanvasPositions[index * 2], starCanvasPositions[index * 2 + 1]);

            // The diameter of the image of the star
            double starCanvasDiameter = PlaneToCanvas.applyToDistance(diameterForMagnitude(s), planeToCanvas);

            //System.out.println(starCanvasPos);
            // Draws and colors the star according to its color temperature
            ctx.setFill(BlackBodyColor.colorForTemperature(s.colorTemperature()));
            drawCircle(starCanvasPos, starCanvasDiameter);

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
    void drawPlanets(ObservedSky sky, Transform planeToCanvas) {
        List<Planet> planets = sky.planets();
        double[] planetPositions = sky.planetPositions(); // The positions of the planets on the plan

        // The positions of the images of the planets
        double[] planetCanvasPositions = PlaneToCanvas.applyToAllPoints(planetPositions, planeToCanvas);

        int index = 0;
        for (Planet p : planets) {
            // The diameter of the image of the planet
            double planetCanvasDiameter = PlaneToCanvas.applyToDistance(diameterForMagnitude(p), planeToCanvas);

            CartesianCoordinates planetCanvasPos = CartesianCoordinates.of(
                    planetCanvasPositions[index * 2], planetCanvasPositions[index * 2 + 1]);

            ctx.setFill(Color.LIGHTGRAY);
            drawCircle(planetCanvasPos, planetCanvasDiameter);

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
    void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Sun sun = sky.sun(); // The observed Sun

        // The position and projected diameter of the Sun on the plane
        CartesianCoordinates sunPosition = sky.sunPosition();
        double sunDiameter = projection.applyToAngle(sun.angularSize());

        // The position and diameter of the image of the Sun
        CartesianCoordinates sunCanvasPosition = PlaneToCanvas.applyToPoint(sunPosition, planeToCanvas);
        double sunCanvasDiameter = PlaneToCanvas.applyToDistance(sunDiameter, planeToCanvas);

        // Draws the three concentric discs composing the image of the Sun, from the largest to the smallest

        ctx.setFill(Color.YELLOW.deriveColor(0, 0, 0, 0.25));
        drawCircle(sunCanvasPosition, sunCanvasDiameter * 2.2);

        ctx.setFill(Color.YELLOW);
        drawCircle(sunCanvasPosition, sunCanvasDiameter + 2.0);

        ctx.setFill(Color.WHITE);
        drawCircle(sunCanvasPosition, sunCanvasDiameter);
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
    void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Moon moon = sky.moon(); // The observed Moon

        // The position and projected diameter of the Moon on the plane
        CartesianCoordinates moonPosition = sky.moonPosition();
        double moonDiameter = projection.applyToAngle(moon.angularSize());

        // The position and diameter of the image of the Moon
        CartesianCoordinates moonCanvasPosition = PlaneToCanvas.applyToPoint(moonPosition, planeToCanvas);
        double moonCanvasDiameter = PlaneToCanvas.applyToDistance(moonDiameter, planeToCanvas);

        ctx.setFill(Color.WHITE);
        drawCircle(moonCanvasPosition, moonCanvasDiameter);
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
    void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        HorizontalCoordinates hor = HorizontalCoordinates.ofDeg(0, 0);

        // x : 800
        // y :  600
        System.out.println(PlaneToCanvas.applyToPoint(projection.circleCenterForParallel(hor), planeToCanvas));

        double canvasRadius = PlaneToCanvas.applyToDistance(projection.circleRadiusForParallel(hor), planeToCanvas);
        CartesianCoordinates center = CartesianCoordinates.of(400 + canvasRadius / 2.0,550);
        //PlaneToCanvas.applyToPoint(projection.circleCenterForParallel(hor), planeToCanvas);

        System.out.println("Center : " + center + ", radius : " + canvasRadius);

        ctx.setLineWidth(2.0);
        ctx.setStroke(Color.RED);

        ctx.strokeOval(center.x() - canvasRadius, center.y() - canvasRadius,  canvasRadius, canvasRadius);

    }

    /**
     * Draws the asterisms on the canvas, using the positions of its stars expressed in the canvas coordinate system.
     *
     * @param sky
     *            The observed sky
     * @param starCanvasPositions
     *            The positions of the asterisms' stars expressed in the canvas coordinate system
     */
    private void drawAsterisms(ObservedSky sky, double[] starCanvasPositions) {
        Bounds borders = canvas.getBoundsInLocal(); // The borders of the canvas

        ctx.setLineWidth(1.0);
        ctx.setStroke(Color.BLUE);

        for (Asterism ast : sky.asterisms()) {
            ctx.beginPath(); // Resets the current path to empty
            List<Integer> asterismIndices = sky.asterismsIndices(ast);

            for (int i = 0; i < asterismIndices.size() - 1; ++i) {
                // The index (in the catalogue) and position (on the canvas) of the star at the beginning of the segment
                int index1 = asterismIndices.get(i);
                Point2D beginning = new Point2D(starCanvasPositions[index1 * 2], starCanvasPositions[index1 * 2 + 1]);

                // Starts the segment to draw at the position of the first star
                ctx.moveTo(beginning.getX(), beginning.getY());

                // The index (in the catalogue) and position (on the canvas) of the star at the end of the segment
                int index2 = asterismIndices.get(i + 1);
                Point2D end = new Point2D(starCanvasPositions[index2 * 2], starCanvasPositions[index2 * 2 + 1]);

                // Adds a segment between the two stars if they are both within the limits of the canvas.
                if (borders.contains(beginning) && borders.contains(end)) {
                    ctx.lineTo(end.getX(), end.getY());
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

        // The size factor (between 10% and 95% of the diameter of an object whose angular size is 0.5 degrees)
        double sizeFactor = (99.0 - 17.0 * clippedMagnitude) / 140.0;
        return sizeFactor * DIAMETER;
    }
}
