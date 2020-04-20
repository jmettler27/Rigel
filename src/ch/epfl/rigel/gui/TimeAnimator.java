package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.ZonedDateTime;

/**
 * A time animator.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class TimeAnimator extends AnimationTimer {

    private final DateTimeBean bean; // The date time bean
    private boolean firstStep = true;
    private long startNano; // The number of seconds elapsed since the beginning of the timer
    private final ZonedDateTime whenStart;

    private final ObjectProperty<TimeAccelerator> accelerator = new SimpleObjectProperty<>(); // The time accelerator property
    private final SimpleBooleanProperty running = new SimpleBooleanProperty(); // The running property

    /**
     * Constructs a time animator through its date/time bean.
     *
     * @param bean The date/time bean
     */
    public TimeAnimator(DateTimeBean bean) {
        this.bean = bean;
        whenStart = bean.getZonedDateTime();
    }

    @Override
    public void handle(long nbNanoSeconds) {
        if (firstStep) {
            startNano = nbNanoSeconds;
            firstStep = false;
        }
        if (getRunning()) {
            bean.setZonedDateTime(getAccelerator().adjust(whenStart, nbNanoSeconds - startNano));
        }
    }

    @Override
    public void start() {
        running.setValue(true);
        super.start();
    }

    @Override
    public void stop() {
        running.setValue(false);
        super.stop();
    }

    /**
     * Returns the time accelerator property.
     * @return the time accelerator property
     */
    public ObjectProperty<TimeAccelerator> acceleratorProperty() {
        return accelerator;
    }

    /**
     * Returns the time accelerator property's content.
     * @return the time accelerator property's content
     */
    public TimeAccelerator getAccelerator() {
        return accelerator.getValue();
    }

    /**
     * Sets the time accelerator property's content
     *
     * @param accelerator The new time accelerator of the time accelerator's property
     */
    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator.setValue(accelerator);
    }

    /**
     * Returns the running property.
     * @return the running property
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    /**
     * Returns the running property's content.
     * @return the running property's content
     */
    public boolean getRunning() {
        return running.getValue();
    }
}
