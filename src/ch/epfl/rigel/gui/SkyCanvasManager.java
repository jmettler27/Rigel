package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.SatelliteCatalogue;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.*;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
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

    public final DoubleBinding mouseAzDeg, mouseAltDeg;
    public final ObjectBinding<CelestialObject> objectUnderMouse;

    private final ViewingParametersBean viewingParameters;
    private final Canvas canvas;
    private final ObserverLocationBean observerLocation;

    private final ObjectProperty<CartesianCoordinates> mousePosition; // The cursor canvas position property

    private final ObjectBinding<StereographicProjection> projection; // The stereographic projection binding
    private final ObjectBinding<Transform> planeToCanvas; // The plane to canvas affine transform binding
    private final ObjectBinding<ObservedSky> observedSky; // The observed sky binding
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;

    // (Bonus) The properties enabling the viewing options
    private final SimpleBooleanProperty
            asterismEnable = new SimpleBooleanProperty(),
            satelliteEnable = new SimpleBooleanProperty(),
            nameEnable = new SimpleBooleanProperty();

    // The maximum distance (in the canvas coordinate system) for searching for the object closest to the mouse cursor
    private static final int MAXIMUM_SEARCH_DISTANCE = 10;

    // The steps (in degrees) of the direction change by each pressing of a cursor key
    private static final double AZ_DEG_KEYBOARD_STEP = 10, ALT_DEG_KEYBOARD_STEP = 5;

    // The valid interval for the field of view (in degrees)
    private static final ClosedInterval FOV_INTERVAL = ClosedInterval.of(30, 150);
    private static final ClosedInterval ALT_STEPS_INTERVAL = ClosedInterval.of(5, 90);

    /**
     * Constructs a sky canvas manager.
     *
     * @param catalogue
     *            The catalogue of the observed stars and asterisms
     * @param dateTime
     *            The instant of observation
     * @param viewingParameters
     *            The parameters of observation
     * @param observerLocation
     *            The place of observation
     */
    public SkyCanvasManager(StarCatalogue catalogue, SatelliteCatalogue satCatalogue, DateTimeBean dateTime,
                            ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {
        this.observerLocation = observerLocation;
        this.viewingParameters = viewingParameters;

        canvas = new Canvas(800, 600); // The canvas on which the sky is drawn
        SkyCanvasPainter painter = new SkyCanvasPainter(canvas); // The painter which draws the sky on the canvas

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

        // Redraws the painter when these properties are changed
        projection.addListener(o -> draw(painter, observedSky.get()));
        planeToCanvas.addListener(o -> draw(painter, observedSky.get()));
        observedSky.addListener(o -> draw(painter, observedSky.get()));
        asterismEnableProperty().addListener(o -> draw(painter, observedSky.get()));
        satelliteEnableProperty().addListener(o -> draw(painter, observedSky.get()));
        nameEnableProperty().addListener(o -> draw(painter, observedSky.get()));


        // The horizontal position of the mouse cursor
        mouseHorizontalPosition = Bindings.createObjectBinding(() -> {
            try {
                // The Cartesian position of the mouse cursor on the plane
                CartesianCoordinates mousePlanePosition = PlaneToCanvas.inverseAtPoint(
                        getMousePosition(), planeToCanvas.get());

                return projection.get().inverseApply(mousePlanePosition);

            } catch (Exception e) {
                return HorizontalCoordinates.of(0, 0);
            }
        }, mousePosition, projection, planeToCanvas);

        // The azimuth (in degrees) of the mouse cursor
        mouseAzDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);

        // The altitude (in degrees) of the mouse cursor
        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        // The celestial object closest to the mouse cursor
        objectUnderMouse = Bindings.createObjectBinding(() -> {
            try {
                CartesianCoordinates mousePlanePosition = PlaneToCanvas.inverseAtPoint(
                        getMousePosition(), planeToCanvas.get());
                double maxPlaneDistance = PlaneToCanvas.inverseAtDistance(MAXIMUM_SEARCH_DISTANCE, planeToCanvas.get());

                Optional<CelestialObject> objectUnderMouse = observedSky.get().objectClosestTo(
                        mousePlanePosition, maxPlaneDistance);

                return objectUnderMouse.orElse(null);

            } catch (Exception e) {
                return null;
            }
        }, observedSky, mousePosition, planeToCanvas);


        // Changes the field of view according to the user's mouse wheel and/or trackpad movements above the canvas
        canvas.setOnScroll(scrollEvent -> {
            double fovDeg = viewingParameters.getFieldOfViewDeg();
            double scrolledFovDeg = fovDeg + scrollMax(scrollEvent.getDeltaX(), scrollEvent.getDeltaY());

            viewingParameters.setFieldOfViewDeg(FOV_INTERVAL.clip(scrolledFovDeg));
        });

        // Changes the direction of observation according to the cursor keys
        canvas.setOnKeyPressed(keyEvent -> {
            keyEvent.consume();
            changeDirection(keyEvent.getCode());
        });

        // Follows the movements of the mouse cursor above the canvas and exports its horizontal coordinates
        canvas.setOnMouseMoved(mouseEvent ->
                setMousePosition(CartesianCoordinates.of(mouseEvent.getX(), mouseEvent.getY())));

        // Sets the mouse as the focus of the keyboard events when a click is detected
        canvas.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                canvas.requestFocus(); // Sets the mouse as the focus of the keyboard events
            }
        });


        draw(painter, observedSky.get());
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
     * Returns the mouse cursor's azimuth property.
     * @return the mouse cursor's azimuth property
     */
    public DoubleBinding mouseAzDegBinding() {
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
    public DoubleBinding mouseAltDegBinding() {
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
     * Returns the mouse horizontal position binding.
     * @return The mouse horizontal position binding
     */
    public ObjectBinding<HorizontalCoordinates> mouseHorizontalPositionBinding() {
        return mouseHorizontalPosition;
    }

    /**
     * Returns the horizontal position of the mouse.
     * @return the horizontal position of the mouse
     */
    public HorizontalCoordinates getMouseHorizontalPosition() {
        return mouseHorizontalPosition.get();
    }

    /**
     * Returns the property of the celestial object under the mouse cursor.
     * @return the property of the celestial object under the mouse cursor
     */
    public ObjectBinding<CelestialObject> objectUnderMouseProperty() {
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
     * @param enable
     *            The condition of enabling (true) or disabling (false) the asterisms
     */
    public void setAsterismEnable(boolean enable) {
        asterismEnable.set(enable);
    }

    /**
     * Returns the property of the satellites' enable.
     * @return the property of the satellites' enable
     */
    public SimpleBooleanProperty satelliteEnableProperty() {
        return satelliteEnable;
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
     * @param enable
     *            The condition of enabling (true) or disabling (false) the satellites
     */
    public void setSatelliteEnable(boolean enable) {
        satelliteEnable.set(enable);
    }

    /**
     * Returns the property of the names' enable.
     * @return the property of the names' enable
     */
    public SimpleBooleanProperty nameEnableProperty() {
        return nameEnable;
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
     * @param enable
     *            The condition of enabling (true) or disabling (false) the names of the brightest objects.
     */
    public void setNameEnable(boolean enable) {
        nameEnable.set(enable);
    }

    /**
     * Sets the position of the mouse on the canvas.
     * @param cart
     *            The new position of the mouse on the canvas
     */
    private void setMousePosition(CartesianCoordinates cart) {
        mousePosition.set(cart);
    }

    /**
     * Returns the maximum of two numbers.
     *
     * @param x
     *            The first number
     * @param y
     *            The second number
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
     * @param keyCode
     *            The code of the directional key
     */
    private void changeDirection(KeyCode keyCode) {
        // The coordinates of the direction of observation
        double centerAzDeg = viewingParameters.getCenter().azDeg();
        double centerAltDeg = viewingParameters.getCenter().altDeg();

        HorizontalCoordinates movedCenter = HorizontalCoordinates.ofDeg(centerAzDeg, centerAltDeg);

        switch (keyCode) {
            case LEFT:
                double azDeg_left = HorizontalCoordinates.AZ_INTERVAL_DEG.reduce(centerAzDeg - AZ_DEG_KEYBOARD_STEP);
                movedCenter = HorizontalCoordinates.ofDeg(azDeg_left, centerAltDeg);
                break;

            case RIGHT:
                double azDeg_right = HorizontalCoordinates.AZ_INTERVAL_DEG.reduce(centerAzDeg + AZ_DEG_KEYBOARD_STEP);
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
     * @param painter
     *            The sky canvas painter
     * @param sky
     *            The observed sky
     */
    private void draw(SkyCanvasPainter painter, ObservedSky sky) {
        painter.clear();
        painter.drawStars(sky, planeToCanvas.get(), asterismEnabled(), nameEnabled());
        painter.drawPlanets(sky, planeToCanvas.get(), nameEnabled());
        painter.drawSun(sky, projection.get(), planeToCanvas.get(), nameEnabled());
        painter.drawMoon(sky, projection.get(), planeToCanvas.get(), observerLocation.getCoordinates(), nameEnabled());
        painter.drawSatellites(sky, planeToCanvas.get(), satelliteEnabled());
        painter.drawHorizon(projection.get(), planeToCanvas.get());
    }
}

