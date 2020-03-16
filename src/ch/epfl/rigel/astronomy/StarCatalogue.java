package ch.epfl.rigel.astronomy;

import javafx.fxml.LoadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class StarCatalogue {

    private final List<Star> stars;
    private final List<Asterism> asterisms;
    private final Map<Asterism, List<Integer>> asterismIndices;

    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        for (Asterism asterism : asterisms) {
            for (Star star : asterism.stars()) {
                if (!(stars.contains(star))) {
                    throw new IllegalArgumentException("At least one asterism contains a star that is not on the star list");
                }
            }
        }

        this.stars = List.copyOf(stars);
        this.asterisms = List.copyOf(asterisms);

        asterismIndices = new HashMap<>();
        for (Asterism a : asterisms) {
            asterismIndices.put(a, indicesStar(a));
        }
    }

    private List<Integer> indicesStar(Asterism asterism) {
        List<Star> starsAsterisms = asterism.stars();
        List<Integer> indices = new ArrayList<>();

        for (Star s : starsAsterisms) {
            indices.add(stars.indexOf(s));
        }
        return indices;
    }

    public List<Star> stars() {
        return List.copyOf(stars);
    }

    public Set<Asterism> asterisms() {
        return asterismIndices.keySet();
    }

    public List<Integer> asterismIndices(Asterism asterism) {
        if (!(asterisms().contains(asterism))) {
            throw new IllegalArgumentException("Asterism not in catalogue");
        }
        return asterismIndices.get(asterism);
    }

    public final static class Builder {
        private List<Star> stars;
        private List<Asterism> asterisms;
        private Map<Asterism, List<Integer>> asterismsIndices;

        Builder() {
            stars = new ArrayList<>();
            asterisms = new ArrayList<>();
            asterismsIndices = new HashMap<>();
        }

        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }

        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
    }

    public interface Loader {

        void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
