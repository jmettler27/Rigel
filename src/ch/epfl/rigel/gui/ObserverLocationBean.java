package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * An observer location bean.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class ObserverLocationBean {

    private final DoubleProperty lonDeg; // The longitude property
    private final DoubleProperty latDeg; // The latitude property
    private final ObjectBinding<GeographicCoordinates> coordinates; // The geographic coordinates binding

    /**
     * Default constructor..
     */
    public ObserverLocationBean() {
        lonDeg = new SimpleDoubleProperty();
        latDeg = new SimpleDoubleProperty();
        coordinates = Bindings.createObjectBinding(
                () -> GeographicCoordinates.ofDeg(getLonDeg(), getLatDeg()),
                lonDeg, latDeg );
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
     * Returns the geographic coordinates binding.
     * @return the geographic coordinates binding
     */
    public ObjectBinding<GeographicCoordinates> coordinatesBinding() {
        return coordinates;
    }

    /**
     * Returns the location of the observer.
     * @return the location of the observer
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates.get();
    }

    /**
     * Sets the observer location.
     */
    public void setCoordinates(GeographicCoordinates coords) {
        setLonDeg(coords.lonDeg());
        setLatDeg(coords.latDeg());
    }
}
