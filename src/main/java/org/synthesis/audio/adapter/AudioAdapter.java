package org.synthesis.audio.adapter;

import edu.princeton.cs.introcs.StdAudio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class AudioAdapter {
    public static final int SAMPLE_RATE = 44100;
    private static final int BITS_PER_SAMPLE = 16;       // 16-bit audio
    private static final int MONO   = 1;
    private static final int STEREO = 2;
    private static final double MAX_16_BIT = 32768;

    public AudioInputStream convertToWav(double[] amplitudes) {
        // assumes 44,100 samples per second
        // use 16-bit audio, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        byte[] data = new byte[2 * amplitudes.length];
        for (int i = 0; i < amplitudes.length; i++) {
            int temp = (short) (amplitudes[i] * MAX_16_BIT);
            data[2*i + 0] = (byte) temp;
            data[2*i + 1] = (byte) (temp >> 8);
        }

        // now save the file
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return new AudioInputStream(bais, format, amplitudes.length);
    }

    public double[] convertToAmplitudeArray(AudioInputStream ais) throws IOException {
        AudioFormat format = ais.getFormat();
        System.out.println(String.format("Debug: Is big endian?: %s", format.isBigEndian()));
        System.out.println(String.format("Debug: Sample size in bits: %s", format.getSampleSizeInBits()));
        System.out.println(String.format("Debug: Encoding: %s", format.getEncoding()));
        System.out.println(String.format("Debug: FrameRate: %s", format.getFrameRate()));
        System.out.println(String.format("Debug: FrameLength: %s", ais.getFrameLength()));
        System.out.println(String.format("Debug: FrameSize: %s", format.getFrameSize()));
        System.out.println(String.format("Debug: Channels: %s", format.getChannels()));

        AudioFormat audioFormat = ais.getFormat();

        // require sampling rate = 44,100 Hz
        if (audioFormat.getSampleRate() != SAMPLE_RATE) {
            throw new IllegalArgumentException("StdAudio.read() currently supports only a sample rate of " + SAMPLE_RATE + " Hz\n"
                    + "audio format: " + audioFormat);
        }

        // require 16-bit audio
        if (audioFormat.getSampleSizeInBits() != BITS_PER_SAMPLE) {
            throw new IllegalArgumentException("StdAudio.read() currently supports only " + BITS_PER_SAMPLE + "-bit audio\n"
                    + "audio format: " + audioFormat);
        }

        // require little endian
        if (audioFormat.isBigEndian()) {
            throw new IllegalArgumentException("StdAudio.read() currently supports only audio stored using little endian\n"
                    + "audio format: " + audioFormat);
        }

        byte[] bytes = null;
        int bytesToRead = ais.available();
        bytes = new byte[bytesToRead];
        int bytesRead = ais.read(bytes);
        if (bytesToRead != bytesRead) {
            throw new IllegalStateException("read only " + bytesRead + " of " + bytesToRead + " bytes");
        }

        int n = bytes.length;

        // little endian, mono
        if (audioFormat.getChannels() == MONO) {
            double[] data = new double[n/2];
            for (int i = 0; i < n/2; i++) {
                // little endian, mono
                data[i] = ((short) (((bytes[2*i+1] & 0xFF) << 8) | (bytes[2*i] & 0xFF))) / ((double) MAX_16_BIT);
            }
            return data;
        }

        // little endian, stereo
        else if (audioFormat.getChannels() == STEREO) {
            double[] data = new double[n/4];
            for (int i = 0; i < n/4; i++) {
                double left  = ((short) (((bytes[4*i+1] & 0xFF) << 8) | (bytes[4*i + 0] & 0xFF))) / ((double) MAX_16_BIT);
                double right = ((short) (((bytes[4*i+3] & 0xFF) << 8) | (bytes[4*i + 2] & 0xFF))) / ((double) MAX_16_BIT);
                data[i] = (left + right) / 2.0;
            }
            return data;
        }

        else throw new IllegalStateException("audio format is neither mono or stereo");


//        // TODO: handle big endian (or other formats)
//        byte[] audioBytes = new byte[getNextHighestPowerOf2((int) audioInputStream.getFrameLength() * format.getFrameSize())];
//
//        audioInputStream.read(audioBytes);
//
//        return convertToAmplitudeArray(audioBytes);
    }

    private int getNextHighestPowerOf2(int x) {
        System.out.println("original length " + x);
        return (int) Math.pow(2, Math.ceil(Math.log(x)/Math.log(2)));
    }

    /**
     * Hard coded to work for wav files right now
     * @param audioBytes
     * @return
     */
    private int[] convertToAmplitudeArray(byte[] audioBytes) {
        int nlengthInSamples = audioBytes.length / 2;
        int[] amplitudes = new int[nlengthInSamples];
        for (int i = 0; i < nlengthInSamples; i++) {
                          /* First byte is LSB (low order) */
            int LSB = audioBytes[2 * i];
                          /* Second byte is MSB (high order) */
            int MSB = audioBytes[2 * i + 1];
            amplitudes[i] = MSB << 8 | (255 & LSB);
        }
        return amplitudes;
    }
}
