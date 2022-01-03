package org.synthesis.learning;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VectorFeature implements Feature {

    private List<Double> vector;

    @Override
    public double getSimilarity(Feature other) {
        if (this == other) {
            return 1;
        } else if (!(other instanceof VectorFeature)) {
            throw new IllegalArgumentException("A VectorFeature can only be compared against another VectorFeature of the same length");
        } else {
            VectorFeature that = (VectorFeature) other;
            if (that.size() != this.size()) {
                throw new IllegalArgumentException("A VectorFeature can only be compared against another VectorFeature" +
                        " of the same length. Size of this " + this.size() + " Size of that: " + that.size());
            }
//            Math.sqrt(IntStream.range(0, size())
//                    .collect(Collectors.summingDouble())

        }
        return 0.0;
    }

    public double getElement(int index) {
        return vector.get(index);
    }

    public int size() {
        return vector.size();
    }
}
