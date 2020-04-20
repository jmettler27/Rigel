package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DateTimeBean {

    private ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();
    private ObjectProperty<LocalTime> timeProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ZoneId> zoneProperty = new SimpleObjectProperty<>();

    public ObjectProperty<LocalDate> dateProperty() {
        return dateProperty;
    }

    public LocalDate getDate() {
        return dateProperty.getValue();
    }

    public void setDate(LocalDate date) {
        dateProperty.setValue(date);
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return timeProperty;
    }

    public LocalTime getTime() {
        return timeProperty.getValue();
    }

    public void setTime(LocalTime time) {
        timeProperty.setValue(time);
    }

    public ObjectProperty<ZoneId> zoneProperty() {
        return zoneProperty;
    }

    public ZoneId getZone() {
        return zoneProperty.getValue();
    }

    public void setZone(ZoneId id) {
        zoneProperty.setValue(id);
    }

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        setDate(zonedDateTime.toLocalDate());
        setTime(zonedDateTime.toLocalTime());
        setZone(zonedDateTime.getZone());
    }
}
