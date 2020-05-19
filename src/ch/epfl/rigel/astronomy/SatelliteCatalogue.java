package ch.epfl.rigel.astronomy;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A catalogue of satellites.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class SatelliteCatalogue {

    private final List<Satellite> satellites;

    /**
     * Constructs a catalogue composed of the given satellites.
     *
     * @param satellites
     *            The satellites of this catalogue
     */
    public SatelliteCatalogue(List<Satellite> satellites) {
        this.satellites = List.copyOf(satellites);
    }

    /**
     * Returns the list of the satellites of the catalogue.
     * @return the list of the satellites of the catalogue
     */
    public List<Satellite> satellites() {
        return satellites;
    }

    /**
     * A builder of a catalogue of satellites.
     *
     * @author Mathias Bouilloud (309979)
     * @author Julien Mettler (309999)
     */
    public final static class Builder {

        private final List<Satellite> satellites;

        /**
         * Default constructor.
         * Constructs a builder such that the catalogue under construction is initially empty.
         */
        public Builder() {
            satellites = new ArrayList<>(); // Empty list of stars
        }

        /**
         * Adds the given satellite to the catalogue under construction.
         *
         * @param satellite
         *            The satellite to be added to the catalogue
         * @throws NullPointerException
         *             if the satellite to be added is null
         * @return the builder of the catalogue under construction
         */
        public Builder addSatellite(Satellite satellite) {
            satellites.add(satellite);
            return this;
        }

        /**
         * Returns an unmodifiable view on the satellites of the catalogue under construction.
         * @return an unmodifiable view on the satellites of the catalogue under construction
         */
        public List<Satellite> satellites() {
            return Collections.unmodifiableList(satellites);
        }

        /**
         * Asks the given loader to add to the catalogue under construction the satellites the loader
         * obtains from the given input stream, and returns the builder of this catalogue.
         *
         * @param inputStream
         *            The input stream
         * @param loader
         *            The loader
         * @throws IOException
         *             in case of input/output error
         * @return the builder of the catalogue under construction
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * Returns the catalogue of the satellites added so far to its builder.
         * @return the built catalogue.
         */
        public SatelliteCatalogue build() {
            return new SatelliteCatalogue(satellites);
        }
    }

    /**
     * A loader of a catalogue of satellites.
     *
     * @author Mathias Bouilloud (309979)
     * @author Julien Mettler (309999)
     */
    public interface Loader {

        /**
         * Loads the stars and/or asterisms from the given input stream and adds them to the catalogue
         * under construction of the builder.
         *
         * @param inputStream
         *            The input stream
         * @param builder
         *            The builder of the catalogue of stars and asterisms
         * @throws IOException
         *             in case of input/output error
         */
       void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
