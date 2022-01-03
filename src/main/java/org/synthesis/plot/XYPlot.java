package org.synthesis.plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class XYPlot {

    private JFreeChart chart;

    public static class Builder {

        private String plotTitle;

        private double[][] xData;

        private double[][] yData;

        private String xLabel;

        private String yLabel;

        private String imageLocation;

        public Builder withPlotTitle(String plotTitle) {
            this.plotTitle = plotTitle;
            return this;
        }

        public Builder withXData(double[]... xData) {
            this.xData = xData;
            return this;
        }

        public Builder withYData(double[]... yData) {
            this.yData = yData;
            return this;
        }

        public Builder withXLabel(String xLabel) {
            this.xLabel = xLabel;
            return this;
        }

        public Builder withYLabel(String yLabel) {
            this.yLabel = yLabel;
            return this;
        }

        public Builder withImageLocation(String imageLocation) {
            this.imageLocation = imageLocation;
            return this;
        }

        public void buildAndPlot() throws IOException {
            XYPlot plot = new XYPlot(plotTitle, xData, yData, xLabel, yLabel);
            plot.save(imageLocation);
        }

        public void buildAndUpdate(JFrame frame) {
            try {
                XYPlot plot = new XYPlot(plotTitle, xData, yData, xLabel, yLabel);
                frame.setContentPane(new ChartPanel(plot.chart));
                frame.repaint();
                frame.revalidate();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public XYPlot(String title, double[][] xData, double[][] yData, String xLabel, String yLabel) throws IOException{
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Length of xData and length of yData must be the same");
        }
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int i = 0; i < xData.length; i++) {

            XYSeries series = new XYSeries("Series " + i);
            int argmax = 0;

            for (int j = 0; j < xData[i].length; j++) {
                series.add(xData[i][j], yData[i][j]);
                if (yData[i][j] > yData[i][argmax]) {
                    argmax = i;
                }
            }
            dataset.addSeries(series);
        }

        chart = createChart(dataset, title, xLabel, yLabel);
    }

    private JFreeChart createChart(XYDataset dataset, String title, String xLabel, String yLabel) throws IOException {

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,             // title
                xLabel,             // x-axis label
                yLabel,             // y-axis label
                dataset,                 // data
                PlotOrientation.VERTICAL,
                true,true,false);
        return chart;
    }

    private void save(String imageLocation) throws IOException{
        File lineChart = new File(imageLocation);
        ChartUtilities.saveChartAsJPEG(lineChart, chart, 900, 700);
    }

}
