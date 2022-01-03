package org.synthesis.image.color;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

public class ColorHistogram {

    double[] reds;
    double[] greens;
    double[] blues;

    private BufferedImage image;
    private ColorModel colorModel;
    private int numBins;

    public ColorHistogram(InputStream input, int numBins) throws IOException {
        this.image = ImageIO.read(input);
        this.colorModel = image.getColorModel();
        this.numBins = numBins;
        this.reds = new double[numBins + 1];
        this.greens = new double[numBins + 1];
        this.blues = new double[numBins + 1];

        evaluateImage();
    }

    public double[] getReds() {
        return reds;
    }

    public double[] getGreens() {
        return greens;
    }

    public double[] getBlues() {
        return blues;
    }

    private void evaluateImage() {
        IntStream.range(0, image.getWidth()).forEach(x ->
            IntStream.range(0, image.getHeight()).forEach(y ->
                adjustHistogramForPixel(image.getRGB(x, y))
            )
        );
    }

    private void adjustHistogramForPixel(int rgb) {
        Color color = new Color(rgb);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int normalizationFactor = red + green + blue;
        reds[(int) Math.floor((double) red / normalizationFactor * numBins)]++;
        greens[(int) Math.floor((double) green / normalizationFactor * numBins)]++;
        blues[(int) Math.floor((double) blue / normalizationFactor * numBins)]++;
    }
}
