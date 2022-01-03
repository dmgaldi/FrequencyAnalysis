package org.synthesis.audio.window;

import java.util.stream.IntStream;

public class BlackmanWindow {

    private static final double A0 = 0.42;
    private static final double A1 = 0.5;
    private static final double A2 = 0.08;

    private int length;

    public BlackmanWindow(int length) {
        this.length = length;
    }

    public double value(int idx) {
        return A0 - A1 * Math.cos((2 * Math.PI * idx) / (length - 1)) + A2 * Math.cos(4 * Math.PI * idx / (length - 1));
    }
}
