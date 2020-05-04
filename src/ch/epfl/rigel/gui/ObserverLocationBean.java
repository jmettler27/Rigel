package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * An observer location bean.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class ObserverLocationBean {

    private final DoubleProperty lonDeg; // The longitude property
    private final DoubleProperty latDeg; // The latitude property
    private final ObjectProperty<GeographicCoordinates> coordinates; // The geographic coordinates property

    /**
     * Default constructor..
     */
    public ObserverLocationBean() {
        lonDeg = new SimpleDoubleProperty();
        latDeg = new SimpleDoubleProperty();
        coordinates = new SimpleObjectProperty<>(null);

        // Binds the coordinates to the longitude and latitude
        ObjectBinding<GeographicCoordinates> coordinatesBind = Bindings.createObjectBinding(
                () -> {
                    setCoordinates(GeographicCoordinates.ofDeg(getLonDeg(), getLatDeg()));
                    return getCoordinates();
                },
                coordinates, lonDeg, latDeg);
    }

    /**
     * Returns the longitude property.
     * @return the longitude property
     */
    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }

    /**
     * Returns the longitude property's content, i.e. the longitude of the observer (in degrees).
     * @return the longitude property's content
     */
    public double getLonDeg() {
        return lonDeg.get();
    }

    /**
     * Sets the longitude property's content to the given longitude (in degrees).
     *
     * @param lon
     *            The new longitude (in degrees) of the longitude property
     */
    public void setLonDeg(double lon) {
        lonDeg.set(lon);
    }

    /**
     * Returns the latitude property.
     * @return the latitude property
     */
    public DoubleProperty latDegProperty() {
        return latDeg;
    }

    /**
     * Returns the latitude property's content, i.e. the latitude of the observer (in degrees).
     * @return the latitude property's content
     */
    public double getLatDeg() {
        return latDeg.get();
    }

    /**
     * Sets the latitude property's content to the given latitude (in degrees).
     *
     * @param lat
     *            The new latitude of the latitude property (in degrees)
     */
    public void setLatDeg(double lat) {
        latDeg.set(lat);
    }

    /**
     * Returns the geographic coordinates property.
     * @return the geographic coordinates property
     */
    public ObjectProperty<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }

    /**
     * Returns the geographic coordinates property's content, i.e. the location of the observer.
     * @return the geographic coordinates property's content
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates.get();
    }

    /**
     * Sets the geographic coordinates property's content to the given geographic coordinates
     */
    public void setCoordinates(GeographicCoordinates coords) {
        coordinates.set(coords);
        setLonDeg(coords.lonDeg());
        setLatDeg(coords.latDeg());
    }
}
