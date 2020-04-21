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

    private final DateTimeBean bean;
    private final ZonedDateTime T0; // The initial simulated time (at the beginning of the animation)

    private final ObjectProperty<TimeAccelerator> accelerator; // The time accelerator property
    private final SimpleBooleanProperty running; // The state of the time animator

    private boolean firstStep = true;
    private long previousNano; // The number of nanoseconds elapsed since the beginning of an animation

    /**
     * Constructs a time animator through its date/time bean.
     *
     * @param bean
     *            The date/time bean
     */
    public TimeAnimator(DateTimeBean bean) {
        this.bean = bean;
        T0 = bean.getZonedDateTime();
        accelerator = new SimpleObjectProperty<>();
        running = new SimpleBooleanProperty();
    }

    /**
     * @see AnimationTimer#handle(long)
     */
    @Override
    public void handle(long nbNanos) {
        if (firstStep) { // The method is called for the first time after the timer starts (beginning of an animation)
            previousNano = nbNanos;
            firstStep = false;
        }

        if (isRunning()) {
            bean.setZonedDateTime(getAccelerator().adjust(bean.getZonedDateTime(), nbNanos - previousNano));
            previousNano = nbNanos;
        }
    }

    /**
     * @see AnimationTimer#start()
     */
    @Override
    public void start() {
        super.start();
        running.setValue(true);
    }

    /**
     * @see AnimationTimer#stop()
     */
    @Override
    public void stop() {
        super.stop();
        running.setValue(false);
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
     * @param newAccelerator
     *            The new time accelerator of the time accelerator's property
     */
    public void setAccelerator(TimeAccelerator newAccelerator) {
        accelerator.setValue(newAccelerator);
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
    public boolean isRunning() {
        return running.getValue();
    }
}
