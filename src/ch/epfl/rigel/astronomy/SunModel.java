package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.*;

public enum SunModel implements CelestialObjectModel<Sun> {

    SUN();

    private static final double EPSILON_G = Angle.ofDeg(279.557208);
    private static final double OMEGA_G = Angle.ofDeg(283.112438);
    private static final double EXCENTRICITY = 0.016705;
    private static final double TROPIC_YEAR = 365.242191;
    private static final double THETA_0 = Angle.ofDeg(0.533128);


    private SunModel() { }

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double meanAnomaly = (Angle.TAU / (TROPIC_YEAR)) * daysSinceJ2010 + EPSILON_G - OMEGA_G;
        double trueAnomaly = meanAnomaly + 2 * EXCENTRICITY * sin(meanAnomaly);

        double lambda = trueAnomaly + OMEGA_G;

        double angularSize = THETA_0 * ((1 + EXCENTRICITY * cos(trueAnomaly) / (1 - EXCENTRICITY * EXCENTRICITY)));

        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(lambda, 0);

        return new Sun(eclipticCoordinates, eclipticToEquatorialConversion.apply(eclipticCoordinates), (float) angularSize, (float) meanAnomaly);
    }
}
