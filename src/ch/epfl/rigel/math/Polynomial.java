package ch.epfl.rigel.math;

/**
 * A polynomial function.
 * 
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 * 
 */
public final class Polynomial {
    private double[] coeffs;

    /**
     * Constructs a polynomial with the given coefficients (in decreasing
     * order).
     * 
     * @param coefficients
     *            The array of coefficients of the polynomial
     */
    private Polynomial(double... coefficients) {
        coeffs = new double[coefficients.length];

        for (int i = 0; i < coefficients.length; ++i) {
            coeffs[i] = coefficients[i];
        }
    }

    /**
     * Returns a polynomial of degree N with the given coefficients.
     * 
     * @param coefficientN
     *            The N-th coefficient of the polynomial, of degree N
     * @param coefficients
     *            The other coefficients, of smaller degree
     * @return a polynomial of degree N with the given coefficients
     * 
     * @throws IllegalArgumentException
     *             if the N-th coefficient is 0
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        if (coefficientN != 0) {
            return null;
            // System.arraycopy(src, srcPos, coeffs, destPos, length);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the value of this polynomial function for the given argument.
     * 
     * @param x
     *            The given argument
     * @return the value of the polynomial function for the given argument
     */
    public double at(double x) {
        return 0;
    }

    /**
     * Returns this polynomial function in the Horner form.
     * 
     * @return this polynomial function in the Horner form
     */
    private Polynomial horner() {
        return null;
    }

    @Override
    public String toString() {

        return null;
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    public final boolean equals() {
        throw new UnsupportedOperationException();
    }

}
