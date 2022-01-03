package org.synthesis.audio.window;

public class IdentityWindowFunction implements WindowFunction {

    @Override
    public double[] apply(double[] input) {
        return input;
    }
}
