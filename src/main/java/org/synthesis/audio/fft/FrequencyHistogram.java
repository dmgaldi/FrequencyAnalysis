package org.synthesis.audio.fft;

import org.hipparchus.complex.Complex;
import org.hipparchus.transform.FastFourierTransformer;
import org.hipparchus.transform.TransformType;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FrequencyHistogram {
    private SortedMap<Double, Double> frequencyToSpectralCoefficient;
    private Complex[] originalComplices;
    private int samplingRateHz;
    private int sampleSize;

    public FrequencyHistogram(Complex[] complices, int samplingRateHz, int sampleSize) {
        this.sampleSize = sampleSize;
        this.samplingRateHz = samplingRateHz;
        final SortedMap<Double, Double> absFrequencyToSpectralCoefficient = IntStream.range(0, complices.length / 2)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toMap(
                        i -> computeFrequency(i),
                        i -> complices[i].abs(),
                        (v1, v2) -> {
                            throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));
                        },
                        TreeMap::new));
        final double scalingFactor = 1.0 / absFrequencyToSpectralCoefficient.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1.0);
        this.frequencyToSpectralCoefficient = absFrequencyToSpectralCoefficient.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue() * scalingFactor,
                        (v1, v2) -> {
                            throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));
                        },
                        TreeMap::new)
                );
        this.originalComplices = Arrays.copyOf(complices, complices.length);
    }

    public double[] getFrequencies() {
        return frequencyToSpectralCoefficient.keySet().stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    public double[] getSpectralCoefficients() {
        return frequencyToSpectralCoefficient.values().stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    private int computeIndex(int frequency) {
        return frequency / (samplingRateHz / sampleSize);
    }

    private Double computeFrequency(int index) {
        return index * ((double) samplingRateHz / sampleSize);
    }
}
