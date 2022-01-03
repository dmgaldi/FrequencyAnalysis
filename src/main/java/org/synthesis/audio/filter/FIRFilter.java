package org.synthesis.audio.filter;

import com.sun.media.sound.FFT;

public interface FIRFilter {

    double[] getKernel();

}
