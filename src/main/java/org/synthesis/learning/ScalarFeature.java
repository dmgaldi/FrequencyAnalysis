package org.synthesis.learning;

public class ScalarFeature implements Feature {

    private double scalar;

    public ScalarFeature(double scalar) {
        this.scalar = scalar;
    }

    public double getScalar() {
        return scalar;
    }

    @Override
    public double getSimilarity(Feature other) {
        if (this == other) {
            return 1;
        } else if (!(other instanceof ScalarFeature)) {
            return 0;
        } else {
            ScalarFeature that = (ScalarFeature) other;
            return Math.sqrt(Math.pow((this.getScalar() - that.getScalar()), 2));
        }
    }
}
