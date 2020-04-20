package ch.epfl.rigel.gui;

import java.time.Duration;

public enum NamedTimeAccelerator {

    TIMES_1("TIMES_1", TimeAccelerator.continuous(1)),
    TIMES_30("TIMES_30", TimeAccelerator.continuous(30)),
    TIMES_300("TIMES_300", TimeAccelerator.continuous(300)),
    TIMES_3000("TIMES_3000", TimeAccelerator.continuous(3000)),
    DAY("DAY", TimeAccelerator.discrete(60, Duration.ofHours(24))),
    SIDEREAL_DAY("SIDEREAL_DAY", TimeAccelerator.discrete(60, Duration.ofSeconds(23 * 3600 + 56 * 60 + 4)));

    private final String name;
    private final TimeAccelerator accelerator;

    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    public String getName() {
        return name;
    }

    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    @Override
    public String toString() {
        return getName();
    }

}
