package org.synthesis.audio.window;

import java.util.stream.IntStream;

public class HammingWindow implements WindowFunction {

    private final double alpha;
    private final double beta;

    public HammingWindow(double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public double[] apply(double[] input) {
        return IntStream.range(0, input.length)
                .mapToDouble(i -> input[i] * getHammingValueAtIndex(i, input.length))
                .toArray();
    }

    private double getHammingValueAtIndex(int idx, int length) {
        return alpha - beta * Math.cos((2 * Math.PI * idx) / (length - 1));
    }
}
