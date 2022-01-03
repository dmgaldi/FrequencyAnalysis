package org.synthesis.audio.org.synthesis.image;

import org.junit.Test;
import org.synthesis.image.color.ColorHistogram;
import org.synthesis.plot.XYPlot;

import java.io.FileInputStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class ColorHistogramTest {

    private ColorHistogram colorHistogram;

    @Test
    public void testColorHistogram() throws Exception {
        String testImageLocation = "test-files/Seattle_Skyline.jpg";
        int numBins = 255;
        colorHistogram = new ColorHistogram(new FileInputStream(testImageLocation), numBins);
        double[] bins = IntStream.range(0, numBins)
                .mapToDouble(i -> (double) i)
                .toArray();
        new XYPlot.Builder()
                .withPlotTitle("Seattle color histogram")
                .withImageLocation("src/main/plots/Seattle_Skyline_Histogram.jpg")
                .withXData(bins, bins, bins)
                .withYData(colorHistogram.getReds(), colorHistogram.getBlues(), colorHistogram.getGreens())
                .withXLabel("Bins")
                .withYLabel("Colors")
                .buildAndPlot();
    }
}
