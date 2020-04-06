package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.coordinates.PlaneToCanvas;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SkyCanvasPainter {

    private final Canvas canvas;
    private final GraphicsContext ctx;

    private static final double DIAMETER = 2.0 * Math.tan(Angle.ofDeg(0.5) / 4.0);
    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);


    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getGraphicsContext2D();
    }

    public void clear() {
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform transform) {
        List<Star> stars = sky.stars();

        double[][] starPositions = sky.starPositions();

        List<Double> list = new ArrayList<>();

        double[] points = new double[2 * stars.size()];
        for (int i = 0; i < stars.size(); i++) {
            list.add(starPositions[0][i]);
            list.add(starPositions[1][i]);
        }

        for (int i = 0; i < 2 * stars.size(); ++i) {
            points[i] = list.get(i);
        }

        double[] transformedPoints = new double[2 * stars.size()];
        Transform concatenation = Transform.translate(transform.getTx(), transform.getTy()).createConcatenation(
                Transform.scale(transform.getMxx(), transform.getMyy()));
        concatenation.transform2DPoints(points, 0, transformedPoints, 0, stars.size());

        drawAsterisms(sky, transformedPoints);

        double[] size = new double[stars.size()];
        int index = 0;
        for (Star star : stars) {
            size[index] = PlaneToCanvas.applyToDistance(diameterForMagnitude(star), transform);

            CartesianCoordinates imagePos = CartesianCoordinates.of(
                    transformedPoints[index * 2], transformedPoints[index * 2 + 1]);

            System.out.println(imagePos.x() + " " + imagePos.y());
            ctx.setFill(BlackBodyColor.colorForTemperature(star.colorTemperature()));
            drawCircle(ctx, size[index], imagePos);

            ++index;
        }
    }

    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform transform) {
        List<Planet> planets = sky.planets();

        double[][] planetPositions = sky.planetPositions();

        List<Double> list = new ArrayList<>();

        double[] points = new double[2 * planets.size()];
        for (int i = 0; i < planets.size(); i++) {
            list.add(planetPositions[0][i]);
            list.add(planetPositions[1][i]);
        }

        for (int i = 0; i < 2 * planets.size(); ++i) {
            points[i] = list.get(i);
        }

        double[] transformedPoints = new double[2 * planets.size()];

        Transform dilatation = Transform.scale(transform.getMxx(), transform.getMyy());
        Transform translation = Transform.translate(transform.getTx(), transform.getTy());
        Transform concatenation = translation.createConcatenation(dilatation);
        concatenation.transform2DPoints(points, 0, transformedPoints, 0, planets.size());

        double[] size = new double[planets.size()];
        int index = 0;
        for (Planet planet : planets) {
            size[index] = PlaneToCanvas.applyToDistance(diameterForMagnitude(planet), transform);

            CartesianCoordinates cartesianPos = CartesianCoordinates.of(
                    transformedPoints[index * 2], transformedPoints[index * 2 + 1]);

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

        CartesianCoordinates imagePos = PlaneToCanvas.applyToPoint(sunPosition, planeToCanvas);

        ctx.setFill(Color.YELLOW.deriveColor(0, 0, 0, 0.25));
        drawCircle(ctx, screenDiameter * 2.2, imagePos);

        ctx.setFill(Color.YELLOW);
        drawCircle(ctx, screenDiameter + 2.0, imagePos);

        ctx.setFill(Color.WHITE);
        drawCircle(ctx, screenDiameter, imagePos);
    }

    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Moon moon = sky.moon();
        CartesianCoordinates moonPosition = sky.moonPosition();
        double diameter = projection.applyToAngle(moon.angularSize());
        double screenDiameter = PlaneToCanvas.applyToDistance(diameter, planeToCanvas);
        CartesianCoordinates imagePos = PlaneToCanvas.applyToPoint(moonPosition, planeToCanvas);

        ctx.setFill(Color.WHITE);
        drawCircle(ctx, screenDiameter, imagePos);
    }

    public void drawHorizon(ObservedSky sky, StereographicProjection projection, Transform transform) {
        HorizontalCoordinates hor = HorizontalCoordinates.ofDeg(45, 0);
        CartesianCoordinates center = PlaneToCanvas.applyToPoint(projection.circleCenterForParallel(hor), transform);
        double radius = PlaneToCanvas.applyToDistance(projection.circleRadiusForParallel(hor), transform);

        ctx.setLineWidth(2); // Trait de largeur 2

        ctx.setFill(Color.RED);
        ctx.fillOval(center.x() - radius, center.y() - radius, radius, radius);

        //drawCircle(ctx, radiusForParallel * 2.0, centerForParallel);
    }

    private void drawAsterisms(ObservedSky sky, double[] starPositions) {
        Set<Asterism> asterisms = sky.asterisms();

        ctx.setLineWidth(1);
        ctx.setFill(Color.BLUE);

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

    private void drawCircle(GraphicsContext ctx, double diameter, CartesianCoordinates coordinates) {
        ctx.fillOval(coordinates.x() - diameter / 2.0, coordinates.y() - diameter / 2.0,
                diameter / 2.0, diameter / 2.0);
    }

    public static double diameterForMagnitude(CelestialObject object) {
        double clippedMagnitude = MAGNITUDE_INTERVAL.clip(object.magnitude());
        double sizeFactor = (99.0 - 17.0 * clippedMagnitude) / 140.0;
        return sizeFactor * DIAMETER;
    }
}
