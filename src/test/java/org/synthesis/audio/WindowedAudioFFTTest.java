package org.synthesis.audio;

import org.junit.Test;
import org.synthesis.animator.JFrameAudioAnimator;
import org.synthesis.audio.adapter.AudioAdapter;
import org.synthesis.audio.fft.AudioFFT;
import org.synthesis.audio.fft.FrequencyHistogram;
import org.synthesis.plot.XYPlot;
import org.synthesis.audio.window.HammingWindow;
import org.synthesis.audio.window.WindowFunction;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class WindowedAudioFFTTest {

    @Test
    public void lowEHighE_windowed() throws Exception{
        performAndPlotFFT("test-files/Low_E_High_E.wav", "src/main/plots/Low_E_High_E/Low_E_High_E_%s.jpeg", 1024);
    }

    @Test
    public void lowG_windowed() throws Exception{
        performAndPlotFFT("test-files/Low_E_High_E.wav", "src/main/plots/Low_E_High_E/Low_E_High_E_%s.jpeg", 1024);
    }

    @Test
    public void treburt() throws Exception{
        performAndPlotFFT("test-files/TreBurt.wav", "src/main/plots/TreBurt/TreBurt_%s.jpeg", 1024 * 8);
    }

    @Test
    public void animate() throws Exception {
        final String inputLocation = "test-files/TreBurt.wav";
        FileInputStream inputStream = new FileInputStream(inputLocation);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        WindowFunction hamming = new HammingWindow(.54, .46);
        AudioFFT fft = new AudioFFT(new AudioAdapter());
        JFrameAudioAnimator animator = new JFrameAudioAnimator(fft);
        animator.animate(new AudioSource(inputLocation));
    }

    private void performAndPlotFFT(String inputLocation, String outputLocation, int sampleSize) throws Exception {
        FileInputStream inputStream = new FileInputStream(inputLocation);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        WindowFunction hamming = new HammingWindow(.54, .46);
        AudioFFT fft = new AudioFFT(new AudioAdapter());

        List<FrequencyHistogram> output = fft.performWindowedFFT(audioStream, hamming, sampleSize);

        IntStream.range(0, output.size())
                .forEach(i -> plotFrequencies(output.get(i), String.format(outputLocation, i), 1024));

    }

    private static void plotFrequencies(FrequencyHistogram histogram, String imageLocation, int frameSize) {
        try {
            new XYPlot.Builder()
                    .withImageLocation(imageLocation)
                    .withPlotTitle("Frequencies Low G Sharp")
                    .withXLabel("Frequencies")
                    .withYLabel("Spectral Coefficient")
                    .withXData(Arrays.copyOfRange(histogram.getFrequencies(), 0, 150))
                    .withYData(Arrays.copyOfRange(histogram.getSpectralCoefficients(), 0, 150))
                    .buildAndPlot();
        } catch(IOException e) {
            throw new IllegalStateException(String.format("Cannot write to file at %s", imageLocation));
        }
    }
}
