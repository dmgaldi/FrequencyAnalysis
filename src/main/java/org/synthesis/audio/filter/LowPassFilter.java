package org.synthesis.audio.filter;

import org.synthesis.audio.window.BlackmanWindow;
import org.synthesis.audio.window.WindowFunction;
import org.synthesis.math.MathUtils;

public class LowPassFilter implements FIRFilter {
    public static double DEFAULT_TRANSITIONBANDWIDTH = 0.01;

    private int kernelLength;
    private int sliceLength;
    private double normalisedCutoffFrequencyIn;

    public LowPassFilter(double normalisedCutoffFrequencyIn) {
        this.normalisedCutoffFrequencyIn = normalisedCutoffFrequencyIn;
        int kernelLength =  (int) (4 / DEFAULT_TRANSITIONBANDWIDTH);
        // kernel length must be odd:
        if (kernelLength % 2 == 0) {
            kernelLength++;
        }
        this.kernelLength = kernelLength;

        final double normalisedCutoffFrequency = normalisedCutoffFrequencyIn;

        if (normalisedCutoffFrequency <= 0 || normalisedCutoffFrequency >= 0.5) {
            throw new IllegalArgumentException("Normalised cutoff frequency must be between 0 and 0.5, got "
                    + normalisedCutoffFrequency);
        }
        double[] kernel = getKernel();
        // determine the length of the slices by which the signal will be consumed:
        // this is the distance to the second next power of two, so that the slice
        // will be at least as long as the kernel.
        sliceLength = MathUtils.closestPowerOfTwoAbove(2 * kernelLength) - kernelLength;
        initialise(kernel, sliceLength);
    }

    private void void initialise(double[] impulseResponse, int sliceLen) {
        denumeratorCoefficients = new double[impulseResponse.length];
        System.arraycopy(impulseResponse, 0, denumeratorCoefficients, 0, impulseResponse.length);

        if (!MathUtils.isPowerOfTwo(impulseResponse.length + sliceLen))
            throw new IllegalArgumentException("Impulse response length plus slice length must be a power of two");
        this.impulseResponseLength = impulseResponse.length;
        this.sliceLength = sliceLen;
        transformedIR = new double[sliceLen + impulseResponse.length];

        System.arraycopy(impulseResponse, 0, transformedIR, 0, impulseResponse.length);
        FFT.realTransform(transformedIR, false);
        // This means, we are not actually saving the impulseResponse, but only
        // its complex FFT transform.
    }

    @Override
    public double[] getKernel() {
        double[] kernel = new double[kernelLength];
        int m = (kernelLength - 1) / 2;
        double fc = normalisedCutoffFrequencyIn;
        double sum = 0.;
        BlackmanWindow window = new BlackmanWindow(kernelLength);
        for (int i = 0; i < m; i++) {
            kernel[i] = Math.sin(2 * Math.PI * fc * (i - m)) / (i - m) * window.value(i);
            kernel[kernelLength - i - 1] = kernel[i];
            sum += 2 * kernel[i];
        }
        kernel[m] = 2 * Math.PI * fc;
        sum += kernel[m];
        // Normalise to area 1:
        for (int i = 0; i < kernelLength; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }
}
