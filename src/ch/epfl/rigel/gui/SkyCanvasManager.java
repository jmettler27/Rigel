package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.*;
import ch.epfl.rigel.math.Angle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.geometry.Point2D;



import java.time.ZonedDateTime;

import static java.lang.Math.*;

/**
 * A sky canvas manager, on which the observed sky is drawn.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class SkyCanvasManager {

    private final StarCatalogue catalogue;
    private final DateTimeBean dateTime;
    private final ObserverLocationBean observerLocation;
    private final ViewingParametersBean viewingParameters;

    private final Canvas canvas;

    public final ObjectProperty<CelestialObject> objectUnderMouse;
    public final DoubleProperty mouseAzDeg; // The azimuth (in degrees) of the position of the mouse's cursor
    public final DoubleProperty mouseAltDeg; // The altitude (in degrees) of the position of the mouse's cursor

    private final ObjectProperty<CartesianCoordinates> mousePosition; // The cursor's canvas position property

    private final ObjectBinding<StereographicProjection> projection; // The stereographic projection binding
    private final ObjectBinding<Transform> planeToCanvas; // The plane to canvas affine transform binding
    private final ObjectBinding<ObservedSky> observedSky; // The observed sky binding
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition; // The cursor's horizontal position binding



    /**
     * Constructs a sky canvas manager.
     *
     * @param catalogue         The catalogue of the observed stars
     * @param dateTime          The stereographic projection of the celestial objects
     * @param viewingParameters The place of observation
     * @param observerLocation  The catalogue of the observed stars
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
        mouseAltDeg = new SimpleDoubleProperty();
        objectUnderMouse = new SimpleObjectProperty<>(null);


        // Informs about the movements of the mouse cursor above the canvas


        // Inform about changes in the bindings and properties that have an impact on the drawing of the sky, and ask
        // the painter to redraw it


        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParameters.getCenter()), viewingParameters.centerProperty()
        );

        observedSky = Bindings.createObjectBinding(
                () -> {
                    ZonedDateTime when = dateTime.getZonedDateTime();
                    GeographicCoordinates where = observerLocation.getCoordinates();
                    return new ObservedSky(when, where, projection.get(), catalogue);
                },
                dateTime.dateProperty(), dateTime.timeProperty(), dateTime.zoneProperty(),
                observerLocation.coordinatesProperty(), projection);

        planeToCanvas = Bindings.createObjectBinding(
                () -> {
                    double fovRad = Angle.ofDeg(viewingParameters.getFieldOfViewDeg());
                    double dilatationFactor = canvas.getWidth() / projection.get().applyToAngle(fovRad);
                    return Transform.affine(dilatationFactor, 0, 0, -dilatationFactor,
                            canvas.getWidth() / 2, canvas.getHeight() / 2);
                }, viewingParameters.fieldOfViewDegProperty(), projection, canvas.widthProperty());

        canvas.setOnMouseMoved(mouseEvent -> {
                    setMousePosition(CartesianCoordinates.of(mouseEvent.getX(), mouseEvent.getY()));
                    //double maxCanvasDistance = PlaneToCanvas.applyToDistance(10, planeToCanvas.get());
            try {
                Transform invert = planeToCanvas.get().createInverse();
                Point2D p = invert.transform(new Point2D(getMousePosition().x(), getMousePosition().y()));
                CartesianCoordinates coords = CartesianCoordinates.of(p.getX(), p.getY());
                CelestialObject object = observedSky.get().objectClosestTo(coords, 10).get();
                setObjectUnderMouse(object);
            } catch (NonInvertibleTransformException e) {}

        });

        // Detects the mouse clicks on the canvas
        canvas.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                canvas.requestFocus(); // Makes the mouse the focus of the keyboard events
            }
        });

        // Reacts to the mouse wheel and/or trackpad movements above the canvas and changes the field of view accordingly
        canvas.setOnScroll(scrollEvent -> {
            double fovDeg = viewingParameters.getFieldOfViewDeg();
            double scrolledFovDeg = fovDeg + scrollMax(scrollEvent.getDeltaX(), scrollEvent.getDeltaY());
            viewingParameters.setFieldOfViewDeg(scrolledFovDeg);
        });

        // Reacts to pressing the cursor keys and changes the direction of observation (i.e. the projection center) accordingly
        canvas.setOnKeyPressed(keyEvent -> {
            keyEvent.consume();
            changeDirection(keyEvent.getCode());
        });

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> {
                    // Il faut inverser la transformation
                    CartesianCoordinates mouseStereoPosition = PlaneToCanvas.applyToPoint(mousePosition.get(), planeToCanvas.get());
                    HorizontalCoordinates hor = projection.get().inverseApply(mouseStereoPosition);
                    mouseAzDeg.setValue(hor.azDeg());
                    mouseAltDeg.setValue(hor.altDeg());
                    return hor;
                }, mousePosition, projection, planeToCanvas);


        /*objectUnderMouseBind = Bindings.createObjectBinding(
                () -> {
                    double maxCanvasDistance = PlaneToCanvas.applyToDistance(10, planeToCanvas.get());
                    return observedSky.get().objectClosestTo(getMousePosition(), maxCanvasDistance).get();
                },observedSky, mousePosition, planeToCanvas);

        setObjectUnderMouse(objectUnderMouseBind.get());*/

        projection.addListener(o -> draw(painter, observedSky.get()));
        planeToCanvas.addListener(o -> draw(painter, observedSky.get()));

        draw(painter, observedSky.get());
    }

    /**
     * Returns the mouse cursor's azimuth property.
     *
     * @return the mouse cursor's azimuth property
     */
    public DoubleProperty mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * @return the azimuth of the mouse cursor (in degrees)
     */
    public double getMouseAzDeg() {
        return mouseAzDeg.get();
    }

    /**
     * @param azDeg The new azimuth of the mouse cursor (in degrees)
     */
    public void setMouseAzDeg(double azDeg) {
        mouseAzDeg.set(azDeg);
    }

    /**
     * @return the mouse cursor's altitude property.
     */
    public DoubleProperty mouseAltDegProperty() {
        return mouseAltDeg;
    }

    /**
     * @return the altitude of the mouse cursor (in degrees)
     */
    public double getMouseAltDeg() {
        return mouseAltDeg.get();
    }

    /**
     * @param altDeg The new altitude of the mouse cursor (in degrees)
     */
    public void setMouseAltDeg(double altDeg) {
        mouseAltDeg.set(altDeg);
    }

    /**
     * Returns the mouse position property.
     *
     * @return The mouse position property
     */
    public ObjectProperty<CartesianCoordinates> mousePositionProperty() {
        return mousePosition;
    }

    /**
     * @return the mouse position on the canvas
     */
    public CartesianCoordinates getMousePosition() {
        return mousePosition.get();
    }

    /**
     * @param cart The new mouse position on the canvas
     */
    public void setMousePosition(CartesianCoordinates cart) {
        mousePosition.set(cart);
    }

    /**
     * Returns the property of the celestial object under the mouse cursor.
     *
     * @return the property of the celestial object under the mouse cursor
     */
    public ObjectProperty<CelestialObject> objectUnderMouseBindProperty() {
        return objectUnderMouse;
    }

    /**
     * Returns the celestial object under the mouse cursor.
     *
     * @return the celestial object under the mouse cursor
     */
    public CelestialObject getObjectUnderMouse() {
        return objectUnderMouse.get();
    }

    /**
     * Sets the celestial object under the mouse cursor to the given celestial object.
     *
     * @param object The new object under the cursor of the mouse
     */
    public void setObjectUnderMouse(CelestialObject object) {
        objectUnderMouse.set(object);
    }

    /**
     * Returns the canvas on which the observed sky is drawn.
     *
     * @return The canvas on which the observed sky is drawn
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * Returns the maximum of two numbers.
     *
     * @param n1 The first number
     * @param n2 The second number
     * @return The maximal number
     */
    private double scrollMax(double n1, double n2) {
        return (max(abs(n1), abs(n2)) == abs(n1)) ? n1 : n2;
    }

    /**
     * Changes the direction of the observation (i.e. the position of the projection's center) using the four directional
     * keys (left, right, up, down).
     *
     * @param keyCode The code of the directional key
     */
    private void changeDirection(KeyCode keyCode) {
        HorizontalCoordinates projCenter = viewingParameters.getCenter(); // The direction of observation
        HorizontalCoordinates movedCenter = HorizontalCoordinates.of(projCenter.az(), projCenter.alt());

        switch (keyCode) {
            case LEFT:
                movedCenter = (projCenter.azDeg() < 10) ?
                        HorizontalCoordinates.ofDeg((projCenter.azDeg() - 10) + 360, projCenter.altDeg()) :
                        HorizontalCoordinates.ofDeg(projCenter.azDeg() - 10, projCenter.altDeg());
                break;
            case RIGHT:
                movedCenter = (projCenter.azDeg() >= 350) ?
                        HorizontalCoordinates.ofDeg((projCenter.azDeg() + 10) - 360, projCenter.altDeg()) :
                        HorizontalCoordinates.ofDeg(projCenter.azDeg() + 10, projCenter.altDeg());
                break;
            case UP:
                if (projCenter.altDeg() <= 85) {
                    movedCenter = HorizontalCoordinates.ofDeg(projCenter.azDeg(), projCenter.altDeg() + 5);
                }
                break;
            case DOWN:
                if (projCenter.altDeg() >= 5) {
                    movedCenter = HorizontalCoordinates.ofDeg(projCenter.azDeg(), projCenter.altDeg() - 5);
                }
                break;
        }
        viewingParameters.setCenter(movedCenter);
    }

    /**
     * Draws the observed sky on the canvas, using a sky canvas painter.
     *
     * @param painter The sky canvas painter
     * @param sky     The observed sky
     */
    private void draw(SkyCanvasPainter painter, ObservedSky sky) {
        painter.clear();
        painter.drawStars(sky, planeToCanvas.get());
        painter.drawPlanets(sky, planeToCanvas.get());
        painter.drawSun(sky, projection.get(), planeToCanvas.get());
        painter.drawMoon(sky, projection.get(), planeToCanvas.get());
        painter.drawHorizon(projection.get(), planeToCanvas.get());
    }
}

