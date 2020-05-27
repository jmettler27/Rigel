package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A viewing parameters bean.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class ViewingParametersBean {

    private final DoubleProperty fieldOfViewDeg; // The field of view property
    private final ObjectProperty<HorizontalCoordinates> center; // The stereographic projection property

    /**
     * Default constructor.
     * Constructs a viewing parameters bean such that the values of its properties are initially null.
     */
    public ViewingParametersBean() {
        fieldOfViewDeg = new SimpleDoubleProperty();
        center = new SimpleObjectProperty<>(null);
    }

    /**
     * Returns the field of view property.
     * @return the field of view property
     */
    public DoubleProperty fieldOfViewDegProperty() {
        return fieldOfViewDeg;
    }

    /**
     * Returns the field of view property's content, i.e. the field of view (in degrees).
     * @return the field of view property's content
     */
    public double getFieldOfViewDeg() {
        return fieldOfViewDeg.getValue();
    }

    /**
     * Sets the field of view property's content to the given field of view (in degrees)
     *
     * @param fovDeg
     *            The new field of view (in degrees) of the field of view property
     */
    public void setFieldOfViewDeg(double fovDeg) {
        fieldOfViewDeg.set(fovDeg);
    }

    /**
     * Returns the center property.
     * @return the center property
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }

    /**
     * Returns the center property's content, i.e. the horizontal coordinates of the projection center.
     * @return the center property's content
     */
    public HorizontalCoordinates getCenter() {
        return center.getValue();
    }

    /**
     * Sets the center property's content to the given Cartesian coordinates.
     *
     * @param hor
     *            The new horizontal coordinates of the center property
     */
    public void setCenter(HorizontalCoordinates hor) {
        center.setValue(hor);
    }
}
