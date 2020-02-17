package ch.epfl.rigel;

import ch.epfl.rigel.math.ClosedInterval;

public class Test {

    public static void main(String[] args) {
        ClosedInterval inter = ClosedInterval.of(3.0, 5.0);
        System.out.println(inter);
    }
}
