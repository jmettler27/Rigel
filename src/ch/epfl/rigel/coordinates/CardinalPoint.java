package ch.epfl.rigel.coordinates;

import java.util.List;

/**
 * The eight cardinal and intercardinal points.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public enum CardinalPoint {

    NORTH(0),
    NORTH_EAST(45),
    EAST(90),
    SOUTH_EAST(135),
    SOUTH(180),
    SOUTH_WEST(225),
    WEST(270),
    NORTH_WEST(315);

    private final HorizontalCoordinates hor;
    private final String name;
    private static final double ALTITUDE_UNDER_HORIZON_DEG = -0.5;

    public static final List<CardinalPoint> ALL = List.copyOf(List.of(values()));

    /**
     * Constructs a cardinal point using its corresponding azimuth (in degrees)
     *
     * @param azDeg
     *            The azimuth of the cardinal point (in degrees)
     */
    CardinalPoint(double azDeg) {
        this.hor = HorizontalCoordinates.ofDeg(azDeg, ALTITUDE_UNDER_HORIZON_DEG);
        this.name = hor.azOctantName("N", "E", "S", "O");
    }

    /**
     * Returns the horizontal coordinates of this cardinal point (in radians).
     * @return the horizontal coordinates of this cardinal point (in radians)
     */
    public HorizontalCoordinates getPosition() {
        return hor;
    }

    /**
     * Returns the name of this cardinal point.
     * @return the name of this cardinal point
     */
    public String getName() {
        return name;
    }
}
