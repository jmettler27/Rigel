package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
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

    private final Canvas canvas; // The canvas on which the sky is drawn
    private final GraphicsContext ctx; // The graphics context associated to the canvas

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
        ctx.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

        // Fills the entire canvas with black
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draws the observed stars of the solar system on the canvas, using an affine transform.
     *
     * @param sky
     *            The observed sky
     * @param transform
     *            The affine transform
     * @param asterismEnabled
     *           Enables the drawing of the asterisms
     * @param nameEnabled
     *           Enables the drawing of the names of the brightest stars
     */
    public void drawStars(ObservedSky sky, Transform transform, boolean asterismEnabled, boolean nameEnabled) {
        // The positions of the observed stars on the canvas
        double[] starCanvasPositions = PlaneToCanvas.applyToAllPoints(sky.starPositions(), transform);

        if(asterismEnabled) drawAsterisms(sky, starCanvasPositions);

        int index = 0;
        for (Star s : sky.stars()) {
            CartesianCoordinates starCanvasPos = CartesianCoordinates.of(
                    starCanvasPositions[index * 2], starCanvasPositions[index * 2 + 1]);

            // The diameter of the image of the star
            double starCanvasDiameter = PlaneToCanvas.applyToDistance(s.discSize(), transform);

            // Draws and colors the star according to its color temperature
            Color starColor = BlackBodyColor.colorForTemperature(s.colorTemperature());
            drawFilledCircle(starCanvasPos, starCanvasDiameter, starColor);

            if(nameEnabled && s.isBright()) drawAnnotation(s.name(), starCanvasPos, starColor);

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
    public void drawPlanets(ObservedSky sky, Transform transform, boolean nameEnabled) {
        // The positions of the observed planets of the solar system on the canvas
        double[] planetCanvasPositions = PlaneToCanvas.applyToAllPoints(sky.planetPositions(), transform);

        int index = 0;
        for (Planet p : sky.planets()) {
            // The diameter of the planet of the canvas
            double planetCanvasDiameter = PlaneToCanvas.applyToDistance(p.discSize(), transform);

            CartesianCoordinates planetCanvasPos = CartesianCoordinates.of(
                    planetCanvasPositions[index * 2], planetCanvasPositions[index * 2 + 1]);

            drawFilledCircle(planetCanvasPos, planetCanvasDiameter, Color.LIGHTGRAY);

            if(nameEnabled) drawAnnotation(p.toString(), planetCanvasPos, Color.FORESTGREEN);

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
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform transform, boolean nameEnable) {
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

        if(nameEnable) drawAnnotation(sky.sun().toString(), sunCanvasPosition, Color.YELLOW);
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
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform transform,
                  GeographicCoordinates observerLocation, boolean nameEnable) {
        // The position and projected diameter of the observed Moon on the plane
        CartesianCoordinates moonPlanePosition = sky.moonPosition();
        double moonPlaneDiameter = projection.applyToAngle(sky.moon().angularSize());

        // The position and diameter of the observed Moon on the canvas
        CartesianCoordinates moonCanvasPosition = PlaneToCanvas.applyToPoint(moonPlanePosition, transform);
        double moonCanvasDiameter = PlaneToCanvas.applyToDistance(moonPlaneDiameter, transform);

        //drawFilledCircle(moonCanvasPosition, moonCanvasDiameter, Color.WHITE);
        drawMoonPhase(sky.moon().phase(), moonCanvasPosition, moonCanvasDiameter, observerLocation);

        if(nameEnable) drawAnnotation(sky.moon().toString(), moonCanvasPosition, Color.WHITE);
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
     * Draws the observed satellites in orbit around Earth on the canvas, using an affine transform.
     *
     * @param sky
     *            The observed sky
     * @param transform
     *            The affine transform
     */
    public void drawSatellites(ObservedSky sky, Transform transform, boolean satelliteEnabled) {
        // The positions of the observed satellites on the canvas
        double[] satelliteCanvasPositions = PlaneToCanvas.applyToAllPoints(sky.satellitePositions(), transform);

        if(satelliteEnabled) {
            for(int i = 0; i < sky.satellites().size(); ++i){
                CartesianCoordinates satelliteCanvasPos = CartesianCoordinates.of(
                        satelliteCanvasPositions[i * 2], satelliteCanvasPositions[i * 2 + 1]);

                // Draws and colors the satellite
                drawFilledCircle(satelliteCanvasPos, 3, Color.GREEN);
            }
        }

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
        ctx.fillOval(center.x() - radius, center.y() - radius, diameter, diameter);
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

        for (int i = 0; i < 7; ++i) {
            HorizontalCoordinates cardinalPoint = HorizontalCoordinates.ofDeg(45 * i, -0.5);
            CartesianCoordinates canvasPos = PlaneToCanvas.applyToPoint(
                    projection.apply(cardinalPoint), transform);
            ctx.fillText(cardinalPoint.azOctantName("N", "E", "S", "O"), canvasPos.x(), canvasPos.y());
        }
    }

    /**
     * Additional method.
     * Draws the Moon's disc according to its phase.
     *
     * @param phase The Moon's phase (unitless)
     * @param moonCanvasPosition The position of the Moon on the canvas
     * @param moonCanvasDiameter The diameter of the Moon on the canvas
     * @param observerLocation The coordinates of the observer on Earth
     */
    private void drawMoonPhase(float phase, CartesianCoordinates moonCanvasPosition, double moonCanvasDiameter,
                               GeographicCoordinates observerLocation) {
        double moonCanvasRadius = moonCanvasDiameter / 2.0;
        boolean isNorthHemisphere = observerLocation.latDeg() >= 0;

        // The Moon is visible
        if (phase > 0.03) {

            // First Crescent
            if (phase <= 0.34) {
                double offset = isNorthHemisphere ? -3 : 3;
                drawFilledCircle(moonCanvasPosition, moonCanvasDiameter, Color.WHITE);
                drawFilledCircle(CartesianCoordinates.of(moonCanvasPosition.x() + offset, moonCanvasPosition.y()),
                        moonCanvasDiameter, Color.BLACK);
            }
            // First Quarter
            else if (phase <= 0.65) {
                double startAngle = (isNorthHemisphere) ? -90 : 90;
                ctx.setFill(Color.WHITE);
                ctx.fillArc(moonCanvasPosition.x() - moonCanvasRadius, moonCanvasPosition.y() - moonCanvasRadius,
                        moonCanvasDiameter * 2, moonCanvasDiameter * 2, startAngle, 180.0, ArcType.ROUND);

            }
            // Full Moon
            else if (phase <= 1) {
                drawFilledCircle(moonCanvasPosition, moonCanvasDiameter, Color.WHITE);
            }
        }
    }
    /**
     * Additional method.
     * Draws an annotation next to the given coordinates on the canvas and using the given color.
     *
     * @param annotation The annotation
     * @param canvasPosition The position on the canvas
     * @param color The color of the annotation
     */
    private void drawAnnotation(String annotation, CartesianCoordinates canvasPosition, Color color) {
        ctx.setFill(color);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.TOP);
        ctx.fillText(annotation, canvasPosition.x(), canvasPosition.y());
    }

}
