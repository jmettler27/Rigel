package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.SatelliteCatalogue;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.*;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.Optional;

import static java.lang.Math.*;

/**
 * A sky canvas manager, on which the observed sky is drawn.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class SkyCanvasManager {

    public final ObjectProperty<CelestialObject> objectUnderMouse;
    public final DoubleProperty mouseAzDeg; // The azimuth (in degrees) of the position of the mouse's cursor
    public final DoubleProperty mouseAltDeg; // The altitude (in degrees) of the position of the mouse's cursor

    private final ViewingParametersBean viewingParameters;
    private final Canvas canvas;
    private final ObserverLocationBean observerLocation;

    private final ObjectBinding<StereographicProjection> projection; // The stereographic projection binding
    private final ObjectBinding<Transform> planeToCanvas; // The plane to canvas affine transform binding
    private final ObjectBinding<ObservedSky> observedSky; // The observed sky binding
    private final ObjectProperty<CartesianCoordinates> mousePosition; // The cursor's canvas position property

    private final SimpleBooleanProperty
            asterismEnable = new SimpleBooleanProperty(),
            nameEnable = new SimpleBooleanProperty(),
            satelliteEnable = new SimpleBooleanProperty();

    // The maximum distance (in the canvas coordinate system) for searching for the object closest to the mouse cursor
    private static final int MAXIMUM_SEARCH_DISTANCE = 10;

    // The steps (in degrees) of the direction change by each pressing of a cursor key
    private static final double AZ_DEG_KEYBOARD_STEP = 10, ALT_DEG_KEYBOARD_STEP = 5;

    // The valid interval for the field of view (in degrees)
    private static final ClosedInterval FOV_INTERVAL = ClosedInterval.of(30, 150);
    private static final ClosedInterval ALT_STEPS_INTERVAL = ClosedInterval.of(5, 90);

    private static final RightOpenInterval AZ_STEPS_INTERVAL =
            RightOpenInterval.of(HorizontalCoordinates.MINIMUM_AZ_DEG, HorizontalCoordinates.MAXIMUM_AZ_DEG);

    /**
     * Constructs a sky canvas manager.
     *
     * @param catalogue         The catalogue of the observed stars and asterisms
     * @param dateTime          The instant of observation
     * @param viewingParameters The parameters of observation
     * @param observerLocation  The place of observation
     */
    public SkyCanvasManager(StarCatalogue catalogue, SatelliteCatalogue satCatalogue, DateTimeBean dateTime,
                            ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {
        this.observerLocation = observerLocation;
        this.viewingParameters = viewingParameters;

        canvas = new Canvas(800, 600);
        SkyCanvasPainter painter = new SkyCanvasPainter(canvas);

        objectUnderMouse = new SimpleObjectProperty<>();
        mouseAzDeg = new SimpleDoubleProperty();
        mouseAltDeg = new SimpleDoubleProperty();
        mousePosition = new SimpleObjectProperty<>();

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParameters.getCenter()), viewingParameters.centerProperty()
        );

        planeToCanvas = Bindings.createObjectBinding(
                () -> {
                    double fovRad = Angle.ofDeg(viewingParameters.getFieldOfViewDeg());
                    double dilatationFactor = canvas.getWidth() / projection.get().applyToAngle(fovRad);

                    return Transform.affine(dilatationFactor, 0, 0, -dilatationFactor,
                            canvas.getWidth() / 2, canvas.getHeight() / 2);
                }, viewingParameters.fieldOfViewDegProperty(), projection, canvas.widthProperty(), canvas.heightProperty());

        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(dateTime.getZonedDateTime(), observerLocation.getCoordinates(), projection.get(),
                        catalogue, satCatalogue),
                dateTime.dateProperty(), dateTime.timeProperty(), dateTime.zoneProperty(),
                observerLocation.coordinatesBinding(), projection);

        // Inform about changes in the bindings and properties that have an impact on the drawing of the sky, and ask
        // the painter to redraw it
        projection.addListener(o -> draw(painter, observedSky.get()));
        planeToCanvas.addListener(o -> draw(painter, observedSky.get()));
        observedSky.addListener(o -> draw(painter, observedSky.get()));
        asterismEnableProperty().addListener(o -> draw(painter, observedSky.get()));

        // Changes the field of view according to the mouse wheel and/or trackpad movements above the canvas
        canvas.setOnScroll(scrollEvent -> {
            double fovDeg = viewingParameters.getFieldOfViewDeg();
            double scrolledFovDeg = fovDeg + scrollMax(scrollEvent.getDeltaX(), scrollEvent.getDeltaY());

            viewingParameters.setFieldOfViewDeg(FOV_INTERVAL.clip(scrolledFovDeg));
        });

        // Changes the direction of observation according to the cursor keys
        canvas.setOnKeyPressed(keyEvent -> {
            keyEvent.consume();
            changeDirection(keyEvent.getCode());

            try {
                CartesianCoordinates mousePlanePosition = PlaneToCanvas.inverseAtPoint(getMousePosition(),
                        planeToCanvas.get());
                double maxPlaneDistance = PlaneToCanvas.inverseAtDistance(MAXIMUM_SEARCH_DISTANCE, planeToCanvas.get());

                Optional<CelestialObject> objectUnderMouse = observedSky.get().objectClosestTo(mousePlanePosition,
                        maxPlaneDistance);
                setObjectUnderMouse(objectUnderMouse.orElse(null));

            } catch (NonInvertibleTransformException e) {
                e.printStackTrace();
            }
        });

        // Informs about the movements of the mouse cursor above the canvas
        canvas.setOnMouseMoved(mouseEvent -> {
            setMousePosition(CartesianCoordinates.of(mouseEvent.getX(), mouseEvent.getY()));
            try {
                CartesianCoordinates mousePlanePosition = PlaneToCanvas.inverseAtPoint(getMousePosition(),
                        planeToCanvas.get());
                double maxPlaneDistance = PlaneToCanvas.inverseAtDistance(MAXIMUM_SEARCH_DISTANCE, planeToCanvas.get());

                Optional<CelestialObject> objectUnderMouse = observedSky.get().objectClosestTo(mousePlanePosition,
                        maxPlaneDistance);
                setObjectUnderMouse(objectUnderMouse.orElse(null));

                HorizontalCoordinates hor = projection.get().inverseApply(mousePlanePosition);
                setMouseAzDeg(hor.azDeg());
                setMouseAltDeg(hor.altDeg());

            } catch (NonInvertibleTransformException e) {
                e.printStackTrace();
            }
        });

        // Detects the mouse clicks on the canvas
        canvas.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                canvas.requestFocus(); // Makes the mouse the focus of the keyboard events
            }
        });

        draw(painter, observedSky.get());
    }

    /**
     * Returns the mouse cursor's azimuth property.
     * @return the mouse cursor's azimuth property
     */
    public ReadOnlyDoubleProperty mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * Returns the azimuth of the mouse cursor (in degrees)
     * @return the azimuth of the mouse cursor (in degrees)
     */
    public double getMouseAzDeg() {
        return mouseAzDeg.get();
    }

    /**
     * Returns the mouse cursor's altitude property.
     * @return the mouse cursor's altitude property
     */
    public ReadOnlyDoubleProperty mouseAltDegProperty() {
        return mouseAltDeg;
    }

    /**
     * Returns the altitude of the mouse cursor (in degrees).
     * @return the altitude of the mouse cursor (in degrees)
     */
    public double getMouseAltDeg() {
        return mouseAltDeg.get();
    }

    /**
     * Returns the mouse position property.
     * @return The mouse position property
     */
    public ReadOnlyProperty<CartesianCoordinates> mousePositionProperty() {
        return mousePosition;
    }

    /**
     * Returns the position of the mouse on the canvas.
     * @return the position of the mouse on the canvas
     */
    public CartesianCoordinates getMousePosition() {
        return mousePosition.get();
    }

    /**
     * Returns the property of the celestial object under the mouse cursor.
     * @return the property of the celestial object under the mouse cursor
     */
    public ReadOnlyObjectProperty<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * Returns the celestial object under the mouse cursor.
     * @return the celestial object under the mouse cursor
     */
    public CelestialObject getObjectUnderMouse() {
        return objectUnderMouse.get();
    }

    /**
     * Returns the canvas on which the observed sky is drawn.
     * @return The canvas on which the observed sky is drawn
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * Returns the observed sky.
     * @return the observed sky
     */
    public ObservedSky observedSky() {
        return observedSky.get();
    }

    /**
     * Returns the property of the asterisms' enable.
     * @return the property of the asterisms' enable
     */
    public SimpleBooleanProperty asterismEnableProperty() {
        return asterismEnable;
    }

    /**
     * Tells if the asterisms are enabled or not.
     * @return true if the asterisms are enabled, false otherwise
     */
    public boolean asterismEnabled() {
        return asterismEnable.get();
    }

    /**
     * Enables or disables the asterisms.
     * @param b The condition of enabling (true) or disabling (false) the asterisms.
     */
    public void setAsterismEnable(boolean b) {
        asterismEnable.set(b);
    }

    /**
     * Tells if the names of the brightest objects are enabled or not.
     * @return true if the names of the brightest objects are enabled, false otherwise
     */
    public boolean nameEnabled() {
        return nameEnable.get();
    }

    /**
     * Enables or disables the names of the brightest objects.
     * @param b The condition of enabling (true) or disabling (false) the names of the brightest objects .
     */
    public void setNameEnable(boolean b) {
        nameEnable.set(b);
    }

    /**
     * Returns the property of the names' enable.
     * @return the property of the names' enable
     */
    public SimpleBooleanProperty nameEnableProperty() {
        return nameEnable;
    }

    /**
     * Tells if the satellites are enabled or not.
     * @return true if the satellites are enabled, false otherwise
     */
    public boolean satelliteEnabled() {
        return satelliteEnable.get();
    }

    /**
     * Enables or disables the satellites.
     * @param b The condition of enabling (true) or disabling (false) the names of the brightest objects .
     */
    public void setSatelliteEnable(boolean b) {
        satelliteEnable.set(b);
    }

    /**
     * Returns the property of the satellites' enable.
     * @return the property of the satellites' enable
     */
    public SimpleBooleanProperty satelliteEnableProperty() {
        return satelliteEnable;
    }

    /**
     * Sets the azimuth of the mouse cursor (in degrees)
     * @param azDeg The new azimuth of the mouse cursor (in degrees)
     */
    private void setMouseAzDeg(double azDeg) {
        mouseAzDeg.set(azDeg);
    }

    /**
     * Sets the altitude of the mouse cursor (in degrees).
     * @param altDeg The new altitude of the mouse cursor (in degrees)
     */
    private void setMouseAltDeg(double altDeg) {
        mouseAltDeg.set(altDeg);
    }

    /**
     * Sets the position of the mouse on the canvas.
     * @param cart The new position of the mouse on the canvas
     */
    private void setMousePosition(CartesianCoordinates cart) {
        mousePosition.set(cart);
    }

    /**
     * Sets the celestial object under the mouse cursor to the given celestial object.
     * @param object The new object under the cursor of the mouse
     */
    private void setObjectUnderMouse(CelestialObject object) {
        objectUnderMouse.set(object);
    }

    /**
     * Returns the maximum of two numbers.
     *
     * @param x The first number
     * @param y The second number
     * @return The maximal number
     */
    private double scrollMax(double x, double y) {
        double absX = abs(x);
        return max(absX, abs(y)) == absX ? x : y;
    }

    /**
     * Changes the direction of the observation (i.e. the position of the projection's center) using the four directional
     * keys (left, right, up, down).
     *
     * @param keyCode The code of the directional key
     */
    private void changeDirection(KeyCode keyCode) {
        // The coordinates of the direction of observation
        double centerAzDeg = viewingParameters.getCenter().azDeg();
        double centerAltDeg = viewingParameters.getCenter().altDeg();

        HorizontalCoordinates movedCenter = HorizontalCoordinates.ofDeg(centerAzDeg, centerAltDeg);

        switch (keyCode) {
            case LEFT:
                double azDeg_left = AZ_STEPS_INTERVAL.reduce(centerAzDeg - AZ_DEG_KEYBOARD_STEP);
                movedCenter = HorizontalCoordinates.ofDeg(azDeg_left, centerAltDeg);
                break;

            case RIGHT:
                double azDeg_right = AZ_STEPS_INTERVAL.reduce(centerAzDeg + AZ_DEG_KEYBOARD_STEP);
                movedCenter = HorizontalCoordinates.ofDeg(azDeg_right, centerAltDeg);
                break;

            case UP:
                double altDeg_up = ALT_STEPS_INTERVAL.clip(centerAltDeg + ALT_DEG_KEYBOARD_STEP);
                movedCenter = HorizontalCoordinates.ofDeg(centerAzDeg, altDeg_up);
                break;

            case DOWN:
                double altDeg_down = ALT_STEPS_INTERVAL.clip(centerAltDeg - ALT_DEG_KEYBOARD_STEP);
                movedCenter = HorizontalCoordinates.ofDeg(centerAzDeg, altDeg_down);
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
        painter.drawStars(sky, planeToCanvas.get(), asterismEnabled());
        painter.drawPlanets(sky, planeToCanvas.get());
        painter.drawSun(sky, projection.get(), planeToCanvas.get());
        painter.drawMoon(sky, projection.get(), planeToCanvas.get(), observerLocation.getCoordinates());
        painter.drawSatellites(sky, planeToCanvas.get(), satelliteEnabled());
        painter.drawHorizon(projection.get(), planeToCanvas.get());
    }
}

