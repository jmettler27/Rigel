package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

public class SkyCanvasManager {

    private final StarCatalogue catalogue;
    private final DateTimeBean dateTime;
    private final ViewingParametersBean viewingParameters;
    private final ObserverLocationBean observerLocation;

    public DoubleBinding mouseAzDeg, mouseAltDeg; // The horizontal coordinates of the mouse cursor
    public ObjectBinding<CelestialObject> objectUnderMouse; // The celestial object closest to the mouse cursor

    private ObjectBinding<StereographicProjection> projection;
    private ObjectBinding<Transform> planeToCanvas;
    private ObjectBinding<ObservedSky> observedSky;

    private ObjectProperty<CartesianCoordinates> mousePosition;
    private ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;

    private ObjectProperty<CelestialObject> objectUnderMouseProperty;

    private Canvas canvas;

    /**
     * @param catalogue
     * @param dateTime
     * @param viewingParameters
     * @param observerLocation
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTime, ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters ) {
        this.catalogue = catalogue;
        this.dateTime = dateTime;
        this.viewingParameters = viewingParameters;
        this.observerLocation = observerLocation;

    }

    /**
     * Returns the longitude property.
     * @return the longitude property
     */
    public ObjectProperty<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouseProperty;
    }

    public Canvas canvas(){
        return null;
    }
}
