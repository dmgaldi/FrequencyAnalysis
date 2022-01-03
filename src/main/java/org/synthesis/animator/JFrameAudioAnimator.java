package org.synthesis.animator;

import org.jfree.chart.ChartPanel;
import org.synthesis.audio.AudioSource;
import org.synthesis.audio.adapter.AudioAdapter;
import org.synthesis.audio.fft.AudioFFT;
import org.synthesis.audio.fft.FrequencyHistogram;
import org.synthesis.audio.window.HammingWindow;
import org.synthesis.audio.window.WindowFunction;
import org.synthesis.plot.XYPlot;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JFrameAudioAnimator {
    private ScheduledThreadPoolExecutor threadPoolExecutor;
    private AudioFFT audioFFT;

    public static void main(String[] args) throws Exception {
        final String inputLocation = "test-files/TreBurt44100.wav";

        WindowFunction hamming = new HammingWindow(.54, .46);
        AudioFFT fft = new AudioFFT(new AudioAdapter());
        JFrameAudioAnimator animator = new JFrameAudioAnimator(fft);
        animator.animate(new AudioSource(inputLocation));
    }

    public JFrameAudioAnimator(AudioFFT audioFFT) {
        this.threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        this.audioFFT = audioFFT;
    }

    public void animate(AudioSource audio) throws Exception {
        JFrame frame = new JFrame("AnimatedAudio");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        final int sampleSize = 1024 * 4;
        try (final AudioInputStream fftAudio = audio.createAudioStream()) {
            final WindowFunction hamming = new HammingWindow(.54, .46);
            final List<FrequencyHistogram> frequencyHistograms = audioFFT.performWindowedFFT(fftAudio, hamming, sampleSize);
            try (final AudioInputStream playableAudio = audio.createAudioStream()) {
                final Clip clip = AudioSystem.getClip();
                threadPoolExecutor.scheduleAtFixedRate(() -> animateFrame(
                        determineFrame(
                                frequencyHistograms,
                                clip,
                                sampleSize
                        ), frame
                ), 0L, 25L, TimeUnit.MILLISECONDS);
                clip.open(playableAudio);
                clip.loop(0);
                clip.start();
                clip.drain();
            }
        }
    }

    private FrequencyHistogram determineFrame(List<FrequencyHistogram> frequencyHistograms,
                                              Clip clip,
                                              int sampleSize) {
        int index = (int) (clip.getFramePosition() / (float) sampleSize);
        return frequencyHistograms.get(index);
    }

    private void animateFrame(FrequencyHistogram histogram, JFrame jFrame) {
        new XYPlot.Builder()
                .withImageLocation("src/main/plots/Low_G_Sharp_Hanning.jpeg")
                .withPlotTitle("Frame")
                .withXLabel("Frequencies")
                .withYLabel("Spectral Coefficient")
                .withXData(histogram.getFrequencies())
                .withYData(histogram.getSpectralCoefficients())
                .withXData(Arrays.copyOfRange(histogram.getFrequencies(), 0, 256))
                .withYData(Arrays.copyOfRange(histogram.getSpectralCoefficients(), 0, 256))
                .buildAndUpdate(jFrame);
    }
}
