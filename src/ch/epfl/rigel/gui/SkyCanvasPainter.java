package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
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
            double starCanvasDiameter = PlaneToCanvas.applyToDistance(s.discSize(), planeToCanvas);

            // Draws and colors the star according to its color temperature
            ctx.setFill(BlackBodyColor.colorForTemperature(s.colorTemperature()));
            drawFilledCircle(starCanvasPos, starCanvasDiameter);

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
            double planetCanvasDiameter = PlaneToCanvas.applyToDistance(p.discSize(), planeToCanvas);

            CartesianCoordinates planetCanvasPos = CartesianCoordinates.of(
                    planetCanvasPositions[index * 2], planetCanvasPositions[index * 2 + 1]);

            ctx.setFill(Color.LIGHTGRAY);
            drawFilledCircle(planetCanvasPos, planetCanvasDiameter);

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
        drawFilledCircle(sunCanvasPosition, sunCanvasDiameter * 2.2);

        ctx.setFill(Color.YELLOW);
        drawFilledCircle(sunCanvasPosition, sunCanvasDiameter + 2.0);

        ctx.setFill(Color.WHITE);
        drawFilledCircle(sunCanvasPosition, sunCanvasDiameter);
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
        drawFilledCircle(moonCanvasPosition, moonCanvasDiameter);
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
        // The parallel of latitude 0 degree. Note : the arbitrarily chosen azimuth does not matter in the calculations
        HorizontalCoordinates parallel = HorizontalCoordinates.ofDeg(0, 0);

        // Projects the parallel of latitude 0 degree on the plane (resulting in the horizon) and expresses its center
        // and radius in the canvas coordinate system
        CartesianCoordinates center = PlaneToCanvas.applyToPoint(projection.circleCenterForParallel(parallel), planeToCanvas);
        double canvasRadius = PlaneToCanvas.applyToDistance(projection.circleRadiusForParallel(parallel), planeToCanvas);

        // Draws the empty red circle corresponding to the horizon
        ctx.setLineWidth(2.0);
        ctx.setStroke(Color.RED);
        ctx.strokeOval(center.x() - canvasRadius, center.y() - canvasRadius,  2.0 * canvasRadius,
                2.0 * canvasRadius);

        drawCardinalPoints(projection, planeToCanvas);
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
    private void drawFilledCircle(CartesianCoordinates upperLeftBound, double diameter) {
        ctx.fillOval(upperLeftBound.x() - diameter / 2.0, upperLeftBound.y() - diameter / 2.0,
                diameter, diameter);
    }

    /**
     * Draws the eight cardinal points on the canvas under the horizon, using a stereographic projection and an affine
     * transform.
     *
     * @param projection
     *            The stereographic projection
     * @param planeToCanvas
     *            The affine transform
     */
    private void drawCardinalPoints(StereographicProjection projection, Transform planeToCanvas) {
        ctx.setFill(Color.RED);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.TOP);

        for (CardinalPoint cardinalPoint : CardinalPoint.ALL) {
            CartesianCoordinates canvasPos = PlaneToCanvas.applyToPoint(projection.apply(cardinalPoint.hor()), planeToCanvas);
            ctx.fillText(cardinalPoint.octantName(), canvasPos.x(), canvasPos.y());
        }
    }
}
