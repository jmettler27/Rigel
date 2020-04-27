package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * A date/time bean.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class DateTimeBean {

    private final ObjectProperty<LocalDate> dateProperty; // The date property
    private final ObjectProperty<LocalTime> timeProperty; // The time property
    private final ObjectProperty<ZoneId> zoneProperty; // The time-zone property

    /**
     * Default constructor.
     * Constructs a date/time bean such that the values of its properties are initially null.
     */
    public DateTimeBean() {
        dateProperty = new SimpleObjectProperty<>(null);
        timeProperty = new SimpleObjectProperty<>(null);
        zoneProperty = new SimpleObjectProperty<>(null);
    }

    /**
     * Returns the date property.
     * @return the date property
     */
    public ObjectProperty<LocalDate> dateProperty() {
        return dateProperty;
    }

    /**
     * Returns the date property's content, i.e. the date of observation.
     * @return the date property's content
     */
    public LocalDate getDate() {
        return dateProperty.get();
    }

    /**
     * Sets the date property's content to the given date.
     *
     * @param date
     *            The new date of the date property
     */
    public void setDate(LocalDate date) {
        dateProperty.set(date);
    }

    /**
     * Returns the time property.
     * @return the time property
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return timeProperty;
    }

    /**
     * Returns the time property's content, i.e. the hour of observation.
     * @return the time property's content
     */
    public LocalTime getTime() {
        return timeProperty.get();
    }

    /**
     * Sets the time property's content to the given time.
     *
     * @param time
     *            The new time of the time property
     */
    public void setTime(LocalTime time) {
        timeProperty.set(time);
    }

    /**
     * Returns the time-zone property.
     * @return the time-zone property
     */
    public ObjectProperty<ZoneId> zoneProperty() {
        return zoneProperty;
    }

    /**
     * Returns the time-zone property's content, i.e. the time-zone of observation.
     * @return the time-zone property's content
     */
    public ZoneId getZone() {
        return zoneProperty.get();
    }

    /**
     * Sets the time-zone property's content to the given zone ID.
     *
     * @param id
     *            The new zone ID of the time-zone property
     */
    public void setZone(ZoneId id) {
        zoneProperty.set(id);
    }

    /**
     * Returns the epoch of observation.
     * @return the epoch of observation
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    /**
     * Sets the epoch of observation to the given epoch.
     *
     * @param when
     *            The new epoch of observation
     */
    public void setZonedDateTime(ZonedDateTime when) {
        setDate(when.toLocalDate());
        setTime(when.toLocalTime());
        setZone(when.getZone());
    }
}
