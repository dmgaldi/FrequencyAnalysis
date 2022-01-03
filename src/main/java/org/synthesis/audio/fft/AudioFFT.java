package org.synthesis.audio.fft;

import org.hipparchus.complex.Complex;
import org.hipparchus.transform.DftNormalization;
import org.hipparchus.transform.FastFourierTransformer;
import org.hipparchus.transform.TransformType;
import org.hipparchus.transform.TransformUtils;
import org.synthesis.audio.adapter.AudioAdapter;
import org.synthesis.audio.window.WindowFunction;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioFFT {

    // Consider mapping AudioFormats to adapters?
    private AudioAdapter audioAdapter;

    public AudioFFT(AudioAdapter audioAdapter) {
        this.audioAdapter = audioAdapter;
    }

    public FrequencyHistogram performFFT(AudioInputStream audio, WindowFunction windowFunction, int numSamples) throws IOException {
        double[] amplitudes = Arrays.stream(audioAdapter.convertToAmplitudeArray(audio))
                .limit(numSamples)
                .toArray();

        if (windowFunction != null) {
            amplitudes = windowFunction.apply(amplitudes);
        }

        return transformAmplitudes(amplitudes, (int) audio.getFormat().getSampleRate());
    }

    public List<FrequencyHistogram> performWindowedFFT(AudioInputStream audio, WindowFunction windowFunction, int sampleSize) throws IOException {
        double[] amplitudes = Arrays.stream(audioAdapter.convertToAmplitudeArray(audio))
                .toArray();

        if (windowFunction != null) {
            amplitudes = windowFunction.apply(amplitudes);
        }

        return transformAmplitudesFrameByFrame(amplitudes, sampleSize, (int) audio.getFormat().getSampleRate());
    }

    /**
     * Slice array into equally sized consecutive sub arrays of size frameSize and transform each sub array
     * @param amplitudes
     * @param frameSize
     * @return
     */
    private List<FrequencyHistogram> transformAmplitudesFrameByFrame(double[] amplitudes, int frameSize, int sampleRate) {
        List<FrequencyHistogram> frequencyHistograms = new ArrayList<>();
        System.out.println("Num values " + amplitudes.length);
        for (int i = 0; i < amplitudes.length; i++) {
            if (i > 48000 * 60) {
                return frequencyHistograms;
            }
            if (i % frameSize == 0) {
                frequencyHistograms.add(transformAmplitudes(Arrays.copyOfRange(amplitudes, i, i + frameSize), sampleRate));
            }
        }
        return frequencyHistograms;
    }


    private FrequencyHistogram transformAmplitudes(double[] amplitudes, int sampleRate) {
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] complices = transformer.transform(amplitudes, TransformType.FORWARD);
        System.out.println(String.format("List of %d complex numbers returned from fourier transform", complices.length));

        return new FrequencyHistogram(complices, sampleRate, amplitudes.length);
    }
}
