package org.synthesis.audio;

import org.junit.Test;
import org.synthesis.audio.adapter.AudioAdapter;
import org.synthesis.audio.fft.AudioFFT;
import org.synthesis.audio.fft.FrequencyHistogram;
import org.synthesis.audio.window.IdentityWindowFunction;
import org.synthesis.plot.XYPlot;
import org.synthesis.audio.window.HammingWindow;
import org.synthesis.audio.window.WindowFunction;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.stream.IntStream;

public class AudioFFTTest {

    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("test-files/Low_G_Sharp.wav");

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        WindowFunction hamming = new HammingWindow(.54, .46);
        AudioFFT fft = new AudioFFT(new AudioAdapter());

        plotFrequencies(fft.performFFT(audioStream, hamming, 1024));
    }

    @Test
    public void testPlotFrequencies() {

    }

    private static void plotFrequencies(FrequencyHistogram fftOutput) throws Exception {
        new XYPlot.Builder()
                .withImageLocation("src/main/plots/Low_G_Sharp_Hanning.jpeg")
                .withPlotTitle("Frequencies Low G Sharp")
                .withXLabel("Frequencies")
                .withYLabel("Spectral Coefficient")
                .withXData(fftOutput.getFrequencies())
                .withYData(fftOutput.getSpectralCoefficients())
                .buildAndPlot();
    }
}
