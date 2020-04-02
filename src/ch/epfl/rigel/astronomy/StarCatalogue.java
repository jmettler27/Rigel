package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A catalogue of stars and asterisms.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public final class StarCatalogue {

    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> asterismsWithIndices;

    /**
     * Constructs a catalogue composed of the given stars and asterisms.
     *
     * @param stars
     *            The stars of this catalogue
     * @param asterisms
     *            The asterisms of this catalogue
     * @throws IllegalArgumentException
     *             if at least one asterism contains a star that is not on the given list of stars
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        for (Asterism ast : asterisms) {
            for (Star s : ast.stars()) {
                Preconditions.checkArgument(stars.contains(s));
            }
        }
        this.stars = List.copyOf(stars); // Immutable list of stars

        // Constructs the map by associating to each asterism (the key) its list of indices (the value)
        Map<Asterism, List<Integer>> map = new HashMap<>();
        for (Asterism ast : asterisms) {
            map.put(ast, indicesOf(ast));
        }
        asterismsWithIndices = Map.copyOf(map); // Immutable map
    }

    /**
     * Returns the list of the stars of the catalogue.
     * @return the list of the stars of the catalogue
     */
    public List<Star> stars() {
        return stars;
    }

    /**
     * Returns an immutable view on the set of the asterisms of the catalogue.
     * @return an immutable view on the set of the asterisms of the catalogue
     */
    public Set<Asterism> asterisms() {
        return Set.copyOf(asterismsWithIndices.keySet());
    }

    /**
     * Returns the list of the indices (in the catalogue) of the stars composing the given asterism
     *
     * @param asterism
     *            The asterism of the catalogue
     * @throws IllegalArgumentException
     *             if the given asterism is not contained in the catalogue
     * @return an immutable list of the indices of the stars composing the given asterism
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        Preconditions.checkArgument(asterisms().contains(asterism));
        return List.copyOf(asterismsWithIndices.get(asterism));
    }

    /**
     * Additional method.
     *
     * Constructs the list of the indices of the stars composing the given asterism (i.e. associates the value to the
     * given key, in the map).
     * Note : the given asterism is assumed to be contained in the catalogue since this method is used in the constructor.
     *
     * @param asterism
     *            The given asterism
     * @return the list of the indices of the stars composing the given asterism
     */
    private List<Integer> indicesOf(Asterism asterism) {
        List<Integer> indices = new ArrayList<>();

        // Adds the index of each star as indexed in the list of the stars of the catalogue
        for (Star s : asterism.stars()) {
            indices.add(stars.indexOf(s));
        }
        return indices;
    }

    /**
     * A builder of a catalogue of stars and asterisms.
     *
     * @author Mathias Bouilloud (309979)
     * @author Julien Mettler (309999)
     */
    public final static class Builder {

        private List<Star> stars;
        private List<Asterism> asterisms;

        /**
         * Default constructor.
         * Constructs a builder such that the catalogue under construction is initially empty.
         */
        public Builder() {
            stars = new ArrayList<>(); // Empty list of stars
            asterisms = new ArrayList<>(); // Empty list of asterisms
        }

        /**
         * Adds the given star to the catalogue under construction.
         *
         * @param star
         *            The star to be added to the catalogue
         * @throws NullPointerException
         *             if the star to be added is null
         * @return the builder of the catalogue under construction
         */
        public Builder addStar(Star star) {
            stars.add(Objects.requireNonNull(star));
            return this;
        }

        /**
         * Returns an unmodifiable view on the stars of the catalogue under construction.
         * @return an unmodifiable view on the stars of the catalogue under construction
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        /**
         * Adds the given asterism to the catalogue under construction.
         *
         * @param asterism
         *            The asterism to be added to the catalogue
         * @throws NullPointerException
         *             if the asterism to be added is null
         * @return the builder of the catalogue under construction
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(Objects.requireNonNull(asterism));
            return this;
        }

        /**
         * Returns an unmodifiable view on the asterisms of the catalogue under construction.
         * @return an unmodifiable view on the asterisms of the catalogue under construction
         */
        public List<Asterism> asterisms() {return Collections.unmodifiableList(asterisms);}

        /**
         * Asks the given loader to add to the catalogue under construction the stars and/or asterisms the loader
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
         * Returns the catalogue of the stars and asterisms added so far to its builder.
         * @return the builded catalogue.
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
    }

    /**
     * A loader of a catalogue of stars and asterisms.
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
