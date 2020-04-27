package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Transform;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SkyCanvasManager {

    private final StarCatalogue catalogue;
    private final DateTimeBean dateTime;
    private final ViewingParametersBean viewingParameters;
    private final ObserverLocationBean observerLocation;


    public ObjectBinding<CelestialObject> objectUnderMouse; // The celestial object closest to the mouse cursor
    private ObjectBinding<StereographicProjection> projection;
    private ObjectBinding<Transform> planeToCanvas;
    private ObjectBinding<ObservedSky> observedSky;
    private ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;

    public DoubleProperty mouseAzDeg, mouseAltDeg; // The horizontal coordinates of the mouse cursor
    private ObjectProperty<CartesianCoordinates> mousePosition;
    private ObjectProperty<CelestialObject> objectUnderMouseProperty;

    private Canvas canvas;

    /**
     * @param catalogue
     * @param dateTime
     * @param viewingParameters
     * @param observerLocation
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTime, ObserverLocationBean observerLocation,
                            ViewingParametersBean viewingParameters) {
        this.catalogue = catalogue;
        this.dateTime = dateTime;
        this.viewingParameters = viewingParameters;
        this.observerLocation = observerLocation;

        canvas = new Canvas(800, 600);
        SkyCanvasPainter painter = new SkyCanvasPainter(canvas);

        mousePosition = new SimpleObjectProperty<>();
        mouseAzDeg = new SimpleDoubleProperty();


        canvas.setOnMouseMoved((event) -> mousePosition.setValue(CartesianCoordinates.of(event.getX(), event.getY())));
        canvas.setOnScroll((event) -> viewingParameters.setFieldOfViewDeg(viewingParameters.getFieldOfViewDeg()
                + scrollMax(event.getDeltaX(), event.getDeltaY())));
        canvas.setOnKeyPressed(keyEvent -> {
            keyEvent.consume();
            keyHandler(keyEvent.getCode(), viewingParameters.centerProperty());
        });

        canvas.setOnMousePressed((event) -> {
            if (event.isPrimaryButtonDown()) {
                canvas.requestFocus();
            }
        });

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParameters.getCenter()), viewingParameters.centerProperty()
        );

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> {
                    HorizontalCoordinates hor = projection.get().inverseApply(mousePosition.getValue());
                    //mouseAzDeg.setValue(hor.azDeg());
                    //mouseAltDeg.setValue(hor.altDeg());
                    return hor;
                }, projection, mousePosition);

        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(
                        dateTime.getZonedDateTime(), observerLocation.getCoordinates(), projection.get(), catalogue),
                dateTime.dateProperty(), observerLocation.coordinatesProperty(), projection);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> observedSky.get().objectClosestTo(mousePosition.get(), 10).get(), observedSky, mousePosition);


        planeToCanvas = Bindings.createObjectBinding(
                () -> {
                    return Transform.affine(canvas.getWidth() / (projection.getValue().applyToAngle(Angle.ofDeg(viewingParameters.getFieldOfViewDeg()))), 0, 0,
                            -canvas.getWidth() / (projection.getValue().applyToAngle(Angle.ofDeg(viewingParameters.getFieldOfViewDeg()))), 400, 300);
                }, viewingParameters.fieldOfViewDegProperty(), projection, canvas.widthProperty());

        planeToCanvas.addListener(o -> draw(painter, observedSky.get()));
        projection.addListener(o -> draw(painter, observedSky.get()));

        draw(painter, observedSky.get());
    }

    /**
     * Returns the longitude property.
     *
     * @return the longitude property
     */
    public ObjectBinding<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    public Canvas canvas() {
        return canvas;
    }

    private double scrollMax(double n1, double n2) {
        if (Math.abs(n1) > Math.abs(n2)) {
            return n1;
        } else if (Math.abs(n1) < Math.abs(n2)) {
            return n2;
        } else {
            return n1;
        }
    }

    private void keyHandler(KeyCode key, ObjectProperty<HorizontalCoordinates> center) {
        HorizontalCoordinates centerCoords = center.getValue();

        if (key == KeyCode.LEFT) {
            center.setValue(HorizontalCoordinates.ofDeg(centerCoords.azDeg() - 10, centerCoords.altDeg()));
        } else if (key == KeyCode.RIGHT) {
            center.setValue(HorizontalCoordinates.ofDeg(centerCoords.azDeg() + 10, centerCoords.altDeg()));
        } else if (key == KeyCode.UP) {
            center.setValue(HorizontalCoordinates.ofDeg(centerCoords.azDeg(), centerCoords.altDeg() + 5));
        } else if (key == KeyCode.DOWN) {
            center.setValue(HorizontalCoordinates.ofDeg(centerCoords.azDeg(), centerCoords.altDeg() - 5));
        }
    }

    public void draw(SkyCanvasPainter painter, ObservedSky sky) {
        painter.clear();
        painter.drawStars(sky, planeToCanvas.get());
        painter.drawPlanets(sky, planeToCanvas.get());
        painter.drawSun(sky, projection.get(), planeToCanvas.get());
        painter.drawMoon(sky, projection.get(), planeToCanvas.get());
        painter.drawHorizon(projection.get(), planeToCanvas.get());
    }


}
