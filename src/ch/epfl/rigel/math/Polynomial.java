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
    private Polynomial(double coefficientN, double... coefficients) {

        coeffs = new double[coefficients.length + 1];

        coeffs[0] = coefficientN;
        for (int i = 0; i < coefficients.length; ++i) {
            coeffs[i + 1] = coefficients[i];
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
     *             if the highest-degree coefficient is 0
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        if (coefficientN != 0) {
            double[] coeffsWithoutN = new double[coefficients.length];
            System.arraycopy(coefficients, 0, coeffsWithoutN, 0,
                    coefficients.length);

            return new Polynomial(coefficientN, coeffsWithoutN);

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

        if (coeffs.length == 0) {
            return 0.0;
        }

        else {
            // lowest-degree term (constant)
            double c0 = coeffs[coeffs.length - 1];

            // Polynomial of degree 0 (polynomial of degree 0)
            // ==> f(x) = v for any x
            if (coeffs.length == 1) {
                return c0;
            }

            else {

                // The value of f(x) to be derived
                double v = c0;

                // Polynomial of degree 1
                if (coeffs.length == 2) {
                    v += coeffs[0] * x;
                }

                // Polynomial of degree at least 2 (Length >= 3)
                else {

                    double s = ((coeffs[0] * x) + coeffs[1]) * x;

                    if (coeffs.length == 3) {
                        v += s;
                        return v;
                    }

                    else {
                        // degree at least 3 (Length >= 4
                        for (int i = 2; i < coeffs.length - 1; ++i) {
                            s = (s + coeffs[i]) * x;
                            // v += (v * x + coeffs[i]);
                        }
                        v += s;
                    }

                }
                return v;
            }

        }

    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder("");

        for (int i = coeffs.length - 1; i >= 0; --i) {
            // N'affiche que les coeffs non nuls
            // Si exposant est 0, affiche juste le coeff
            // Si exposant est 1, affiche just le coeff multiplie par x
            // Si le coeff est negatif, mettre un signe -

            if (coeffs[i] != 0) {

                if (Math.abs(coeffs[i]) == 1) {
                    if (coeffs[i] == 1) {
                        if (i == coeffs.length - 1) {
                            b.append(coeffs[i]);
                        } else if (i == coeffs.length - 2) {
                            b.append(displaySign(i) + "x");
                        } else {
                            b.append(displaySign(i) + "x^"
                                    + (coeffs.length - 1 - i));
                        }
                    } else {
                        if (i == coeffs.length - 1) {
                            b.append(coeffs[i]);
                        } else if (i == coeffs.length - 2) {
                            b.append("-x");
                        } else {
                            b.append("-x^" + (coeffs.length - 1 - i));
                        }
                    }
                } else {
                    if (coeffs[i] > 0) {
                        if (i == coeffs.length - 1) {
                            b.append(coeffs[i]);
                        } else if (i == coeffs.length - 2) {
                            b.append(displaySign(i) + coeffs[i] + "x");
                        } else {
                            b.append(displaySign(i) + coeffs[i] + "x^"
                                    + (coeffs.length - 1 - i) + " ");
                        }
                    } else {
                        if (i == coeffs.length - 1) {
                            b.append(coeffs[i]);
                        } else if (i == coeffs.length - 2) {
                            b.append(coeffs[i] + "x");
                        } else {
                            b.append(coeffs[i] + "x^" + (coeffs.length - 1 - i)
                                    + " ");
                        }
                    }
                }
            }
        }
        return b.toString();
    }

    /**
     * Verifies if the i-th (strictly positive) coefficient is the first
     * non-zero displayed coefficient. If it is the case, the sign (+) is not
     * displayed.
     * 
     * @param i
     *            The position of the i-th coefficient
     * @return the string displayed
     */
    public String displaySign(int i) {
        boolean verif = false;
        for (int j = i + 1; j < coeffs.length; j++) {
            if (coeffs[j] == 0) {
                verif = true;
            }
        }
        return (verif ? "" : "+");
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    public final boolean equals() {
        throw new UnsupportedOperationException();
    }

}
