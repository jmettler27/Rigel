package ch.epfl.rigel;

import ch.epfl.rigel.math.Polynomial;

public class Test {

    public static void main(String[] args) {
        Polynomial p = Polynomial.of(5,6,4,7);
        System.out.println(p);
    }
}
