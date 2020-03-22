package ch.epfl.rigel.math;

/**
 * A polynomial function.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public final class Polynomial {
    private final double[] coeffs;

    /**
     *Constructs a polynomial with the given coefficients (in decreasing order).
     *
     * @param coefficientN
     *             The n-th degree coefficient
     *
     * @param coefficients
     *             The array of coefficients (from degree n-1 to 0) of this polynomial
     */
    private Polynomial(double coefficientN, double... coefficients) {
        coeffs = new double[coefficients.length + 1];
        coeffs[0] = coefficientN;
        System.arraycopy(coefficients, 0, coeffs, 1, coefficients.length);
    }

    /**
     * Returns a polynomial of degree N with the given coefficients.
     *
     * @param coefficientN
     *            The N-th coefficient of the polynomial, of degree N
     * @param coefficients
     *            The other coefficients, of smaller degree
     *
     * @return a polynomial of degree N with the given coefficients
     *
     * @throws IllegalArgumentException
     *             if the highest-degree coefficient is 0
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        if (coefficientN == 0) {
            throw new IllegalArgumentException();
        }
        double[] coeffsWithoutN = new double[coefficients.length];
        System.arraycopy(coefficients, 0, coeffsWithoutN, 0, coefficients.length);

        return new Polynomial(coefficientN, coeffsWithoutN);
    }

    /**
     * Returns the value of this polynomial function for the given argument : derives f(x).
     *
     * @param x
     *            The given argument
     *
     * @return the value of the polynomial function for the given argument
     */
    public double at(double x) {
        // Polynomial with all coefficients equal to 0
        if (coeffs.length == 0) {
            return 0.0;
        }
        // The lowest-degree coefficient (constant)
        double c0 = coeffs[coeffs.length - 1];

        // Polynomial of degree 0 (constant polynomial), i.e. f(x) = c0 for any x
        if (coeffs.length == 1) {
            return c0;
        }

        else {
            // The value of f(x) to be derived
            double value = coeffs[0]*x;

            // Polynomial of degree at least 2 (exactly two coefficients)
            if (coeffs.length != 2) {
                for (int i = 1; i < coeffs.length - 1; ++i) {
                    value = (value + coeffs[i]) * x;
                }
            }

            // Polynomial of degree 1
            value += c0;

            return value;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Constant polynomial (of degree 0)
        if (coeffs.length == 1) {
            builder.append(coeffs[0]);
        }

        // Linear polynomial (of degree 1)
        else if (coeffs.length == 2) {
            if (Math.abs(coeffs[0]) != 1) {
                builder.append(coeffs[0])
                        .append("x");
            } else if (coeffs[0] == 1) {
                builder.append("x");
            } else {
                builder.append("-x");
            }
        }

        // Polynomial of degree at least 2
        else {
            // Display of the highest-degree (at index 0) coefficient
            if (Math.abs(coeffs[0]) != 1) {
                builder.append(coeffs[0])
                        .append("x^");
            } else if (coeffs[0] == 1) {
                builder.append("x^");
            } else {
                builder.append("-x^");
            }
            builder.append(coeffs.length - 1);
        }

        // Display of the coefficients at index at least 1
        for (int i = 1; i < coeffs.length; ++i) {
            if (coeffs[i] != 0) {
                // Does not display the coefficient if the latter is 1 or -1,
                // unless the last coefficient is a non-zero constant
                if (Math.abs(coeffs[i]) == 1) {
                    if (coeffs[i] == 1) {
                        if (i == coeffs.length - 1) {
                            builder.append("+1");
                        } else if (i == coeffs.length - 2) {
                            builder.append("+x");
                        } else {
                            builder.append("+x^")
                                    .append(coeffs.length - 1 - i);
                        }
                    } else {
                        if (i == coeffs.length - 1) {
                            builder.append("-1");
                        } else if (i == coeffs.length - 2) {
                            builder.append("-x");
                        } else {
                            builder.append("-x^")
                                    .append(coeffs.length - 1 - i);
                        }
                    }
                }
                // The coefficient is not 1
                else {
                    if (coeffs[i] > 0) {
                        builder.append("+");
                    }
                    if (i == coeffs.length - 1) {
                        builder.append(coeffs[i]);
                    } else if (i == coeffs.length - 2) {
                        builder.append(coeffs[i])
                                .append("x");
                    } else {
                        builder.append(coeffs[i])
                                .append("x^")
                                .append(coeffs.length - 1 - i);
                    }
                }
            }
        }
        return builder.toString();
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
