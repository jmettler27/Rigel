package ch.epfl.rigel;

import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Polynomial;

public class Test {

    public static void main(String[] args) {
        Polynomial p = Polynomial.of(5,-4,-1);
        System.out.print(p);
    }
}
