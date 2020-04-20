package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class TimeAnimator extends AnimationTimer {

    private final DateTimeBean bean;
    private boolean firstStep = true;
    private long startNano;

    private ObjectProperty<TimeAccelerator> accelerator = new SimpleObjectProperty<>();
    private SimpleBooleanProperty running = new SimpleBooleanProperty();

    public TimeAnimator(DateTimeBean bean) {
        this.bean = bean;
    }

    @Override
    public void handle(long nbNanoSeconds) {
        if (firstStep) {
            startNano = nbNanoSeconds;
            firstStep = false;
        }
        if (getRunning()) {
            bean.setZonedDateTime(getAccelerator().adjust(bean.getZonedDateTime(), nbNanoSeconds - startNano));
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

    public ObjectProperty<TimeAccelerator> acceleratorProperty() {
        return accelerator;
    }

    public TimeAccelerator getAccelerator() {
        return accelerator.getValue();
    }

    public void setAccelerator(TimeAccelerator newAccelerator) {
        accelerator.setValue(newAccelerator);
    }

    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    public boolean getRunning() {
        return running.getValue();
    }


}
