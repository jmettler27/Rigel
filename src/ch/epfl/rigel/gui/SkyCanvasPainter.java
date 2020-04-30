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
        ctx.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

        // Fills the entire canvas with black
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Constructs a representation of the sky at a given epoch and place of observation.
     *
     * @param sky
     *            The observed sky
     * @param transform
     *            The affine transform
     */
    void drawStars(ObservedSky sky, Transform transform) {
        // The positions of the observed stars on the canvas
        double[] starCanvasPositions = PlaneToCanvas.applyToAllPoints(sky.starPositions(), transform);

        drawAsterisms(sky, starCanvasPositions);

        int index = 0;
        for (Star s : sky.stars()) {
            CartesianCoordinates starCanvasPos = CartesianCoordinates.of(
                    starCanvasPositions[index * 2], starCanvasPositions[index * 2 + 1]);

            // The diameter of the image of the star
            double starCanvasDiameter = PlaneToCanvas.applyToDistance(s.discSize(), transform);

            // Draws and colors the star according to its color temperature
            drawFilledCircle(starCanvasPos, starCanvasDiameter, BlackBodyColor.colorForTemperature(s.colorTemperature()));

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
    void drawPlanets(ObservedSky sky, Transform transform) {
        // The positions of the observed planets of the solar system on the canvas
        double[] planetCanvasPositions = PlaneToCanvas.applyToAllPoints(sky.planetPositions(), transform);

        int index = 0;
        for (Planet p : sky.planets()) {
            // The diameter of the planet of the canvas
            double planetCanvasDiameter = PlaneToCanvas.applyToDistance(p.discSize(), transform);

            CartesianCoordinates planetCanvasPos = CartesianCoordinates.of(
                    planetCanvasPositions[index * 2], planetCanvasPositions[index * 2 + 1]);

            drawFilledCircle(planetCanvasPos, planetCanvasDiameter, Color.LIGHTGRAY);

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
    void drawSun(ObservedSky sky, StereographicProjection projection, Transform transform) {
        // The position and projected diameter of the observed Sun on the plane
        CartesianCoordinates sunPlanePosition = sky.sunPosition();
        double sunPlaneDiameter = projection.applyToAngle(sky.sun().angularSize());

        // The position and diameter of the observed Sun on the canvas
        CartesianCoordinates sunCanvasPosition = PlaneToCanvas.applyToPoint(sunPlanePosition, transform);
        double sunCanvasDiameter = PlaneToCanvas.applyToDistance(sunPlaneDiameter, transform);

        // Draws the three concentric discs composing the image of the Sun, from the largest to the smallest
        drawFilledCircle(sunCanvasPosition, sunCanvasDiameter * 2.2, Color.YELLOW.deriveColor(1, 1, 1, 0.25));
        drawFilledCircle(sunCanvasPosition, sunCanvasDiameter + 2.0, Color.YELLOW);
        drawFilledCircle(sunCanvasPosition, sunCanvasDiameter, Color.WHITE);
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
    void drawMoon(ObservedSky sky, StereographicProjection projection, Transform transform) {
        // The position and projected diameter of the observed Moon on the plane
        CartesianCoordinates moonPlanePosition = sky.moonPosition();
        double moonPlaneDiameter = projection.applyToAngle(sky.moon().angularSize());

        // The position and diameter of the observed Moon on the canvas
        CartesianCoordinates moonCanvasPosition = PlaneToCanvas.applyToPoint(moonPlanePosition, transform);
        double moonCanvasDiameter = PlaneToCanvas.applyToDistance(moonPlaneDiameter, transform);

        drawFilledCircle(moonCanvasPosition, moonCanvasDiameter, Color.WHITE);
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
    void drawHorizon(StereographicProjection projection, Transform transform) {
        // The parallel of latitude 0 degree. Note : the arbitrarily chosen azimuth does not matter in the calculations
        HorizontalCoordinates parallel = HorizontalCoordinates.of(0, 0);

        // Projects the parallel of latitude 0 degree on the plane (resulting in the horizon) and expresses its center
        // and radius in the canvas coordinate system
        CartesianCoordinates center = PlaneToCanvas.applyToPoint(projection.circleCenterForParallel(parallel), transform);
        double horizonRadius = PlaneToCanvas.applyToDistance(projection.circleRadiusForParallel(parallel), transform);
        double horizonDiameter = 2.0 * horizonRadius;

        // Draws the empty red circle corresponding to the horizon
        ctx.setLineWidth(2.0);
        ctx.setStroke(Color.RED);
        ctx.strokeOval(center.x() - horizonRadius, center.y() - horizonRadius,  horizonDiameter,
                horizonDiameter);

        drawCardinalPoints(projection, transform);
    }

    /**
     * Draws a filled circle of given diameter.
     *
     * @param center
     *            The coordinates of the center of the circle, expressed in the canvas coordinate system
     * @param diameter
     *            The diameter of the circle, expressed in the canvas coordinate system
     */
    private void drawFilledCircle(CartesianCoordinates center, double diameter, Color color) {
        double radius = diameter / 2.0;
        ctx.setFill(color);

        // Translates the coordinates of the center of the circle to the coordinates of its upper left bound
        ctx.fillOval(center.x() - radius, center.y() - radius,
                diameter, diameter);
    }

    /**
     * Draws the asterisms on the canvas, using the positions of its stars expressed in the canvas coordinate system.
     *
     * @param sky
     *            The observed sky
     * @param starCanvasPositions
     *            The positions of the observed asterisms' stars, expressed in the canvas coordinate system
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

                // Adds a segment between the two stars if at least one star has its center within the limits of the canvas.
                if (borders.contains(beginning) || borders.contains(end)) {
                    ctx.lineTo(end.getX(), end.getY());
                }
                ctx.fill(); // Colors the segment in blue
            }
            ctx.closePath();
            ctx.stroke();
        }
    }

    /**
     * Draws the eight cardinal points on the canvas under the horizon, using a stereographic projection and an affine
     * transform.
     *
     * @param projection
     *            The stereographic projection
     * @param transform
     *            The affine transform
     */
    private void drawCardinalPoints(StereographicProjection projection, Transform transform) {
        ctx.setFill(Color.RED);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.TOP);

        for (CardinalPoint cardinalPoint : CardinalPoint.ALL) {
            CartesianCoordinates canvasPos = PlaneToCanvas.applyToPoint(projection.apply(cardinalPoint.getPosition()), transform);
            ctx.fillText(cardinalPoint.getName(), canvasPos.x(), canvasPos.y());
        }
    }
}
