package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CardinalPoint;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;

public final class DrawSky extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try (InputStream hs = resourceStream("/hygdata_v3.csv");
             InputStream as = resourceStream("/asterisms.txt")) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();

            ZonedDateTime when = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
            GeographicCoordinates where = GeographicCoordinates.ofDeg(6.57, 46.52);
            HorizontalCoordinates projCenter = HorizontalCoordinates.ofDeg(0, 23);
            StereographicProjection projection = new StereographicProjection(projCenter);
            ObservedSky sky = new ObservedSky(when, where, projection, catalogue);

            // Soleil (az=277.6075°, alt=-23.6281°)
            // Lune (az=3.6945°, alt=-65.3622°)
            // Mercure (az=273.5008°, alt=-9.0768°)
            // Vénus (az=260.0222°, alt=15.7877°)
            // Mars (az=340.5677°, alt=-66.1499°)
            // Jupiter (az=310.1084°, alt=-57.9559°)
            // Saturne (az=298.4731°, alt=-50.7679°)
            // Uranus (az=282.1674°, alt=-33.1451°)
            // Neptune (az=268.5595°, alt=-6.6930°)

            Canvas canvas = new Canvas(800, 600);
            Transform planeToCanvas = Transform.affine(1300, 0, 0, -1300, 400, 300);
            SkyCanvasPainter painter = new SkyCanvasPainter(canvas);

            painter.clear();
            painter.drawStars(sky, planeToCanvas);
            painter.drawPlanets(sky, planeToCanvas);
            painter.drawSun(sky, projection, planeToCanvas);
            painter.drawMoon(sky, projection, planeToCanvas);
            painter.drawHorizon(projection, planeToCanvas);

            WritableImage fxImage = canvas.snapshot(null, null);
            BufferedImage swingImage = SwingFXUtils.fromFXImage(fxImage, null);
            ImageIO.write(swingImage, "png", new File("sky.png"));
        }
        Platform.exit();
    }
}