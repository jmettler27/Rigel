package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.coordinates.PlaneToCanvas;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.util.List;

public final class SkyCanvasPainter {

    private final Canvas canvas;
    private static final double DIAMETER = 2.0 * Math.tan(Angle.ofDeg(0.5) / 4.0);
    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
    }

    public void clear() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform transform) {
        List<Star> stars = sky.stars();

        double[][] starPositions = sky.starPositions();
        double[][] transformedStarPositions = new double[2][stars.size()];
        PlaneToCanvas.applyToArray(starPositions, transform, transformedStarPositions);


        GraphicsContext ctx = canvas.getGraphicsContext2D();

        double[] size = new double[stars.size()];
        int index = 0;
        for(Star star : stars){
            size[index] = diameterForMagnitude(star);

            CartesianCoordinates cartesianPos = CartesianCoordinates.of(
                    transformedStarPositions[0][index], transformedStarPositions[1][index]);

            ctx.setFill(BlackBodyColor.colorForTemperature(star.colorTemperature()));
            drawCircle(ctx, size[index], cartesianPos);

            ++index;
        }
    }

    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform transform) {
        List<Planet> planets = sky.planets();

        double[][] planetPositions = sky.planetPositions();
        double[][] transformedPlanetPositions = new double[2][7];
        PlaneToCanvas.applyToArray(planetPositions, transform, transformedPlanetPositions);

        GraphicsContext ctx = canvas.getGraphicsContext2D();

        double[] size = new double[7];
        int index = 0;
        for (Planet planet : planets) {
            size[index] = diameterForMagnitude(planet);

            CartesianCoordinates cartesianPos = CartesianCoordinates.of(
                    transformedPlanetPositions[0][index], transformedPlanetPositions[1][index]);

            ctx.setFill(Color.LIGHTGRAY);
            drawCircle(ctx, size[index], cartesianPos);

            ++index;
        }
    }

    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Sun sun = sky.sun();
        CartesianCoordinates sunPosition = sky.sunPosition();

        double diameter = projection.applyToAngle(sun.angularSize());
        double screenDiameter = PlaneToCanvas.applyToDistance(diameter, planeToCanvas);

        CartesianCoordinates cartesianPos = PlaneToCanvas.applyToPoint(sunPosition, planeToCanvas);

        GraphicsContext ctx = canvas.getGraphicsContext2D();

        ctx.setFill(Color.YELLOW.deriveColor(0, 0, 0, 0.25));
        drawCircle(ctx, screenDiameter * 2.2, cartesianPos);

        ctx.setFill(Color.YELLOW);
        drawCircle(ctx, screenDiameter + 2.0, cartesianPos);

        ctx.setFill(Color.WHITE);
        drawCircle(ctx, screenDiameter, cartesianPos);
    }

    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Moon moon = sky.moon();
        CartesianCoordinates moonPosition = sky.moonPosition();
        double diameter = projection.applyToAngle(moon.angularSize());
        double screenDiameter = PlaneToCanvas.applyToDistance(diameter, planeToCanvas);
        CartesianCoordinates coordinates = PlaneToCanvas.applyToPoint(moonPosition, planeToCanvas);

        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.setFill(Color.WHITE);
        drawCircle(ctx, screenDiameter, coordinates);
    }

    public void drawHorizon(ObservedSky sky, StereographicProjection projection, Transform transform) {

    }

    private void drawCircle(GraphicsContext ctx, double diameter, CartesianCoordinates coordinates) {
        ctx.strokeOval(coordinates.x() - diameter / 2.0, coordinates.y() - diameter / 2.0,
                diameter / 2.0, diameter / 2.0);
        ctx.fillOval(coordinates.x() - diameter / 2.0, coordinates.y() - diameter / 2.0,
                diameter / 2.0, diameter / 2.0);
    }

    private static double diameterForMagnitude(CelestialObject object) {
        double clippedMagnitude = MAGNITUDE_INTERVAL.clip(object.magnitude());
        double sizeFactor = (99.0 - 17.0 * clippedMagnitude) / 140.0;
        return sizeFactor * DIAMETER;
    }
}
