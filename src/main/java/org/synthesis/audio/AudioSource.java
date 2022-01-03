package org.synthesis.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class AudioSource {
    private String fileLocation;

    public AudioSource(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public AudioInputStream createAudioStream() throws Exception {
        FileInputStream inputStream = new FileInputStream(fileLocation);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return AudioSystem.getAudioInputStream(bufferedInputStream);
    }
}
