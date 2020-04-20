package ch.epfl.rigel.gui;

import java.time.Duration;

/**
 * A named time accelerator, i.e. a name/accelerator pair.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum NamedTimeAccelerator {

    TIMES_1("1×", TimeAccelerator.continuous(1)),
    TIMES_30("30x", TimeAccelerator.continuous(30)),
    TIMES_300("300x", TimeAccelerator.continuous(300)),
    TIMES_3000("3000x", TimeAccelerator.continuous(3000)),
    DAY("jour", TimeAccelerator.discrete(60, Duration.ofHours(24))),
    SIDEREAL_DAY("jour sidéral", TimeAccelerator.discrete(60, Duration.ofSeconds(23 * 3600 + 56 * 60 + 4)));

    private final String name;
    private final TimeAccelerator accelerator;

    /**
     * Constructs a named time accelerator through its french name and time accelerator.
     *
     * @param name The name of the time accelerator
     * @param accelerator The time accelerator
     */
    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the time accelerator.
     * @return the time accelerator
     */
    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    @Override
    public String toString() {
        return getName();
    }
}
