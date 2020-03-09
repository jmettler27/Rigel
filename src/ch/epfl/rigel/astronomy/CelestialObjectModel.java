package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * A model of a celestial object, i.e. a way of calculating the characteristics
 * of this object at a given epoch, based on its observed position at the epoch
 * J2010.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 *
 */
public interface CelestialObjectModel<O> {

    /**
     * Returns the celestial object modeled by this model for the given number
     * of days since the epoch J2010, using the given conversion from the
     * ecliptic to equatorial coordinates of the object.
     * 
     * @param daysSinceJ2010
     *            The number of days elapsed from the epoch J2010 to the epoch
     *            for which we wish to determine the position of the celestial
     *            object (may be negative).
     * @param eclipticToEquatorialConversion
     *            The conversion from ecliptic to equatorial coordinates of the
     *            celestial object
     * 
     * @return the celestial object modeled by this model
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
